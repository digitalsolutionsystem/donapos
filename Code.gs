/**
 * DoonaPOS — Backend Google Apps Script
 * Database : Google Spreadsheet tempat script ini terpasang (bound script)
 * API      : Web App (POST, body JSON dikirim sebagai text/plain agar tanpa preflight CORS)
 *
 * Cara pasang: lihat PANDUAN-SETUP.md
 */

// ------------------------------------------------------------------
// KONFIGURASI
// ------------------------------------------------------------------
var SESSION_HOURS = 12;          // masa berlaku token login
var MAX_FAILED_LOGIN = 5;        // gagal PIN berturut-turut sebelum dikunci
var LOCK_MINUTES = 5;            // lama penguncian login

/**
 * Skema sheet. `key` = nama koleksi di appState (frontend).
 * `cols`  = kolom yang mudah dibaca manusia di Spreadsheet.
 * Setiap baris juga menyimpan kolom `updatedAt` dan `json` (record lengkap).
 * Kolom `json` adalah sumber kebenaran saat data dibaca kembali, jadi
 * field baru di aplikasi tetap aman walau belum ada kolomnya.
 * `num` = kolom bertipe angka (selain itu diperlakukan sebagai teks).
 */
var SCHEMA = {
  StockItems: {
    key: 'stockItems',
    cols: ['id', 'name', 'unit', 'qty', 'minLimit'],
    num: ['qty', 'minLimit']
  },
  Products: {
    key: 'products',
    cols: ['id', 'name', 'category', 'price', 'image', 'ingredients'],
    num: ['price']
  },
  ConsignmentProducts: {
    key: 'consignmentProducts',
    cols: ['id', 'name', 'vendor', 'category', 'price', 'storePercent', 'costPrice', 'qty', 'soldQty', 'minLimit', 'image'],
    num: ['price', 'storePercent', 'costPrice', 'qty', 'soldQty', 'minLimit']
  },
  Transactions: {
    key: 'transactions',
    cols: ['id', 'baseCode', 'dateISO', 'date', 'customer', 'cashier', 'method', 'total', 'cashGiven', 'cashChange', 'items'],
    num: ['total', 'cashGiven', 'cashChange']
  },
  ConsignmentTransactions: {
    key: 'consignmentTransactions',
    cols: ['id', 'baseCode', 'dateISO', 'date', 'customer', 'cashier', 'method', 'total', 'cashGiven', 'cashChange', 'items'],
    num: ['total', 'cashGiven', 'cashChange']
  },
  Expenses: {
    key: 'expenses',
    cols: ['id', 'dateISO', 'time', 'category', 'amount', 'note', 'timestamp'],
    num: ['amount', 'timestamp']
  },
  ConsignmentExpenses: {
    key: 'consignmentExpenses',
    cols: ['id', 'dateISO', 'time', 'category', 'vendor', 'amount', 'note', 'timestamp'],
    num: ['amount', 'timestamp']
  },
  PartnerSettings: {
    key: 'consignmentPartnerSettings',
    cols: ['id', 'storePercent'],
    num: ['storePercent']
  }
};

var USERS_SHEET = 'Users';
var LOG_SHEET = 'Log';

// ------------------------------------------------------------------
// MENU & SETUP (dijalankan dari Spreadsheet)
// ------------------------------------------------------------------
function onOpen() {
  SpreadsheetApp.getUi()
    .createMenu('DoonaPOS')
    .addItem('Siapkan / perbaiki sheet', 'setup')
    .addSeparator()
    .addItem('Ganti PIN Kasir', 'menuSetPinKasir')
    .addItem('Ganti PIN Pemilik', 'menuSetPinOwner')
    .addToUi();
}

/** Membuat semua sheet + header, pengguna awal, dan kunci rahasia. Aman dijalankan berulang. */
function setup() {
  var ss = SpreadsheetApp.getActive();
  var props = PropertiesService.getScriptProperties();

  if (!props.getProperty('SECRET')) props.setProperty('SECRET', Utilities.getUuid() + Utilities.getUuid());
  if (!props.getProperty('SALT')) props.setProperty('SALT', Utilities.getUuid());

  Object.keys(SCHEMA).forEach(function (name) {
    var def = SCHEMA[name];
    var sh = ss.getSheetByName(name) || ss.insertSheet(name);
    var headers = def.cols.concat(['updatedAt', 'json']);
    sh.getRange(1, 1, 1, headers.length).setValues([headers]).setFontWeight('bold').setBackground('#e7ede5');
    sh.setFrozenRows(1);
    // Kolom teks diformat sebagai teks agar tanggal/ID tidak diubah otomatis oleh Sheets
    headers.forEach(function (h, i) {
      if (def.num.indexOf(h) === -1) sh.getRange(2, i + 1, sh.getMaxRows() - 1, 1).setNumberFormat('@');
    });
  });

  var users = ss.getSheetByName(USERS_SHEET) || ss.insertSheet(USERS_SHEET);
  if (users.getLastRow() < 1) {
    users.getRange(1, 1, 1, 3).setValues([['role', 'pinHash', 'updatedAt']]).setFontWeight('bold').setBackground('#e7ede5');
    users.setFrozenRows(1);
  }
  if (!findUserRow_(users, 'kasir')) setPin_('kasir', '1234');
  if (!findUserRow_(users, 'owner')) setPin_('owner', '8888');

  var log = ss.getSheetByName(LOG_SHEET) || ss.insertSheet(LOG_SHEET);
  if (log.getLastRow() < 1) {
    log.getRange(1, 1, 1, 5).setValues([['waktu', 'pengguna', 'peran', 'aksi', 'detail']]).setFontWeight('bold').setBackground('#e7ede5');
    log.setFrozenRows(1);
  }

  // Sheet Users (berisi hash PIN) disembunyikan agar tidak ikut terlihat sehari-hari
  users.hideSheet();

  try { SpreadsheetApp.getUi().alert('Setup selesai. PIN awal: Kasir 1234, Pemilik 8888. Segera ganti lewat menu DoonaPOS.'); } catch (e) {}
}

function menuSetPinKasir() { promptPin_('kasir', 'Kasir'); }
function menuSetPinOwner() { promptPin_('owner', 'Pemilik'); }

function promptPin_(role, label) {
  var ui = SpreadsheetApp.getUi();
  var r = ui.prompt('Ganti PIN ' + label, 'Masukkan PIN baru (4–8 digit angka):', ui.ButtonSet.OK_CANCEL);
  if (r.getSelectedButton() !== ui.Button.OK) return;
  var pin = String(r.getResponseText()).trim();
  if (!/^\d{4,8}$/.test(pin)) { ui.alert('PIN harus 4–8 digit angka.'); return; }
  setPin_(role, pin);
  ui.alert('PIN ' + label + ' berhasil diganti.');
}

// ------------------------------------------------------------------
// ENTRY POINT API
// ------------------------------------------------------------------
function doGet() {
  return json_({ ok: true, data: { service: 'DoonaPOS API', time: new Date().toISOString() } });
}

function doPost(e) {
  var req;
  try {
    req = JSON.parse(e.postData.contents);
  } catch (err) {
    return json_({ ok: false, code: 'BAD_REQUEST', error: 'Format permintaan tidak valid' });
  }

  try {
    switch (req.action) {
      case 'ping':
        return json_({ ok: true, data: { time: new Date().toISOString() } });
      case 'login':
        return json_({ ok: true, data: login_(req) });
      case 'bootstrap': {
        var user = verifyToken_(req.token);
        return json_({ ok: true, data: bootstrap_(user) });
      }
      case 'sync': {
        var u = verifyToken_(req.token);
        return json_({ ok: true, data: sync_(u, req) });
      }
      default:
        return json_({ ok: false, code: 'BAD_REQUEST', error: 'Aksi tidak dikenal' });
    }
  } catch (err) {
    var code = err && err.code ? err.code : 'SERVER';
    return json_({ ok: false, code: code, error: err && err.message ? err.message : String(err) });
  }
}

function json_(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj)).setMimeType(ContentService.MimeType.JSON);
}

function fail_(code, message) {
  var e = new Error(message);
  e.code = code;
  return e;
}

// ------------------------------------------------------------------
// AUTENTIKASI
// ------------------------------------------------------------------
function login_(req) {
  var role = req.role === 'owner' ? 'owner' : 'kasir';
  var name = String(req.name || '').trim().slice(0, 40) || (role === 'owner' ? 'Pemilik' : 'Kasir 1');
  var pin = String(req.pin || '');

  var cache = CacheService.getScriptCache();
  var failKey = 'fail_' + role;
  var fails = Number(cache.get(failKey) || 0);
  if (fails >= MAX_FAILED_LOGIN) {
    throw fail_('LOCKED', 'Terlalu banyak percobaan. Coba lagi dalam ' + LOCK_MINUTES + ' menit.');
  }

  var users = SpreadsheetApp.getActive().getSheetByName(USERS_SHEET);
  if (!users) throw fail_('SETUP', 'Sheet belum disiapkan. Jalankan menu DoonaPOS > Siapkan sheet.');
  var row = findUserRow_(users, role);
  var ok = row && safeEqual_(String(users.getRange(row, 2).getValue()), hashPin_(pin));

  if (!ok) {
    cache.put(failKey, String(fails + 1), LOCK_MINUTES * 60);
    log_(name, role, 'login_gagal', '');
    throw fail_('PIN', 'PIN tidak sesuai untuk peran ini.');
  }
  cache.remove(failKey);

  var exp = Date.now() + SESSION_HOURS * 3600 * 1000;
  var token = signToken_({ r: role, n: name, e: exp });
  log_(name, role, 'login', '');
  return { token: token, expiresAt: exp, user: { role: role, name: name } };
}

function hashPin_(pin) {
  var salt = PropertiesService.getScriptProperties().getProperty('SALT') || '';
  var bytes = Utilities.computeDigest(Utilities.DigestAlgorithm.SHA_256, salt + ':' + pin, Utilities.Charset.UTF_8);
  return bytes.map(function (b) { return ('0' + (b & 0xff).toString(16)).slice(-2); }).join('');
}

function findUserRow_(sheet, role) {
  var last = sheet.getLastRow();
  if (last < 2) return 0;
  var roles = sheet.getRange(2, 1, last - 1, 1).getValues();
  for (var i = 0; i < roles.length; i++) if (roles[i][0] === role) return i + 2;
  return 0;
}

function setPin_(role, pin) {
  var users = SpreadsheetApp.getActive().getSheetByName(USERS_SHEET);
  var row = findUserRow_(users, role) || users.getLastRow() + 1;
  users.getRange(row, 1, 1, 2).setNumberFormat('@');
  users.getRange(row, 1, 1, 3).setValues([[role, hashPin_(pin), new Date()]]);
}

function signToken_(payload) {
  var body = Utilities.base64EncodeWebSafe(JSON.stringify(payload), Utilities.Charset.UTF_8);
  return body + '.' + hmac_(body);
}

function hmac_(text) {
  var secret = PropertiesService.getScriptProperties().getProperty('SECRET') || '';
  return Utilities.base64EncodeWebSafe(Utilities.computeHmacSha256Signature(text, secret));
}

function verifyToken_(token) {
  if (!token || String(token).indexOf('.') === -1) throw fail_('AUTH', 'Sesi tidak valid. Silakan masuk kembali.');
  var parts = String(token).split('.');
  if (!safeEqual_(hmac_(parts[0]), parts[1])) throw fail_('AUTH', 'Sesi tidak valid. Silakan masuk kembali.');
  var p;
  try {
    p = JSON.parse(Utilities.newBlob(Utilities.base64DecodeWebSafe(parts[0])).getDataAsString());
  } catch (e) {
    throw fail_('AUTH', 'Sesi tidak valid. Silakan masuk kembali.');
  }
  if (!p.e || p.e < Date.now()) throw fail_('AUTH', 'Sesi berakhir. Silakan masuk kembali.');
  return { role: p.r, name: p.n };
}

function safeEqual_(a, b) {
  a = String(a); b = String(b);
  if (a.length !== b.length) return false;
  var diff = 0;
  for (var i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diff === 0;
}

// ------------------------------------------------------------------
// DATA
// ------------------------------------------------------------------
function bootstrap_(user) {
  var ss = SpreadsheetApp.getActive();
  var collections = {};
  Object.keys(SCHEMA).forEach(function (name) {
    var def = SCHEMA[name];
    var sh = ss.getSheetByName(name);
    if (!sh) throw fail_('SETUP', 'Sheet "' + name + '" belum ada. Jalankan menu DoonaPOS > Siapkan sheet.');
    var last = sh.getLastRow();
    var records = [];
    if (last >= 2) {
      var jsonCol = def.cols.length + 2;
      var vals = sh.getRange(2, jsonCol, last - 1, 1).getValues();
      for (var i = 0; i < vals.length; i++) {
        if (!vals[i][0]) continue;
        try { records.push(JSON.parse(vals[i][0])); } catch (e) { /* baris rusak dilewati */ }
      }
    }
    collections[def.key] = records;
  });

  var initialized = PropertiesService.getScriptProperties().getProperty('INITIALIZED') === '1';
  return { initialized: initialized, collections: collections, serverTime: new Date().toISOString() };
}

/**
 * req.changes = { <key>: { upsert: [record...], delete: [id...] } }
 * req.markInitialized = true  -> tandai data awal sudah di-upload
 */
function sync_(user, req) {
  var lock = LockService.getScriptLock();
  if (!lock.tryLock(25000)) throw fail_('BUSY', 'Server sedang sibuk. Akan dicoba lagi.');
  try {
    var ss = SpreadsheetApp.getActive();
    var changes = req.changes || {};
    var applied = {};
    var byKey = {};
    Object.keys(SCHEMA).forEach(function (n) { byKey[SCHEMA[n].key] = n; });

    Object.keys(changes).forEach(function (key) {
      var name = byKey[key];
      if (!name) return;
      var sh = ss.getSheetByName(name);
      if (!sh) throw fail_('SETUP', 'Sheet "' + name + '" belum ada.');
      applied[key] = applyChanges_(sh, SCHEMA[name], changes[key] || {});
    });

    if (req.markInitialized) PropertiesService.getScriptProperties().setProperty('INITIALIZED', '1');

    var summary = Object.keys(applied).map(function (k) {
      return k + ':+' + applied[k].upserted + '/-' + applied[k].deleted;
    }).join(' ');
    if (summary) log_(user.name, user.role, 'sync', summary);

    return { applied: applied, serverTime: new Date().toISOString() };
  } finally {
    lock.releaseLock();
  }
}

function applyChanges_(sh, def, change) {
  var upserts = change.upsert || [];
  var deletes = change.delete || [];
  var width = def.cols.length + 2;
  var now = new Date();

  // Peta id -> nomor baris
  var last = sh.getLastRow();
  var idRows = {};
  if (last >= 2) {
    var ids = sh.getRange(2, 1, last - 1, 1).getValues();
    for (var i = 0; i < ids.length; i++) idRows[String(ids[i][0])] = i + 2;
  }

  var toAppend = [];
  var upserted = 0;
  upserts.forEach(function (rec) {
    if (!rec || rec.id === undefined || rec.id === null || rec.id === '') return;
    var rowValues = toRow_(def, rec, now);
    var existing = idRows[String(rec.id)];
    if (existing) {
      sh.getRange(existing, 1, 1, width).setValues([rowValues]);
    } else {
      toAppend.push(rowValues);
    }
    upserted++;
  });

  if (toAppend.length) {
    var start = sh.getLastRow() + 1;
    var range = sh.getRange(start, 1, toAppend.length, width);
    // pastikan kolom teks tetap teks pada baris baru
    for (var c = 0; c < width; c++) {
      var h = c < def.cols.length ? def.cols[c] : (c === def.cols.length ? 'updatedAt' : 'json');
      if (def.num.indexOf(h) === -1 && h !== 'updatedAt') sh.getRange(start, c + 1, toAppend.length, 1).setNumberFormat('@');
    }
    range.setValues(toAppend);
  }

  // Hapus dari bawah ke atas agar nomor baris tidak bergeser
  var delRows = [];
  deletes.forEach(function (id) {
    var r = idRows[String(id)];
    if (r) delRows.push(r);
  });
  delRows.sort(function (a, b) { return b - a; });
  delRows.forEach(function (r) { sh.deleteRow(r); });

  return { upserted: upserted, deleted: delRows.length };
}

function toRow_(def, rec, now) {
  var row = def.cols.map(function (c) {
    var v = rec[c];
    if (v === undefined || v === null) return '';
    if (typeof v === 'object') return JSON.stringify(v);
    return v;
  });
  row.push(now);
  row.push(JSON.stringify(rec));
  return row;
}

// ------------------------------------------------------------------
// LOG AUDIT
// ------------------------------------------------------------------
function log_(name, role, action, detail) {
  try {
    var sh = SpreadsheetApp.getActive().getSheetByName(LOG_SHEET);
    if (sh) sh.appendRow([new Date(), name, role, action, detail]);
  } catch (e) { /* log tidak boleh menggagalkan permintaan */ }
}
