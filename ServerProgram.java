import RelayChatServer.Server;

import java.io.IOException;
import java.net.BindException;

/**
 * A szerver futtatásáért felelős program
 */
public class ServerProgram {
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: ServerProgram PortNumber");
            return;
        }
        int SERVER_PORT;
        try{
            SERVER_PORT = Integer.parseInt(args[0]);
        } catch(NumberFormatException e){
            System.out.println("Port number is invalid");
            return;
        }
        Server server;
        try {
            server = new Server(SERVER_PORT);
        } catch(BindException e) {
            System.out.println("Port already in use.");
            return;
        } catch (IllegalArgumentException e){
            System.out.println("Port number out of range.");
            return;
        } catch (IOException e) {
            System.err.println("IOException occurred while instantiating Server object.");
            e.printStackTrace(System.err);
            return;
        }
        server.Listen();
    }
}
