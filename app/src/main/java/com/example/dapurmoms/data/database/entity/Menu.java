package com.example.dapurmoms.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity untuk tabel menu.
 * Menyimpan daftar menu yang dijual oleh DapurMoms
 * termasuk harga jual, kategori, dan ketersediaan.
 */
@Entity(tableName = "menu")
public class Menu {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Nama menu */
    @ColumnInfo(name = "nama_menu")
    private String namaMenu;

    /** Deskripsi menu */
    private String deskripsi;

    /** Harga jual dalam Rupiah */
    @ColumnInfo(name = "harga_jual")
    private long hargaJual;

    /** Kategori menu (contoh: Nasi Box & Catering, Kue & Dessert, Minuman) */
    private String kategori;

    /** Status ketersediaan menu */
    private boolean tersedia;

    /** Catatan tambahan */
    private String catatan;

    /** Waktu persiapan (contoh: "1 jam", "30 menit") */
    @ColumnInfo(name = "waktu_persiapan")
    private String waktuPersiapan;

    // ========================
    // Constructor
    // ========================

    public Menu() {
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

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public long getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(long hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getWaktuPersiapan() {
        return waktuPersiapan;
    }

    public void setWaktuPersiapan(String waktuPersiapan) {
        this.waktuPersiapan = waktuPersiapan;
    }
}
