package com.deskmanager.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.deskmanager.app.data.entities.ReservationEntity;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    long insert(ReservationEntity reservation);

    @Update
    void update(ReservationEntity reservation);

    @Delete
    void delete(ReservationEntity reservation);

    @Query("SELECT * FROM reservations ORDER BY start_date DESC")
    LiveData<List<ReservationEntity>> getAllReservations();

    @Query("SELECT * FROM reservations WHERE user_id = :userId ORDER BY start_date DESC")
    LiveData<List<ReservationEntity>> getReservationsByUser(int userId);

    @Query("SELECT * FROM reservations WHERE id = :id LIMIT 1")
    ReservationEntity getReservationById(int id);

    @Query("SELECT COUNT(*) FROM reservations " +
           "WHERE desk_id = :deskId " +
           "AND status = 'ACTIVE' " +
           "AND start_date < :endDate " +
           "AND end_date > :startDate")
    int countOverlapping(int deskId, long startDate, long endDate);

    @Query("SELECT * FROM reservations WHERE desk_id = :deskId AND status = 'ACTIVE' ORDER BY start_date DESC")
    List<ReservationEntity> getActiveReservationsForDesk(int deskId);

    @Query("SELECT * FROM reservations WHERE desk_id = :deskId AND user_id = :userId AND status = 'ACTIVE' LIMIT 1")
    ReservationEntity getActiveReservationForUserOnDesk(int deskId, int userId);

    @Query("SELECT COUNT(*) FROM reservations WHERE user_id = :userId AND status = 'ACTIVE'")
    int countActiveForUser(int userId);

    @Query("SELECT * FROM reservations WHERE user_id = :userId AND status = 'ACTIVE'")
    List<ReservationEntity> getActiveReservationsByUserSync(int userId);

    @Query("SELECT * FROM reservations WHERE status = 'ACTIVE' AND end_date < :nowMillis")
    List<ReservationEntity> getExpiredActiveReservations(long nowMillis);
}
