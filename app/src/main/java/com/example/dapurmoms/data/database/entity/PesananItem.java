package com.example.dapurmoms.data.database.entity;

public class PesananItem {
    private String namaMenu;
    private int jumlah;
    private long hargaSatuan;
    private long total;

    public PesananItem() {
    }

    public PesananItem(String namaMenu, int jumlah, long hargaSatuan) {
        this.namaMenu = namaMenu;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.total = (long) jumlah * hargaSatuan;
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
        this.total = (long) this.jumlah * this.hargaSatuan;
    }

    public long getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(long hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
        this.total = (long) this.jumlah * this.hargaSatuan;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PesananItem that = (PesananItem) o;
        if (jumlah != that.jumlah) return false;
        if (hargaSatuan != that.hargaSatuan) return false;
        if (total != that.total) return false;
        return namaMenu != null ? namaMenu.equals(that.namaMenu) : that.namaMenu == null;
    }

    @Override
    public int hashCode() {
        int result = namaMenu != null ? namaMenu.hashCode() : 0;
        result = 31 * result + jumlah;
        result = 31 * result + (int) (hargaSatuan ^ (hargaSatuan >>> 32));
        result = 31 * result + (int) (total ^ (total >>> 32));
        return result;
    }
}
