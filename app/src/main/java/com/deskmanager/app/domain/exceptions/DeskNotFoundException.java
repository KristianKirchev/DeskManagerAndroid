package com.deskmanager.app.domain.exceptions;

public class DeskNotFoundException extends Exception {

    public DeskNotFoundException(int deskId) {
        super("Desk #" + deskId + " was not found.");
    }
}
