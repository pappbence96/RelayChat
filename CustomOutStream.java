import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Saját kimeneti stream, a rá küldött adatokat egy JTextArea-ra írja
 */
public class CustomOutStream extends OutputStream {
    private JTextArea jta;

    /**
     * Konstruktor
     * @param _jta a kimeneti JTextArea
     */
    public CustomOutStream(JTextArea _jta){
        jta = _jta;
    }

    @Override
    public void write(int b) throws IOException {
        jta.append(String.valueOf((char)b));
        jta.setCaretPosition(jta.getDocument().getLength());
    }

    public void write(char[] buf, int off, int len) {
        String s = new String(buf, off, len);
        jta.append(s);
    }
}
