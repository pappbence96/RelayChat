package MyLogger;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Egyszerű naplózó osztály
 * Kezdetleges implementáció
 */
public class Logger {
    private PrintStream output;
    private Calendar c;

    /**
     * Naplózó konstruktora
     * @param ps kimeneti stream
     */
    public Logger(PrintStream ps){
        output = ps;
        c = Calendar.getInstance();
    }

    /**
     * Naplózza az adott típusú üzenetet
     * @param type üzenet típusa
     * @param message üzenet szövege
     */
    public void Log(String type, String message){
        //Format: "<date>\t|\t[<type>]\t|\t<text>
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss").format(c.getTime());
        output.println(timeStamp + "\t|\t[" + type +"]\t|\t"  + message);
    }
}
