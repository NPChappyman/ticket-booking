package com.example.ticketbooking.exception;

/** Бизнес-конфликт: например, место уже занято или время сеанса пересекается. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
