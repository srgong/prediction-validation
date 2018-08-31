package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharon on 8/24/18.
 * Reads in 3 files, actual, predicted, and window.
 * Actual and predicted are streamed in on an hourly batch period. 
 * Once both are read in, hourly average errors are staged in a Comparison aggregate object.
 * Finally, an average error is calculated based on a rolling window period. 
 * If stocks do not exist at every hour stated in the period, the average will represent stocks that do exist in it.
 */
public class PredictionValidation {
    List<Comparison> avgErrors = new ArrayList<>();
    List<Stock> unmatchedActual = new ArrayList<>();
    List<Stock> unmatchedPredicted = new ArrayList<>();

    /**
     * Predicted files are read first. New or 'unmatched' predicted stocks are stored.
     * Actual files follow this same process.
     * Once completed, a list of Comparison objects act as an aggregate environment before the rolling window is applied.
     * @param actualPath
     * @param predictedPath
     * @throws IOException
     */
    public void calculateComparisons(String actualPath, String predictedPath) throws IOException{
        
        try (BufferedReader actualIn = new BufferedReader(new FileReader(actualPath)); 
             BufferedReader predictedIn = new BufferedReader(new FileReader(predictedPath))
        ) {
            String predictedLine;
            Stock predicted, initPredicted, initActual = new Stock();
            int c = 1;

            initPredicted = new Stock(predictedIn.readLine().split("[|]"));
            unmatchedPredicted.add(initPredicted);

            while ((predictedLine =  predictedIn.readLine()) != null) {
                predicted = new Stock(((char) c + predictedLine).split("[|]"));
                unmatchedPredicted.add(predicted);

                // reached beginning of new batch! process actual's batch now
                if (initPredicted.getHour() != predicted.getHour()) {
                    initPredicted = predicted;

                    initActual = new Stock(actualIn.readLine().split("[|]"));
                    unmatchedActual.add(initActual);

                    unmatchedActual = processActualHourlyBatch(actualIn, unmatchedActual, initActual);
                    initActual = unmatchedActual.get(unmatchedActual.size() - 1);
                    processAllHourlyBatches(unmatchedActual, unmatchedPredicted);
                }

                // reached end of file, last actual batch to process!
                if ((c = predictedIn.read()) == -1) {
                    unmatchedActual = processActualHourlyBatch(actualIn, unmatchedActual, initActual);
                    processAllHourlyBatches(unmatchedActual, unmatchedPredicted);
                }
            }
        } catch (IOException e) {
            throw new IOException();
        }
        
    }

    /**
     *  Process single hourly batch. New or 'unmatched' stocks are stored.
     * @param actualIn
     * @param unmatchedActual
     * @param initActual
     * @return
     * @throws IOException
     */
    public List<Stock> processActualHourlyBatch(BufferedReader actualIn,List<Stock> unmatchedActual, Stock initActual) throws IOException {
        
        String actualLine;
        Stock actual;

        while ((actualLine = actualIn.readLine()) != null) {
            actual = new Stock(actualLine.split("[|]"));
            unmatchedActual.add(actual);
            //reached beginning of new batch!
            if (initActual.getHour() != actual.getHour()) {
                break;
            }
        }
        return unmatchedActual;
        
    }

    /**
     * Matches all unmatched predicted to actual stocks by hour and stock ticker.
     * Successful matches are saved in a list of Comparisons.
     * @param unmatchedPredicted
     * @param unmatchedActual
     */
    public void processAllHourlyBatches(List<Stock> unmatchedPredicted, List<Stock> unmatchedActual) {
        
        Comparison c;
        List<Stock> matchedActual = new ArrayList<>();
        List<Stock> matchedPredicted = new ArrayList<>();

        for (Stock p : unmatchedPredicted) {
            for (Stock a : unmatchedActual) {
                if (p.equals(a)) {
                    c = new Comparison(a, p);
                    if (avgErrors.contains(c)) {
                        avgErrors.get(avgErrors.indexOf(c)).setAvgError(a.getPrice(), p.getPrice());
                    } else {
                        avgErrors.add(c);
                    }
//                    System.out.println(a.getHour() + " " + a + " | " + p + " | " + avgErrors.get(avgErrors.indexOf(b)).getNewAvgError());
                    matchedActual.add(a);
                    matchedPredicted.add(p);
                    break;
                }
            }
        }
        unmatchedActual.removeAll(matchedActual);
        unmatchedPredicted.removeAll(matchedPredicted);
        
    }

    /**
     * Iterates through matches and writes final error aggregation over a period of time specified by Window file
     * @param windowFile
     * @param outputFile
     */
    public void calculateRollingWindowErrors(String windowFile, String outputFile) {
        
        int start, end, numStocks;
        double errorSum;
        Window w;
        Comparison b;
        String windowLine;

        try(BufferedReader windowIn = new BufferedReader(new FileReader(windowFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            while((windowLine = windowIn.readLine()) != null) {
//                System.out.println("working with window: " + windowLine);
                for(int i = 0; i < avgErrors.size(); i++) {
                    start = i;
                    end = start + Integer.parseInt(windowLine)-1;
                    errorSum = 0;
                    numStocks = 0;
                    for(int j=start; (j<=end && j<=avgErrors.size()-1); j++) {
                        b = avgErrors.get(j);
                        errorSum += (b.getAvgError() * b.getNumComparisons());
                        numStocks += b.getNumComparisons();
                    }
                    w = new Window(start+1, end+1, errorSum, numStocks);
                    System.out.println(w);
                }
            }
            bw.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}
