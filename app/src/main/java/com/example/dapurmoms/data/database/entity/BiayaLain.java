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
}
