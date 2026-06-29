package com.example.dapurmoms.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dapurmoms.data.database.DapurMomsDatabase;
import com.example.dapurmoms.data.database.dao.BelanjaBahanDao;
import com.example.dapurmoms.data.database.dao.BiayaLainDao;
import com.example.dapurmoms.data.database.dao.MenuDao;
import com.example.dapurmoms.data.database.dao.PesananDao;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Menu;
import com.example.dapurmoms.data.database.entity.Pesanan;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository untuk aplikasi DapurMoms.
 * Bertindak sebagai lapisan abstraksi antara ViewModel dan sumber data (Room Database).
 * Semua operasi tulis (insert, update, delete) dijalankan di background thread
 * menggunakan ExecutorService, sedangkan query LiveData ditangani otomatis oleh Room.
 */
public class DapurMomsRepository {

    // ========================
    // DAOs
    // ========================
    private final PesananDao pesananDao;
    private final BelanjaBahanDao belanjaBahanDao;
    private final BiayaLainDao biayaLainDao;
    private final MenuDao menuDao;

    /** ExecutorService dengan 4 thread untuk operasi background */
    private final ExecutorService executorService;

    // ========================
    // LiveData (cache dari DAO)
    // ========================
    private final LiveData<List<Pesanan>> allPesanan;
    private final LiveData<List<BelanjaBahan>> allBelanja;
    private final LiveData<List<BiayaLain>> allBiaya;
    private final LiveData<List<Menu>> allMenu;
    private final LiveData<List<String>> allMenuNames;

    private final LiveData<Long> totalUangMasuk;
    private final LiveData<Long> totalBelanja;
    private final LiveData<Long> totalBiaya;

    /**
     * Membuat instance repository baru.
     * Menginisialisasi database, semua DAO, dan cache LiveData.
     *
     * @param application konteks aplikasi
     */
    public DapurMomsRepository(Application application) {
        DapurMomsDatabase database = DapurMomsDatabase.getInstance(application);

        pesananDao = database.pesananDao();
        belanjaBahanDao = database.belanjaBahanDao();
        biayaLainDao = database.biayaLainDao();
        menuDao = database.menuDao();

        executorService = Executors.newFixedThreadPool(4);

        // Inisialisasi cache LiveData
        allPesanan = pesananDao.getAllPesanan();
        allBelanja = belanjaBahanDao.getAllBelanja();
        allBiaya = biayaLainDao.getAllBiaya();
        allMenu = menuDao.getAllMenu();
        allMenuNames = menuDao.getAllMenuNames();

        totalUangMasuk = pesananDao.getTotalUangMasuk();
        totalBelanja = belanjaBahanDao.getTotalBelanja();
        totalBiaya = biayaLainDao.getTotalBiaya();
    }

    // ================================================================
    // PESANAN
    // ================================================================

    /**
     * Mengambil semua pesanan.
     *
     * @return LiveData berisi daftar semua pesanan
     */
    public LiveData<List<Pesanan>> getAllPesanan() {
        return allPesanan;
    }

    /**
     * Mengambil total pemasukan dari semua pesanan.
     *
     * @return LiveData berisi total uang masuk
     */
    public LiveData<Long> getTotalUangMasuk() {
        return totalUangMasuk;
    }

    /**
     * Mengambil total pemasukan pesanan dalam rentang bulan tertentu.
     *
     * @param startOfMonth awal bulan dalam milidetik
     * @param endOfMonth   akhir bulan dalam milidetik
     * @return LiveData berisi total uang masuk bulan tersebut
     */
    public LiveData<Long> getTotalUangMasukBulan(long startOfMonth, long endOfMonth) {
        return pesananDao.getTotalUangMasukBulan(startOfMonth, endOfMonth);
    }

    /**
     * Menambahkan pesanan baru (dijalankan di background thread).
     *
     * @param pesanan data pesanan yang akan ditambahkan
     */
    public void insertPesanan(Pesanan pesanan) {
        executorService.execute(() -> pesananDao.insert(pesanan));
    }

    /**
     * Memperbarui pesanan (dijalankan di background thread).
     *
     * @param pesanan data pesanan yang akan diperbarui
     */
    public void updatePesanan(Pesanan pesanan) {
        executorService.execute(() -> pesananDao.update(pesanan));
    }

    /**
     * Menghapus pesanan (dijalankan di background thread).
     *
     * @param pesanan data pesanan yang akan dihapus
     */
    public void deletePesanan(Pesanan pesanan) {
        executorService.execute(() -> pesananDao.delete(pesanan));
    }

    // ================================================================
    // BELANJA BAHAN
    // ================================================================

    /**
     * Mengambil semua data belanja bahan.
     *
     * @return LiveData berisi daftar semua belanja bahan
     */
    public LiveData<List<BelanjaBahan>> getAllBelanja() {
        return allBelanja;
    }

    /**
     * Mengambil total pengeluaran belanja bahan.
     *
     * @return LiveData berisi total belanja
     */
    public LiveData<Long> getTotalBelanja() {
        return totalBelanja;
    }

    /**
     * Mengambil total belanja bahan dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik
     * @param end   akhir periode dalam milidetik
     * @return LiveData berisi total belanja pada periode tersebut
     */
    public LiveData<Long> getTotalBelanjaBulan(long start, long end) {
        return belanjaBahanDao.getTotalBelanjaBulan(start, end);
    }

    /**
     * Menambahkan data belanja bahan baru (dijalankan di background thread).
     *
     * @param belanjaBahan data belanja yang akan ditambahkan
     */
    public void insertBelanja(BelanjaBahan belanjaBahan) {
        executorService.execute(() -> belanjaBahanDao.insert(belanjaBahan));
    }

    /**
     * Memperbarui data belanja bahan (dijalankan di background thread).
     *
     * @param belanjaBahan data belanja yang akan diperbarui
     */
    public void updateBelanja(BelanjaBahan belanjaBahan) {
        executorService.execute(() -> belanjaBahanDao.update(belanjaBahan));
    }

    /**
     * Menghapus data belanja bahan (dijalankan di background thread).
     *
     * @param belanjaBahan data belanja yang akan dihapus
     */
    public void deleteBelanja(BelanjaBahan belanjaBahan) {
        executorService.execute(() -> belanjaBahanDao.delete(belanjaBahan));
    }

    // ================================================================
    // BIAYA LAIN
    // ================================================================

    /**
     * Mengambil semua data biaya lain-lain.
     *
     * @return LiveData berisi daftar semua biaya lain
     */
    public LiveData<List<BiayaLain>> getAllBiaya() {
        return allBiaya;
    }

    /**
     * Mengambil total pengeluaran biaya lain-lain.
     *
     * @return LiveData berisi total biaya lain
     */
    public LiveData<Long> getTotalBiaya() {
        return totalBiaya;
    }

    /**
     * Mengambil total biaya lain dalam rentang bulan tertentu.
     *
     * @param start awal periode dalam milidetik
     * @param end   akhir periode dalam milidetik
     * @return LiveData berisi total biaya lain pada periode tersebut
     */
    public LiveData<Long> getTotalBiayaBulan(long start, long end) {
        return biayaLainDao.getTotalBiayaBulan(start, end);
    }

    /**
     * Menambahkan data biaya lain baru (dijalankan di background thread).
     *
     * @param biayaLain data biaya yang akan ditambahkan
     */
    public void insertBiaya(BiayaLain biayaLain) {
        executorService.execute(() -> biayaLainDao.insert(biayaLain));
    }

    /**
     * Memperbarui data biaya lain (dijalankan di background thread).
     *
     * @param biayaLain data biaya yang akan diperbarui
     */
    public void updateBiaya(BiayaLain biayaLain) {
        executorService.execute(() -> biayaLainDao.update(biayaLain));
    }

    /**
     * Menghapus data biaya lain (dijalankan di background thread).
     *
     * @param biayaLain data biaya yang akan dihapus
     */
    public void deleteBiaya(BiayaLain biayaLain) {
        executorService.execute(() -> biayaLainDao.delete(biayaLain));
    }

    // ================================================================
    // MENU
    // ================================================================

    /**
     * Mengambil semua data menu.
     *
     * @return LiveData berisi daftar semua menu
     */
    public LiveData<List<Menu>> getAllMenu() {
        return allMenu;
    }

    /**
     * Mengambil data menu berdasarkan kategori.
     *
     * @param kategori kategori menu yang dicari
     * @return LiveData berisi daftar menu pada kategori tersebut
     */
    public LiveData<List<Menu>> getMenuByKategori(String kategori) {
        return menuDao.getMenuByKategori(kategori);
    }

    /**
     * Mengambil semua nama menu untuk autocomplete.
     *
     * @return LiveData berisi daftar nama menu
     */
    public LiveData<List<String>> getAllMenuNames() {
        return allMenuNames;
    }

    /**
     * Menambahkan menu baru (dijalankan di background thread).
     *
     * @param menu data menu yang akan ditambahkan
     */
    public void insertMenu(Menu menu) {
        executorService.execute(() -> menuDao.insert(menu));
    }

    /**
     * Memperbarui data menu (dijalankan di background thread).
     *
     * @param menu data menu yang akan diperbarui
     */
    public void updateMenu(Menu menu) {
        executorService.execute(() -> menuDao.update(menu));
    }

    /**
     * Menghapus data menu (dijalankan di background thread).
     *
     * @param menu data menu yang akan dihapus
     */
    public void deleteMenu(Menu menu) {
        executorService.execute(() -> menuDao.delete(menu));
    }
}
