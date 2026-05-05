package com.deskmanager.app.data.database;

import androidx.room.TypeConverter;

import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.domain.enums.UserRole;

public class Converters {

    @TypeConverter
    public static String fromUserRole(UserRole role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static UserRole toUserRole(String value) {
        return value == null ? null : UserRole.valueOf(value);
    }

    @TypeConverter
    public static String fromDeskStatus(DeskStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static DeskStatus toDeskStatus(String value) {
        return value == null ? null : DeskStatus.valueOf(value);
    }

    @TypeConverter
    public static String fromReservationStatus(ReservationStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static ReservationStatus toReservationStatus(String value) {
        return value == null ? null : ReservationStatus.valueOf(value);
    }
}
