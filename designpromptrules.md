# ROLE
Kamu adalah front-end engineer senior. Tugasmu: MEREDESAIN SELURUH TAMPILAN webapp POS ini (semua menu, halaman, popup, dan overlay) agar memakai SATU design system bergaya aplikasi native tablet Android (Material 3). Webapp ini besar dan kompleks, jadi kerjakan secara sistematis, bukan menambal per halaman.

# ATURAN MUTLAK
1. HANYA ubah presentasi (CSS/SCSS/Tailwind/styled-components, class, markup struktural seperlunya, ikon). DILARANG mengubah logika bisnis, state, store, API, routing, nama fungsi/props/event, validasi, atau struktur data.
2. Nol hardcode: warna, radius, ketebalan border, bayangan, z-index, spacing, dan font-size HARUS memakai token di bagian "TOKENS". Tidak boleh ada hex/rgb/hsl, `px` radius, atau `z-index` angka di luar file token.
3. Satu jenis elemen = satu gaya. Jangan ada dua gaya tombol, dua gaya tabel, atau dua gaya modal untuk fungsi yang sama.
4. Bangun lapisan komponen bersama (primitives) lalu pakai ulang di semua menu. Kalau komponen belum ada, buat dulu di lapisan itu, jangan menulis style lokal.
5. Sesuaikan dengan stack proyek (React/Vue/Svelte/Blade/dll, Tailwind/CSS Modules/dll). Kalau Tailwind: petakan token ke `theme.extend`, larang nilai arbitrary `[...]` di luar token.
6. Untuk komponen yang TIDAK tercantum di katalog: turunkan dari komponen terdekat (lihat "ATURAN TURUNAN"), jangan membuat gaya baru.
7. Jangan menyentuh file yang bukan presentasi (backend, migrasi DB, service, util logika). Kalau perubahan markup menyentuh handler event, pertahankan handler apa adanya.

# ALUR KERJA (WAJIB BERURUTAN, LAPORKAN SETIAP FASE)
## Fase 0 — Inventaris (belum boleh mengubah kode)
Telusuri seluruh proyek lalu hasilkan dokumen `DESIGN-INVENTORY.md` berisi:
- Daftar SEMUA route/menu/halaman (dari router, sidebar, dan menu navigasi).
- Daftar SEMUA popup/overlay: modal, dialog konfirmasi, drawer, bottom sheet, dropdown, popover, tooltip, toast/snackbar/alert, datepicker, select/combobox, context menu, loading overlay, popup pembayaran, preview struk, lightbox, dan semua library popup pihak ketiga (SweetAlert, Bootstrap modal, MUI, Ant, Select2, Flatpickr, dll). Cari dengan grep: `modal`, `dialog`, `drawer`, `popover`, `dropdown`, `tooltip`, `toast`, `swal`, `alert(`, `confirm(`, `position: fixed`, `z-index`.
- Daftar semua library UI/CSS yang dipakai dan versinya.
- Daftar pelanggaran: warna hardcode, border/radius/shadow tidak konsisten, `!important`, inline style, `z-index` liar, ikon campur (emoji/FontAwesome/dll), font ganda. Sertakan jumlah dan lokasi file.
- Rencana migrasi: urutkan modul dari yang paling sering dipakai (kasir) ke yang jarang. Tunggu konfirmasiku sebelum lanjut.

## Fase 1 — Fondasi
Buat file token global tunggal + reset/base style + tema terang/gelap. Pasang font Roboto. Buat set ikon SVG tunggal. Belum menyentuh halaman.

## Fase 2 — Primitives
Buat komponen bersama sesuai katalog: Button, IconButton, Chip, Badge, Field/Input/Select/Textarea/Checkbox/Radio/Switch, Card, Table, Tabs, Nav, PageHeader, Modal/Dialog/Drawer/Sheet, Menu/Popover/Tooltip, Toast/Snackbar, EmptyState, Skeleton, Pagination. Semua state wajib ada (lihat "STATE").

## Fase 3 — Migrasi per modul
Migrasi satu modul/menu per iterasi memakai primitives. Setiap selesai satu modul: hapus style lama yang tidak terpakai, jalankan build/test yang ada, laporkan file yang berubah. JANGAN migrasi semuanya dalam satu commit.

## Fase 4 — Penyapuan & verifikasi
Jalankan pencarian otomatis (grep) untuk memastikan tidak tersisa: hex/rgb/hsl, `!important`, inline `style=`, `z-index` angka, `border-radius` angka, emoji, dan class warisan. Laporkan hasilnya dengan jumlah nol atau daftar sisa beserta alasannya. Selesaikan dengan "CHECKLIST VERIFIKASI".

# TOKENS
```css
:root{
  /* Permukaan: belakang -> depan */
  --surface:#d5ddd3;   /* latar halaman/app */
  --c-low:#ffffff;     /* kartu, tabel, search bar, chip, input di atas latar */
  --c:#fbfcfa;         /* panel besar, drawer, dialog, sidebar */
  --c-high:#e7ede5;    /* area tertanam di dalam panel: ringkasan, header tabel, stepper, tombol netral */
  /* Teks */
  --on:#191c1a; --on-v:#414942;
  /* Outline (charcoal) */
  --outline:#36454f; --out-v:#36454f;
  /* Aksen utama */
  --pri:#9e0202; --on-pri:#ffffff;
  --pri-c:#ffdad6; --on-pri-c:#410002;
  --sec-c:#f6d3d0; --on-sec-c:#3b0000;   /* tint aksen: item terpilih, badge info utama */
  /* Pendamping */
  --ter-c:#ffdea0; --on-ter-c:#261900;   /* ochre: label khusus (titip jual), highlight */
  /* Status semantik (jangan pakai warna aksen untuk status) */
  --ok:#1e6b3a;   --ok-c:#cfeedb;   --on-ok-c:#06210f;
  --warn:#8a5a00; --warn-c:#ffe6b0; --on-warn-c:#2a1b00;
  --info:#1f5a8a; --info-c:#d4e6f7; --on-info-c:#06223a;
  --err:#b3261e;  --err-c:#ffdad6;  --on-err-c:#410002;
  /* Snackbar / tooltip */
  --inv:#2e312f; --on-inv:#f0f1ec; --inv-pri:#ffb4ab;
  /* Overlay */
  --scrim:rgba(0,0,0,.45);
  /* Elevasi */
  --sh-1:0 1px 2px rgba(25,28,26,.14),0 3px 10px rgba(25,28,26,.10);          /* kartu, input, chip */
  --sh-2:0 2px 4px rgba(25,28,26,.12),0 8px 24px rgba(25,28,26,.14);          /* panel, drawer, dropdown */
  --sh-3:0 12px 40px rgba(0,0,0,.35);                                          /* dialog, modal */
  /* Garis */
  --bw:2px; --bw-focus:3px;
  /* Radius */
  --r-xs:6px; --r-sm:10px; --r-md:12px; --r-lg:14px; --r-xl:20px; --r-2xl:28px; --r-pill:999px;
  /* Spacing (kelipatan 4) */
  --s-1:4px; --s-2:8px; --s-3:12px; --s-4:16px; --s-5:20px; --s-6:24px; --s-8:32px;
  /* Sentuh */
  --touch:40px; --touch-lg:48px; --touch-xl:56px;
  /* Tipografi */
  --font:Roboto,system-ui,sans-serif;
  --t-xs:12px; --t-sm:13px; --t-md:14px; --t-base:15px; --t-lg:16px; --t-xl:20px; --t-2xl:24px; --t-3xl:30px;
  /* Z-index (satu-satunya tempat angka z-index boleh ada) */
  --z-sticky:10; --z-nav:20; --z-dropdown:40; --z-drawer:50; --z-scrim:60; --z-modal:70; --z-toast:80; --z-tooltip:90;
  /* Motion */
  --ease:cubic-bezier(.2,0,0,1); --d-fast:.15s; --d-base:.25s; --d-slow:.3s;
}
@media (prefers-color-scheme:dark){
  :root{
    --surface:#090c0a; --c-low:#1d2420; --c:#232b26; --c-high:#2d3631;
    --on:#e0e3de; --on-v:#c1c9c0;
    --outline:#7d8a93; --out-v:#56636b;
    --pri:#ff8a80; --on-pri:#4d0000; --pri-c:#7a0000; --on-pri-c:#ffdad6;
    --sec-c:#4a2a28; --on-sec-c:#ffdad6;
    --ter-c:#5c4300; --on-ter-c:#ffdea0;
    --ok:#7fd69a; --ok-c:#12381f; --on-ok-c:#cfeedb;
    --warn:#f0c15a; --warn-c:#4a3500; --on-warn-c:#ffe6b0;
    --info:#8dc4f0; --info-c:#12344f; --on-info-c:#d4e6f7;
    --err:#ffb4ab; --err-c:#93000a; --on-err-c:#ffdad6;
    --inv:#e0e3de; --on-inv:#2e312f; --inv-pri:#9e0202;
    --scrim:rgba(0,0,0,.6);
    --sh-1:0 1px 2px rgba(0,0,0,.6),0 4px 14px rgba(0,0,0,.45);
    --sh-2:0 2px 4px rgba(0,0,0,.6),0 8px 24px rgba(0,0,0,.5);
    --sh-3:0 12px 40px rgba(0,0,0,.7);
  }
}
```
Jika proyek sudah punya mode tema manual (toggle), arahkan token gelap ke selector yang sama (`[data-theme="dark"]`) tanpa mengubah mekanisme toggle-nya.

# LAYOUT GLOBAL
- Seluruh app: `height:100dvh`, latar `--surface`, padding `--s-3`, tanpa container berpusat/max-width (penuh layar seperti app native). Scroll terjadi di dalam panel konten, bukan di body, kecuali di bawah 900px.
- Pola halaman dasar (pilih sesuai isi halaman, jangan membuat pola baru):
  1. **Dua panel** (kasir): konten fleksibel di kiri + panel rincian 392px di kanan (`--c`, radius `--r-2xl`).
  2. **Navigasi + konten**: sidebar/rail kiri 88–280px (`--c`, outline, `--sh-2`) + area konten (`--surface`) berisi PageHeader lalu Card/Table.
  3. **Master–detail**: daftar di kiri (kartu/tabel), detail di kanan dalam panel atau drawer.
  4. **Halaman penuh** (laporan, pengaturan): PageHeader + grup Card yang tersusun vertikal/grid.
- Grid kartu: `repeat(auto-fill, minmax(220px,1fr))`, gap `--s-3`.
- Di bawah 900px: satu kolom, sidebar jadi navigasi bawah atau drawer.

# HIERARKI PERMUKAAN DAN OUTLINE (inti masalah "menyatu")
Setiap elemen harus terpisah dari induknya lewat tiga hal sekaligus: tone warna berbeda, outline charcoal, dan bayangan.
- Urutan lapisan: `--surface` < kartu/tabel/input (`--c-low`) < panel/sidebar/drawer/dialog (`--c`) < area tertanam di dalam panel (`--c-high`).
- WAJIB `border: var(--bw) solid var(--out-v)` pada: Card, panel, sidebar, Table (kontainer), search bar, Chip tidak terpilih, tombol netral/outlined, stepper, kotak ringkasan, dropdown/menu, popover, drawer, dialog. Input/Select/Textarea memakai `var(--outline)`.
- Hanya SATU ketebalan outline (`--bw`). Pengecualian tunggal: state fokus memakai `--bw-focus` warna `--pri`. Divider internal (garis pemisah baris tabel/list) memakai `1px solid color-mix(in srgb, var(--out-v) 35%, transparent)`.
- Bayangan: `--sh-1` untuk kartu/input/chip; `--sh-2` untuk panel/sidebar/drawer/dropdown; `--sh-3` untuk dialog/modal.
- Dilarang: elemen satu tone dengan induknya, elemen tanpa outline di atas `--surface`, atau elemen yang hanya dibedakan lewat bayangan.

# TIPOGRAFI
- Roboto 400/500/700. `font-variant-numeric: tabular-nums` global.
- Sentence case. DILARANG huruf kapital semua untuk label, header tabel, atau tombol.
- Skala: judul halaman `--t-2xl`/500; judul panel/section `--t-xl`/500; isi `--t-base`; tombol/chip/label `--t-md`/500; pendukung `--t-xs`–`--t-sm` (`--on-v`); angka penting (total, KPI) `--t-3xl`/700; harga `--t-lg`/700 warna `--pri`.
- Dilarang font monospace untuk angka/label.

# KATALOG KOMPONEN
## Aksi
- **Button filled (utama):** pil, tinggi `--touch-xl` untuk aksi utama halaman/panel, `--touch-lg` di dalam dialog/form. Latar `--pri`, teks `--on-pri`. Maksimal SATU tombol filled per area.
- **Button tonal:** latar `--sec-c`, teks `--on-sec-c`, aksi sekunder penting.
- **Button outlined/netral:** latar `--c-high`, teks `--on`, outline `--bw`. Dipakai untuk Batal, Reset, Filter.
- **Button text:** tanpa latar, teks `--pri`, untuk aksi di dialog/snackbar.
- **Button destructive:** latar `--err`, teks putih (terang) / `--on-err-c` (gelap). Hanya muncul di dalam dialog konfirmasi hapus/batal. Selalu sertakan ikon + teks, jangan hanya warna.
- **IconButton:** lingkaran `--touch`/`--touch-lg`, ikon 20–24px.
- Disabled: `opacity:.45`, tanpa bayangan, cursor default.
- **Loading:** spinner SVG kecil menggantikan ikon, tombol tetap lebar sama, `aria-busy`.

## Input & form
- Semua field: outlined, tinggi `--touch-xl` (input satu baris), radius `--r-md`, outline `--bw` `--outline`, ikon awal opsional 24px, label mengambang (naik ke garis atas saat fokus/berisi). Fokus: border `--bw-focus` `--pri`.
- Berlaku untuk: text, number, password, email, tel, search, textarea, select/dropdown native, combobox/autocomplete, date/time picker, file upload. Select kustom membuka Menu (lihat bawah), bukan dropdown bawaan browser/library.
- **Checkbox/Radio/Switch:** ukuran sentuh `--touch`, tercentang = `--pri`, outline `--bw` saat tidak tercentang.
- **Validasi:** error = outline `--err` + ikon + pesan di bawah field (`--t-xs`, warna `--err`). Jangan memakai warna sendirian untuk menandai error.
- Grup form: dibungkus Card, jarak antar field `--s-4`, tombol aksi di footer kanan.
- **Input angka/uang:** rata kanan, prefiks "Rp" sebagai teks di dalam field.
- **Numpad/keypad pembayaran:** tombol bulat/rounded `--r-xl` ukuran min `--touch-xl`, latar `--c-low`, outline, ripple.

## Chip, badge, status
- **Chip filter:** tinggi 36px, radius `--r-sm`, latar `--c-low`, outline `--bw`. Terpilih: latar `--pri`, teks `--on-pri`, centang di kiri.
- **Badge/status pil:** radius `--r-xs`, padding `2px 8px`, `--t-xs`/500. Pemetaan wajib: sukses/lunas/aktif = `--ok-c`; menunggu/stok menipis = `--warn-c`; info/proses = `--info-c`; gagal/batal/habis = `--err-c`; khusus/titip = `--ter-c`; netral = `--c-high` + outline. Selalu ada teks, bukan hanya titik warna.
- **Badge angka (counter):** bulat, `--pri`, `--on-pri`.

## Card & panel
- Card: `--c-low`, radius `--r-xl`, outline `--bw`, `--sh-1`, padding `--s-4`. Panel besar: `--c`, radius `--r-2xl`, `--sh-2`.
- KPI/statistik: Card dengan angka `--t-3xl`/700, label `--t-sm` `--on-v`, ikon tonal 40px (`--sec-c`). Jangan memakai gradien.
- Kartu produk/grid: lihat pola kasir (gambar/ikon tint radius `--r-lg`, nama, harga `--pri`, meta).
- Kartu bisa diklik: ripple, tanpa efek hover yang mengangkat.

## Tabel & daftar data
- Kontainer: `--c-low`, radius `--r-xl`, outline `--bw`, `--sh-1`, `overflow:hidden`, scroll horizontal di dalam kontainer.
- Header: latar `--c-high`, teks `--t-sm`/700 `--on-v`, sticky (`--z-sticky`), sentence case, garis bawah `--bw`.
- Baris: tinggi min `--touch-lg`, divider tipis, zebra DILARANG. Baris terpilih: latar `--sec-c`. Hover hanya di `@media (hover:hover)`: overlay 6%.
- Angka/harga rata kanan; kolom aksi memakai IconButton/menu "lainnya".
- Footer: Pagination (tombol bulat `--touch`, halaman aktif `--pri`) + info jumlah baris.
- Toolbar tabel (search, filter chip, tombol ekspor) berada di atas kontainer dalam satu baris, gaya sama seperti app bar kasir.
- Library tabel pihak ketiga (DataTables, AG Grid, MUI/Ant Table, dll): override lewat CSS/variabel tema memakai token, jangan memodifikasi file library.
- Daftar (list) non-tabel: item tinggi min `--touch-xl`, ikon/avatar tonal di kiri, teks utama + pendukung, aksi di kanan.

## Tab & navigasi
- **Tabs:** garis bawah `--bw` di bawah daftar tab; tab aktif teks `--pri` + indikator `--pri` setebal `--bw-focus`; tinggi `--touch-lg`. Untuk segmen 2–4 opsi pendek, gunakan **segmented button** (outline `--bw`, terpilih `--sec-c` + centang).
- **Sidebar/Rail:** `--c`, outline, `--sh-2`. Item tinggi `--touch-lg`, ikon 24px + label. Aktif: latar `--sec-c`, teks `--on-sec-c`, pil. Grup menu diberi judul kecil `--t-xs` `--on-v` (sentence case).
- **PageHeader:** judul `--t-2xl`/500, breadcrumb opsional `--t-sm`, aksi utama (tombol filled) di kanan.
- **Stepper/wizard:** nomor bulat 32px, langkah aktif `--pri`, selesai `--ok`, belum `--c-high` + outline.

# KATALOG POPUP / OVERLAY (SEMUA WAJIB MEMAKAI POLA INI)
Aturan umum overlay:
- Scrim `--scrim` (`--z-scrim`), klik scrim = tutup (kecuali dialog yang sedang menyimpan/bayar). Tombol Esc menutup. Fokus dikunci di dalam overlay, kembali ke pemicu saat ditutup, `role` dan `aria-modal` benar.
- Hanya satu modal aktif; kalau ada modal bertingkat, yang di atas memakai `--z-modal` + 1 lewat kelas tumpukan, bukan angka manual.
- Tutup overlay selalu bisa lewat tombol Batal/Tutup yang terlihat.

| Jenis | Bentuk |
|---|---|
| **Dialog konfirmasi / alert** | Lebar `min(380px,92vw)`, `--c`, radius `--r-2xl`, outline `--bw`, `--sh-3`, padding `--s-6`. Judul `--t-2xl`/400, isi `--t-md` `--on-v`, aksi rata kanan: tombol text "Batal" + tombol filled (atau destructive) |
| **Modal form** | Lebar sm 420px, md 560px, lg 760px, xl 980px (`min(...,94vw)`). Header (judul + IconButton tutup), isi scroll dengan field standar, footer sticky berisi aksi. Tinggi maks `90dvh` |
| **Modal layar penuh** | Untuk alur kompleks (pembayaran multi-langkah, edit pesanan): `inset:0`, radius 0 di tablet kecil, header dengan tombol kembali |
| **Popup pembayaran** | Modal md: total `--t-3xl`/700, segmented metode bayar, field nominal/numpad, ringkasan kembalian di kotak `--c-high`, aksi "Bayar" filled |
| **Preview struk/invoice** | Modal md/lg, area struk berlatar putih + outline + font tetap struk, tombol Cetak (filled) dan Tutup |
| **Drawer samping** | Kanan, lebar 420px, `--c`, outline sisi dalam, `--sh-2`, header + isi scroll + footer aksi, animasi geser `--d-slow` |
| **Bottom sheet** | Untuk layar sempit: radius atas `--r-2xl`, handle 32×4px di tengah atas |
| **Menu / dropdown / select kustom / context menu** | `--c`, radius `--r-lg`, outline `--bw`, `--sh-2`, padding `--s-2`, item tinggi `--touch`, item aktif `--sec-c`, ikon opsional, divider tipis, `--z-dropdown`, lebar min = lebar pemicu |
| **Popover / datepicker** | Sama seperti Menu, padding `--s-4`. Datepicker: hari terpilih lingkaran `--pri`, hari ini outline `--bw`, navigasi bulan memakai IconButton |
| **Autocomplete** | Daftar hasil memakai gaya Menu, teks yang cocok dicetak tebal |
| **Tooltip** | `--inv`/`--on-inv`, radius `--r-xs`, `--t-xs`, `--z-tooltip`, tanpa outline, hanya untuk hint singkat (bukan info penting) |
| **Toast / snackbar** | Tengah bawah, `--inv`, radius `--r-xs`, min-tinggi 48px, `--z-toast`, aksi teks `--inv-pri` (mis. "Urungkan"), otomatis hilang ±4 dtk. Semua `alert()`/`confirm()`/SweetAlert dan notifikasi bawaan library diganti ke pola ini atau Dialog konfirmasi |
| **Banner/alert inline** | Di dalam halaman, latar `--*-c` sesuai status, outline `--bw` warna `--*`, ikon + teks |
| **Loading overlay** | Scrim + spinner di Card kecil; untuk muat data halaman gunakan Skeleton (blok `--c-high` berdenyut) |
| **Lightbox gambar** | Scrim lebih gelap, gambar `max 90vw/90dvh`, radius `--r-lg`, tombol tutup di pojok |

# STATE (WAJIB UNTUK SEMUA KOMPONEN INTERAKTIF)
default, hover (`@media (hover:hover)` saja), pressed (ripple), focus-visible (`outline:2px solid var(--pri); outline-offset:2px`), disabled, loading, selected, error, readonly. Tiga state halaman data: **loading** (Skeleton), **empty** (lingkaran ikon 88px `--sec-c` + judul `--t-xl` + teks bantuan + tombol aksi bila relevan), **error** (banner dengan ikon, penjelasan masalah, tombol "Coba lagi").

# IKON
- Satu set SVG inline gaya Lucide: `stroke:currentColor; stroke-width:1.8; fill:none; stroke-linecap/linejoin:round`, ukuran `1em`.
- DILARANG emoji sebagai ikon. Ganti FontAwesome/Material Icons/Bootstrap Icons ke set tunggal ini bila memungkinkan; jika terlalu banyak, buat komponen `<Icon name>` dan migrasikan bertahap.

# NUANSA NATIVE
- `user-select:none` (kecuali input/textarea dan area teks yang memang perlu disalin), `touch-action:manipulation`, `overscroll-behavior:none`, `-webkit-tap-highlight-color:transparent`.
- Target sentuh minimal `--touch` (utama `--touch-lg`).
- Ripple sebagai feedback sentuh (±.55s, lingkaran memudar dari titik sentuh) pada tombol, chip, kartu klik, item menu/list/tab.
- Tanpa underline link, tanpa cursor pointer, tanpa kesan situs web (header/footer web, container berpusat).
- Animasi: dialog scale-in `--d-slow`, snackbar slide-up `--d-base`, drawer geser `--d-slow`, badge pop. Hormati `prefers-reduced-motion`.

# LIBRARY PIHAK KETIGA
Untuk setiap library UI di inventaris: (1) coba konfigurasi tema resminya dengan token; (2) bila tidak cukup, buat satu file override terisolasi (`vendor-overrides.css`) berisi selector library yang diarahkan ke token. Dilarang `!important` kecuali di file override itu dan harus diberi komentar alasannya.

# CETAK & STRUK
Gaya cetak (struk/laporan) dipisah di `@media print`: latar putih, teks hitam, tanpa bayangan/outline dekoratif, font tetap, tanpa tombol/navigasi. Jangan mengubah template struk selain menyesuaikan font dan jarak bila perlu, dan konfirmasi dulu padaku bila ukuran kertas termal berubah.

# ATURAN TURUNAN (komponen yang tidak ada di katalog)
Tentukan induk terdekat lalu warisi: bentuk daftar → List/Table; kontainer informasi → Card; aksi → Button; pilihan → Chip/Segmented/Menu; melayang di atas konten → Menu/Popover; menutup layar → Dialog/Drawer; pesan sementara → Snackbar. Aturan outline, radius, bayangan, dan state induk berlaku penuh. Catat komponen turunan baru di `DESIGN-INVENTORY.md`.

# LARANGAN
- Gradien dekoratif, glow, glassmorphism, bayangan berwarna.
- Efek hover yang mengangkat/membesar elemen.
- Radius, outline, atau warna di luar token. Warna abu-abu ad-hoc.
- Huruf kapital semua, tanda titik tengah sebagai pemisah teks, panah "→" tempelan di akhir label tombol.
- Aksen `--pri` dipakai untuk status error/sukses (pakai token status).
- Mengubah teks/label fungsional tanpa persetujuan (kecuali kapitalisasi ke sentence case).

# CHECKLIST VERIFIKASI (laporkan satu per satu)
- [ ] `DESIGN-INVENTORY.md` mencakup semua route dan semua popup, tidak ada yang terlewat
- [ ] Nol hex/rgb/hsl, nol `z-index` angka, nol `border-radius` angka, nol inline style di luar file token (atau daftar sisa + alasan)
- [ ] Semua Card/panel/tabel/input/dropdown/dialog punya outline `--bw` charcoal dan bayangan sesuai levelnya
- [ ] Setiap halaman: lapisan (latar < kartu < isi tertanam) terbedakan jelas di mode terang dan gelap
- [ ] Semua popup (modal, dialog, drawer, dropdown, datepicker, toast, tooltip, popup bayar, preview struk) memakai pola katalog; `alert/confirm/swal` bawaan sudah diganti
- [ ] Tidak ada dua gaya berbeda untuk elemen yang sama (tombol, tabel, modal, badge)
- [ ] Semua state ada: hover, focus, disabled, loading, error, empty
- [ ] Logika, state, API, routing tidak berubah (diff hanya presentasi); build dan test yang ada lulus
- [ ] Tidak ada emoji/ikon campur; ikon satu set
- [ ] Kontras teks WCAG AA; target sentuh ≥ 40px; fokus keyboard terlihat
- [ ] Layout benar di tablet landscape, portrait, dan di bawah 900px
- [ ] Gaya cetak struk/laporan tidak rusak
