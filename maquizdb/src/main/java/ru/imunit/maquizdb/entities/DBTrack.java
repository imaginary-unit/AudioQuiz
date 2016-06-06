package ru.imunit.maquizdb.entities;

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
    private short isBlacklisted;

    public DBTrack() {
        this("", "", "");
    }

    public DBTrack(String name, String artist, String uri) {
        this.name = name;
        this.artist = artist;
        this.uri = uri;
        this.isRemote = 0;
        this.guess = 0;
        this.correctGuess = 0;
        this.isBlacklisted = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DBTrack other = (DBTrack)obj;
        if (!name.equals(other.getName()) |
            !artist.equals(other.getArtist()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + artist.hashCode();
        return result;
    }

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
