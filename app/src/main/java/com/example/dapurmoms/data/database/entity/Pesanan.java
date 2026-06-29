package com.example.dapurmoms.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity untuk tabel pesanan.
 * Menyimpan data pesanan dari pelanggan termasuk nama menu,
 * jumlah, harga satuan, dan total pembayaran.
 */
@Entity(tableName = "pesanan")
public class Pesanan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Tanggal pesanan dalam milidetik sejak epoch */
    private long tanggal;

    /** Nama pemesan / pelanggan */
    @ColumnInfo(name = "nama_pemesan")
    private String namaPemesan;

    /** Nama menu yang dipesan */
    @ColumnInfo(name = "nama_menu")
    private String namaMenu;

    /** Jumlah porsi / unit yang dipesan */
    private int jumlah;

    /** Harga per satuan dalam Rupiah */
    @ColumnInfo(name = "harga_satuan")
    private long hargaSatuan;

    /** Total harga (jumlah × hargaSatuan) dalam Rupiah */
    private long total;

    /** Catatan tambahan untuk pesanan */
    private String catatan;

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

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
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
}
