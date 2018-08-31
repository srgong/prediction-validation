package model;

public class Comparison {
    int hour;
    int start;
    int end;
    double avgError;
    int numComparisons=0;
    double newError=0;

    public Comparison(Stock actual, Stock predicted) {
        this.start = actual.getHour();
        this.end = predicted.getHour();
        this.hour = actual.getHour();
        this.avgError = updateAvgError(actual.getPrice(), predicted.getPrice());
    }
    
    public int getHour() {
        return this.hour;
    }

    public double updateAvgError(double actual, double predicted) {
        double sumError = numComparisons * avgError;
        double newError = Math.abs(actual-predicted);
        return (newError + sumError) / ++numComparisons;
    }

    public void setAvgError(double actual, double predicted) {
        double sumError = numComparisons * avgError;
        newError = Math.abs(actual-predicted);
        avgError = (newError + sumError) / ++numComparisons;
    }
    
    public double getNewAvgError() {
        return round(this.newError/2, 2);
    }

    public double getAvgError() {
        return avgError;
    }

    public int getNumComparisons() {
        return numComparisons;
    }

    private static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    @Override
    public int hashCode() {
        return start * 31 + end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final Comparison other = (Comparison) obj;
        return other.start==this.start && other.end==this.end;
    }

    @Override
    public String toString() {
        return start + "|" + end + "|" + round(avgError, 4);
    }
}
