package ru.imunit.maquizdb.entities;

/**
 * Created by imunit on 10.10.15.
 */
public class DBGame {

    private long id;
    private long score;
    private long avgGuessTime;
    private long bestGuessTime;
    private long guess;
    private long correct_guess;

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

    public long getGuess() {
        return guess;
    }

    public void setGuess(long guess) {
        this.guess = guess;
    }

    public long getCorrectGuess() {
        return correct_guess;
    }

    public void setCorrectGuess(long correct_guess) {
        this.correct_guess = correct_guess;
    }

}
