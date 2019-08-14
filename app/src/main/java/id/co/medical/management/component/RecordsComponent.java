package id.co.medical.management.component;

public class RecordsComponent {

    //Variabel pada adater harus sesuai dengan JSON PHP
    String id, kalori, target_kalori, nama_makanan, jumlah_kalori, nomor, nomor_hp, nama, waktu, jenis_olahraga, durasi_olahraga, cek_gula, hasil_cek_gula;

    public void setId(String id) {
        this.id = id;
    }

    public void setKalori(String kalori) {
        this.kalori = kalori;
    }

    public void setTarget_kalori(String target_kalori) {
        this.target_kalori = target_kalori;
    }

    public void setNama_makanan(String nama_makanan) {
        this.nama_makanan = nama_makanan;
    }

    public void setJumlah_kalori(String jumlah_kalori) {
        this.jumlah_kalori = jumlah_kalori;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public void setNomor_hp(String nomor_hp) {
        this.nomor_hp = nomor_hp;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public void setJenis_olahraga(String jenis_olahraga) {
        this.jenis_olahraga = jenis_olahraga;
    }

    public void setDurasi_olahraga(String durasi_olahraga) {
        this.durasi_olahraga = durasi_olahraga;
    }

    public void setCek_gula(String cek_gula) {
        this.cek_gula = cek_gula;
    }

    public void setHasil_cek_gula(String hasil_cek_gula) {
        this.hasil_cek_gula = hasil_cek_gula;
    }

    public String getId() {

        return id;
    }

    public String getKalori() {

        return kalori;
    }

    public String getTargetKalori() {

        return target_kalori;
    }

    public String getNamaMakanan() {

        return nama_makanan;
    }

    public String getJmlKalori() {

        return jumlah_kalori;
    }

    public String getNomor() {

        return nomor;
    }

    public String getNomorHP() {

        return nomor_hp;
    }

    public String getName() {

        return nama;
    }

    public String getWaktu() {

        return waktu;
    }

    public String getJenisOlahraga() {

        return jenis_olahraga;
    }

    public String getDurasiOlahraga() {

        return durasi_olahraga;
    }

    public String getCekGula() {

        return cek_gula;
    }

    public String getHasilCekGula() {

        return hasil_cek_gula;
    }
}