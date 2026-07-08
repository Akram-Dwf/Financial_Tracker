package com.example.dapurmoms.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

/**
 * Entity untuk tabel belanja bahan.
 * Menyimpan data pembelian bahan baku untuk produksi
 * termasuk nama bahan, toko, volume, dan harga beli.
 */
@Entity(tableName = "belanja_bahan", indices = {@Index("tanggal")})
public class BelanjaBahan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Tanggal belanja dalam milidetik sejak epoch */
    private long tanggal;

    /** Nama bahan yang dibeli */
    @ColumnInfo(name = "nama_bahan")
    private String namaBahan;

    /** Nama toko tempat pembelian */
    private String toko;

    /** Satuan volume (contoh: Kg, Pcs, Ltr) */
    private String volume;

    /** Jumlah unit yang dibeli */
    @ColumnInfo(name = "jumlah_unit")
    private double jumlahUnit;

    /** Harga beli per unit dalam Rupiah */
    @ColumnInfo(name = "harga_beli")
    private long hargaBeli;

    /** Total harga pembelian dalam Rupiah */
    @ColumnInfo(name = "total_harga")
    private long totalHarga;

    /** Catatan tambahan untuk pembelian */
    private String catatan;

    /** Metode pembayaran: Cash, Transfer, atau Utang */
    @ColumnInfo(name = "metode_pembayaran", defaultValue = "Cash")
    private String metodePembayaran = "Cash";

    // ========================
    // Constructor
    // ========================

    public BelanjaBahan() {
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

    public String getNamaBahan() {
        return namaBahan;
    }

    public void setNamaBahan(String namaBahan) {
        this.namaBahan = namaBahan;
    }

    public String getToko() {
        return toko;
    }

    public void setToko(String toko) {
        this.toko = toko;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public double getJumlahUnit() {
        return jumlahUnit;
    }

    public void setJumlahUnit(double jumlahUnit) {
        this.jumlahUnit = jumlahUnit;
    }

    public long getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(long hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public long getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(long totalHarga) {
        this.totalHarga = totalHarga;
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
}
