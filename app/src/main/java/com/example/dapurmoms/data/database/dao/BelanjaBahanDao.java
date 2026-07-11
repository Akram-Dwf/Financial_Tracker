package com.example.dapurmoms.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dapurmoms.data.database.entity.BelanjaBahan;

import java.util.List;

/**
 * Data Access Object untuk tabel belanja bahan.
 * Menyediakan method untuk operasi CRUD dan query agregasi
 * pada data pembelian bahan baku.
 */
@Dao
public interface BelanjaBahanDao {

    /**
     * Mengambil semua data belanja bahan, diurutkan dari tanggal terbaru.
     *
     * @return LiveData berisi daftar semua belanja bahan
     */
    @Query("SELECT * FROM belanja_bahan WHERE is_deleted = 0 ORDER BY tanggal DESC")
    LiveData<List<BelanjaBahan>> getAllBelanja();

    /**
     * Mengambil data belanja bahan dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik sejak epoch
     * @param end   akhir periode dalam milidetik sejak epoch
     * @return LiveData berisi daftar belanja bahan pada periode tersebut
     */
    @Query("SELECT * FROM belanja_bahan WHERE tanggal >= :start AND tanggal <= :end AND is_deleted = 0 ORDER BY tanggal DESC")
    LiveData<List<BelanjaBahan>> getBelanjaBulan(long start, long end);

    /**
     * Menghitung total pengeluaran belanja bahan dari semua data.
     *
     * @return LiveData berisi total belanja bahan (SUM dari kolom total_harga)
     */
    @Query("SELECT COALESCE(SUM(total_harga), 0) FROM belanja_bahan WHERE is_deleted = 0")
    LiveData<Long> getTotalBelanja();

    /**
     * Menghitung total pengeluaran belanja bahan dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik sejak epoch
     * @param end   akhir periode dalam milidetik sejak epoch
     * @return LiveData berisi total belanja bahan pada periode tersebut
     */
    @Query("SELECT COALESCE(SUM(total_harga), 0) FROM belanja_bahan WHERE tanggal >= :start AND tanggal <= :end AND is_deleted = 0")
    LiveData<Long> getTotalBelanjaBulan(long start, long end);

    /** Total belanja per metode pembayaran per bulan */
    @Query("SELECT COALESCE(SUM(total_harga), 0) FROM belanja_bahan WHERE tanggal >= :start AND tanggal <= :end AND metode_pembayaran = :metode AND is_deleted = 0")
    LiveData<Long> getTotalBelanjaByMetodeBulan(long start, long end, String metode);

    /** Total utang belanja (belum dibayar ke pemasok) per bulan */
    @Query("SELECT COALESCE(SUM(total_harga), 0) FROM belanja_bahan WHERE tanggal >= :start AND tanggal <= :end AND metode_pembayaran = 'Utang' AND is_deleted = 0")
    LiveData<Long> getTotalUtangBelanjaBulan(long start, long end);

    @Query("SELECT * FROM belanja_bahan WHERE metode_pembayaran = 'Utang' AND is_deleted = 0 ORDER BY tanggal DESC")
    LiveData<List<BelanjaBahan>> getUtangBelanjaAktif();

    @Query("SELECT COALESCE(SUM(total_harga), 0) FROM belanja_bahan WHERE metode_pembayaran = 'Utang' AND is_deleted = 0")
    LiveData<Long> getTotalUtangBelanjaAktif();

    @Query("UPDATE belanja_bahan SET is_deleted = 1, deleted_at = :deletedAt WHERE id = :id")
    void softDelete(int id, long deletedAt);

    @Query("UPDATE belanja_bahan SET is_deleted = 0, deleted_at = 0 WHERE id = :id")
    void restoreFromTrash(int id);

    @Query("SELECT * FROM belanja_bahan WHERE is_deleted = 1 ORDER BY deleted_at DESC")
    LiveData<List<BelanjaBahan>> getDeletedBelanja();

    @Query("DELETE FROM belanja_bahan WHERE is_deleted = 1 AND deleted_at < :limitTimestamp")
    void permanentlyDeleteOlderThan(long limitTimestamp);

    /**
     * Menambahkan data belanja bahan baru ke database.
     *
     * @param belanjaBahan data belanja yang akan ditambahkan
     */
    @Insert
    void insert(BelanjaBahan belanjaBahan);

    /**
     * Memperbarui data belanja bahan yang sudah ada.
     *
     * @param belanjaBahan data belanja yang akan diperbarui
     */
    @Update
    void update(BelanjaBahan belanjaBahan);

    /**
     * Menghapus data belanja bahan dari database.
     *
     * @param belanjaBahan data belanja yang akan dihapus
     */
    @Delete
    void delete(BelanjaBahan belanjaBahan);
}
