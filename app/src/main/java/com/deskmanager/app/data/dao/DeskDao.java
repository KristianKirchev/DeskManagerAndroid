package com.deskmanager.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.deskmanager.app.data.entities.DeskEntity;

import java.util.List;

@Dao
public interface DeskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DeskEntity desk);

    @Update
    void update(DeskEntity desk);

    @Delete
    void delete(DeskEntity desk);

    @Query("SELECT * FROM desks ORDER BY floor ASC, room_number ASC")
    LiveData<List<DeskEntity>> getAllDesks();

    @Query("SELECT * FROM desks WHERE id = :id LIMIT 1")
    DeskEntity getDeskById(int id);

    @Query("SELECT * FROM desks WHERE status = :status ORDER BY floor ASC, room_number ASC")
    LiveData<List<DeskEntity>> getDesksByStatus(String status);

    @Query("SELECT * FROM desks WHERE room_number LIKE '%' || :query || '%' OR CAST(floor AS TEXT) LIKE '%' || :query || '%'")
    LiveData<List<DeskEntity>> searchDesks(String query);

    @Query("SELECT * FROM desks WHERE floor = :floor ORDER BY room_number ASC")
    LiveData<List<DeskEntity>> getDesksForFloor(int floor);

    @Query("SELECT DISTINCT floor FROM desks ORDER BY floor ASC")
    List<Integer> getDistinctFloorsSync();
}
