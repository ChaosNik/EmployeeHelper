package org.unibl.etf.mdp.chat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.view.Main;
import org.unibl.etf.mdp.chat.view.MessageViewController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

public class ChatThread extends Thread
{

	public ListView<User> listUsers;
	public HashMap<User, ArrayList<String>> messages;
	public static Boolean sendScreen = false;

	public ChatThread(ListView<User> listUsers, HashMap<User, ArrayList<String>> messages) {
		this.listUsers = listUsers;
		this.messages = messages;
	}

	public void updateUserList(String response)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<User> users = ChatService.getUsers(response);
				listUsers.setItems(FXCollections.observableArrayList(users));
			}
		});
	}

	public void updateMessageList(String response)
	{
		String[] params = response.split("#");
		String from = params[1];
		String message = params[2];

		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				User user = new User(from);
				if (!messages.containsKey(user))
				{
					messages.put(user, new ArrayList<String>());
				}
				messages.get(user).add(message);				
			}
		});
	}
	
	public void upadateSendScreen(boolean startstop)
	{
		sendScreen = startstop;
		//MessageViewController.sendScreen();
	}

	@Override
	public void run()
	{
		while (Main.isRunning)
		{
			String response;
			try
			{
				response = Main.service.in.readLine();
				if (response == null)
				{
					response = "";
				}
				if (response.startsWith("LIST#"))
				{
					String[] params = response.split("#");
					if (params.length == 2)
					{
						updateUserList(params[1]);
					}
				}
				if (response.startsWith("MESSAGE#"))
				{
					updateMessageList(response);
				}
				
				if(response.startsWith("START_SCREEN"))
				{
					upadateSendScreen(true);
				}
				
				if(response.startsWith("STOP_SCREEN"))
				{
					upadateSendScreen(false);
				}
			}
			catch (IOException e)
			{
				//e.printStackTrace();
			}

		}
	}
}