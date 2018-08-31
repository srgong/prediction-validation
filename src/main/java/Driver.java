import model.PredictionValidation;
import java.io.*;

class Driver {
    /**
     * checks inputs exist and is a file
     * @param f
     * @throws FileNotFoundException
     */
    public static void checkInputFiles(File f) throws FileNotFoundException {
        if(!f.exists()) throw new FileNotFoundException();
        if(f.isDirectory()) throw new FileNotFoundException();
    }

    /**
     * creates output file if it does not already exist
     * @param f
     * @throws IOException
     */
    public static void checkOutputFile(File f) throws IOException {
        f.createNewFile();
    }
    
    public static void main(String args[]) {
        
        if(args.length != 4) {
            System.err.println("Usage:\n    " +
                    "java Driver.java <actual_file> <predicted_file> <window_file> <comparison_file>");
            System.exit(1);
        }

        String actualPath= args[0];
        String predictedPath= args[1];
        String windowPath = args[2];
        String comparisonPath = args[3];

        try {
            
            File [] inputDir = new File [] {new File(actualPath), new File(predictedPath), new File(windowPath), new File(comparisonPath)};
            for(int i = 0; i < 2; i++) {
                checkInputFiles(inputDir[i]);
            }
            checkOutputFile(inputDir[3]);
            
            PredictionValidation pv = new PredictionValidation();
            pv.calculateComparisons(actualPath, predictedPath);
            pv.calculateRollingWindowErrors(windowPath, comparisonPath);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
