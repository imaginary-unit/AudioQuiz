package ru.imunit.maquiz.exceptions;

/**
 * Created by lemoist on 14.07.16.
 */

public class NoMusicException extends RuntimeException {

    public NoMusicException() {

    }

    public NoMusicException(String error) {
        super(error);
    }

    public NoMusicException(String error, Throwable cause) {
        super(error, cause);
    }

}
