package com.deskmanager.app.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.deskmanager.app.domain.enums.DeskStatus;

@Entity(
    tableName = "desks",
    indices = { @Index("floor") }
)
public class DeskEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "room_number")
    private String roomNumber;

    @ColumnInfo(name = "floor")
    private int floor;

    @ColumnInfo(name = "is_occupied")
    private boolean isOccupied;

    @ColumnInfo(name = "status")
    private DeskStatus status;

    public DeskEntity() {}

    @Ignore
    public DeskEntity(String roomNumber, int floor, boolean isOccupied, DeskStatus status) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.isOccupied = isOccupied;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }

    public DeskStatus getStatus() { return status; }
    public void setStatus(DeskStatus status) { this.status = status; }
}
