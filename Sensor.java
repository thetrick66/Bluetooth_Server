import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

public class Sensor extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;
	private JButton btnNewButton;
	//Create a UUID for SPP
    private UUID uuid = new UUID("1101", true);
    //Create the servicve url
    private String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
    StreamConnectionNotifier streamConnNotifier;
    StreamConnection connection;
    InputStream inStream;
    OutputStream outStream;
    Write write;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sensor frame = new Sensor();
					LocalDevice localDevice = LocalDevice.getLocalDevice();
			        System.out.println("Address: "+localDevice.getBluetoothAddress());
			        System.out.println("Name: "+localDevice.getFriendlyName());
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public Sensor() {
		try {
			streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
		        	write = new Write();
		            write.execute();
		        } catch (Exception e2) {
		            e2.printStackTrace();
		        }
			}
		});
		textField.setBounds(118, 75, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnNewButton = new JButton("Enviar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        try {
		        	write = new Write();
		            write.execute();
		        } catch (Exception e2) {
		            e2.printStackTrace();
		        }
			}
		});
		btnNewButton.setBounds(253, 74, 89, 23);
		contentPane.add(btnNewButton);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(92, 172, 154, 22);
		contentPane.add(textArea);
		Read read = new Read();
        try {
            read.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
	}
	
	class Read extends SwingWorker {
        /**
         * @throws Exception
         */
        protected Object doInBackground() throws Exception {
        	int i = 0;
            while(true){
            	//Wait for client connection
                if(i==0){
                	connection=streamConnNotifier.acceptAndOpen();
                	inStream=connection.openInputStream();
                	outStream=connection.openOutputStream();
                	RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
                    System.out.println("Remote device address: "+dev.getBluetoothAddress());
                    System.out.println("Remote device name: "+dev.getFriendlyName(true));
                	i=1;
                }
                
              //read string from spp client
                BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
                System.out.println("Listo para leer");
                String lineRead=bReader.readLine();
                textArea.setText(lineRead);
                //System.out.println(lineRead);
                
                //streamConnNotifier.close();
            }
        }
    }
	
	class Write extends SwingWorker {
        /**
         * @return 
         * @throws Exception
         */
        protected Object doInBackground() throws Exception {
        	//open server url
            	//StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
            	//Wait for client connection
                //System.out.println("\nServer Started. Waiting for clients to connect...");
                //StreamConnection connection=streamConnNotifier.acceptAndOpen();

                //RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
                //System.out.println("Write to remote device address: "+dev.getBluetoothAddress());
                //System.out.println("Write to remote device name: "+dev.getFriendlyName(true));                

              //send response to spp client
                if(textField.getText().equals("")){
                	outStream.write("\n".getBytes());
                }
                else{
                	outStream.write((textField.getText()+"\n").getBytes());
                }
                System.out.println("Enviado");
                textField.setText("");
                
                //streamConnNotifier.close();
            return 0;
        }
    }
	
}
