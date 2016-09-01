package guis;

import data.BotConfig;
import data.LoginConfig;
import irc.IRC;
import logging.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Michael Young
 */
public class MainGUI extends JFrame{
    
    private final String[] INTERVALS = {"0", "1", "2", "3", "4", "5", "6", "10", "12", "15", "20", "30", "60"};
    private final JComboBox INTERVALS_DROPDOWN;
    
    private final JButton BUTTON_CONNECT;
    private final JButton BUTTON_CLEAR;
    private final JButton BUTTON_DISCONNECT;
    private final JButton BUTTON_OPTIONS;
    private final JButton BUTTON_HELP;
    
    private final JTextField TEXTFIELD_USERNAME;
    private final JTextField TEXTFIELD_CHANNEL;
    private final JTextField TEXTFIELD_CURRENCY_NAME;
    private final JTextField TEXTFIELD_PAYOUT_AMOUNT;
    
    private final JPasswordField TEXTFIELD_PASSWORD;
    
    private final Font F;
    
    private final JScrollPane JSP;
    private final JTextArea JTA;
    
    private final short GUI_WIDTH = 700;
    private final short GUI_HEIGHT = 350;
    
    private final short OBJECT_WIDTH = 150;
    private final byte OBJECT_HEIGHT = 25;
    
    private final byte C = 5;
    private final byte TEXTFIELD_POS_X = 5;
    private final byte TEXTFIELD_POS_Y = 5;
    private final byte POSITION_OFFSET = OBJECT_HEIGHT + C;
    
    private final short CONSOLE_WIDTH = GUI_WIDTH - OBJECT_WIDTH - 40;
    
    private LoginConfig lc;
    private BotConfig bc;
    private final FiltersGUI G;
    
    private final String BOT_CONFIG_LOCATION = "config.dat";
    private final String LOGIN_CONFIG_LOCATION = "login.dat";
    
    /**
     *
     * @param title
     */
    @SuppressWarnings("unchecked")    
    public MainGUI(String title) {
        //Do some standard JFrame stuff
        super(title);
        setLayout(null);
        setSize(GUI_WIDTH, GUI_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //Initialize objects
        BUTTON_CONNECT = new JButton("Connect");
        BUTTON_CLEAR = new JButton("Clear");
        BUTTON_DISCONNECT = new JButton("Disconnect");
        BUTTON_OPTIONS = new JButton("Options");
        BUTTON_HELP = new JButton("Help");
        INTERVALS_DROPDOWN = new JComboBox(INTERVALS);
        TEXTFIELD_USERNAME = new JTextField();
        TEXTFIELD_CHANNEL = new JTextField();
        TEXTFIELD_CURRENCY_NAME = new JTextField();
        TEXTFIELD_PAYOUT_AMOUNT = new JTextField();
        TEXTFIELD_PASSWORD = new JPasswordField();
        JTA = new JTextArea();
        JSP = new JScrollPane(JTA, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        F = new Font("ARIAL", 0, 14);
       
        //Modify objects for better interface
        TEXTFIELD_USERNAME.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        TEXTFIELD_USERNAME.setFont(F);
        TEXTFIELD_USERNAME.setLocation(TEXTFIELD_POS_X, TEXTFIELD_POS_Y);
        
        TEXTFIELD_PASSWORD.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        TEXTFIELD_PASSWORD.setLocation(TEXTFIELD_POS_X, 
                TEXTFIELD_USERNAME.getY() + POSITION_OFFSET);
        
        TEXTFIELD_CHANNEL.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        TEXTFIELD_CHANNEL.setFont(F);
        TEXTFIELD_CHANNEL.setLocation(TEXTFIELD_POS_X, 
                TEXTFIELD_PASSWORD.getY() + POSITION_OFFSET);
        
        TEXTFIELD_CURRENCY_NAME.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        TEXTFIELD_CURRENCY_NAME.setFont(F);
        TEXTFIELD_CURRENCY_NAME.setLocation(TEXTFIELD_POS_X, 
                TEXTFIELD_CHANNEL.getY() + POSITION_OFFSET);
        
        INTERVALS_DROPDOWN.setSize(OBJECT_WIDTH / 2 - 10, OBJECT_HEIGHT);
        INTERVALS_DROPDOWN.setLocation(TEXTFIELD_POS_X, 
                TEXTFIELD_CURRENCY_NAME.getY() + POSITION_OFFSET);
        
        TEXTFIELD_PAYOUT_AMOUNT.setSize(OBJECT_WIDTH / 2 - 10, OBJECT_HEIGHT);
        TEXTFIELD_PAYOUT_AMOUNT.setLocation(INTERVALS_DROPDOWN.getX() + 
                2 * POSITION_OFFSET + 25, INTERVALS_DROPDOWN.getY());
        
        BUTTON_CONNECT.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        BUTTON_CONNECT.setLocation(TEXTFIELD_POS_X, 
                INTERVALS_DROPDOWN.getY() + POSITION_OFFSET);
        
        BUTTON_DISCONNECT.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        BUTTON_DISCONNECT.setLocation(TEXTFIELD_POS_X, 
                BUTTON_CONNECT.getY() + POSITION_OFFSET);
        
        BUTTON_CLEAR.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        BUTTON_CLEAR.setLocation(TEXTFIELD_POS_X, 
                BUTTON_DISCONNECT.getY() + POSITION_OFFSET);
        
        BUTTON_OPTIONS.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        BUTTON_OPTIONS.setLocation(TEXTFIELD_POS_X, 
                BUTTON_CLEAR.getY() + POSITION_OFFSET);
        
        BUTTON_HELP.setSize(OBJECT_WIDTH, OBJECT_HEIGHT);
        BUTTON_HELP.setLocation(TEXTFIELD_POS_X, 
                BUTTON_OPTIONS.getY() + POSITION_OFFSET);
        
        JTA.setEditable(false);
        JTA.setLineWrap(true);
        JTA.setWrapStyleWord(true);
        JSP.setSize(CONSOLE_WIDTH, GUI_HEIGHT - 50);
        JSP.setLocation(TEXTFIELD_PAYOUT_AMOUNT.getX() + 
                2 * POSITION_OFFSET + 15, 0);
                
        //Read the configuration file for past options
        lc = (LoginConfig) ObjectIO.readObjectFromFile(LOGIN_CONFIG_LOCATION);
        if (lc == null) {
            lc = new LoginConfig();
        }
        bc = (BotConfig) ObjectIO.readObjectFromFile(BOT_CONFIG_LOCATION);
        if (bc == null) {
            bc = new BotConfig();
        }
        
        readConfig();
        G = new FiltersGUI("Options", bc);

        //Set the event handling
        EventHandler eh = new EventHandler();
        TEXTFIELD_USERNAME.addActionListener(eh);
        TEXTFIELD_PASSWORD.addActionListener(eh);
        TEXTFIELD_CHANNEL.addActionListener(eh);
        TEXTFIELD_CURRENCY_NAME.addActionListener(eh);
        TEXTFIELD_PAYOUT_AMOUNT.addActionListener(eh);
        BUTTON_CONNECT.addActionListener(eh);
        BUTTON_CLEAR.addActionListener(eh);
        BUTTON_OPTIONS.addActionListener(eh);
        BUTTON_DISCONNECT.addActionListener(eh);
        BUTTON_HELP.addActionListener(eh);

        //Add objects to the JFrame
        add(BUTTON_CONNECT);
        add(BUTTON_CLEAR);
        add(BUTTON_DISCONNECT);
        add(BUTTON_OPTIONS);
        add(BUTTON_HELP);
        add(INTERVALS_DROPDOWN);
        add(TEXTFIELD_USERNAME);
        add(TEXTFIELD_CHANNEL);
        add(TEXTFIELD_CURRENCY_NAME);
        add(TEXTFIELD_PAYOUT_AMOUNT);
        add(TEXTFIELD_PASSWORD);
        add(JSP);
        
        setVisible(true);
        readConsole();
    }
    
    /**
     * Start a thread that reads the console class. As this may be the final
     * thread that will run, have this thread check to see if IRC was 
     * disconnected, and if it wasn't, disconnect it.
     */
    private void readConsole() {
        new Thread() {
                        
            @Override
            public void run() {
                this.setName("Console Thread");
                BufferedWriter bw;
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                File f = new File("logs");
                if (!f.exists()) {
                    f.mkdir();
                }
                String date = String.format(f.toString() + "/%d-%d-%d.txt", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                String s;
                try {
                    bw = new BufferedWriter(new FileWriter(date, true));

                    while (isDisplayable()) {
                        s = Console.readLine();
                        if (s != null) {
                            JTA.append(s);
                            bw.append(s);
                            bw.flush();
                        }
                        else {
                            try {
                                Thread.sleep(250);
                            }
                            catch (InterruptedException e) { }
                        }
                    }
                    bw.close();
                }
                catch (IOException e) {
                    Console.writeLine("Could not open logging file. Will still write to console.");
                    ErrorLog.writeToLog(e);
                    while (isDisplayable()) {
                        s = Console.readLine();
                        if (s != null) {
                            JTA.append(s);
                        }
                        else {
                            try {
                                Thread.sleep(250);
                            }
                            catch (InterruptedException ex) { }
                        }
                    }
                }
                finally {
                    ObjectIO.writeObjectToFile(BOT_CONFIG_LOCATION, bc);
                    ObjectIO.writeObjectToFile(LOGIN_CONFIG_LOCATION, lc);
                    if (IRC.isConnected()) {
                        IRC.disconnect();
                    }
                    G.dispose();
                    ErrorLog.closeErrorLog();
                }
            }
        }.start();
    }
    
    /**
     * Set the dropdown to represent the last known interval.
     * @param interval the last known interval
     */
    private void setInterval(int interval) {
        for (int i = 0; i < INTERVALS.length; i++) {
            if (interval == Integer.parseInt(INTERVALS[i])) {
                INTERVALS_DROPDOWN.setSelectedIndex(i);
                return;
            }
        }
    }
    
    /**
     * Read the last configuration from file.
     */
    private void readConfig() {
        if (!lc.getUsername().equals("")) {
            TEXTFIELD_USERNAME.setText(lc.getUsername());
        }
        else {
            TEXTFIELD_USERNAME.setText("Username");
        }
        
        if (!lc.getPassword().equals("")) {
            TEXTFIELD_PASSWORD.setText(lc.getPassword());
        }
        else {
            TEXTFIELD_PASSWORD.setText("Password");
        }
        
        if (!lc.getChannel().equals("")) {
            TEXTFIELD_CHANNEL.setText(lc.getChannel());
        }
        else {
            TEXTFIELD_CHANNEL.setText("Channel");
        }
        
        if (!bc.getCurrencyName().equals("")) {
            TEXTFIELD_CURRENCY_NAME.setText(bc.getCurrencyName());
        }
        else {
            TEXTFIELD_CURRENCY_NAME.setText("Currency Name");
        }
        
        if (bc.getPayoutAmount() != 0) {
            TEXTFIELD_PAYOUT_AMOUNT.setText(Integer.toString(bc.getPayoutAmount()));
        }
        else {
            TEXTFIELD_PAYOUT_AMOUNT.setText("0");
        }
        setInterval(bc.getPayoutInterval());
    }
    
    /**
     * Launch the application by creating the main gui.
     * @param args command line arguments. Completely pointless in this case.
     */
    public static void main(String[] args) {
        //IRC test = new IRC("Geno__bot", "oauth:ihdnnuuqbsh38wzxj2snmkr5xqnbam", "zwinkster", "genopoints", 0, 0);
        //Note to self, WRITE DOWN THE FUCKING EMAIL: genobot7956@gmail standard password
        ErrorLog.initErrorLog();
        MainGUI g = new MainGUI("Geno_bot Beta");
    }
 
    /**
     * A class that handles the events within the GUI.  Only the buttons
     * really need to be listened to, as the fields could add undesirable
     * results for accidental keystrokes.
     */
    private class EventHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource().equals(BUTTON_CONNECT)) {
                lc.setUsername(TEXTFIELD_USERNAME.getText());
                lc.setPassword(new String(TEXTFIELD_PASSWORD.getPassword()));
                lc.setChannel(TEXTFIELD_CHANNEL.getText());
                bc.setCurrencyName(TEXTFIELD_CURRENCY_NAME.getText());
                bc.setPayoutInterval(Integer.parseInt(INTERVALS[INTERVALS_DROPDOWN.getSelectedIndex()]));
                bc.setPayoutAmount(Integer.parseInt(TEXTFIELD_PAYOUT_AMOUNT.getText()));
                if (TEXTFIELD_USERNAME.getText() != null) {
                    if (IRC.isConnected()) {
                        IRC.disconnect();
                    }
                    IRC.init(lc, bc);
                    new Thread() {
                        
                        @Override
                        public void run() {
                            this.setName("Connection Thread");
                            IRC.connect();
                        }
                    }.start();
                }
            }
            else if (ae.getSource().equals(BUTTON_DISCONNECT)) {
                if (IRC.isConnected()) {
                    IRC.disconnect();
                }
            }
            else if (ae.getSource().equals(BUTTON_CLEAR)) {
                JTA.setText("");
            }
            else if (ae.getSource().equals(BUTTON_HELP)) {
                
            }
            else if (ae.getSource().equals(BUTTON_OPTIONS)) {
                G.setVisible(true);
            }
        }
    }
}
