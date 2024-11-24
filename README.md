# AplikasiCekCuacaSederhana
 Tugas6 - Muhammad Ihsan - 2210010286


# Pembuat
Nama     : Muhammad Ihsan    
NPM      : 2210010286

# 1. Deskripsi Program
Aplikasi ini adalah aplikasi sederhana untuk mengecek dan menyimpan data cuaca berdasarkan kota yang dimasukkan. Fitur utama aplikasi meliputi:
- **Integrasi API Cuaca**: Mengambil data cuaca secara real-time dari API OpenWeatherMap berdasarkan nama kota.
- **Menampilkan Data Cuaca**: Menampilkan deskripsi cuaca dan suhu dalam tampilan yang user-friendly.
- **Menampilkan Ikon Cuaca**: Menampilkan ikon cuaca berdasarkan kondisi cuaca.
- **Simpan Data Cuaca**: Menyimpan data cuaca ke dalam file CSV untuk penggunaan di masa depan.
- **Manajemen Kota Favorit**: Menyimpan daftar kota yang sering digunakan dan memuatnya kembali dari file.

# 2. Komponen GUI: JFrame, JPanel, JLabel, JTextField, JButton, JComboBox
- **JFrame**: Window utama aplikasi.
- **JPanel**: Panel untuk menampung komponen UI.
- **JTextField**: Untuk memasukkan nama kota yang ingin dicek cuacanya.
- **JComboBox**: Menampilkan daftar kota favorit yang bisa dipilih.
- **JButton**: Tombol untuk berbagai aksi seperti cek cuaca, simpan kota, hapus kota, simpan data cuaca, dan keluar dari aplikasi.
- **JLabel**: Untuk menampilkan deskripsi cuaca, suhu, dan ikon cuaca.
- **JTable**: Untuk menampilkan data cuaca yang tersimpan di file CSV.

# 3. Logika Program: API Eksternal dan Penyimpanan Data
- **Koneksi API**: Aplikasi terhubung ke API OpenWeatherMap untuk mengambil data cuaca berdasarkan kota yang dimasukkan.
- **Parsing JSON**: Data cuaca diparsing dari format JSON untuk menampilkan deskripsi cuaca, suhu, dan ikon cuaca.
- **Simpan Data Cuaca**: Data cuaca yang ditampilkan dapat disimpan ke dalam file CSV untuk digunakan di masa mendatang.
- **Pengelolaan Kota Favorit**: Aplikasi mendukung penyimpanan kota favorit yang sering digunakan, yang bisa dipilih melalui JComboBox.

# 4. Events:
Menggunakan **ActionListener** dan **ItemListener** untuk menangani interaksi pengguna dengan GUI.

## a. **ActionListener untuk Tombol Cek Cuaca**
Tombol ini akan mengambil data cuaca berdasarkan nama kota yang dimasukkan dan menampilkan informasi cuaca pada aplikasi.

```java
private void TombolCekCuacaActionPerformed(java.awt.event.ActionEvent evt) {                                               
    String namakota = FieldMasukkanNamaKota.getText();
    if (!namakota.isEmpty()) {
        try {
            MenampilkanCuaca(namakota);
            
            boolean KotaDitemukan = false;
            for (int i = 0; i < ComboBoxKota.getItemCount(); i++) {
                if (ComboBoxKota.getItemAt(i).equalsIgnoreCase(namakota)) {
                    KotaDitemukan = true;
                    break;
                }
            }

            if (!KotaDitemukan) {
                ComboBoxKota.addItem(namakota);
                SimpanKotaKeDataFile(); // Simpan kota ke file setelah menambahkannya
            }
        } catch (JSONException ex) {
            Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(Level.SEVERE, null, ex);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Silakan masukkan nama kota!");
    }
}
```

## b. **ItemListener pada JComboBox untuk Memilih Kota**
Menampilkan data cuaca untuk kota yang dipilih dari **JComboBox** dan mengisinya pada **JTextField**.

```java
private void ComboBoxKotaItemStateChanged(java.awt.event.ItemEvent evt) {                                              
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        String selectedCity = (String) ComboBoxKota.getSelectedItem();
        FieldMasukkanNamaKota.setText(selectedCity);
        try {
            MenampilkanCuaca(selectedCity);
        } catch (JSONException ex) {
            Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
```

# 5. Variasi:
Aplikasi ini memiliki beberapa fitur tambahan, antara lain:

## **Daftar Kota Favorit**
Pengguna dapat menambahkan kota ke dalam daftar kota favorit yang akan disimpan di file. Daftar ini akan ditampilkan dalam **JComboBox**.

```java
private void TombolCekCuacaActionPerformed(java.awt.event.ActionEvent evt) {
    String namakota = FieldMasukkanNamaKota.getText();
    if (!namakota.isEmpty()) {
        try {
            MenampilkanCuaca(namakota);
            
            boolean KotaDitemukan = false;
            for (int i = 0; i < ComboBoxKota.getItemCount(); i++) {
                if (ComboBoxKota.getItemAt(i).equalsIgnoreCase(namakota)) {
                    KotaDitemukan = true;
                    break;
                }
            }

            if (!KotaDitemukan) {
                ComboBoxKota.addItem(namakota);
                SimpanKotaKeDataFile(); // Simpan kota ke file setelah menambahkannya
            }
        } catch (JSONException ex) {
            Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(Level.SEVERE, null, ex);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Silakan masukkan nama kota!");
    }
}
```

## **Menyimpan Data Cuaca ke CSV**
Data cuaca yang ditampilkan dapat disimpan ke dalam file CSV untuk penggunaan di masa mendatang.

```java
private void TombolSimpanCuacaActionPerformed(java.awt.event.ActionEvent evt) {
    String namakota = FieldMasukkanNamaKota.getText();
    String gambaranCuaca = LabelDeskripsi.getText();
    String suhuStr = LabelHasilSuhu.getText().replace("°C", "");

    if (!namakota.isEmpty() && !gambaranCuaca.isEmpty() && !suhuStr.isEmpty()) {
        double suhu = Double.parseDouble(suhuStr);
        SimpanDataCuacaKeDataFile(namakota, gambaranCuaca, suhu);
    } else {
        JOptionPane.showMessageDialog(this, "Data cuaca tidak lengkap. Pastikan semua data sudah terisi.");
    }
}
```

## **Memuat Data Cuaca dari CSV**
Data cuaca yang disimpan di file CSV dapat dimuat dan ditampilkan di **JTable**.

```java
private void TombolMemuatDataActionPerformed(java.awt.event.ActionEvent evt) {
    try (BufferedReader reader = new BufferedReader(new FileReader("data/cuaca_data.csv"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 3) {
                // Menambahkan data ke dalam tabel
                modelTabelCuaca.addRow(new Object[]{data[0], data[1], data[2]});
            }
        }
        JOptionPane.showMessageDialog(this, "Data cuaca berhasil dimuat ke tabel.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat data cuaca.");
        e.printStackTrace();
    }
}
```

# 6. Tampilan Pada Saat Aplikasi Di Jalankan

![Tugas6](https://github.com/user-attachments/assets/a9ba17b0-648c-44a3-9861-725fdff4b8de)


## Indikator Penilaian:

| No  | Komponen         |  Persentase  |
| :-: | --------------   |   :-----:    |
|  1  | Komponen GUI     |    10    |
|  2  | Logika Program   |    20    |
|  3  |  Events          |    10    |
|  4  | Kesesuaian UI    |    20    |
|  5  | Memenuhi Variasi |    40    |
|     | **TOTAL**        | **100**     |

## Pembuat
Nama  : Muhammad Ihsan   
NPM   : 2210010286
Kelas : 5A Ti Reg Pagi BJM
