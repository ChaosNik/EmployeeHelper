package org.unibl.etf.mdp.chat.view;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.axis.encoding.Base64;
import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.LoginService;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.ScreenSendingThread;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MessageViewController implements Initializable
{
	private static String SERIALIZATION = "serialization";
	private static String REST_LOGOUT = "http://localhost:8080/REST/api/rest/logout/";
	private static String MULTICAST_ADDRESS = "224.0.0.3";
	private static int MULTICAST_PORT = 8888;
	
	static
	{
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try
	    {
	    	reader=new FileReader("config.properties"); 
			p.load(reader);
		}
	    catch (IOException e)
	    {
			e.printStackTrace();
		}  
	    SERIALIZATION = p.getProperty("SERIALIZATION");
	    REST_LOGOUT = p.getProperty("REST_LOGOUT");
	    MULTICAST_ADDRESS = p.getProperty("MULTICAST_ADDRESS");
	    MULTICAST_PORT = Integer.parseInt(p.getProperty("MULTICAST_PORT"));
	}
	
	private FileChooser fc;
	
	private MulticastSocket clientSocket;
	
	private Gson gson = new Gson();
	
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private Label lblUsage;
	
	@FXML
	private TextArea txtMessage;
	
	@FXML
	private Label txtAdminMessage;

	@FXML
	private ListView<String> listMessages;

	@FXML
	private ListView<User> listUsers;
	
	@FXML
	private ListView<String> listFiles;

	@FXML
	private Button btnSend;
	
	@FXML
	private Button btnBroadcast;
	
	@FXML
	private Button btnSendFile;
	
	@FXML
	private Button btnDownload;

	// cuvamo parove korisnika i svih poruka koje smo od njih dobili
	private HashMap<User, ArrayList<String>> allMessages = new HashMap<>();

	void logout()
	{
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOGOUT + Main.username);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
	}
	
	void adminMessage(String message)
	{
		txtAdminMessage.setText("Poruka od administratora: " + message);
	}
	
	private void adminMessage()
	{

		InetAddress groupAddress;
		try
		{
			//groupAddress = InetAddress.getByName(prop.getProperty("IP_ADDRESS_MULTICAST"));
			groupAddress = InetAddress.getByName(MULTICAST_ADDRESS);

			clientSocket = new MulticastSocket(MULTICAST_PORT);
			clientSocket.joinGroup(groupAddress);
		}
		catch (UnknownHostException e2)
		{
			//LOGGER.log(Level.SEVERE, e2.getMessage());
		}
		catch (NumberFormatException | IOException e1)
		{
			//LOGGER.log(Level.SEVERE, e1.getMessage());
		}

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{
					getAdminMessage();
				}
			}
		}).start();
	}
	
	private void getAdminMessage()
	{

		byte[] buf = new byte[256];

		DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
		try
		{
			clientSocket.receive(msgPacket);
			String notification = new String(msgPacket.getData(), msgPacket.getOffset(), msgPacket.getLength());

			if (notification != null)
			{
				serializeNotification(notification);
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						txtAdminMessage.setText(notification);
					}
				});
			}
		}
		catch (IOException e)
		{
			//LOGGER.log(Level.SEVERE, e.getMessage());
		}

	}
	
	private void serializeNotification(String notification)
	{
		try
		{
			File root = new File(SERIALIZATION);
			if(!root.exists()) root.mkdir();
			File dir = new File(SERIALIZATION + File.separator + Main.username);
			if(!dir.exists()) dir.mkdir();
			//logger.log(Level.INFO, "File " + fileName + " sent from user " + userFrom + " to user " + userTo);
		
			int numberOfSerializedFiles = dir.listFiles().length;
			File file = new File(SERIALIZATION + File.separator + Main.username + File.separator + numberOfSerializedFiles);
			if(!file.exists()) file.createNewFile();
			int serializationNumber = numberOfSerializedFiles % 4;
			if (serializationNumber == 0)
					Files.write(file.toPath(), notification.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
			else if (serializationNumber == 1)
				Files.write(file.toPath(), gson.toJson(notification).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
			else if (serializationNumber == 2)
			{
					Kryo kryo = new Kryo();
					Output output = new Output(new FileOutputStream(file.getPath()));
					kryo.writeClassAndObject(output, notification);
				    output.close();
			}
			else if (serializationNumber == 3)
			{
				FileOutputStream fos1 = new FileOutputStream(file.getPath());
				java.beans.XMLEncoder xe1 = new java.beans.XMLEncoder(fos1);
				xe1.writeObject(notification);
				xe1.close();
			}
		}
		catch (Exception e)
		{
			//logger.log(Level.INFO, e.toString(), e);
		}
	}
	
	@FXML
	void onSendFile(ActionEvent event)
	{
		fc = new FileChooser();
		File file = fc.showOpenDialog(Main.myStage);
		if(file != null)
		{
			try
			{
				byte[] data = Files.readAllBytes(file.toPath());
				RMIService.save
				(
					file.getName(),
					data,
					Main.username,
					listUsers.getSelectionModel().getSelectedItem().getUsername()
				);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void onDownload(ActionEvent event)
	{
		fc = new FileChooser();
		File file = fc.showSaveDialog(Main.myStage);
		if(file != null)
		{
			try
			{
				byte[] data =
						RMIService.download
						(
							listFiles.getSelectionModel().getSelectedItem(),
							listUsers.getSelectionModel().getSelectedItem().getUsername(),
							Main.username
						);
				Files.write(file.toPath(), data, StandardOpenOption.CREATE);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void onUsage(ActionEvent event)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("ActivityView.fxml"));
			Scene scene = new Scene(root);
			Main.myStage.setScene(scene);
			Main.myStage.show();
			scene.getWindow().centerOnScreen();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void onChangePassword(ActionEvent event)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("ChangePasswordView.fxml"));
			Scene scene = new Scene(root);
			Main.myStage.setScene(scene);
			Main.myStage.show();
			scene.getWindow().centerOnScreen();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void onExit(ActionEvent event)
	{
		logout();
		Main.isRunning = false;
		Main.service.disconnect();
		Platform.exit();
        System.exit(0);
	}
	
	@FXML
	void onSend(ActionEvent event)
	{
		if (!listUsers.getSelectionModel().isEmpty())
		{
			String to = listUsers.getSelectionModel().getSelectedItem().getUsername();
			String message = txtMessage.getText();
			ChatService.sendMessage(to, message);
			User user = new User(to);
			if (allMessages.containsKey(user))
			{
				allMessages.get(user).add(message);
			}
			else
			{
				ArrayList<String> msgs = new ArrayList<>();
				msgs.add(message);
				allMessages.put(user, msgs);
			}
			txtMessage.clear();
		}
	}
	
	@FXML
	void onBroadcast(ActionEvent event)
	{
		String message = txtMessage.getText();
		ChatService.sendBroadcast(message);
		
		for(User user : listUsers.getItems())
		{
			if (allMessages.containsKey(user))
			{
				allMessages.get(user).add(message);
			}
			else
			{
				ArrayList<String> msgs = new ArrayList<>();
				msgs.add(message);
				allMessages.put(user, msgs);
			}
			txtMessage.clear();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{

		listUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>()
		{

			@Override
			public void changed(ObservableValue arg0, User user, User arg1)
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						if (user != null)
						{
							ArrayList<String> messages = allMessages.get(user);
							if (messages != null)
							{
								listMessages.setItems(FXCollections.observableArrayList(messages));
							}
							else
							{
								allMessages.put(user, new ArrayList<String>());
							}
						}
					}
				});
			}
		});

		ChatThread t = new ChatThread(listUsers, allMessages);
		t.start();

		UserListRequestThread userListThread = new UserListRequestThread();
		userListThread.start();
		
		UserFilesListThread userFilesThread = new UserFilesListThread(listFiles, listUsers);
		userFilesThread.start();
		
		ScreenSendingThread screenSendingThread = new ScreenSendingThread();
		screenSendingThread.start();
		
		adminMessage("");
		Main.myStage.setTitle("Employee " + Main.username);
		
		adminMessage();
	}
	
	public static void sendScreen()
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					BufferedImage originalImage = new Robot()
							.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
					int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					BufferedImage resizedImage = new BufferedImage(1000, 700, type);
					Graphics2D g = resizedImage.createGraphics();
					g.drawImage(originalImage, 0, 0, 1000, 700, null);
					g.dispose();
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ImageIO.write(resizedImage, "jpg", bos);
					byte[] data = bos.toByteArray();
					Base64 codec = new Base64();
					String encodedData = codec.encode(data);
					ChatService.sendScreen(encodedData);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}
