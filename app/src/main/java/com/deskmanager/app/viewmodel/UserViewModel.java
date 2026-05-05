package com.deskmanager.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.data.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<String> loginError  = new MutableLiveData<>();
    private final MutableLiveData<UserEntity> loggedInUser = new MutableLiveData<>();
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();
    private final LiveData<List<UserEntity>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    public void login(String username, String password) {
        userRepository.login(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(UserEntity user) {
                loggedInUser.postValue(user);
            }

            @Override
            public void onError(String message) {
                loginError.postValue(message);
            }
        });
    }

    public void register(UserEntity user) {
        userRepository.insert(user, new UserRepository.InsertCallback() {
            @Override
            public void onSuccess(int newId) {
                user.setId(newId);
                registerResult.postValue("SUCCESS");
            }

            @Override
            public void onError(String message) {
                registerResult.postValue("ERROR:" + message);
            }
        });
    }

    public void updateUser(UserEntity user) {
        userRepository.update(user);
    }

    public void appendPendingCancelNotif(int userId, String roomName) {
        userRepository.appendPendingCancelNotif(userId, roomName);
    }

    public void clearPendingCancelNotif(int userId) {
        userRepository.clearPendingCancelNotif(userId);
    }

    public void resetPassword(UserEntity user, String tempPassword, UserRepository.ResetPasswordCallback callback) {
        userRepository.resetPassword(user, tempPassword, callback);
    }

    public void getUserById(int userId, UserRepository.GetUserCallback callback) {
        userRepository.getUserById(userId, callback);
    }

    public void deleteUser(UserEntity user, UserRepository.DeleteCallback callback) {
        userRepository.deleteWithReservations(user, callback);
    }

    public LiveData<List<UserEntity>> getAllUsers()    { return allUsers; }
    public LiveData<String> getLoginError()           { return loginError; }
    public LiveData<UserEntity> getLoggedInUser()     { return loggedInUser; }
    public LiveData<String> getRegisterResult()       { return registerResult; }
}
