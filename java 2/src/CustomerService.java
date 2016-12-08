 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class CustomerService extends JFrame{

    BufferedReader in;
    PrintWriter out;
    JPanel contentPane;
    JTextField textField;
    JTextArea messageArea;

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public CustomerService() {
    	setTitle("Customer Service");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setBounds(100,100,455,555);
    	
    	contentPane = new JPanel();
    	contentPane.setBorder(new EmptyBorder(5,5,5,5));
    	setContentPane(contentPane);
    	contentPane.setLayout(null);
    	
    	JMenuBar menuBar = new JMenuBar();
    	JMenu menu = new JMenu("Menu");
    	menuBar.add(menu);
    	JMenuItem logout = new JMenuItem("Logout");
    	logout.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e){
    			out.println("@DC");
    			System.exit(0);
    		}
    	});
    	menu.add(logout);
    	setJMenuBar(menuBar);
//    	JMenuItem saveChat = new JMenuItem("Save Chat");
    	JScrollPane scrollPane = new JScrollPane();
    	messageArea = new JTextArea();
    	textField = new JTextField();
    	
    	
    	
    	logout.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e){
    			out.println("DC");
    		}
    	});
    	
    	scrollPane.setBounds(12,13,413,421);
    	contentPane.add(scrollPane);
    	
    	
    	DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    	messageArea.setEditable(false);
    	scrollPane.setViewportView(messageArea);
    	
    	
    	textField.setBounds(12,442,413,29);
    	contentPane.add(textField);
    	textField.setColumns(10);
    	
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                messageArea.append("Costumer Service : " + textField.getText() + System.lineSeparator());
                
                textField.setText("");
                
            }
        });
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            contentPane,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    private String getUsername() {
        return JOptionPane.showInputDialog(
            contentPane,
            "Insert Username:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private String getPassword() {
        return JOptionPane.showInputDialog(
            contentPane,
            "Insert password:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }
    
   
    private void run() throws IOException, InterruptedException {

        // Make connection and initialize streams
    	try{
        String serverAddress = getServerAddress();
        
		Socket socket = new Socket(serverAddress, 8989);
		System.out.println("connected");
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    	}catch(Exception e){
    		e.printStackTrace();
    	}

        // Process all messages from server, according to the protocol.
        while (true) {
        	String line = in.readLine();
        	if(line != null){
            
	            if (line.startsWith("LOGIN")) {
	                out.println(getUsername());
	            
	            }else if(line.startsWith("USERNAMEACCEPTED")){
	            	out.println(getPassword());
	            }else if(line.startsWith("COBALAGI")){
	            	JOptionPane.showMessageDialog(contentPane, "Coba lagi ");
	            	
	            }
	            
	            else if (line.startsWith("From")) {
	                messageArea.append(line.substring(5) + "\n");
	            }
	            else if(line.startsWith("Name")){
	            	messageArea.append(line + "\n");
	            	textField.setEditable(true);
	            }
        	}
        	else 
        		wait();
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        CustomerService custServ = new CustomerService();
        custServ.setVisible(true);
        custServ.run();
    }
}