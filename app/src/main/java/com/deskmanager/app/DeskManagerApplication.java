package com.deskmanager.app;

import android.app.Application;

import com.deskmanager.app.data.database.AppDatabase;
import com.deskmanager.app.data.repository.ReservationRepository;
import com.deskmanager.app.utils.NotificationHelper;

public class DeskManagerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.getInstance(this);
        NotificationHelper.createChannels(this);
        new ReservationRepository(this).expireReservations();
    }
}
