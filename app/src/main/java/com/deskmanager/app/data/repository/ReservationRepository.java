package com.deskmanager.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.deskmanager.app.data.dao.DeskDao;
import com.deskmanager.app.data.dao.ReservationDao;
import com.deskmanager.app.data.database.AppDatabase;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.domain.exceptions.DeskAlreadyReservedException;
import com.deskmanager.app.domain.exceptions.InvalidReservationException;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ReservationRepository {

    private final ReservationDao reservationDao;
    private final DeskDao deskDao;
    private final ExecutorService executor;

    public ReservationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        reservationDao = db.reservationDao();
        deskDao = db.deskDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void createReservation(ReservationEntity reservation, ResultCallback callback) {
        executor.execute(() -> {
            try {
                if (reservation.getEndDate() < reservation.getStartDate()) {
                    throw new InvalidReservationException("End date must not be before start date.");
                }

                int overlapping = reservationDao.countOverlapping(
                        reservation.getDeskId(),
                        reservation.getStartDate(),
                        reservation.getEndDate()
                );
                if (overlapping > 0) {
                    throw new DeskAlreadyReservedException(reservation.getDeskId());
                }

                reservationDao.insert(reservation);

                DeskEntity desk = deskDao.getDeskById(reservation.getDeskId());
                if (desk != null) {
                    desk.setOccupied(true);
                    desk.setStatus(DeskStatus.OCCUPIED);
                    deskDao.update(desk);
                }

                if (callback != null) callback.onSuccess();

            } catch (InvalidReservationException | DeskAlreadyReservedException e) {
                if (callback != null) callback.onError(e.getMessage());
            } catch (Exception e) {
                if (callback != null) callback.onError("Unexpected error: " + e.getMessage());
            }
        });
    }

    public void cancelReservation(ReservationEntity reservation, ResultCallback callback) {
        executor.execute(() -> {
            try {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationDao.update(reservation);

                List<ReservationEntity> active =
                        reservationDao.getActiveReservationsForDesk(reservation.getDeskId());

                if (active.isEmpty()) {
                    DeskEntity desk = deskDao.getDeskById(reservation.getDeskId());
                    if (desk != null) {
                        desk.setOccupied(false);
                        desk.setStatus(DeskStatus.AVAILABLE);
                        deskDao.update(desk);
                    }
                }

                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public LiveData<List<ReservationEntity>> getAllReservations() {
        return reservationDao.getAllReservations();
    }

    public void freeDesk(int userId, int deskId, ResultCallback callback) {
        executor.execute(() -> {
            try {
                ReservationEntity reservation =
                        reservationDao.getActiveReservationForUserOnDesk(deskId, userId);

                if (reservation == null) {
                    if (callback != null) callback.onError("You have no active reservation for this desk.");
                    return;
                }

                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationDao.update(reservation);

                List<ReservationEntity> remaining =
                        reservationDao.getActiveReservationsForDesk(deskId);

                if (remaining.isEmpty()) {
                    DeskEntity desk = deskDao.getDeskById(deskId);
                    if (desk != null) {
                        desk.setOccupied(false);
                        desk.setStatus(DeskStatus.AVAILABLE);
                        deskDao.update(desk);
                    }
                }

                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public LiveData<List<ReservationEntity>> getReservationsByUser(int userId) {
        return reservationDao.getReservationsByUser(userId);
    }

    public void hasActiveReservation(int userId, int deskId, BooleanCallback callback) {
        executor.execute(() -> {
            ReservationEntity r =
                    reservationDao.getActiveReservationForUserOnDesk(deskId, userId);
            if (callback != null) callback.onResult(r != null);
        });
    }

    public void countActiveForUser(int userId, IntCallback callback) {
        executor.execute(() -> {
            int count = reservationDao.countActiveForUser(userId);
            if (callback != null) callback.onResult(count);
        });
    }

    public void expireReservations() {
        executor.execute(() -> {
            long nowMidnight = todayMidnight();
            List<ReservationEntity> expired =
                    reservationDao.getExpiredActiveReservations(nowMidnight);
            for (ReservationEntity r : expired) {
                r.setStatus(ReservationStatus.CANCELLED);
                reservationDao.update(r);

                List<ReservationEntity> stillActive =
                        reservationDao.getActiveReservationsForDesk(r.getDeskId());
                if (stillActive.isEmpty()) {
                    DeskEntity desk = deskDao.getDeskById(r.getDeskId());
                    if (desk != null) {
                        desk.setOccupied(false);
                        desk.setStatus(DeskStatus.AVAILABLE);
                        deskDao.update(desk);
                    }
                }
            }
        });
    }

    private static long todayMidnight() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public interface ResultCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface BooleanCallback {
        void onResult(boolean value);
    }

    public interface IntCallback {
        void onResult(int value);
    }
}
