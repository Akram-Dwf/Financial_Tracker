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
    @Query("SELECT * FROM biaya_lain WHERE is_deleted = 0 ORDER BY tanggal DESC")
    LiveData<List<BiayaLain>> getAllBiaya();

    /**
     * Mengambil data biaya lain dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik sejak epoch
     * @param end   akhir periode dalam milidetik sejak epoch
     * @return LiveData berisi daftar biaya lain pada periode tersebut
     */
    @Query("SELECT * FROM biaya_lain WHERE tanggal >= :start AND tanggal <= :end AND is_deleted = 0 ORDER BY tanggal DESC")
    LiveData<List<BiayaLain>> getBiayaBulan(long start, long end);

    /**
     * Menghitung total pengeluaran biaya lain-lain dari semua data.
     *
     * @return LiveData berisi total biaya lain (SUM dari kolom jumlah)
     */
    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain WHERE is_deleted = 0")
    LiveData<Long> getTotalBiaya();

    /**
     * Menghitung total pengeluaran biaya lain-lain dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik sejak epoch
     * @param end   akhir periode dalam milidetik sejak epoch
     * @return LiveData berisi total biaya lain pada periode tersebut
     */
    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain WHERE tanggal >= :start AND tanggal <= :end AND is_deleted = 0")
    LiveData<Long> getTotalBiayaBulan(long start, long end);

    /** Total biaya per metode pembayaran per bulan */
    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain WHERE tanggal >= :start AND tanggal <= :end AND metode_pembayaran = :metode AND is_deleted = 0")
    LiveData<Long> getTotalBiayaByMetodeBulan(long start, long end, String metode);

    /** Total utang biaya (belum dibayar) per bulan */
    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain WHERE tanggal >= :start AND tanggal <= :end AND metode_pembayaran = 'Utang' AND is_deleted = 0")
    LiveData<Long> getTotalUtangBiayaBulan(long start, long end);

    @Query("SELECT * FROM biaya_lain WHERE metode_pembayaran = 'Utang' AND is_deleted = 0 ORDER BY tanggal DESC")
    LiveData<List<BiayaLain>> getUtangBiayaAktif();

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM biaya_lain WHERE metode_pembayaran = 'Utang' AND is_deleted = 0")
    LiveData<Long> getTotalUtangBiayaAktif();

    @Query("UPDATE biaya_lain SET is_deleted = 1, deleted_at = :deletedAt WHERE id = :id")
    void softDelete(int id, long deletedAt);

    @Query("UPDATE biaya_lain SET is_deleted = 0, deleted_at = 0 WHERE id = :id")
    void restoreFromTrash(int id);

    @Query("SELECT * FROM biaya_lain WHERE is_deleted = 1 ORDER BY deleted_at DESC")
    LiveData<List<BiayaLain>> getDeletedBiaya();

    @Query("DELETE FROM biaya_lain WHERE is_deleted = 1 AND deleted_at < :limitTimestamp")
    void permanentlyDeleteOlderThan(long limitTimestamp);

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
