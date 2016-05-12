package ru.imunit.maquiz.playlists;

/**
 * Created by imunit on 26.10.15.
 */
public class AudioDirectory {

    private String name;
    private int tracksCount;
    private int dirsCount;
    private int state;

    public AudioDirectory() {

    }

    public AudioDirectory(String name, int tracksCnt, int dirsCnt, int state) {
        this.name = name;
        this.tracksCount = tracksCnt;
        this.dirsCount = dirsCnt;
        this.state = state;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTracksCount() {
        return tracksCount;
    }

    public void setTracksCount(int tracksCount) {
        this.tracksCount = tracksCount;
    }

    public int getDirsCount() {
        return dirsCount;
    }

    public void setDirsCount(int dirsCount) {
        this.dirsCount = dirsCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
