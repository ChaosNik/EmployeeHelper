package org.unibl.etf.mdp.chat.view;

import org.unibl.etf.mdp.chat.service.LoginService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class LoginViewController
{
	private static String REST_LOGINADMIN = "http://localhost:8080/REST/api/rest/loginadmin/";
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
	    REST_LOGINADMIN = p.getProperty("REST_LOGINADMIN");
	}
	@FXML
	private TextField txtUsername;
	
	@FXML
	private PasswordField txtPassword;

	@FXML
	private Button btnLogin;
	
	@FXML
	private Button btnExit;

	@FXML
	public void login(ActionEvent event)
	{
		Main.username = txtUsername.getText();
		String password = txtPassword.getText();
		
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(REST_LOGINADMIN + Main.username);
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.post(Entity.json(password));

		if(("SUCCESS").equals(response.readEntity(String.class)))
		{
			LoginService.login(txtUsername.getText());
			final Node source = (Node) event.getSource();
			final Stage stage = (Stage) source.getScene().getWindow();
			try
			{
				Parent root = FXMLLoader.load(getClass().getResource("MessageView.fxml"));
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
				scene.getWindow().centerOnScreen();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Prijava na sistem nije uspjesna");
			alert.setContentText("Unesite drugo korisnicko ime");
			alert.showAndWait();
		}
		
		/*if (LoginService.login(txtUsername.getText()))
		{
			final Node source = (Node) event.getSource();
			final Stage stage = (Stage) source.getScene().getWindow();
			try
			{
				Parent root = FXMLLoader.load(getClass().getResource("MessageView.fxml"));
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Prijava na sistem nije uspjesna");
			alert.setContentText("Unesite drugo korisnicko ime");
			alert.showAndWait();
		}*/
	}
	
	@FXML
	void onExit(ActionEvent event)
	{
		Platform.exit();
        System.exit(0);
	}
}
