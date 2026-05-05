package com.deskmanager.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.deskmanager.app.data.dao.DeskDao;
import com.deskmanager.app.data.database.AppDatabase;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.exceptions.DeskNotFoundException;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class DeskRepository {

    private final DeskDao deskDao;
    private final ExecutorService executor;

    public DeskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        deskDao = db.deskDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void insert(DeskEntity desk, ResultCallback callback) {
        executor.execute(() -> {
            try {
                long id = deskDao.insert(desk);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void update(DeskEntity desk, ResultCallback callback) {
        executor.execute(() -> {
            try {
                deskDao.update(desk);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void delete(DeskEntity desk, ResultCallback callback) {
        executor.execute(() -> {
            try {
                deskDao.delete(desk);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public LiveData<List<DeskEntity>> getAllDesks() {
        return deskDao.getAllDesks();
    }

    public LiveData<List<DeskEntity>> getDesksByStatus(DeskStatus status) {
        return deskDao.getDesksByStatus(status.name());
    }

    public LiveData<List<DeskEntity>> searchDesks(String query) {
        return deskDao.searchDesks(query);
    }

    public void getDeskById(int id, GetDeskCallback callback) {
        executor.execute(() -> {
            DeskEntity desk = deskDao.getDeskById(id);
            if (callback != null) {
                if (desk != null) {
                    callback.onSuccess(desk);
                } else {
                    callback.onError(new DeskNotFoundException(id).getMessage());
                }
            }
        });
    }

    public LiveData<List<DeskEntity>> getDesksForFloor(int floor) {
        return deskDao.getDesksForFloor(floor);
    }

    public void getDistinctFloors(FloorsCallback callback) {
        executor.execute(() -> {
            List<Integer> floors = deskDao.getDistinctFloorsSync();
            if (callback != null) callback.onResult(floors);
        });
    }

    public interface ResultCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface GetDeskCallback {
        void onSuccess(DeskEntity desk);
        void onError(String message);
    }

    public interface FloorsCallback {
        void onResult(List<Integer> floors);
    }
}
