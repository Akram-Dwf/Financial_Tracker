package com.example.dapurmoms.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.dapurmoms.data.database.dao.BelanjaBahanDao;
import com.example.dapurmoms.data.database.dao.BiayaLainDao;
import com.example.dapurmoms.data.database.dao.MenuDao;
import com.example.dapurmoms.data.database.dao.PesananDao;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Menu;
import com.example.dapurmoms.data.database.entity.Pesanan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database utama aplikasi DapurMoms.
 * Menggunakan Room Persistence Library dengan pola Singleton
 * untuk memastikan hanya satu instance database yang aktif.
 *
 * <p>Tabel yang tersedia:
 * <ul>
 *   <li>{@link Pesanan} — data pesanan pelanggan</li>
 *   <li>{@link BelanjaBahan} — data belanja bahan baku</li>
 *   <li>{@link BiayaLain} — data biaya lain-lain</li>
 *   <li>{@link Menu} — daftar menu yang dijual</li>
 * </ul>
 */
@Database(
        entities = {
                Pesanan.class,
                BelanjaBahan.class,
                BiayaLain.class,
                Menu.class
        },
        version = 2,
        exportSchema = false
)
public abstract class DapurMomsDatabase extends RoomDatabase {

    // ========================
    // Abstract DAO methods
    // ========================

    /** @return DAO untuk operasi pada tabel pesanan */
    public abstract PesananDao pesananDao();

    /** @return DAO untuk operasi pada tabel belanja bahan */
    public abstract BelanjaBahanDao belanjaBahanDao();

    /** @return DAO untuk operasi pada tabel biaya lain */
    public abstract BiayaLainDao biayaLainDao();

    /** @return DAO untuk operasi pada tabel menu */
    public abstract MenuDao menuDao();

    // ========================
    // Singleton
    // ========================

    private static volatile DapurMomsDatabase INSTANCE;

    /** ExecutorService untuk menjalankan operasi database di background thread */
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Mendapatkan instance tunggal dari database.
     * Jika database belum ada, akan dibuat baru dengan data awal menu.
     *
     * @param context konteks aplikasi
     * @return instance DapurMomsDatabase
     */
    public static DapurMomsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DapurMomsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    DapurMomsDatabase.class,
                                    "dapur_moms_database"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Callback untuk mengisi data awal (pre-populate) saat database pertama kali dibuat.
     * Data awal berisi daftar menu dari katalog DapurMoms.
     */
    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    databaseWriteExecutor.execute(() -> {
                        MenuDao menuDao = INSTANCE.menuDao();

                        // === Kategori: Nasi Box & Catering ===
                        Menu nasiBoxAyam = new Menu();
                        nasiBoxAyam.setNamaMenu("Nasi Box Ayam Bakar");
                        nasiBoxAyam.setDeskripsi("Nasi box dengan ayam bakar bumbu spesial");
                        nasiBoxAyam.setHargaJual(28000);
                        nasiBoxAyam.setKategori("Nasi Box & Catering");
                        nasiBoxAyam.setTersedia(true);
                        nasiBoxAyam.setCatatan("");
                        nasiBoxAyam.setWaktuPersiapan("1 jam");
                        menuDao.insert(nasiBoxAyam);

                        Menu nasiBoxRendang = new Menu();
                        nasiBoxRendang.setNamaMenu("Nasi Box Rendang");
                        nasiBoxRendang.setDeskripsi("Nasi box dengan rendang daging sapi");
                        nasiBoxRendang.setHargaJual(35000);
                        nasiBoxRendang.setKategori("Nasi Box & Catering");
                        nasiBoxRendang.setTersedia(true);
                        nasiBoxRendang.setCatatan("");
                        nasiBoxRendang.setWaktuPersiapan("1.5 jam");
                        menuDao.insert(nasiBoxRendang);

                        Menu tumpengMini = new Menu();
                        tumpengMini.setNamaMenu("Tumpeng Mini");
                        tumpengMini.setDeskripsi("Tumpeng mini lengkap dengan lauk pauk");
                        tumpengMini.setHargaJual(350000);
                        tumpengMini.setKategori("Nasi Box & Catering");
                        tumpengMini.setTersedia(true);
                        tumpengMini.setCatatan("");
                        tumpengMini.setWaktuPersiapan("3 jam");
                        menuDao.insert(tumpengMini);

                        // === Kategori: Kue & Dessert ===
                        Menu kueUltah = new Menu();
                        kueUltah.setNamaMenu("Kue Ulang Tahun");
                        kueUltah.setDeskripsi("Kue ulang tahun custom dengan dekorasi");
                        kueUltah.setHargaJual(180000);
                        kueUltah.setKategori("Kue & Dessert");
                        kueUltah.setTersedia(true);
                        kueUltah.setCatatan("");
                        kueUltah.setWaktuPersiapan("2 jam");
                        menuDao.insert(kueUltah);

                        Menu browniesPanggang = new Menu();
                        browniesPanggang.setNamaMenu("Brownies Panggang");
                        browniesPanggang.setDeskripsi("Brownies panggang cokelat premium");
                        browniesPanggang.setHargaJual(65000);
                        browniesPanggang.setKategori("Kue & Dessert");
                        browniesPanggang.setTersedia(true);
                        browniesPanggang.setCatatan("");
                        browniesPanggang.setWaktuPersiapan("1.5 jam");
                        menuDao.insert(browniesPanggang);

                        Menu pudingCokelat = new Menu();
                        pudingCokelat.setNamaMenu("Puding Cokelat");
                        pudingCokelat.setDeskripsi("Puding cokelat lembut dengan saus vla");
                        pudingCokelat.setHargaJual(12000);
                        pudingCokelat.setKategori("Kue & Dessert");
                        pudingCokelat.setTersedia(true);
                        pudingCokelat.setCatatan("");
                        pudingCokelat.setWaktuPersiapan("30 menit");
                        menuDao.insert(pudingCokelat);

                        // === Kategori: Minuman ===
                        Menu esTehManis = new Menu();
                        esTehManis.setNamaMenu("Es Teh Manis");
                        esTehManis.setDeskripsi("Es teh manis segar");
                        esTehManis.setHargaJual(5000);
                        esTehManis.setKategori("Minuman");
                        esTehManis.setTersedia(true);
                        esTehManis.setCatatan("");
                        esTehManis.setWaktuPersiapan("5 menit");
                        menuDao.insert(esTehManis);

                        Menu esJeruk = new Menu();
                        esJeruk.setNamaMenu("Es Jeruk");
                        esJeruk.setDeskripsi("Es jeruk peras segar");
                        esJeruk.setHargaJual(7000);
                        esJeruk.setKategori("Minuman");
                        esJeruk.setTersedia(true);
                        esJeruk.setCatatan("");
                        esJeruk.setWaktuPersiapan("5 menit");
                        menuDao.insert(esJeruk);
                    });
                }
            };
}
