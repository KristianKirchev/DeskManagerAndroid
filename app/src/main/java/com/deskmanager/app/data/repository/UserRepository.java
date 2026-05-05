package com.deskmanager.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.deskmanager.app.data.dao.UserDao;
import com.deskmanager.app.data.database.AppDatabase;
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.data.dao.ReservationDao;
import com.deskmanager.app.data.dao.DeskDao;
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.ReservationStatus;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class UserRepository {

    private final UserDao userDao;
    private final ReservationDao reservationDao;
    private final DeskDao deskDao;
    private final ExecutorService executor;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        reservationDao = db.reservationDao();
        deskDao = db.deskDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void insert(UserEntity user, InsertCallback callback) {
        executor.execute(() -> {
            try {
                int count = userDao.countByUsername(user.getUsername());
                if (count > 0) {
                    if (callback != null) callback.onError("Username already taken.");
                    return;
                }
                long id = userDao.insert(user);
                if (callback != null) callback.onSuccess((int) id);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void update(UserEntity user) {
        executor.execute(() -> userDao.update(user));
    }

    public void appendPendingCancelNotif(int userId, String roomName) {
        executor.execute(() -> {
            UserEntity user = userDao.getUserById(userId);
            if (user != null) {
                String existing = user.getPendingCancelNotif();
                String updated = (existing == null || existing.isEmpty())
                        ? roomName
                        : existing + "\n" + roomName;
                user.setPendingCancelNotif(updated);
                userDao.update(user);
            }
        });
    }

    public void clearPendingCancelNotif(int userId) {
        executor.execute(() -> {
            UserEntity user = userDao.getUserById(userId);
            if (user != null) {
                user.setPendingCancelNotif(null);
                userDao.update(user);
            }
        });
    }

    public void resetPassword(UserEntity user, String newTempPassword, ResetPasswordCallback callback) {
        executor.execute(() -> {
            try {
                user.setPassword(newTempPassword);
                user.setMustChangePassword(true);
                userDao.update(user);
                if (callback != null) callback.onSuccess(newTempPassword);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void delete(UserEntity user) {
        executor.execute(() -> userDao.delete(user));
    }

    public void deleteWithReservations(UserEntity user, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                List<ReservationEntity> active =
                        reservationDao.getActiveReservationsByUserSync(user.getId());
                for (ReservationEntity r : active) {
                    r.setStatus(ReservationStatus.CANCELLED);
                    reservationDao.update(r);
                    List<ReservationEntity> remaining =
                            reservationDao.getActiveReservationsForDesk(r.getDeskId());
                    if (remaining.isEmpty()) {
                        DeskEntity desk = deskDao.getDeskById(r.getDeskId());
                        if (desk != null) {
                            desk.setOccupied(false);
                            desk.setStatus(DeskStatus.AVAILABLE);
                            deskDao.update(desk);
                        }
                    }
                }
                userDao.delete(user);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public LiveData<List<UserEntity>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void getUserById(int userId, GetUserCallback callback) {
        executor.execute(() -> {
            UserEntity user = userDao.getUserById(userId);
            if (callback != null) callback.onResult(user);
        });
    }

    public void login(String username, String password, LoginCallback callback) {
        executor.execute(() -> {
            UserEntity user = userDao.login(username, password);
            if (callback != null) {
                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("Invalid username or password.");
                }
            }
        });
    }

    public interface InsertCallback {
        void onSuccess(int newId);
        void onError(String message);
    }

    public interface LoginCallback {
        void onSuccess(UserEntity user);
        void onError(String message);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface ResetPasswordCallback {
        void onSuccess(String tempPassword);
        void onError(String message);
    }

    public interface GetUserCallback {
        void onResult(UserEntity user);
    }
}
