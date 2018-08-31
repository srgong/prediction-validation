package model;

/**
 * Created by Sharon on 8/26/18.
 * An object to calculate rolling average and store aggregates
 **/

public class Window{
    int start;
    int end;
    double errorSum;
    int numStocks;
    double avgError;
    

    public Window(int start, int end, double errorSum, int numStocks) {
        this.end = end;
        this.start = start;
        this.numStocks = numStocks;
        this.errorSum = errorSum;
        this.avgError = getAvgError();
    }

    public double getErrorSum() {
        return errorSum;
    }

    public void setErrorSum(double errorSum) {
        this.errorSum = errorSum;
    }

    public double getAvgError() {
        return this.avgError = errorSum/numStocks;
    }

    private static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    @Override
    public String toString() {
        return start + "|" + end + "|" + round(avgError, 2);
    }
}
