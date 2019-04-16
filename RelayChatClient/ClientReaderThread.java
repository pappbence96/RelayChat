package RelayChatClient;

import java.io.*;
import java.net.Socket;

/**
 * Kliens socket olvasó szál. Feladata a szerver üzeneteinek fogadása, és átadása a kliensnek
 */
public class ClientReaderThread implements Runnable {
    private Socket socket;
    private Client client;
    OutputStream outStream;

    /**
     * Kliens socket olvasó konstruktor
     * @param s olvasandó socket
     * @param c kliens, amihez tartozik
     * @param os kimeneti stream, nem használja
     */
    public ClientReaderThread(Socket s, Client c, OutputStream os){
        socket = s;
        client = c;
        outStream = os;
    }

    /**
     * Kezeli az indulást és leállást
     */
    @Override
    public void run() {
        ReadFromServer();
        //Handle lost connection: notify the client
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.SetConnected(false);
    }

    /**
     * Olvas a socketről, majd átadja a kliensnek az üzenetet
     */
    private void ReadFromServer(){
        //Initialize reader
        BufferedReader serverStream;
        try {
            serverStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            //On failure, disconnect client
            System.out.println("ClientReaderThread failed to open stream from server.");
            client.SetConnected(false);
            //e.printStackTrace();
            return;
        }

        //Read lines until main thread ends or connection terminates
        String line;
        while(client.IsConnected()){
            try {
                line = serverStream.readLine();
            } catch (IOException e) {
                System.out.println("ClientReaderThread lost connection to the server.");
                client.SetConnected(false);
                //e.printStackTrace();
                return;
            }
            //Pass received message back to the client
            client.ProcessMessage(line);
        }
    }
}
