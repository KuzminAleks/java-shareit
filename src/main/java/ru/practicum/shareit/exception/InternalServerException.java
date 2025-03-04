package ru.practicum.shareit.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String mes) {
        super(mes);
    }
}
