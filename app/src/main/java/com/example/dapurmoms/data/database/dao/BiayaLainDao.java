package com.example.dapurmoms.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dapurmoms.data.database.entity.BiayaLain;

import java.util.List;

/**
 * Data Access Object untuk tabel biaya lain-lain.
 * Menyediakan method untuk operasi CRUD dan query agregasi
 * pada data pengeluaran selain bahan baku.
 */
@Dao
public interface BiayaLainDao {

    /**
     * Mengambil semua data biaya lain, diurutkan dari tanggal terbaru.
     *
     * @return LiveData berisi daftar semua biaya lain
     */
    @Query("SELECT * FROM biaya_lain ORDER BY tanggal DESC")
    LiveData<List<BiayaLain>> getAllBiaya();

    /**
     * Menghitung total pengeluaran biaya lain-lain dari semua data.
     *
     * @return LiveData berisi total biaya lain (SUM dari kolom jumlah)
     */
    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain")
    LiveData<Long> getTotalBiaya();

    /**
     * Menghitung total pengeluaran biaya lain-lain dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik sejak epoch
     * @param end   akhir periode dalam milidetik sejak epoch
     * @return LiveData berisi total biaya lain pada periode tersebut
     */
    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain WHERE tanggal >= :start AND tanggal <= :end")
    LiveData<Long> getTotalBiayaBulan(long start, long end);

    /**
     * Menambahkan data biaya lain baru ke database.
     *
     * @param biayaLain data biaya yang akan ditambahkan
     */
    @Insert
    void insert(BiayaLain biayaLain);

    /**
     * Memperbarui data biaya lain yang sudah ada.
     *
     * @param biayaLain data biaya yang akan diperbarui
     */
    @Update
    void update(BiayaLain biayaLain);

    /**
     * Menghapus data biaya lain dari database.
     *
     * @param biayaLain data biaya yang akan dihapus
     */
    @Delete
    void delete(BiayaLain biayaLain);
}
