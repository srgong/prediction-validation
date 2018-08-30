import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Sharon on 8/24/18.
 */
public class PredictionValidation {
    BufferedReader b;
    String actual;
    String predicted;
    String output;

    PredictionValidation(BufferedReader b, String actual, String predicted, String output){
     this.b = b;
     this.actual = actual;
     this.predicted = predicted;
     this.output = output;
    }

    void read() {
        String readLine="";
        try {


            while ((readLine = b.readLine()) != null) {
                System.out.println(readLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    left off: think about best way to read 2 files at once



}
