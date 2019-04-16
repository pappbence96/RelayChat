package RelayChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import MyLogger.Logger;


/**
 * Chat szerver osztály
 */
public class Server {
    private ServerSocket serverSocket;
    public Logger logger;
    public List<ServerThread> clients;

    /**
     * Szerver konstruktor, a megadott porton figyeli a bejövő kapcsolatokat
     * @param port szerver port
     * @throws IOException kivétel keletkezhet
     */
    public Server(int port) throws IOException {
        logger = new Logger(System.out);
        logger.Log("SYS", "Server starting up...");
        clients = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        logger.Log("SYS", "Server started, listening for incoming client connections on port " + serverSocket.getLocalPort() + ".");
    }

    /**
     * Elindítja a figyelési folyamatot
     */
    public void Listen(){
        //Keep listening for new clients
        while(true){
            //Accept new connection
            Socket newSocket = null;
            try {
                newSocket = serverSocket.accept();
                logger.Log("SYS", "Client connected!");
            } catch (IOException e) {
                logger.Log("SYS", "Failed to create client socket.");
                e.printStackTrace();
            }

            //Launch worker thread that handles further exchange
            ServerThread st;
            try {
                st = new ServerThread(newSocket, this);
                new Thread(st).start();
            } catch (IOException e) {
                logger.Log("SYS", "Failed to create thread for client.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Felveszi az új csatlakozott klienst kezelő szálat
     * @param st klienst kezelő szál
     */
    public void RegisterNewClient(ServerThread st){
        logger.Log("USER", "User " + st.getUserID() + " registered.");
        clients.add(st);
        RelayMessage("CONN", "", st.getUserID());
    }

    /**
     * Eltávolítja a csatlakozott klienst kezelő szálat
     * @param st klienst kezelő szál
     */
    public void DeleteClient(ServerThread st){
        logger.Log("USER", "User " + st.getUserID() + " unregistered.");
        clients.remove(st);
        RelayMessage("DISC","", st.getUserID());
    }

    /**
     * Szétküldi az üzenetet a csatlakozott klienseknek, kivéve a feladóját
     * @param type az üzenet típusa
     * @param message az üzenet szövege
     * @param sender a feladó neve
     */
    public void RelayMessage(String type, String message, String sender){
        //Broadcasts the message to all connected clients, except the sender
        for (ServerThread st : clients) {
            if(type.equals("MSG")) {
                if (!st.getUserID().equals(sender)) {
                    st.SendMessage(type + "\t" + sender + ": " + message);
                }
            }
            else if(type.equals("CONN")){
                if (!st.getUserID().equals(sender)) {
                    st.SendMessage("CONN" + "\t" + sender);
                }
            }
            else if(type.equals("DISC")){
                if (!st.getUserID().equals(sender)) {
                    st.SendMessage("DISC" + "\t" + sender);
                }
            }
        }
    }

    /**
     * Ellenőrzi, hogy van-e már adott nevű kliens csatlakozva
     * @param name a keresett név
     * @return a keresés eredménye
     */
    public boolean IsNameTaken(String name){
        //Check whether the name is already taken (server does not allow more than one user with any given name)
        for(ServerThread st: clients){
            if(st.getUserID().equals(name)){
                return true;
            }
        }
        return false;
    }
}
