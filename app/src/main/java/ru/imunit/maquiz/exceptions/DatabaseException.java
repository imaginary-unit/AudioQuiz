package ru.imunit.maquiz.exceptions;

/**
 * Created by theuser on 13.07.16.
 */

public class DatabaseException extends RuntimeException {

    public DatabaseException() {

    }

    public DatabaseException(String error) {
        super(error);
    }

    public DatabaseException(String error, Throwable cause) {
        super(error, cause);
    }
}
