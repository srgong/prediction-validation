package model;

/**
 * Created by Sharon on 8/26/18.
 **/

public class Window extends Row {

    int interval;
    double error;

    public Window(String interval) {
        this.interval = Integer.parseInt(interval);
    }

    public Window(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    double getError() {
        return error;
    }

    void setError(double error) {
        this.error = error;
    }
}
