package com.example.dapurmoms.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dapurmoms.data.database.entity.Menu;

import java.util.List;

/**
 * Data Access Object untuk tabel menu.
 * Menyediakan method untuk operasi CRUD dan query
 * pada data daftar menu DapurMoms.
 */
@Dao
public interface MenuDao {

    /**
     * Mengambil semua data menu.
     *
     * @return LiveData berisi daftar semua menu
     */
    @Query("SELECT * FROM menu")
    LiveData<List<Menu>> getAllMenu();

    /**
     * Mengambil data menu berdasarkan kategori tertentu.
     *
     * @param kategori kategori menu yang dicari
     * @return LiveData berisi daftar menu pada kategori tersebut
     */
    @Query("SELECT * FROM menu WHERE kategori = :kategori")
    LiveData<List<Menu>> getMenuByKategori(String kategori);

    /**
     * Mengambil semua nama menu untuk keperluan autocomplete
     * pada form pesanan.
     *
     * @return LiveData berisi daftar nama menu
     */
    @Query("SELECT nama_menu FROM menu")
    LiveData<List<String>> getAllMenuNames();

    /**
     * Menambahkan menu baru ke database.
     *
     * @param menu data menu yang akan ditambahkan
     */
    @Insert
    void insert(Menu menu);

    /**
     * Memperbarui data menu yang sudah ada.
     *
     * @param menu data menu yang akan diperbarui
     */
    @Update
    void update(Menu menu);

    /**
     * Menghapus data menu dari database.
     *
     * @param menu data menu yang akan dihapus
     */
    @Delete
    void delete(Menu menu);
}
