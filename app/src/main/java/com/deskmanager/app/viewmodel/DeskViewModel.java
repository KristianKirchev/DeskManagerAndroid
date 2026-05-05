package com.deskmanager.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.data.repository.DeskRepository;
import com.deskmanager.app.domain.enums.DeskStatus;

import java.util.List;

public class DeskViewModel extends AndroidViewModel {

    private final DeskRepository deskRepository;

    private final LiveData<List<DeskEntity>> allDesks;
    private final MutableLiveData<DeskEntity> selectedDesk = new MutableLiveData<>();
    private final MutableLiveData<String> operationResult  = new MutableLiveData<>();

    public DeskViewModel(@NonNull Application application) {
        super(application);
        deskRepository = new DeskRepository(application);
        allDesks = deskRepository.getAllDesks();
    }

    public void addDesk(DeskEntity desk) {
        deskRepository.insert(desk, new DeskRepository.ResultCallback() {
            @Override public void onSuccess()              { operationResult.postValue("Desk added successfully."); }
            @Override public void onError(String message)  { operationResult.postValue("ERROR:" + message); }
        });
    }

    public void updateDesk(DeskEntity desk) {
        deskRepository.update(desk, new DeskRepository.ResultCallback() {
            @Override public void onSuccess()              { operationResult.postValue("Desk updated successfully."); }
            @Override public void onError(String message)  { operationResult.postValue("ERROR:" + message); }
        });
    }

    public void deleteDesk(DeskEntity desk) {
        deskRepository.delete(desk, new DeskRepository.ResultCallback() {
            @Override public void onSuccess()              { operationResult.postValue("Desk deleted successfully."); }
            @Override public void onError(String message)  { operationResult.postValue("ERROR:" + message); }
        });
    }

    public void selectDesk(int deskId) {
        deskRepository.getDeskById(deskId, new DeskRepository.GetDeskCallback() {
            @Override public void onSuccess(DeskEntity desk) { selectedDesk.postValue(desk); }
            @Override public void onError(String message)    { operationResult.postValue("ERROR:" + message); }
        });
    }

    public LiveData<List<DeskEntity>> getDesksByStatus(DeskStatus status) {
        return deskRepository.getDesksByStatus(status);
    }

    public LiveData<List<DeskEntity>> searchDesks(String query) {
        return deskRepository.searchDesks(query);
    }

    public LiveData<List<DeskEntity>> getDesksForFloor(int floor) {
        return deskRepository.getDesksForFloor(floor);
    }

    public void getDistinctFloors(DeskRepository.FloorsCallback callback) {
        deskRepository.getDistinctFloors(callback);
    }

    public void getDeskById(int id, DeskRepository.GetDeskCallback callback) {
        deskRepository.getDeskById(id, callback);
    }

    public LiveData<List<DeskEntity>> getAllDesks()     { return allDesks; }
    public LiveData<DeskEntity> getSelectedDesk()       { return selectedDesk; }
    public LiveData<String> getOperationResult()        { return operationResult; }
    public void clearOperationResult()                  { operationResult.setValue(null); }
}
