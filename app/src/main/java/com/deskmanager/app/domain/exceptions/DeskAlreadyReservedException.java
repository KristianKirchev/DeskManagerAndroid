package com.deskmanager.app.domain.exceptions;

public class DeskAlreadyReservedException extends Exception {

    public DeskAlreadyReservedException(int deskId) {
        super("Desk #" + deskId + " is already reserved for the selected period.");
    }
}
