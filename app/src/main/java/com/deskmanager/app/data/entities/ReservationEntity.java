package com.deskmanager.app.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.deskmanager.app.domain.enums.ReservationStatus;

@Entity(
    tableName = "reservations",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "user_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = DeskEntity.class,
            parentColumns = "id",
            childColumns = "desk_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("user_id"),
        @Index(value = {"desk_id", "user_id", "status"})
    }
)
public class ReservationEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "desk_id")
    private int deskId;

    @ColumnInfo(name = "start_date")
    private long startDate;

    @ColumnInfo(name = "end_date")
    private long endDate;

    @ColumnInfo(name = "status")
    private ReservationStatus status;

    public ReservationEntity() {}

    @Ignore
    public ReservationEntity(int userId, int deskId, long startDate, long endDate, ReservationStatus status) {
        this.userId = userId;
        this.deskId = deskId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDeskId() { return deskId; }
    public void setDeskId(int deskId) { this.deskId = deskId; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
