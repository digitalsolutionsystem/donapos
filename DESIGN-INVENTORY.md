# DESIGN INVENTORY & AUDIT SISTEM UI (FASE 0)
**Proyek:** DoonaPOS — Sistem Kasir & Inventaris Terpadu  
**Target Desain:** Native Tablet Android (Material 3) Design System  
**Dokumen Acuan:** `designpromptrules.md`  
**Status Fase:** Fase 0 — Inventaris Lengkap (Belum Mengubah Kode Aplikasi)  
**File Aplikasi Sumber:** `donapos.html` (8.458 baris, 471 KB)

---

## 1. DAFTAR SEMUA ROUTE, MENU, DAN HALAMAN
Navigasi dikendalikan oleh fungsi `switchTab(tabId, overrideTarget)` yang mengatur visibilitas kontainer `.tab-content` serta highlight status pada `#app-sidebar` dan header mobile.

| No | ID Tab / Route | Nama Menu (Label Resmi) | Tipe Pola Layout | Deskripsi & Isi Layar |
|---|---|---|---|---|
| 1 | `pos` (`#tab-pos`) | **Kasir (POS)** | Dua Panel (Workstation) | **Kiri:** Search bar produk, filter chip kategori (Semua, Kopi, Non-Kopi, Makanan, Titip Jual), grid katalog produk (`#pos-catalog-grid`).<br>**Kanan:** Panel keranjang pesanan 392px (`#pos-cart-panel`), input nama pelanggan, pilihan tipe order (Dine-in / Takeaway), daftar item keranjang (`#cart-items-list`), subtotal & kalkulasi, tombol Checkout (`#btn-checkout`), tombol Kosongkan Keranjang (`clearCart()`). |
| 2 | `payment` (`#tab-payment`) | **Kasir · Layar Pembayaran** | Halaman Penuh / Checkout Alur | Tombol kembali ke POS, ringkasan rincian tagihan pesanan, segmented button metode pembayaran (Tunai / QRIS), input nominal & chip uang cepat (Pas, 20k, 50k, 100k) + numpad/keypad, kalkulasi kembalian otomatis, tombol Konfirmasi Pembayaran (`#btn-confirm-payment`). |
| 3 | `products` (`#tab-products`) | **Katalog & Resep** | Navigasi + Konten (Tabel Master) | Toolbar (search bar, filter kategori, tombol "Tambah Menu Baru"), tabel master produk (foto, nama, kategori, HPP, harga jual, margin laba, tombol rincian formula bahan/BOM, tombol aksi edit & hapus). |
| 4 | `stock` (`#tab-stock`) | **Bahan Mentah** | Navigasi + Konten (Tabel Master) | Toolbar (search bar, filter satuan bahan baku, tombol "Salin Rekap Stok", tombol "Tambah Item Bahan Mentah"), tabel inventaris bahan baku (nama, satuan, stok aktual, limit minimum, status badge peringatan stok, aksi restock cepat, edit, hapus). |
| 5 | `history` (`#tab-history`) | **Riwayat Transaksi** | Navigasi + Konten (Tabel Data) | Toolbar (search nomor invoice, popover kalender tanggal riwayat, segmented filter tipe transaksi: Semua/Reguler/Titip, tombol "Rekap Penjualan Hari Ini"), tabel transaksi (ID invoice, tanggal/waktu, ringkasan pesanan, metode bayar Tunai/QRIS, total nominal, status lunas, tombol lihat/cetak struk fisik). |
| 6 | `report` (`#tab-report`) | **Buku Kas** | Halaman Penuh (Laporan & KPI) | Toolbar (filter periode tanggal dengan popover kalender, preset Hari Ini/7 Hari/Bulan Ini), 6 Kartu KPI ringkasan finansial (Omzet Kotor, Pengeluaran, Laba Bersih, Rata-rata Transaksi), bilah progres perbandingan Tunai vs QRIS, tabel rincian laba/omzet dan daftar menu terlaris. |
| 7 | `expenses` (`#tab-expenses`) | **Pengeluaran** | Navigasi + Konten (Tabel Data) | Toolbar (filter tanggal dengan popover kalender, tombol "Catat Pengeluaran"), ringkasan total petty cash keluar, tabel transaksi beban operasional toko (waktu, kategori, rincian keperluan, nominal, aksi hapus). |
| 8 | `fluctuation` (`#tab-fluctuation`) | **Fluktuasi Omzet** | Halaman Penuh (Analitik) | Toolbar (pilihan bulan & tahun via select dropdown), kartu KPI omzet harian tertinggi/terendah/rata-rata, visualisasi grafik batang fluktuasi harian, perbandingan performa hari kerja vs akhir pekan, analisis jam sibuk kasir. |
| 9 | `consign-stock` (`#tab-consign-stock`) | **Stok Titip** | Navigasi + Konten (Tabel Data) | Toolbar (filter nama mitra, search bar, tombol "Salin Rekap Stok Titipan", tombol "Tambah Barang Titip Jual"), tabel stok barang konsinyasi (nama item, mitra, stok masuk, terjual, sisa, harga jual, bagi hasil toko vs mitra, tombol restock titipan, edit, hapus). |
| 10 | `consign-report` (`#tab-consign-report`) | **Rekap Titip** | Halaman Penuh (Laporan Konsinyasi) | Toolbar (filter tanggal popover kalender, filter mitra, tombol "Atur Bagi Hasil Mitra", tombol "Catat Setoran Titip"), kartu KPI rekapitulasi konsinyasi (omzet kotor titipan, hak mitra/setor, hak toko/komisi), bilah progres bagi hasil, tabel performa penjualan per mitra dan per produk. |
| 11 | `consign-expenses` (`#tab-consign-expenses`) | **Setoran Titip** | Navigasi + Konten (Tabel Data) | Toolbar (filter tanggal kalender popover, tombol "Catat Setoran Titip"), ringkasan total setoran terbayar, tabel transaksi setoran dana ke mitra titip jual (tanggal, mitra, nominal setor, catatan, bukti, aksi hapus). |

*Catatan Sub-rute/Alias:*
- Panggilan rute `consign-history` secara otomatis diarahkan ke `history` dengan mengaktifkan filter tipe `consign`.

---

## 2. DAFTAR SEMUA POPUP, MODAL, OVERLAY, DAN ELEMEN MELAYANG
Hasil pencarian menyeluruh terhadap kata kunci `modal`, `dialog`, `drawer`, `sheet`, `popover`, `dropdown`, `tooltip`, `toast`, `alert`, `confirm`, `position: fixed`, dan `z-index`.

### A. Modal Dialog (15 Komponen)
Semua modal saat ini bertengger di lapisan `position: fixed; inset: 0; z-index: 50` dengan backdrop gelap `bg-stone-950/70` dan class `backdrop-blur-xs`:

| No | ID Modal | Judul / Fungsi | Ukuran / Pola Target | Pemicu (Trigger) & Penutup (Close) | Elemen & Form di Dalamnya |
|---|---|---|---|---|---|
| 1 | `#expense-modal` | Catat Pengeluaran Petty Cash | Modal Form (`sm: 420px`) | `openExpenseModal()` / `closeExpenseModal()` | Input tanggal, input jam, select kategori pengeluaran, input nominal uang, textarea catatan/keperluan, tombol Batal & Simpan. |
| 2 | `#product-modal` | Tambah & Edit Menu / Produk | Modal Form (`lg: 760px`) | `openProductModal(id?)` / `closeProductModal()` | Input nama produk, select kategori, input harga jual, upload file/URL gambar menu + preview box, daftar dinamis takaran bahan baku (BOM) dengan tombol tambah baris, tombol Batal & Simpan. |
| 3 | `#stock-modal` | Tambah & Edit Item Bahan Mentah | Modal Form (`sm: 420px`) | `openStockModal(id?)` / `closeStockModal()` | Input nama bahan baku, select satuan (gram, ml, pcs, lembar), input jumlah stok awal, input batas stok minimum, tombol Batal & Simpan. |
| 4 | `#restock-modal` | Restock Cepat Bahan Baku | Modal Form (`sm: 380px`) | `openRestockModal(id)` / `closeRestockModal()` | Label nama bahan baku terpilih, input angka jumlah penambahan stok, tombol Batal & Tambah Stok. |
| 5 | `#receipt-modal` | Struk Cetak Fisik & Void Transaksi | Preview Struk (`md: 560px`) | `openReceiptModal(trx)` / `closeReceiptModal()` | Area preview kertas struk kasir thermal 80mm/58mm, tombol "Print RawBT", tombol "Cetak Struk (Browser)", tombol "Void Transaksi (Batalkan)" (hanya untuk kasir/admin), tombol Tutup. |
| 6 | `#recipe-detail-modal` | Rincian Formula Resep (BOM Popup) | Dialog Tonal (`md: 560px`) | `openRecipeDetailModal(id)` / `closeRecipeDetailModal()` | Kartu nama produk & kategori, badge porsi estimasi yang bisa dibuat dari stok saat ini, harga jual, daftar komponen takaran bahan mentah per porsi, tombol Tutup. |
| 7 | `#confirm-modal` | Dialog Konfirmasi Umum | Dialog Konfirmasi (`min(380px, 92vw)`) | `showCustomConfirm(title, msg, cb)` / `closeConfirmModal(proceed)` | Ikon konfirmasi, teks judul dialog (`#confirm-title`), teks pesan penjelasan (`#confirm-message`), tombol text "Batal" dan tombol filled/destructive "Ya, Lanjutkan". |
| 8 | `#consign-modal` | Tambah & Edit Barang Titip Jual | Modal Form (`md: 560px`) | `openConsignModal(id?)` / `closeConsignModal()` | Input nama barang, input nama mitra penitip, input harga jual, skema bagi hasil (% komisi toko & harga pokok mitra), input stok awal titipan, input gambar preview, tombol Batal & Simpan. |
| 9 | `#consign-restock-modal` | Restock Cepat Barang Titip Jual | Modal Form (`sm: 380px`) | `openConsignRestockModal(id)` / `closeConsignRestockModal()` | Nama barang titip terpilih, input jumlah unit titipan baru masuk, tombol Batal & Tambahkan. |
| 10 | `#consign-expense-modal` | Catat Setoran Titip / Pengeluaran Konsinyasi | Modal Form (`sm: 420px`) | `openConsignExpenseModal()` / `closeConsignExpenseModal()` | Select nama mitra penitip, input tanggal setor, input nominal uang setor bagi hasil, input keterangan bukti bayar, tombol Batal & Simpan. |
| 11 | `#consign-partner-settings-modal` | Pengaturan Bagi Hasil Semua Mitra | Modal Data (`lg: 760px`) | `openConsignPartnerSettingsModal()` / `closeConsignPartnerSettingsModal()` | Daftar seluruh mitra penitip, total item masing-masing, skema komisi aktif (% toko vs % mitra), tombol "Ubah Skema" per mitra, tombol Tutup. |
| 12 | `#edit-partner-split-modal` | Edit Persentase Komisi Mitra | Modal Form (`sm: 420px`) | `openEditPartnerSplitModal(key)` / `closeEditPartnerSplitModal()` | Nama mitra terpilih, range slider rasio pembagian (0-100%), visual progress bar perbandingan toko & mitra, badge persentase live, tombol Batal & Simpan Perubahan. |
| 13 | `#copy-stock-modal` | Salin Rekap Stok Gudang | Modal Textarea (`md: 560px`) | `openCopyStockReportModal()` / `closeCopyStockReportModal()` | Textarea format teks rekapitulasi stok siap kirim WA, tombol "Salin ke Clipboard", tombol Tutup. |
| 14 | `#copy-consign-stock-modal` | Salin Rekap Stok Barang Titipan | Modal Textarea (`md: 560px`) | `openCopyConsignStockModal()` / `closeCopyConsignStockModal()` | Filter mitra, textarea laporan stok & penjualan konsinyasi hari ini, tombol "Salin Teks", tombol Tutup. |
| 15 | `#daily-sales-rekap-modal` | Rekap Penjualan Harian & Export CSV | Modal Layar Penuh / Form XL (`xl: 980px`) | `openDailySalesRecapModal(date?)` / `closeDailySalesRecapModal()` | Pemilih tanggal, 6 KPI cards (Kas Tunai, QRIS/Bank, Total Omzet, Pengeluaran Beban, Sisa Kas Laci Fisik, Omzet Bersih), tabel rangkuman penjualan per produk urut terlaris, tabel rincian pengeluaran kas, tombol "Salin WA", tombol "Print RawBT", tombol "Download CSV", tombol Tutup. |

### B. Navigation Drawers & Bottom Sheets (2 Komponen)
| ID Komponen | Jenis | Pemicu (Trigger) & Penutup (Close) | Deskripsi |
|---|---|---|---|
| `#app-sidebar` & `#sidebar-backdrop` | Side Drawer Navigasi | `openSidebar()`, `closeSidebar()`, `toggleSidebar()`, shortcut keyboard `M` | Panel navigasi samping melayang (`position: fixed; left: 12px; top: 12px; bottom: 12px; width: 260px`). Berisi identitas toko, status live clock & register kasir, 11 tombol navigasi menu berkategori, dan status tab aktif di footer. |
| `#mobile-cart-bar` | Bottom Sheet / Mobile Sticky Cart | Otomatis tampil saat di tab `pos` dan keranjang > 0 di layar `< lg` | Bilah mengambang di bawah layar (`fixed bottom-4 left-0 right-0 z-20`) berisi badge jumlah item, total belanja, dan tombol pintas ke alur pembayaran. |

### C. Popover Kalender Tanggal (5 Komponen)
Semua popover saat ini memakai `position: absolute; right: 0; z-40` dengan penutupan klik di luar via document click listener:
| ID Popover | ID Tombol Pemicu | Terletak Pada Tab | Fungsi Filter |
|---|---|---|---|
| `#calendar-popover-history` | `#history-calendar-btn` | `tab-history` | Filter tanggal transaksi riwayat penjualan |
| `#calendar-popover-report` | `#report-calendar-btn` | `tab-report` | Filter periode buku kas & laporan laba |
| `#calendar-popover-consign-report` | `#consign-report-calendar-btn` | `tab-consign-report` | Filter tanggal rekapitulasi penjualan titip jual |
| `#calendar-popover-expenses` | `#expenses-calendar-btn` | `tab-expenses` | Filter tanggal pengeluaran operasional toko |
| `#calendar-popover-consign-expenses` | `#consign-expenses-calendar-btn` | `tab-consign-expenses` | Filter tanggal setoran konsinyasi ke mitra |

### D. Toast & Notifikasi (1 Komponen)
| ID Komponen | Fungsi & Lokasi | Status & Pelanggaran Saat Ini |
|---|---|---|
| `#toast-container` | Notifikasi mengambang di kanan bawah (`fixed bottom-4 right-4 z-50`) dipicu oleh `showToast(msg, type)` | Menggunakan styling Tailwind ad-hoc (`bg-stone-900`, `bg-emerald-600`, `bg-rose-600`, `bg-amber-600`), animasi custom translate, dan backdrop-blur. Sesuai aturan katalog, toast/snackbar harus berada di **tengah bawah**, menggunakan token `--inv` dan `--on-inv`, radius `--r-xs`, tinggi minimal 48px, dan `--z-toast`. |

### E. Cetak & Preview Struk Luar (Print Engine)
| Mekanisme | Fungsi | Keterangan |
|---|---|---|
| `#print-area` | Template cetak struk fisik thermal kasir (80mm) | Ditampilkan hanya pada `@media print` untuk dialog print bawaan browser. |
| RawBT Intent URL | Cetak langsung ke printer Bluetooth Android | Membuka skema URL `rawbt:data:...`. Tidak memerlukan perubahan logika bisnis. |
| Popup Window Konsinyasi | Template cetak laporan bagi hasil mitra | Menggunakan `window.open` dengan tag HTML/CSS cetak inline (baris 7670–7750). |

---

## 3. DAFTAR LIBRARY UI / CSS YANG DIPAKAI & VERSI
| Library / Aset Eksternal | Sumber Pemanggilan | Versi | Status Evaluasi terhadap Desain Native Tablet |
|---|---|---|---|
| **Tailwind CSS CDN** | `<script src="https://cdn.tailwindcss.com"></script>` (baris 8) | Runtime CDN (Tailwind v3.x unversioned) | Digunakan untuk utilitas tata letak. Sesuai Aturan Mutlak #5: token harus dipetakan ke dalam `tailwind.config = { theme: { extend: { ... } } }`, dan dilarang memakai nilai arbitrary `[...]` di luar token. |
| **Google Fonts (Plus Jakarta Sans & JetBrains Mono)** | `<link href="...Plus+Jakarta+Sans...JetBrains+Mono..." rel="stylesheet">` (baris 12) | Google Fonts API Web | **Pelanggaran:** Aturan Mutlak mewajibkan satu font: **Roboto** (400, 500, 700) dengan `font-variant-numeric: tabular-nums` global. Penggunaan font monospace untuk angka/label dilarang keras. |
| **Lucide / Heroicons / Inline SVG** | Inline SVG langsung di markup HTML dan JS string template | Set campuran (122 tag SVG) | Stroke-width bervariasi antara 1.5, 2.0, dan 2.2. Harus diseragamkan ke satu set SVG konsisten: `stroke: currentColor; stroke-width: 1.8; fill: none; stroke-linecap/linejoin: round`. |
| **Library UI Pihak Ketiga** | *Tidak ada* | - | Proyek ini bersih dari library berat seperti SweetAlert, Bootstrap Modal, MUI, AntD, Select2, ataupun Flatpickr. Semua komponen dibuat manual. |

---

## 4. DAFTAR PELANGGARAN TERHADAP ATURAN DESIGN SYSTEM

### A. Warna Hardcode & Warna Ad-hoc
- **Hex Colors (`#...`):** Ditemukan **282 kemunculan** di `donapos.html` (baris 23–40, 53–60, 157–342, 349–373, 704–794, 3940–3954, dan template JS). Termasuk warna acak seperti `#26a2ad`, `#059669`, `#4f46e5`, `#d97706`, `#475569`, `#0d9488`, `#e11d48`, `#7c3aed`, `#be123c`, `#356b46`.
- **RGB / RGBA:** Ditemukan **69 kemunculan** (misalnya `rgba(15, 23, 42, 0.16)`, `rgba(0, 0, 0, 0.45)`, `rgba(28, 25, 23, 0.45)`).
- **Nilai Arbitrary Tailwind `[...]`:** Ditemukan **330 kemunculan** (misalnya `border-[#2a2f38]`, `bg-[#26a2ad]`, `text-[10px]`, `text-[11px]`, `text-[9px]`, `max-h-[92vh]`, `min-w-[760px]`).
- **Palet Warna Tailwind Berantakan:** Komponen kasir dan laporan mencampur aduk `stone-*`, `emerald-*`, `indigo-*`, `amber-*`, `slate-*`, `teal-*`, `rose-*`, `purple-*`, `blue-*` tanpa hierarki semantik.

### B. Deklarasi `!important`
- Ditemukan **282 kemunculan** `!important` di dalam blok `<style>` (baris 50–887). Ini menandakan styling ad-hoc yang saling bertabrakan untuk menimpa class Tailwind. Seluruhnya harus dibersihkan dan dialihkan ke token CSS murni.

### C. Inline Style (`style=`)
- Ditemukan **14 kemunculan** atribut inline `style="..."`:
  - Baris 1716 & 1717: Progress bar Tunai vs QRIS di Buku Kas (`style="width: 50%;"`).
  - Baris 1989 & 1990: Progress bar Tunai vs QRIS di Rekap Titip (`style="width: 50%;"`).
  - Baris 3239 & 3240: Progress bar split toko vs mitra di Modal Bagi Hasil (`style="width: 20%;"` & `style="width: 80%;"`).
  - Baris 7681, 7689, 7690, 7693, 7694, 7697, 7698, 7735: Template struk cetak konsinyasi di JavaScript.
- Ditemukan **8 manipulasi DOM** `.style.*` di JavaScript (baris 3953, 3954, 4955, dll).

### D. Nilai `z-index` Liar & Hardcode
- Ditemukan **34 kemunculan** nilai `z-index` angka hardcode di luar token:
  - Tailwind: `z-50` (16x), `z-40` (6x), `z-10` (6x), `z-30` (1x), `z-20` (1x).
  - Raw CSS: `z-index: 50` (baris 74), `z-index: 45` (baris 136), `z-index: 25` (baris 449), `z-index: 0` (baris 396).
  - Seluruhnya harus diganti menjadi token: `--z-sticky: 10`, `--z-nav: 20`, `--z-dropdown: 40`, `--z-drawer: 50`, `--z-scrim: 60`, `--z-modal: 70`, `--z-toast: 80`, `--z-tooltip: 90`.

### E. Border, Radius, & Bayangan Tidak Konsisten
- **Border:** Tersebar 287x `border-2`, 246x `border`, 63x `border-b-2`, 31x `border-t-2` dengan ketebalan dan warna campur aduk (`border-stone-300`, `border-stone-400`, `border-stone-950`, `border-emerald-800`, dll). Aturan menetapkan: **tepat 1 ketebalan outline `--bw: 2px` dengan warna `--out-v` / `--outline` (`#36454f`)**, divider tabel/list menggunakan `1px solid color-mix(...)`, dan state fokus memakai `--bw-focus: 3px solid var(--pri)`.
- **Radius:** Tersebar 173x `rounded-lg`, 138x `rounded-xl`, 63x `rounded-md`, 31x `rounded-full`, 14x `rounded-2xl`, 2x `rounded-sm`, serta CSS `border-radius: 6px, 8px, 10px, 12px, 18px, 20px, 22px, 24px, 999px, 9999px`. Harus dipetakan ketat ke token: `--r-xs: 6px`, `--r-sm: 10px`, `--r-md: 12px`, `--r-lg: 14px`, `--r-xl: 20px`, `--r-2xl: 28px`, `--r-pill: 999px`.
- **Shadow:** Tersebar 65x `shadow-xs`, 57x `shadow-sm`, 46x `shadow`, 35x `shadow-2xs`, 22x `shadow-2xl`, 5x `shadow-md`, 3x `shadow-lg`, 2x `shadow-inner`, 2x `shadow-hover`. Harus diganti hanya ke 3 tingkatan elevasi token: `--sh-1` (kartu/input/chip), `--sh-2` (panel/drawer/dropdown), `--sh-3` (dialog/modal).

### F. Ikon Campur & Emoji Karakter
- Ditemukan emoji di antarmuka pengguna:
  - Baris 3122: Emoji `💡` pada kotak formula modal bagi hasil.
  - Baris 6842: Emoji `⚙️ Atur` pada tombol pengaturan mitra di tabel.
  - Baris 6585 & 7616: Karakter unicode `✕` pada tombol hapus pengeluaran.
  - Baris 6207–6228: Emoji `📅`, `💵`, `💰`, `🔻`, `📥`, `✨`, `💸`, `📦` pada template salin WhatsApp.
- 122 tag SVG inline memakai stroke width berbeda-beda (2.2, 2.0, 1.5).

### G. Tipografi & Kapitalisasi
- Ditemukan **406 kemunculan** class `font-mono` untuk harga, kuantitas, nomor telepon, dan label umum. Aturan melarang keras font monospace untuk angka/label, dan mewajibkan font Roboto proporsional dengan `font-variant-numeric: tabular-nums`.
- Ditemukan **182 kemunculan** class `uppercase` pada label badge, header kolom tabel, judul panel, dan tombol. Aturan mewajibkan **sentence case** di seluruh antarmuka.

### H. Efek yang Dilarang (Gradien, Hover Membesar, Glassmorphism)
- Baris 77–80, 348–376, 582–583, 798–805: Glassmorphism (`backdrop-filter: blur(...)`).
- Baris 380–398, 704, 738, 750, 791, 830: Gradien dekoratif pada latar belakang `body`, ambient glow radial, dan tombol.
- Baris 159, 162, 166, 191, 711, 745, 825, 1087: Hover transform yang mengangkat/membesarkan tombol (`translateY(-1.5px)`, `scale(1.08)`). Aturan melarang pengangkatan hover dan mewajibkan efek ripple sentuh native.

---

## 5. RENCANA MIGRASI BERURUTAN (MODUL PALING SERING KE JARANG)
Migrasi akan dilaksanakan secara modular dan berurutan setelah konfirmasi. Setiap modul akan diintegrasikan dengan primitives dari Fase 2 tanpa mengubah logika JavaScript atau struktur state:

1. **Fondasi & Primitives (Fase 1 & Fase 2):**
   - Pasang token CSS global (`:root` & `@media (prefers-color-scheme: dark)`), reset layout `100dvh`, font Roboto, utilitas komponen primitives (Button, Chip, Badge, Field, Card, Table, Modal/Dialog, Drawer, Popover/Menu, Toast/Snackbar, Skeleton).
   - Petakan token ke `tailwind.config`.

2. **Modul 1: Kasir / Layar POS Utama (`#tab-pos` & `#mobile-cart-bar`)** *(Prioritas Tertinggi)*
   - Desain ulang workstation dual-pane: grid kartu produk di kiri (`repeat(auto-fill, minmax(220px, 1fr))`), chip kategori sentence-case, search bar outlined.
   - Panel keranjang kanan 392px (`--c`, radius `--r-2xl`, outline `--bw`), list item keranjang dengan counter sentuh minimal 40px, tombol checkout pil utama (`--pri`).
   - Bottom sheet keranjang mobile di bawah 900px.

3. **Modul 2: Layar Pembayaran (`#tab-payment`)**
   - Desain ulang rincian tagihan pesanan, segmented button metode pembayaran (Tunai / QRIS), input nominal & keypad numpad dengan tombol bulat `--r-xl` ukuran `--touch-xl`, kotak ringkasan kembalian di atas `--c-high`, tombol konfirmasi pembayaran.

4. **Modul 3: Riwayat Transaksi & Struk (`#tab-history`, `#receipt-modal`, `#daily-sales-rekap-modal`)**
   - Tabel riwayat transaksi dengan kontainer outline `--bw`, header `--c-high`, badge status pembayaran (`--ok-c`, `--info-c`).
   - Popover kalender tanggal (`#calendar-popover-history`).
   - Modal struk fisik thermal 80mm & tombol tindakan.
   - Modal rekap penjualan harian (`#daily-sales-rekap-modal`): standardisasi 6 KPI cards, tabel sum produk, dan tombol ekspor.

5. **Modul 4: Inventaris Bahan Mentah (`#tab-stock`, `#stock-modal`, `#restock-modal`, `#copy-stock-modal`)**
   - Tabel inventaris bahan mentah, badge stok menipis (`--warn-c`), modal tambah/edit item, modal restock cepat, dan modal salin rekap stok.

6. **Modul 5: Katalog & Resep Menu (`#tab-products`, `#product-modal`, `#recipe-detail-modal`)**
   - Tabel katalog menu produk, HPP, margin laba, modal tambah/edit produk dengan upload gambar dan takaran bahan mentah, modal rincian formula resep (BOM).

7. **Modul 6: Biaya Operasional / Pengeluaran Toko (`#tab-expenses`, `#expense-modal`, `#calendar-popover-expenses`)**
   - Ringkasan total petty cash, tabel transaksi pengeluaran kasir, popover kalender, modal catat pengeluaran.

8. **Modul 7: Buku Kas & Finansial (`#tab-report`, `#calendar-popover-report`)**
   - Kartu KPI laporan finansial (omzet, beban, laba bersih), bilah progres Tunai vs QRIS, tabel ringkasan laba dan pergerakan kas.

9. **Modul 8: Titip Jual - Stok Titip Mitra (`#tab-consign-stock`, `#consign-modal`, `#consign-restock-modal`, `#copy-consign-stock-modal`)**
   - Tabel inventaris konsinyasi, badge mitra, modal tambah barang titip, modal restock titipan, modal salin rekap.

10. **Modul 9: Titip Jual - Rekap Penjualan Titip (`#tab-consign-report`, `#consign-partner-settings-modal`, `#edit-partner-split-modal`, `#calendar-popover-consign-report`)**
    - Kartu KPI konsinyasi, modal manajemen bagi hasil mitra, modal slider split komisi toko vs mitra.

11. **Modul 10: Titip Jual - Setoran Titip (`#tab-consign-expenses`, `#consign-expense-modal`, `#calendar-popover-consign-expenses`)**
    - Tabel transaksi setoran dana ke mitra titipan, modal catat setoran.

12. **Modul 11: Analisis Fluktuasi Omzet (`#tab-fluctuation`)**
    - Selector bulan/tahun, kartu analitik omzet, visualisasi grafik fluktuasi harian tanpa gradien dekoratif.

13. **Modul Shell & Dialog Global:**
    - Sidebar rail / drawer navigasi (`#app-sidebar` & `#sidebar-backdrop`) bergaya native tablet dengan active indicator pill `--sec-c`.
    - Dialog konfirmasi umum (`#confirm-modal`).
    - Toast / Snackbar global (`#toast-container`) di tengah bawah.
    - Template cetak struk kasir thermal `@media print`.

---

## 6. STATUS FASE 0 & KONFIRMASI
Dokumen inventaris ini telah memetakan 100% komponen antarmuka, rute, popup, pelanggaran, dan rencana migrasi proyek DoonaPOS secara teliti tanpa mengubah kode fungsional aplikasi. 

Sesuai aturan alur kerja di `designpromptrules.md`:  
> *"Tunggu konfirmasiku sebelum lanjut."*

Pekerjaan Fase 0 telah selesai. Menunggu konfirmasi dari pengguna untuk memulai **Fase 1 (Fondasi: Token Global, Reset, Roboto, & Ikon)**.
