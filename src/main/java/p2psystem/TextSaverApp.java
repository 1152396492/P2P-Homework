package p2psystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TextSaverApp {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private BufferedWriter writer;
    
    private static final String SEVERADDRESS = "127.0.0.1" ; 
	private static final int PORT = 8189 ;
	private static int logicNum = 1 ;
	Socket socket ;
	BufferedReader in ; 
	PrintWriter out ; 
	
	public void setLogicNum ( int logicNum ) { 
		this.logicNum = logicNum ; 
	}
	
    public TextSaverApp() {
        setupUI();
        setupWriter("output.txt");
        try {
        	socket = new Socket ( SEVERADDRESS , PORT ) ; 
        	in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) ) ; 
        	out = new PrintWriter ( socket.getOutputStream() , true ) ; 
        } catch ( IOException e ) {
        	e.getStackTrace() ; 
        }
    }

    public TextSaverApp(String filePath) {
        setupUI();
        setupWriter(filePath);
        try {
        	socket = new Socket ( SEVERADDRESS , PORT ) ; 
        	in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) ) ; 
        	out = new PrintWriter ( socket.getOutputStream() , true ) ; 
        } catch ( IOException e ) {
        	e.getStackTrace() ; 
        }
    }

    private void setupUI() {
        frame = new JFrame("Text Saver");
        textArea = new JTextArea(20, 50);
        textField = new JTextField(40);
        sendButton = new JButton("Send");

        textArea.setEditable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.add(textField);
        inputPanel.add(sendButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setupWriter(String filePath) {
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendText() {
        String text = textField.getText();
        if (!text.isEmpty()) {
        	Message message = new Message ( 0 , logicNum , text ) ;
			String messageJson = MessageUtils.serialize(message) ; 
			logicNum = logicNum + 1 ; 
			out.println(messageJson);
			textField.setText("");
            /*textArea.append(text + "\n");
            

            try {
                writer.write(text);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void appendText(String text) {
        if (!text.isEmpty()) {
            textArea.append(text + "\n");

            try {
                writer.write(text);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
