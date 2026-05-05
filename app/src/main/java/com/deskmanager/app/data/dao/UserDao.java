package com.deskmanager.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.deskmanager.app.data.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getUserById(int id);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    UserEntity login(String username, String password);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countByUsername(String username);
}
