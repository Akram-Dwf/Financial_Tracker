package com.example.dapurmoms.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
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
        version = 3,
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
                            .addMigrations(MIGRATION_2_3)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Callback saat database pertama kali dibuat.
     */
    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    // Dapur Moms Hijra menu will be added dynamically by the user later
                }
            };

    /**
     * Migrasi dari versi 2 ke 3.
     * Menambahkan kolom metode_pembayaran ke tabel pesanan, belanja_bahan, dan biaya_lain.
     * Data yang sudah ada tidak akan hilang — semua data lama diberi default 'Cash'.
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pesanan ADD COLUMN metode_pembayaran TEXT NOT NULL DEFAULT 'Cash'");
            database.execSQL("ALTER TABLE belanja_bahan ADD COLUMN metode_pembayaran TEXT NOT NULL DEFAULT 'Cash'");
            database.execSQL("ALTER TABLE biaya_lain ADD COLUMN metode_pembayaran TEXT NOT NULL DEFAULT 'Cash'");
        }
    };
}
