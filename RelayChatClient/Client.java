package RelayChatClient;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Chat kliens osztály
 */
public class Client {
    private Socket socket; //Socket for communication
    private PrintWriter connectionOutStream;
    private BufferedReader connectionInStream;
    private String name;
    private PrintStream logStream = System.out;
    private Thread wt;


    OutputStream outStream;
    PrintWriter outPrintWriter;

    private boolean connected;
    private boolean authenticated;

    /**
     * Kliens konstruktor. Beállítja az állapotváltozókat.
     * @param os kimeneti stream
     */
    public Client(OutputStream os) {
        System.out.println("Client started up.");
        //Init state variables & objects
        name = null;
        socket = null;
        connected = false;
        authenticated = false;
        outStream = os;
        outPrintWriter = new PrintWriter(os);
    }

    /**
     * Csatlakozik a megadott címen levő szerverhez
     * @param ipaddr a szerver IP-címe
     * @param port a szerver portszáma
     * @throws IOException kivétel keletkezhet
     */
    public void Connect(String ipaddr, int port) throws IOException {
        socket = new Socket(ipaddr, port);
        Log("Connected to server.");
        Log("Opening stream to server...");
        InitializeStreams(socket.getInputStream(), socket.getOutputStream());
        Log("Stream created.");
        connected = true;
    }

    /**
     * Beállítja a kliens nevét
     * @param _name név
     */
    public void SetName(String _name){
        name = _name;
    }

    /**
     * Visszaadja a kliens nevét
     * @return a kliens neve
     */
    public String GetName(){ return name; }

    /**
     * Elküld egy üzenetet a szerver felé, "PREFIX\tMESSAGE" formában
     * @param prefix az üzenet típusa
     * @param msg az üzenet szövege
     * @throws Exception kivétel keletkezhet
     */
    public void SendMessage(String prefix, String msg) throws Exception {
        if(!connected){
            throw new Exception("Disconnected");
        }
        connectionOutStream.println(prefix + "\t" + msg);
        connectionOutStream.flush();
    }

    /**
     * Visszatér a kliens kapcsolatának állapotával
     * @return kapcsolódás állapota
     */
    public boolean IsConnected(){
        return connected;
    }

    /**
     * Beállítja a kliens kapcsolatának állapotát
     * @param c a kívánt állapot értéke
     */
    public void SetConnected(boolean c) { connected = c; }

    /**
     * Visszatér a kliens autentikációs állapotával
     * @return autentikációs állapot
     */
    public boolean IsAuthenticated() { return authenticated; }

    /**
     * Megnyitja a Streameket a socketen
     * @param is socket input stream
     * @param os socket output strean
     * @throws IOException kivétel keletkezhet
     */
    private void InitializeStreams(InputStream is, OutputStream os) throws IOException {
        //Opens the necessary Writer/Reader pair
        connectionOutStream = new PrintWriter(new OutputStreamWriter(os));
        connectionInStream = new BufferedReader(new InputStreamReader(is));
    }

    /**
     * Névvel azonosítja a klienst a szerveren
     * @throws Exception kivétel keletkezhet
     */
    public void Authenticate() throws Exception {
        //Send auth message
        SendMessage("AUTH", name);
        //Get & process server auth response
        String serverResponse = connectionInStream.readLine();
        String[] splitResponse = serverResponse.split("\t");
        if(splitResponse[0].equals("ACK")){
            //Auth succesful
            Log("Authentication succesful");
            authenticated = true;
        }
        else{
            //Auth failed
            Log("Auth failed");
            socket.close();
            connected = false;
        }
        return;
    }

    /**
     * Naplózza a log streamen a kívánt üzenetet (nem teljesen implementált)
     * @param str rögzítendő szöveg
     */
    private void Log(String str){
        logStream.println(str);
    }

    /**
     * Megszakítja a szerverrel a kapcsolatot
     * @throws Exception kivétel keletkezhet
     */
    public void Shutdown() throws Exception {
        Log("Client shutting down");
        //Signal to server, set state variables
        SendMessage("CMD", "exit");
        connected = false;
        authenticated = false;
    }

    /**
     * Elindítja az olvasó szálat (a kimeneti streamet nem használja)
     * @param os kimeneti stream
     */
    public void StartSocketReader(OutputStream os){
        //Start the daemon thread that notifies client about messages
        wt = new Thread(new ClientReaderThread(socket, this, os));
        wt.setDaemon(true);
        wt.start();
    }

    /**
     * Feldolgozza a kapott üzenetet
     * @param msg a feldolgozandó üzenet
     */
    public void ProcessMessage(String msg){
        if(msg == null){
            return;
        }
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String[] splitMsg = msg.split("\t");
        if(splitMsg[0].equals("MSG")){
            //Message
            outPrintWriter.println("(" + timeStamp + ") " + splitMsg[1]);
        } else if(splitMsg[0].equals("CONN")){
            //Someone connected
            outPrintWriter.println("(" + timeStamp + ") " + splitMsg[1] + " joined!");
        } else if(splitMsg[0].equals("DISC")){
            //Someone left
            outPrintWriter.println("(" + timeStamp + ") " + splitMsg[1] + " left!");
        }
        outPrintWriter.flush();
    }
}
