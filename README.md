# Repository SIBI (Sistem Isyarat Bahasa Indonesia)

Repository ini berisikan dua jenis aplikasi sebagai bagian dari proyek SIBI, yaitu Frontend (berada di branch `master`) dan Feature Extraction (berada di branch `frontend`). Silakan untuk melakukan *clone* dan melakukan perpindahan antar branch untuk melihat kode untuk Frontend dan Feature Extraction.

### Frontend

Pada bagian Frontend, terdapat tampilan untuk aplikasi yang kedepannya akan dijadikan aplikasi untuk membantu orang-orang dengan disabilitas untuk mendeteksi gerakan tangan dan menghasilkan sebuah tulisan sebagai feedbacknya. Spesifikasi singkat:

- Bahasa pemrograman: Java
- Halaman-halaman: Halaman utama, menu SIBI, BISINDO (belum diimplementasikan), CameraView untuk mengambil input berupa video
- Fitur: Camera Stream output (melakukan stream terhadap kamera dan disimpan kedalam sebuah file yang akan dilakukan passing ke modul feature extraction)

### Feature Extraction

Pada bagian Feature Extraction, terdapat tampilan untuk mengambil File Video yang akan dilakukan *feature extraction*. Selanjutnya fitur-fitur yang sudah di-*extract* disimpan dalam sebuah file `.txt`. Spesifikasi singkat:

- Bahasa pemrograman: Kotlin
- External Library: OpenCV Library 3.40 Dev, JavaCV, ND4j, FFMPEG (untuk melakukan pemotongan video per frame)
- Fitur: Feature Extraction (dalam bentuk Kotlin, dikonversi dari kode Python), Elliptical Fourier Descriptor (dalam bentuk Kotlin, dikonversi dari kode Python).

### Kontak

Untuk pertanyaan lebih lanjut, bisa menghubungi:

**Izzan Fakhril Islam (089667515857)**

