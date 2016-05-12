package ru.imunit.maquizdb;

/**
 * Created by smirnov on 29.09.2015.
 */
public class DBTrack {

    private String name;
    private String artist;
    private String uri;
    private short isRemote;
    private long guess;
    private long correctGuess;
    private  short isBlacklisted;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public short isRemote() {
        return isRemote;
    }

    public void setIsRemote(short isRemote) {
        this.isRemote = isRemote;
    }

    public long getGuess() {
        return guess;
    }

    public void setGuess(long guess) {
        this.guess = guess;
    }

    public long getCorrectGuess() {
        return correctGuess;
    }

    public void setCorrectGuess(long correctGuess) {
        this.correctGuess = correctGuess;
    }

    public short getIsBlacklisted() {
        return isBlacklisted;
    }

    public void setIsBlacklisted(short isBlacklisted) {
        this.isBlacklisted = isBlacklisted;
    }

}
