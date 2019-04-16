import RelayChatClient.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A kliens logika és GUI összekötéséért felelős osztály
 *
 */
public class ClientController {
    static ClientGUI cgui;
    static Client c;

    /**
     * Elindítja a programot
     * @param args argumentumok, nem használja
     */
    public static void main(String[] args){
        cgui = new ClientGUI();
        cgui.SetConnectionState(false);
        c = new Client(new CustomOutStream(cgui.getIncomingArea()));
        cgui.AddConnectButtonListener(new ConnectListener());
        cgui.AddDisconnectButtonListener(new DisconnectListener());
        cgui.AddMessageAreaListener(new MessageKeyListener());
    }

    /**
     * Hozzárendeli a csatlakozás gombhoz a listenert
     */
    static class ConnectListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            int port = Integer.parseInt(cgui.GetPort());
            //If the port number is invalid
            if((port < 1) || (port > 65535)){
                JOptionPane.showMessageDialog(cgui, "Port number must be an integer between 1 and 65535 inclusive!", "Connection parameter error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //Attempt connection
            String addr = cgui.GetAddress();
            try {
                c.Connect(addr, port);
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(cgui, "Could not connect to the server.", "Connection error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //Attempt authentication
            String name = cgui.GetName();
            c.SetName(name);
            try {
                c.Authenticate();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(cgui, "Could not authenticate with the server.", "Authentication error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //Everything went fine
            if(c.IsAuthenticated()) {
                cgui.getIncomingArea().append("You've connected to the server.\n");
                c.StartSocketReader(new CustomOutStream(cgui.getIncomingArea()));
                cgui.SetConnectionState(true);
            } else{
                cgui.getIncomingArea().append("Authentication failed.\n");
            }
        }
    }

    /**
     * Hozzárendeli a kapcsolat bontása gombhoz a listenert
     */
    private static class DisconnectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                c.Shutdown();
            } catch (Exception e1) {
                //Should not happen
                e1.printStackTrace();
            }
            cgui.SetConnectionState(false);
            cgui.getIncomingArea().append("You've disconnected from the server.\n");
        }
    }

    /**
     * Hozzárendeli az üzenetbeviteli mezőhöz a keylistenert
     */
    private static class MessageKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar() == KeyEvent.VK_ENTER){
                if(!cgui.GetInput().equals("")) {
                    String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    cgui.getIncomingArea().append("("+ timeStamp +") " + cgui.GetName() + ": " + cgui.GetInput());
                    try {
                        c.SendMessage("MSG", cgui.GetInput());
                    } catch (Exception e1) {
                        //We were disconnected
                        cgui.getIncomingArea().append("You have been disconnected from the server.");
                        cgui.SetConnectionState(false);
                    }
                    cgui.ClearMessageText();
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
