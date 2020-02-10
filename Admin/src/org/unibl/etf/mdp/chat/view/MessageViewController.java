package org.unibl.etf.mdp.chat.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.service.ChatService;
import org.unibl.etf.mdp.chat.service.ChatThread;
import org.unibl.etf.mdp.chat.service.LoginService;
import org.unibl.etf.mdp.chat.service.RMIService;
import org.unibl.etf.mdp.chat.service.UserFilesListThread;
import org.unibl.etf.mdp.chat.service.UserListRequestThread;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import net.etfbl.api.Service;
import net.etfbl.api.ServiceServiceLocator;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MessageViewController implements Initializable
{
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
	    REST_LOGOUT = p.getProperty("REST_LOGOUT");
	    MULTICAST_ADDRESS = p.getProperty("MULTICAST_ADDRESS");
	    MULTICAST_PORT = Integer.parseInt(p.getProperty("MULTICAST_PORT"));
	}
	@FXML
	private TextField txtAdminMessage;
	
	@FXML
	private Button btnMulticast;
	
	@FXML
	private Button btnExit;

	@FXML
	private ListView<User> listUsers;
	
	@FXML
	private ListView<String> listFiles;

	@FXML
	private Button btnUsage;
	
	@FXML
	private Button btnRemoteScreen;
	
	@FXML
	private Button btnBlockUser;

	@FXML
	private TextField txtUsername;
	
	@FXML
	private PasswordField txtPassword;
	
	@FXML
	private Button btnNewUser;
	// cuvamo parove korisnika i svih poruka koje smo od njih dobili
	private HashMap<User, ArrayList<String>> allMessages = new HashMap<>();

	void logout()
	{
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOGOUT + Main.username);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
	}
	
	@FXML
	void onMulticast(ActionEvent event)
	{
		String notificationText = txtAdminMessage.getText();
		InetAddress address = null;
		try
		{
			//address = InetAddress.getByName(prop.getProperty("IP_ADDRESS_MULTICAST"));
			address = InetAddress.getByName(MULTICAST_ADDRESS);
		}
		catch (UnknownHostException e1)
		{
			//LOGGER.log(Level.SEVERE, e1.getMessage());
			return;
		}

		try (DatagramSocket socket = new DatagramSocket())
		{

			//Integer PORT = Integer.parseInt(prop.getProperty("PORT_MULTICAST"));
			Integer PORT = MULTICAST_PORT;
			DatagramPacket packet = new DatagramPacket(notificationText.getBytes(), notificationText.getBytes().length,
					address, PORT);
			socket.send(packet);
			txtAdminMessage.setText("");
		}
		catch (IOException e)
		{
			//LOGGER.log(Level.SEVERE, e.getMessage());
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
	void onRemoteScreen(ActionEvent event)
	{
		try
		{
			ChatService.sendStartScreen(Main.selectedUser);
			
			ScreenController.imageScreen = new ImageView(ChatThread.image);
			
			Parent root = FXMLLoader.load(getClass().getResource("ScreenView.fxml"));
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
	void onBlockUser(ActionEvent event)
	{
		ServiceServiceLocator locator = new ServiceServiceLocator();
		try
		{
			net.etfbl.model.User user = new net.etfbl.model.User(false, false, txtPassword.getText(), txtUsername.getText());
			Service service = locator.getService();
			boolean result = service.blockUser(Main.username, Main.selectedUser);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void onNewUser(ActionEvent event)
	{
		ServiceServiceLocator locator = new ServiceServiceLocator();
		try
		{
			net.etfbl.model.User user = new net.etfbl.model.User(false, false, txtPassword.getText(), txtUsername.getText());
			Service service = locator.getService();
			System.out.println();
			boolean result = service.newUser(Main.username, user);
			
			txtUsername.setText("");
			txtPassword.setText("");
		}
		catch(Exception e)
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
							Main.selectedUser = user.getUsername();
							//System.out.println(Main.selectedUser);
							ArrayList<String> messages = allMessages.get(user);
							if (messages != null)
							{
								//listMessages.setItems(FXCollections.observableArrayList(messages));
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
		
		Main.myStage.setTitle("Admin " + Main.username);
	}

}
