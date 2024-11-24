
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class AplikasiCekCuacaSederhana extends javax.swing.JFrame {

    // Konstanta API dan path file
    private static final String API_KEY = "574eec43b18e178f783ee454e9ccdd27"; // API key untuk OpenWeatherMap
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather"; // URL API cuaca
    private static final String CITY_LIST_FILE = System.getProperty("user.dir") + "/data/kota_pilihan.txt"; // Path untuk daftar kota
    private static final String CUACA_DATA_FILE = System.getProperty("user.dir") + "/data/cuaca_data.csv"; // Path untuk data cuaca
    private DefaultTableModel modelTabelCuaca; // Model tabel untuk menampilkan data cuaca
    
    public AplikasiCekCuacaSederhana() {
        initComponents();
        CekDanBuatFolderData(); // Cek atau buat folder 'data'
        MemuatKotaDariDataFile(); // Muat data kota yang tersimpan dari file
        modelTabelCuaca = new DefaultTableModel(new String[]{"Kota", "Cuaca", "Suhu"}, 0);
        TabelCuaca.setModel(modelTabelCuaca); // Pasang model tabel pada jTable1
    }
    
    // Ambil data cuaca dari API berdasarkan kota
    private String MengambilDataCuaca(String namakota) {
        String result = "";
        try {
            // Buat URL lengkap dengan parameter API dan kota
            String urlString = API_URL + "?q=" + namakota + ",id&appid=" + API_KEY + "&units=metric&lang=id";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Periksa respon dari server
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                result = sb.toString();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal Mendapatkan Data Cuaca. Kode Respons: " + responseCode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Koneksi: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    private void hapusKota() {
    String selectedCity = (String) ComboBoxKota.getSelectedItem();
    if (selectedCity != null) {
        ComboBoxKota.removeItem(selectedCity);
        SimpanKotaKeDataFile(); // Simpan perubahan setelah kota dihapus
        JOptionPane.showMessageDialog(this, "Kota " + selectedCity + " berhasil dihapus dari daftar favorit.");
    } else {
        JOptionPane.showMessageDialog(this, "Tidak ada kota yang dipilih untuk dihapus.");
    }
}
    
    // Tampilkan data cuaca di GUI berdasarkan kota
    private void MenampilkanCuaca(String namakota) throws JSONException {
        String jsonResponse = MengambilDataCuaca(namakota);
        JSONObject json = new JSONObject(jsonResponse);

        // Ambil deskripsi cuaca dan suhu dari JSON
        String DeskripsiCuacanya = json.getJSONArray("weather").getJSONObject(0).getString("description");
        String BentukIcon = json.getJSONArray("weather").getJSONObject(0).getString("icon");
        double Temperatur = json.getJSONObject("main").getDouble("temp");

        LabelDeskripsi.setText(DeskripsiCuacanya);
        LabelHasilSuhu.setText(Temperatur + "°C");

        // Ambil URL ikon cuaca dan tampilkan
        String iconUrl = "http://openweathermap.org/img/wn/" + BentukIcon + "@2x.png";
        try {
            ImageIcon weatherIcon = new ImageIcon(new URL(iconUrl));
            LabelIcon.setIcon(weatherIcon);
        } catch (Exception e) {
            e.printStackTrace();
            LabelIcon.setText("Gagal memuat ikon");
        }
    }
    

    
    // Metode untuk mengecek dan membuat folder 'data' jika belum ada
    private void    CekDanBuatFolderData() {
        File folder = new File(System.getProperty("user.dir") + "/data");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
    
    
    // Simpan kota yang ada di combobox ke dalam file
    private void SimpanKotaKeDataFile() {
        // Membuat folder 'data' jika belum ada
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir(); // Membuat folder
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CITY_LIST_FILE))) {
            for (int i = 0; i < ComboBoxKota.getItemCount(); i++) {
                writer.write(ComboBoxKota.getItemAt(i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Simpan data cuaca ke dalam file CSV
    private void SimpanDataCuacaKeDataFile(String Namakota, String gambaranCuaca, double suhu) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUACA_DATA_FILE, true))) {
            writer.write(Namakota + "," + gambaranCuaca + "," + suhu + "°C");
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Data Cuaca Berhasil Disimpan!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Saat Menyimpan Data.");
            e.printStackTrace();
        }
    }
    
    
     // Muat kota  dari file
    private void MemuatKotaDariDataFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CITY_LIST_FILE))) {
            String city;
            while ((city = reader.readLine()) != null) {
                ComboBoxKota.addItem(city);
            }
        } catch (IOException e) {
            System.out.println("Tidak ada kota yang tersimpan.");
        }
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        LabelMasukkanNamaKota = new javax.swing.JLabel();
        FieldMasukkanNamaKota = new javax.swing.JTextField();
        TombolCekCuaca = new javax.swing.JButton();
        LabelKeadaanCuaca = new javax.swing.JLabel();
        DeskripsiCuaca = new javax.swing.JLabel();
        LabelSuhu = new javax.swing.JLabel();
        LabelPilihKota = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelCuaca = new javax.swing.JTable();
        TombolSimpanCuaca = new javax.swing.JButton();
        TombolMemuatData = new javax.swing.JButton();
        ComboBoxKota = new javax.swing.JComboBox<>();
        LabelIcon = new javax.swing.JLabel();
        LabelDeskripsi = new javax.swing.JLabel();
        LabelHasilSuhu = new javax.swing.JLabel();
        TombolHapus = new javax.swing.JButton();
        TombolKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Cek Cuaca");

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Aplikasi Cek Cuaca");

        LabelMasukkanNamaKota.setText("Masukkan Nama Kota : ");

        FieldMasukkanNamaKota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FieldMasukkanNamaKotaMouseClicked(evt);
            }
        });

        TombolCekCuaca.setText("Cek Cuaca");
        TombolCekCuaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TombolCekCuacaActionPerformed(evt);
            }
        });

        LabelKeadaanCuaca.setText("Keadaan Cuaca :");

        DeskripsiCuaca.setText("Deskripsi Cuaca : ");

        LabelSuhu.setText("Suhu Saat Ini : ");

        LabelPilihKota.setText("Pilih Kota : ");

        TabelCuaca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Kota", "Keadaan Cuaca", "Suhu"
            }
        ));
        jScrollPane1.setViewportView(TabelCuaca);

        TombolSimpanCuaca.setText("Simpan Data Cuaca");
        TombolSimpanCuaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TombolSimpanCuacaActionPerformed(evt);
            }
        });

        TombolMemuatData.setText("Memuat Data Cuaca");
        TombolMemuatData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TombolMemuatDataActionPerformed(evt);
            }
        });

        ComboBoxKota.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBoxKotaItemStateChanged(evt);
            }
        });

        TombolHapus.setText("Hapus");
        TombolHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TombolHapusActionPerformed(evt);
            }
        });

        TombolKeluar.setText("Keluar");
        TombolKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TombolKeluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(TombolSimpanCuaca))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(253, 253, 253)
                        .addComponent(TombolCekCuaca)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(LabelPilihKota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBoxKota, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LabelKeadaanCuaca)
                                    .addComponent(DeskripsiCuaca))
                                .addGap(46, 75, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LabelDeskripsi)
                                    .addComponent(LabelIcon)
                                    .addComponent(LabelHasilSuhu)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(TombolMemuatData)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LabelSuhu)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(LabelMasukkanNamaKota)
                                .addGap(70, 70, 70)
                                .addComponent(FieldMasukkanNamaKota, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(TombolHapus)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(TombolKeluar)
                        .addGap(38, 38, 38))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelMasukkanNamaKota)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FieldMasukkanNamaKota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(TombolHapus)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TombolCekCuaca)
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelKeadaanCuaca)
                    .addComponent(LabelIcon))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DeskripsiCuaca, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelDeskripsi))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelSuhu)
                    .addComponent(LabelHasilSuhu))
                .addGap(23, 23, 23)
                .addComponent(TombolSimpanCuaca)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LabelPilihKota)
                            .addComponent(ComboBoxKota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44)
                        .addComponent(TombolMemuatData))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(TombolKeluar)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TombolCekCuacaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TombolCekCuacaActionPerformed
        String namakota = FieldMasukkanNamaKota.getText();
        if (!namakota.isEmpty()) {
            try {
                MenampilkanCuaca(namakota);
            } catch (JSONException ex) {
                Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(Level.SEVERE, null, ex);
            }

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
        } else {
            JOptionPane.showMessageDialog(this, "Silakan masukkan nama kota!");
        }
           
    }//GEN-LAST:event_TombolCekCuacaActionPerformed

    private void TombolSimpanCuacaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TombolSimpanCuacaActionPerformed
        String namakota = FieldMasukkanNamaKota.getText();
        String gambaranCuaca = LabelDeskripsi.getText();
        String suhuStr = LabelHasilSuhu.getText().replace("°C", "");

        if (!namakota.isEmpty() && !gambaranCuaca.isEmpty() && !suhuStr.isEmpty()) {
            double suhu = Double.parseDouble(suhuStr);
            SimpanDataCuacaKeDataFile(namakota, gambaranCuaca, suhu);
        } else {
            JOptionPane.showMessageDialog(this, "Data cuaca tidak lengkap. Pastikan semua data sudah terisi.");
        }              
    }//GEN-LAST:event_TombolSimpanCuacaActionPerformed

    private void TombolMemuatDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TombolMemuatDataActionPerformed
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
    }//GEN-LAST:event_TombolMemuatDataActionPerformed

    private void ComboBoxKotaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBoxKotaItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String selectedCity = (String) ComboBoxKota.getSelectedItem();
            FieldMasukkanNamaKota.setText(selectedCity);
            try {
                MenampilkanCuaca(selectedCity);
            } catch (JSONException ex) {
                Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ComboBoxKotaItemStateChanged

    private void FieldMasukkanNamaKotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FieldMasukkanNamaKotaMouseClicked
        // TODO add your handling code here:
        // Cek apakah `txtKota` mengandung data yang valid sebelum dikosongkan
        String namakota = FieldMasukkanNamaKota.getText();
        if (!namakota.isEmpty()) {
            boolean KotaDitemukan = false;

            // Cek apakah kota sudah ada di `cmbKota`
            for (int i = 0; i < ComboBoxKota.getItemCount(); i++) {
                if (ComboBoxKota.getItemAt(i).equalsIgnoreCase(namakota)) {
                    KotaDitemukan = true;
                    break;
                }
            }

            // Tambahkan kota ke `cmbKota` jika belum ada, kemudian simpan
            if (!KotaDitemukan) {
                ComboBoxKota.addItem(namakota);
                SimpanKotaKeDataFile(); // Panggil metode simpan setelah menambahkan kota baru
            }
        }

        // Kosongkan teks pada `txtKota`
        FieldMasukkanNamaKota.setText("");
    }//GEN-LAST:event_FieldMasukkanNamaKotaMouseClicked

    private void TombolHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TombolHapusActionPerformed
        // TODO add your handling code here:
        
        TombolHapus.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            hapusKota();
        }
    });
    }//GEN-LAST:event_TombolHapusActionPerformed

    private void TombolKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TombolKeluarActionPerformed
        // TODO add your handling code here:
         System.exit(0);
    }//GEN-LAST:event_TombolKeluarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AplikasiCekCuacaSederhana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBoxKota;
    private javax.swing.JLabel DeskripsiCuaca;
    private javax.swing.JTextField FieldMasukkanNamaKota;
    private javax.swing.JLabel LabelDeskripsi;
    private javax.swing.JLabel LabelHasilSuhu;
    private javax.swing.JLabel LabelIcon;
    private javax.swing.JLabel LabelKeadaanCuaca;
    private javax.swing.JLabel LabelMasukkanNamaKota;
    private javax.swing.JLabel LabelPilihKota;
    private javax.swing.JLabel LabelSuhu;
    private javax.swing.JTable TabelCuaca;
    private javax.swing.JButton TombolCekCuaca;
    private javax.swing.JButton TombolHapus;
    private javax.swing.JButton TombolKeluar;
    private javax.swing.JButton TombolMemuatData;
    private javax.swing.JButton TombolSimpanCuaca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
