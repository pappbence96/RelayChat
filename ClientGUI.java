import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import say.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A kliens grafikus felülete
 */
public class ClientGUI extends JFrame {
    private JTextField portTextField;
    private JTextField nameTextField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JPanel TopPanel;
    private JPanel BottomPanel;
    private JPanel ChatWindow;
    private JTextArea messageArea;
    private JTextArea incomingArea;
    private JScrollPane incomingScrollPane;
    private JTextField ipTextField;

    /**
     * Konstruktor, elvégzi a Frame felépítésének egy részét
     */
    public ClientGUI() {
        super();
        //Construct Menu
        JMenuBar menuBar = new JMenuBar();
        //Looks menu
        JMenu looksMenu = new JMenu("Looks");
        JMenuItem bgColorMenu = new JMenuItem("Text area background color");
        bgColorMenu.addActionListener((ActionEvent e) -> getIncomingArea().setBackground(JColorChooser.showDialog(null, "Text area background", Color.WHITE)));
        looksMenu.add(bgColorMenu);
        JMenuItem textColorMenu = new JMenuItem("Text color");
        textColorMenu.addActionListener((ActionEvent e) -> getIncomingArea().setForeground(JColorChooser.showDialog(null, "Text area background", Color.WHITE)));
        looksMenu.add(textColorMenu);
        JMenuItem textFontMenu = new JMenuItem("Text font");
        textFontMenu.addActionListener(((ActionEvent e) -> {
            JFontChooser fontChooser = new JFontChooser();
            if (fontChooser.showDialog(null) == JFontChooser.OK_OPTION) {
                getIncomingArea().setFont(fontChooser.getSelectedFont());
            }
        }));
        looksMenu.add(textFontMenu);
        menuBar.add(looksMenu);
        this.setJMenuBar(menuBar);
        //File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveLogMenu = new JMenuItem("Save chat log");
        saveLogMenu.addActionListener((ActionEvent e) -> {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(Calendar.getInstance().getTime());
            try {
                String fileName = timeStamp + "-chatlog.txt";
                File outFile = new File(fileName);
                PrintWriter pw = new PrintWriter(new FileOutputStream(outFile));
                pw.println(getIncomingArea().getText());
                pw.close();
            } catch (FileNotFoundException e1) {
                JOptionPane.showMessageDialog(this, "Could not save log:\n" + e1.toString(), "File operation error!", JOptionPane.ERROR_MESSAGE);
            }
        });
        fileMenu.add(saveLogMenu);
        menuBar.add(fileMenu);

        setContentPane(ChatWindow);
        setSize(650, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Visszatér a beírt portszámmal
     *
     * @return portszám
     */
    public String GetPort() {
        return portTextField.getText();
    }

    /**
     * Visszatér a beírt névvel, eltávolítja a nem alfanumerikus karaktereket
     *
     * @return név
     */
    public String GetName() {
        String tmp = nameTextField.getText();
        StringBuilder returnString = new StringBuilder();
        //No funny characters
        for (int i = 0; i < tmp.length(); i++) {
            if (Character.isAlphabetic(tmp.charAt(i)) || Character.isDigit(tmp.charAt(i))) {
                returnString.append(tmp.charAt(i));
            }
        }
        return returnString.toString();
    }

    /**
     * Visszatér a beírt üzenettel
     *
     * @return beírt üzenet
     */
    public String GetInput() {
        return messageArea.getText().replace("\t", " ");
    }

    /**
     * Visszatér a beírt IP-címmel
     *
     * @return IP-cím
     */
    public String GetAddress() {
        return ipTextField.getText();
    }

    /**
     * Kiüríti az üzenet-beviteli mezőt
     */
    public void ClearMessageText() {
        messageArea.setText("");
    }

    /**
     * Visszatér a beírt üzenettel, eltávolítja a tab karaktereket
     *
     * @return üzenet
     */
    public JTextArea getIncomingArea() {
        return incomingArea;
    }

    /**
     * Hozzárendeli a kapott listenert a csatlakozás gombhoz
     *
     * @param l listener
     */
    void AddConnectButtonListener(ActionListener l) {
        connectButton.addActionListener(l);
    }

    /**
     * Hozzárendeli a kapott listenert a kapcsolat bontása gombhoz
     *
     * @param l listener
     */
    void AddDisconnectButtonListener(ActionListener l) {
        disconnectButton.addActionListener(l);
    }

    /**
     * Beállítja a kapcsolat állapotától függően a felület elemeinek szerkeszthetőségét és láthatóságát
     *
     * @param state kapcsolat állapota
     */
    void SetConnectionState(boolean state) {
        //state == true -> connected
        portTextField.setEditable(!state);
        ipTextField.setEditable(!state);
        nameTextField.setEditable(!state);
        connectButton.setEnabled(!state);
        connectButton.setVisible(!state);
        disconnectButton.setEnabled(state);
        disconnectButton.setVisible(state);
        messageArea.setEditable(state);
        messageArea.setText("");
    }

    /**
     * Hozzárendeli a kapott listenert az üzenetbeviteli mezőhöz
     *
     * @param l listener
     */
    void AddMessageAreaListener(KeyListener l) {
        messageArea.addKeyListener(l);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        ChatWindow = new JPanel();
        ChatWindow.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        TopPanel = new JPanel();
        TopPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        ChatWindow.add(TopPanel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ipTextField = new JTextField();
        ipTextField.setColumns(16);
        ipTextField.setText("localhost");
        TopPanel.add(ipTextField);
        portTextField = new JTextField();
        portTextField.setColumns(5);
        portTextField.setText("19642");
        TopPanel.add(portTextField);
        nameTextField = new JTextField();
        nameTextField.setColumns(10);
        nameTextField.setText("Username");
        TopPanel.add(nameTextField);
        connectButton = new JButton();
        connectButton.setText("Connect");
        TopPanel.add(connectButton);
        disconnectButton = new JButton();
        disconnectButton.setText("Disconnect");
        TopPanel.add(disconnectButton);
        final Spacer spacer1 = new Spacer();
        ChatWindow.add(spacer1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        BottomPanel = new JPanel();
        BottomPanel.setLayout(new BorderLayout(0, 0));
        ChatWindow.add(BottomPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        messageArea = new JTextArea();
        BottomPanel.add(messageArea, BorderLayout.SOUTH);
        incomingScrollPane = new JScrollPane();
        BottomPanel.add(incomingScrollPane, BorderLayout.CENTER);
        incomingArea = new JTextArea();
        incomingArea.setBackground(new Color(-14738870));
        incomingArea.setColumns(50);
        incomingArea.setEditable(false);
        incomingArea.setForeground(new Color(-4672076));
        incomingArea.setLineWrap(true);
        incomingArea.setRows(0);
        incomingArea.setText("");
        incomingArea.setWrapStyleWord(true);
        incomingScrollPane.setViewportView(incomingArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return ChatWindow;
    }
}
