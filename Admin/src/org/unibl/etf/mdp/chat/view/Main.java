package org.unibl.etf.mdp.chat.view;

import java.util.List;

import org.unibl.etf.mdp.chat.service.BaseNetworkService;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application
{

	public static BaseNetworkService service;
	public static String username;
	public static String selectedUser;
	public static boolean isRunning = true;
	public static Stage myStage = null;
	
	@Override
	public void start(Stage stage)
	{
		System.out.println("Admin...");
		try
		{
			myStage = stage;
			Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));

			Scene scene = new Scene(root);
			stage.setResizable(false);
			//stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("Admin ");
			stage.setScene(scene);
			stage.show();
			scene.getWindow().centerOnScreen();

			service = new BaseNetworkService();
			service.connect();

			stage.setOnCloseRequest(new EventHandler()
			{
				@Override
				public void handle(Event arg0)
				{
					arg0.consume();
					/*isRunning = false;
					service.disconnect();*/
				}
			});

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
