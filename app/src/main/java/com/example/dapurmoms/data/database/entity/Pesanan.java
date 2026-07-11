package com.example.dapurmoms.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.room.TypeConverters;
import java.util.List;

/**
 * Entity untuk tabel pesanan.
 * Menyimpan data pesanan dari pelanggan termasuk nama menu,
 * jumlah, harga satuan, dan total pembayaran.
 */
@Entity(tableName = "pesanan", indices = {@Index("tanggal")})
public class Pesanan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Tanggal pesanan dalam milidetik sejak epoch */
    private long tanggal;

    /** Nama pemesan / pelanggan */
    @ColumnInfo(name = "nama_pemesan")
    private String namaPemesan;

    /** Daftar item menu yang dipesan */
    @ColumnInfo(name = "nama_menu")
    @TypeConverters(PesananItemConverter.class)
    private List<PesananItem> namaMenu;

    /** Jumlah porsi / unit yang dipesan */
    private int jumlah;

    /** Harga per satuan dalam Rupiah */
    @ColumnInfo(name = "harga_satuan")
    private long hargaSatuan;

    /** Total harga (jumlah × hargaSatuan) dalam Rupiah */
    private long total;

    /** Catatan tambahan untuk pesanan */
    private String catatan;

    /** Metode pembayaran: Cash, Transfer, atau Piutang */
    @ColumnInfo(name = "metode_pembayaran", defaultValue = "Cash")
    private String metodePembayaran = "Cash";

    // ========================
    // Constructor
    // ========================

    public Pesanan() {
    }

    // ========================
    // Getter & Setter
    // ========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTanggal() {
        return tanggal;
    }

    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    public String getNamaPemesan() {
        return namaPemesan;
    }

    public void setNamaPemesan(String namaPemesan) {
        this.namaPemesan = namaPemesan;
    }

    public List<PesananItem> getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(List<PesananItem> namaMenu) {
        this.namaMenu = namaMenu;
    }

    /**
     * Mengembalikan ringkasan nama menu yang telah diformat menjadi teks yang mudah dibaca.
     * Contoh: "Nasi Goreng, Mie Ayam" atau "Nasi Goreng + 2 menu lainnya"
     */
    public String getMenuSummary() {
        if (namaMenu == null || namaMenu.isEmpty()) return "-";
        if (namaMenu.size() == 1) {
            return namaMenu.get(0).getNamaMenu();
        }
        StringBuilder sb = new StringBuilder(namaMenu.get(0).getNamaMenu());
        if (namaMenu.size() <= 3) {
            for (int i = 1; i < namaMenu.size(); i++) {
                sb.append(", ").append(namaMenu.get(i).getNamaMenu());
            }
        } else {
            sb.append(" + ").append(namaMenu.size() - 1).append(" menu lainnya");
        }
        return sb.toString();
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public long getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(long hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    private boolean isDeleted = false;

    @ColumnInfo(name = "deleted_at", defaultValue = "0")
    private long deletedAt = 0;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
