package com.example.dapurmoms;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import com.example.dapurmoms.data.database.DapurMomsDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    @Test
    public void testDatabaseInitialization() {
        Context context = ApplicationProvider.getApplicationContext();
        DapurMomsDatabase db = Room.inMemoryDatabaseBuilder(context, DapurMomsDatabase.class)
                .allowMainThreadQueries()
                .build();
        assertNotNull(db);
        
        // Trigger database creation and schema validation
        db.query("SELECT * FROM pesanan", null);
        System.out.println("Room database created and validated successfully!");
    }
}