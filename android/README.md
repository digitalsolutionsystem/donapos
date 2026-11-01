# DoonaPOS Native Android (Kotlin & Jetpack Compose)

Aplikasi Kasir & Inventaris Modern (Point of Sale) Native Android dibangun dengan **Kotlin**, **Jetpack Compose (Material 3)**, **Room Database**, dan terintegrasi langsung dengan backend **Google Sheets (Google Apps Script)**.

---

## 🌟 Fitur Utama Aplikasi

1. **Layar Kasir (POS) Responsif**:
   - Tampilan adaptif untuk Smartphone & Tablet POS.
   - Filter kategori cepat (Semua, Kopi, Non-Kopi, Makanan, Titip Jual).
   - Pencarian produk instan.
   - Keranjang belanja interaktif dengan kalkulasi otomatis.

2. **Sistem Pembayaran & Struk Thermal**:
   - Metode pembayaran: Tunai, QRIS, Kartu Debit, Transfer Bank.
   - Tombol nominal cepat (*Uang Pas*, *10.000*, *20.000*, *50.000*, *100.000*).
   - Penghitungan kembalian otomatis.
   - Format struk belanja standar printer thermal (58mm / 80mm ESC/POS) yang dapat langsung dicetak atau dibagikan via Bluetooth/Share dialog.

3. **Gudang & Inventaris Bahan Baku (BOM Recipes)**:
   - Manajemen bahan mentah (kopi, susu, sirup, cup, dll.).
   - Pengurangan stok otomatis saat produk terjual sesuai resep racikan.
   - Indikator peringatan stok kritis (*Low stock alerts*).
   - Penambahan stok cepat (*Quick add* +10, +50, +100, +500).

4. **Titip Jual (Konsinyasi)**:
   - Pengelolaan produk titipan mitra/vendor.
   - Skema bagi hasil otomatis (komisi toko vs hak vendor).
   - Pelacakan stok sisa, jumlah terjual, dan total hutang ke mitra.

5. **Laporan Keuangan & Penjualan**:
   - Ringkasan total omset, pengeluaran toko, dan estimasi laba bersih.
   - Peringkat 5 produk terlaris (*Best Sellers*).

6. **Beban & Pengeluaran Operasional**:
   - Pencatatan biaya harian (Bahan Baku, Listrik/Air, Gaji, Operasional, dll.).

7. **Sinkronisasi Cloud Google Sheets**:
   - Terhubung dengan Web App Google Apps Script (`Code.gs`).
   - Bekerja secara **100% Offline-First** dengan database lokal Room SQLite.
   - Tombol sinkronisasi dua arah untuk cadangan data ke Google Spreadsheet.

8. **Keamanan Berbasis PIN**:
   - Peran Kasir: PIN default `1234`
   - Peran Pemilik: PIN default `8888`
   - Kemudahan ganti PIN lewat menu Pengaturan.

---

## 🚀 Cara Membuka & Membuat APK

### Cara 1: Menggunakan Android Studio (Disarankan)

1. Buka aplikasi **Android Studio**.
2. Pilih menu **File > Open...**.
3. Navigasikan ke folder:
   ```
   /home/hp/donapos/android
   ```
4. Klik **OK** dan tunggu proses Gradle Sync selesai.
5. Untuk membuat file APK:
   - Klik menu **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   - Setelah selesai, klik notifikasi **locate** untuk mengambil file `app-debug.apk`.
   - File APK akan berada di folder:
     ```
     android/app/build/outputs/apk/debug/app-debug.apk
     ```
6. Untuk langsung menjalankan ke Handphone / Tablet / Emulator:
   - Hubungkan HP Android dengan kabel USB (aktifkan USB Debugging).
   - Tekan tombol **Run (▶️)** di toolbar Android Studio.

---

### Cara 2: Build APK via Terminal (CLI)

Jalankan perintah berikut di direktori `android/`:

```bash
cd /home/hp/donapos/android
./gradlew assembleDebug
```

File APK siap dipasang pada perangkat Android Anda di:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📂 Struktur Proyek

```
android/
├── app/
│   ├── build.gradle.kts           # Konfigurasi dependensi Android & Compose
│   └── src/main/
│       ├── AndroidManifest.xml    # Manifest izin internet, bluetooth thermal
│       └── java/com/doonapos/app/
│           ├── MainActivity.kt    # Entry point & Navigasi Drawer
│           ├── DoonaApplication.kt
│           ├── data/
│           │   ├── local/         # Room Database, DAOs, Entities, DataStore
│           │   ├── model/         # Domain models (CartItem, Ingredient, Session)
│           │   ├── remote/        # Retrofit API service & Google Apps Script sync
│           │   └── repository/    # PosRepository (Logika bisnis POS & transaksi)
│           ├── ui/
│           │   ├── MainViewModel.kt
│           │   ├── theme/         # Color palette Pine Green, Typography, Theme
│           │   ├── components/    # TopBar, CartView, ProductCard, Dialogs
│           │   └── screens/       # PosScreen, StockScreen, ProductScreen, dll.
│           └── util/              # ThermalPrinterHelper, Formatters Rupiah
└── gradle/
    └── libs.versions.toml         # Version catalog Gradle modern
```
