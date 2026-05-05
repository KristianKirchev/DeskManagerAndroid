package com.deskmanager.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.data.repository.ReservationRepository;

import java.util.List;

public class ReservationViewModel extends AndroidViewModel {

    private final ReservationRepository reservationRepository;

    private final LiveData<List<ReservationEntity>> allReservations;
    private final MutableLiveData<String> operationResult = new MutableLiveData<>();

    public ReservationViewModel(@NonNull Application application) {
        super(application);
        reservationRepository = new ReservationRepository(application);
        allReservations = reservationRepository.getAllReservations();
    }

    public void createReservation(ReservationEntity reservation) {
        reservationRepository.createReservation(reservation,
                new ReservationRepository.ResultCallback() {
                    @Override public void onSuccess()              { operationResult.postValue("Reservation created successfully!"); }
                    @Override public void onError(String message)  { operationResult.postValue("ERROR:" + message); }
                });
    }

    public void cancelReservation(ReservationEntity reservation) {
        reservationRepository.cancelReservation(reservation,
                new ReservationRepository.ResultCallback() {
                    @Override public void onSuccess()              { operationResult.postValue("Reservation cancelled."); }
                    @Override public void onError(String message)  { operationResult.postValue("ERROR:" + message); }
                });
    }

    public void freeDesk(int userId, int deskId) {
        reservationRepository.freeDesk(userId, deskId,
                new ReservationRepository.ResultCallback() {
                    @Override public void onSuccess()              { operationResult.postValue("Desk freed successfully."); }
                    @Override public void onError(String message)  { operationResult.postValue("ERROR:" + message); }
                });
    }

    public LiveData<List<ReservationEntity>> getAllReservations() {
        return allReservations;
    }

    public LiveData<List<ReservationEntity>> getReservationsByUser(int userId) {
        return reservationRepository.getReservationsByUser(userId);
    }

    public void hasActiveReservation(int userId, int deskId,
                                     ReservationRepository.BooleanCallback callback) {
        reservationRepository.hasActiveReservation(userId, deskId, callback);
    }

    public void countActiveForUser(int userId, ReservationRepository.IntCallback callback) {
        reservationRepository.countActiveForUser(userId, callback);
    }

    public LiveData<String> getOperationResult() { return operationResult; }

    public void clearOperationResult() { operationResult.setValue(null); }
}
