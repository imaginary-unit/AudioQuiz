package ru.imunit.maquizdb;

/**
 * Created by imunit on 10.10.15.
 */
public class DBGame {

    private long id;
    private long score;
    private long avgGuessTime;
    private long bestGuessTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getAvgGuessTime() {
        return avgGuessTime;
    }

    public void setAvgGuessTime(long avgGuessTime) {
        this.avgGuessTime = avgGuessTime;
    }

    public long getBestGuessTime() {
        return bestGuessTime;
    }

    public void setBestGuessTime(long bestGuessTime) {
        this.bestGuessTime = bestGuessTime;
    }

}
