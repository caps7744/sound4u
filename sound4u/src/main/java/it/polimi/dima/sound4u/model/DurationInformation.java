package it.polimi.dima.sound4u.model;

/**
 * Created by canidio-andrea on 25/02/14.
 */
public class DurationInformation {
    private int totalMillisDuration;
    private int currentMillisDuration;

    public DurationInformation(int totalMillisDuration, int currentMillisDuration) {
        this.totalMillisDuration = totalMillisDuration;
        this.currentMillisDuration = currentMillisDuration;
    }

    public int getTotalMillisDuration() {
        return totalMillisDuration;
    }

    public int getCurrentMillisDuration() {
        return currentMillisDuration;
    }
}
