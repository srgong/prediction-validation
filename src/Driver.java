import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import model.Row;
import model.Stock;
import model.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Boundary {
    int start;
    int end;
    double avgError=0;
    int numComparisons=0;

    Boundary(Stock actual, Stock predicted) {
        this.start = actual.getHour();
        this.end = predicted.getHour();
        this.avgError = updateAvgError(actual.getPrice(), predicted.getPrice());
    }

    Boundary(int start, int end) {
        this.start = start;
        this.end = end;
        this.avgError = 0;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public double updateAvgError(double actual, double predicted) {
        double sumError = numComparisons * avgError;
        double newError = Math.abs(actual-predicted);
        return (newError + sumError) / ++numComparisons;
    }

    public void setAvgError(double actual, double predicted) {
        double sumError = numComparisons * avgError;
        double newError = Math.abs(actual-predicted);
        avgError = (newError + sumError) / ++numComparisons;
    }

    public double getAvgError() {
        return avgError;
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

        final Boundary other = (Boundary) obj;
        return other.start==this.start && other.end==this.end;
    }
}

class Driver {

    public static void main(String args[]) {
//        if(args.length != 2) {
//            System.err.println("Usage:\n    " +
//                    "java Driver.java <input dir> <output dir>");
//            System.exit(1);
//        }
//        String inputDir = args[0];
//        String outputDir = args[1];
//
//        List<Stock> actual =new ArrayList<Stock>;
//        List<Stock> predicted =new ArrayList<Stock>;
//        List<Stock> all;
//
//        try (Stream<Path> paths = Files.walk(Paths.get(inputDir))) {
//            List<File> inputFiles = paths.filter(Files::isRegularFile)
//                    .map(Path::toFile)
//                    .collect(Collectors.toList());
//
//            BufferedReader in;
//            String line;
//            Stock s;
//            for(File file : inputFiles) {
//                in = new BufferedReader(new FileReader(file));
//
//                while((line = in.readLine()) != null) {
//                    s = new Stock(line.split("|"));
//                    if(file.getName().contains("actual")) {
//                        actual.add(s);
//                    } else if(file.getName().contains("predicted")) {
//                        predicted.add(s);
//                    }
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




        if(args.length != 4) {
            System.err.println("Usage:\n    " +
                    "java Driver.java <actual_file> <predicted_file> <window_file> <comparison_file>");
            System.exit(1);
        }

        String actualPath= args[0];
        String predictedPath= args[1];
        String windowPath = args[2];
        String comparisonFile = args[3];

//        List<Row> actual ;
//        List<Row> predicted ;
//        List<Row> window ;
//        List<Row> comparison;
        List<Row> unmatchedActual = new ArrayList<>();
        List<Row> unmatchedPredicted = new ArrayList<>();
        List<Row> matchedActual = new ArrayList<>();
        List<Row> matchedPredicted = new ArrayList<>();
        List<Row> unmatched = new ArrayList<>();

        try {
//            File [] inputDir = new File [] {new File(actualPath), new File(predictedPath)};
//            actual = serializeFile("actual", new BufferedReader(new FileReader(actualPath)), new ArrayList<>());
//            predicted = serializeFile("predicted", new BufferedReader(new FileReader(predictedPath)), new ArrayList<>());
//            window = serializeFile("window", new BufferedReader(new FileReader(windowPath)), new ArrayList<>());

            String windowLine;
            String actualLine;
            String predictedLine;
            BufferedReader windowIn = new BufferedReader(new FileReader(windowPath));
            BufferedReader actualIn = new BufferedReader(new FileReader(actualPath));
            BufferedReader predictedIn = new BufferedReader(new FileReader(predictedPath));

            Stock actual;
            Stock predicted;
            Boundary b;
//            Set<Boundary> avgErrors = new HashSet<>();
            List<Boundary> avgErrors = new ArrayList<>();

            while((windowLine = windowIn.readLine()) != null) {
                System.out.println("working with window: " + windowLine);
                while((actualLine = actualIn.readLine()) != null) {
                    actual = new Stock(actualLine.split("[|]"));
                    unmatchedActual.add(actual);
                    while ((predictedLine = predictedIn.readLine()) != null) {
                        predicted = new Stock(predictedLine.split("[|]"));
                        unmatchedPredicted.add(predicted);

                        // done processing this hour
                        if(predicted.getHour() != actual.getHour() && unmatched.size() == 0) {
                            unmatchedActual.clear();
                            unmatchedPredicted.clear();
                            continue;
                        }
                        // check that stocks are equal
                        else if(actual.getStock().equals(predicted.getStock())) {
                            b = new Boundary(actual, predicted);
                            if(avgErrors.contains(b)) {
                                avgErrors.get(avgErrors.indexOf(b)).setAvgError(actual.getPrice(), predicted.getPrice());
                            } else {
                                avgErrors.add(b);
                            }
                            unmatched.remove(predicted);
                        }
                        else {
                            // don't forget to check previous unmatched actuals
                            for (Row a : unmatchedActual) {
                                for (Row p : unmatchedPredicted) {
                                    if (a.getStock().equals(p.getStock())) {
                                        b = new Boundary((Stock) a, (Stock) p);
                                        if (avgErrors.contains(b)) {
                                            avgErrors.get(avgErrors.indexOf(b)).setAvgError(a.getPrice(), p.getPrice());
                                        } else {
                                            avgErrors.add(b);
                                        }
                                        matchedPredicted.add(predicted);
                                        matchedActual.add(actual);
                                        break;
                                    }
                                }
                            }
                            unmatchedActual.removeAll(matchedActual);
                            unmatchedPredicted.removeAll(matchedPredicted);
                        }
                    }

                }
            }



//
//            Set<Integer> actualStarts = new HashSet<>();
//            Set<Integer> predictedStarts= new HashSet<>();
//            for(Row a: actual) {
//                actualStarts.add(((Stock) a).getHour());
//            }
//
//            for(Row p: predicted) {
//                predictedStarts.add(((Stock) p).getHour());
//            }
//
//            Boundary b;
//            int i = 0;
//            for(Row w: window) {
//                System.out.println(((Window) w).getInterval());
////                for(int aStart: actualStarts) {
//                for( Row a : actual) {
//                    int aStart = ((Stock) a).getHour();
//                    for(Row p: predicted) {
//                        i++;
//                        int pStart = ((Stock) p).getHour();
////                    for(int pStart: predictedStarts) {
//                        if(pStart >= aStart && pStart <= aStart + ((Window) w).getInterval()-1 ) {
//                            double aPrice = a.getPrice();
//                            double pPrice = p.getPrice();
//                            b = new Boundary(aStart, pStart);
//                            b.updateAvgError(aPrice, pPrice);
//                        }
//                    }
//                }
//            }
//
//
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<Row> serializeFile (String fileType, BufferedReader in, ArrayList<Row> arList) {
        String line;
        try {
            if(!fileType.equals("window")) {
                while((line = in.readLine()) != null) {
                    arList.add(new Stock(line.split("[|]")));
//                    System.out.println(line);
                }
            } else {
                while((line = in.readLine()) != null) {
                    arList.add(new Window(line));
//                    System.out.println(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return arList;
    }

}
