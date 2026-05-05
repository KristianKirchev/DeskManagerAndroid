package com.deskmanager.app.utils;

import android.content.Context;

import com.deskmanager.app.R;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.domain.enums.UserRole;

public class DisplayUtils {

    public static String deskStatus(Context ctx, DeskStatus status) {
        switch (status) {
            case AVAILABLE: return ctx.getString(R.string.status_available);
            case OCCUPIED:  return ctx.getString(R.string.status_occupied);
            default:        return status.name();
        }
    }

    public static String reservationStatus(Context ctx, ReservationStatus status) {
        switch (status) {
            case ACTIVE:    return ctx.getString(R.string.status_active);
            case CANCELLED: return ctx.getString(R.string.status_cancelled);
            default:        return status.name();
        }
    }

    public static String userRole(Context ctx, UserRole role) {
        switch (role) {
            case ADMIN: return ctx.getString(R.string.role_admin);
            case USER:  return ctx.getString(R.string.role_user);
            case GUEST: return ctx.getString(R.string.role_guest);
            default:    return role.name();
        }
    }
}
