package RelayChatServer;

import MyLogger.Logger;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * A csatlakozott klienssel foglalkozó szál
 */
public class ServerThread implements Runnable{
    private Logger logger;
    private Server mainServ;
    private PrintWriter outConnectionStream;
    private BufferedReader inConnectionStream;
    private Socket socket;
    private String userID;
    private boolean connected = false;

    /**
     * Konstruktor
     * @param sock socket, amihez a kliens csatlakozott
     * @param serv a fő szerver
     * @throws IOException kivétel keletkezhet
     */
    public ServerThread(Socket sock, Server serv) throws IOException {
        //Initialize
        socket = sock;
        mainServ = serv;
        outConnectionStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        inConnectionStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Elindítja a szálat
     */
    @Override
    public void run() {
        logger = mainServ.logger; //Use main thread's logger
        //Authenticate user, exit on failure
        if(!AuthenticateUser()){
            logger.Log("[SYS]", "User failed to authenticate.");
            return;
        }
        mainServ.RegisterNewClient(this); //Register self
        ListenClient();
        mainServ.DeleteClient(this); //Remove self because user is disconnected
    }

    /**
     * Elvégzi a felhasználó azonosítását
     * @return az azonosítás eredménye
     */
    private boolean AuthenticateUser(){
        logger.Log("SYS", "Authenticating user...");
        //Try reading auth message from the client
        String clientMessage = null;
        try {
            clientMessage = inConnectionStream.readLine();
        } catch (IOException e) {
            logger.Log("SYS", "Failed to get client message.");
            e.printStackTrace();
            return false;
        }

        //Validate message (is in the form of "AUTH\t<USERNAME>"
        String[] split = clientMessage.split("\t");
        if(!split[0].equals("AUTH") || (split.length != 2)){
            logger.Log("SYS", "Malformed authentication message.");
            return false;
        }

        //Check name
        userID = split[1];
        if((userID == null) || userID.isEmpty() || mainServ.IsNameTaken(userID)){
            outConnectionStream.println("NACK");
            outConnectionStream.flush();
            try {
                socket.close();
            } catch (IOException e) {
                //This shouldn't happen
                e.printStackTrace();
            }
            return false;
        }

        //Acknowledge authentication
        outConnectionStream.println("ACK");
        outConnectionStream.flush();
        logger.Log("SYS", "User \"" + userID + "\" connected.");
        connected = true;
        return true;
    }

    /**
     * Elkezdi a kliens "hallgatását"
     */
    private void ListenClient(){
        logger.Log("SYS", "Listening user " + userID);
        //Start listening to the client's messages
        String clientMessage = null;
        while(connected){
            try {
                clientMessage = inConnectionStream.readLine();
            } catch (SocketException e){
                //Client disappeared, terminate process
                connected = false;
                continue;
            } catch (IOException e) {
                logger.Log("SYS", "Failed to get client message.");
                e.printStackTrace();
                continue;
                //connected = false;
            }
            //If for some reason, the client sends NULL, terminate the connection and the process
            if(clientMessage == null){
                connected = false;
                continue;
            }
            //Assuming message is always well-formatted and does not contain additional tabs
            String[] split = clientMessage.split("\t");

            //If it is a message
            if(split[0].equals("MSG")) {
                logger.Log("USER", userID + " sent: " + split[1]);
                //Echo back to all clients
                mainServ.RelayMessage("MSG", split[1], userID);
            }

            //If it is a command (only exit command is valid ATM)
            else if(split[0].equals("CMD")){
                if(split[1].equals("exit")){
                    //User disconnects via message
                    connected = false;
                }
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            //Does not really matter anymore...
            e.printStackTrace();
        }
    }

    /**
     * Visszaadja a kliens nevét
     * @return a kliens neve
     */
    public String getUserID(){
        return userID;
    }

    /**
     * Elküldi a kliensnek az üzenetet
     * @param message az üzenet
     */
    public void SendMessage(String message){
        outConnectionStream.println(message);
        outConnectionStream.flush();
    }
}
