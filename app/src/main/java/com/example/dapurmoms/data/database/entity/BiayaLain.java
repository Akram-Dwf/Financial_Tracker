package com.example.dapurmoms.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

/**
 * Entity untuk tabel biaya_lain.
 * Menyimpan data pengeluaran operasional di luar bahan baku
 * seperti listrik, transportasi, kemasan, dll.
 */
@Entity(tableName = "biaya_lain", indices = {@Index("tanggal")})
public class BiayaLain {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Tanggal biaya dalam milidetik sejak epoch */
    private long tanggal;

    /** Keterangan biaya */
    private String keterangan;

    /** Kategori biaya (contoh: Operasional, Transportasi, dll) */
    private String kategori;

    /** Jumlah biaya dalam Rupiah */
    private long jumlah;

    /** Catatan tambahan */
    private String catatan;

    /** Metode pembayaran: Cash, Transfer, atau Utang */
    @ColumnInfo(name = "metode_pembayaran", defaultValue = "Cash")
    private String metodePembayaran = "Cash";

    // ========================
    // Constructor
    // ========================

    public BiayaLain() {
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

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public long getJumlah() {
        return jumlah;
    }

    public void setJumlah(long jumlah) {
        this.jumlah = jumlah;
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
