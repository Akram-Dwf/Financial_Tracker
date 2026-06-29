package com.example.dapurmoms.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dapurmoms.data.database.entity.Pesanan;

import java.util.List;

/**
 * Data Access Object untuk tabel pesanan.
 * Menyediakan method untuk operasi CRUD dan query agregasi
 * pada data pesanan pelanggan.
 */
@Dao
public interface PesananDao {

    /**
     * Mengambil semua data pesanan, diurutkan dari tanggal terbaru.
     *
     * @return LiveData berisi daftar semua pesanan
     */
    @Query("SELECT * FROM pesanan ORDER BY tanggal DESC")
    LiveData<List<Pesanan>> getAllPesanan();

    /**
     * Menghitung total pemasukan dari semua pesanan.
     *
     * @return LiveData berisi total uang masuk (SUM dari kolom total)
     */
    @Query("SELECT COALESCE(SUM(total), 0) FROM pesanan")
    LiveData<Long> getTotalUangMasuk();

    /**
     * Menghitung total pemasukan dari pesanan dalam rentang bulan tertentu.
     *
     * @param startOfMonth awal bulan dalam milidetik sejak epoch
     * @param endOfMonth   akhir bulan dalam milidetik sejak epoch
     * @return LiveData berisi total uang masuk pada bulan tersebut
     */
    @Query("SELECT COALESCE(SUM(total), 0) FROM pesanan WHERE tanggal >= :startOfMonth AND tanggal <= :endOfMonth")
    LiveData<Long> getTotalUangMasukBulan(long startOfMonth, long endOfMonth);

    /**
     * Menambahkan pesanan baru ke database.
     *
     * @param pesanan data pesanan yang akan ditambahkan
     */
    @Insert
    void insert(Pesanan pesanan);

    /**
     * Memperbarui data pesanan yang sudah ada.
     *
     * @param pesanan data pesanan yang akan diperbarui
     */
    @Update
    void update(Pesanan pesanan);

    /**
     * Menghapus data pesanan dari database.
     *
     * @param pesanan data pesanan yang akan dihapus
     */
    @Delete
    void delete(Pesanan pesanan);
}
