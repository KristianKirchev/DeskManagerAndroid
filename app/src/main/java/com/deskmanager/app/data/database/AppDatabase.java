package com.deskmanager.app.data.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.deskmanager.app.data.dao.DeskDao;
import com.deskmanager.app.data.dao.ReservationDao;
import com.deskmanager.app.data.dao.UserDao;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.UserRole;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {UserEntity.class, DeskEntity.class, ReservationEntity.class},
    version = 4,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = "AppDatabase";
    private static final String DB_NAME = "desk_manager.db";

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract DeskDao deskDao();
    public abstract ReservationDao reservationDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(populateCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                "ALTER TABLE users ADD COLUMN must_change_password INTEGER NOT NULL DEFAULT 0"
            );
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                "ALTER TABLE users ADD COLUMN pending_cancel_notif TEXT"
            );
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX IF EXISTS index_reservations_desk_id");
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_reservations_desk_id_user_id_status " +
                "ON reservations (desk_id, user_id, status)"
            );
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_reservations_user_id " +
                "ON reservations (user_id)"
            );
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_desks_floor ON desks (floor)"
            );
        }
    };


    private static final RoomDatabase.Callback populateCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                try {
                    seedDatabase(INSTANCE);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to seed database", e);
                }
            });
        }
    };

    private static void seedDatabase(AppDatabase database) {
        UserDao userDao = database.userDao();
        DeskDao deskDao = database.deskDao();

        userDao.insert(new UserEntity("Alice Admin",  "admin",  "admin123",  UserRole.ADMIN));
        userDao.insert(new UserEntity("Bob User",     "bob",    "user123",   UserRole.USER));
        userDao.insert(new UserEntity("Carol User",   "carol",  "user456",   UserRole.USER));
        userDao.insert(new UserEntity("Guest Access", "guest",  "guest123",  UserRole.GUEST));

        deskDao.insert(new DeskEntity("A-101", 1, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("A-102", 1, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("A-103", 1, true,  DeskStatus.OCCUPIED));
        deskDao.insert(new DeskEntity("B-201", 2, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("B-202", 2, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("B-203", 2, true,  DeskStatus.OCCUPIED));
        deskDao.insert(new DeskEntity("C-301", 3, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("C-302", 3, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("C-303", 3, false, DeskStatus.AVAILABLE));
        deskDao.insert(new DeskEntity("C-304", 3, true,  DeskStatus.OCCUPIED));

        Log.d(TAG, "Database seeded with sample data.");
    }
}
