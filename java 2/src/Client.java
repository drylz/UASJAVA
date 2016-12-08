import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
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


public class Client extends JFrame {

    BufferedReader in;
    PrintWriter out;
    JPanel contentPane;
    JTextField textField;
    JTextArea messageArea;
    String name;


    public Client() {

    	setTitle("Client");
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
    	setJMenuBar(menuBar);menu.add(logout);
    	
    	setJMenuBar(menuBar);
    	
    	JScrollPane scrollPane = new JScrollPane();
    	messageArea = new JTextArea();
    	textField = new JTextField();
    	
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
                messageArea.append("Me : " + textField.getText() + System.lineSeparator());
                DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                textField.setText("");
                
            }
        });
    }

    
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            contentPane,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

   
    private String getNamee() {
        return JOptionPane.showInputDialog(
            contentPane,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

   
    private void run() throws IOException, InterruptedException {

        
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

       
        while (true) {
        	String line = in.readLine();
        	if(line != null){
            
	            if (line.startsWith("SUBMITNAME")) {
	            	name = getNamee();
	                out.println(name);
	                
	                  
	            
	            }else if(line.startsWith("NAMEACCEPTED"))
	            	textField.setEditable(true);
	            
	            else if (line.startsWith("From")) {
	                messageArea.append(line.substring(5) + "\n");
	                
	            }
	            else if(line.startsWith("Name")){
	            	messageArea.append(line + "\n");
	            	messageArea.append("Ready to chat");

	            }
        	}
        	else 
        		wait();
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.setVisible(true);
        client.run();
    }
}