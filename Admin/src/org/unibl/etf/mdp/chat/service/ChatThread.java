package org.unibl.etf.mdp.chat.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.axis.encoding.Base64;
import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.view.Main;
import org.unibl.etf.mdp.chat.view.ScreenController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;

public class ChatThread extends Thread
{

	public ListView<User> listUsers;
	public HashMap<User, ArrayList<String>> messages;
	public static Image image;

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
	
	public void updateImage(String response)
	{
		String[] params = response.split("#");
		String data = params[1];

		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				Base64 codec = new Base64();
				byte[] decodedContent = codec.decode(data);
				image = new Image(new ByteArrayInputStream(decodedContent));
				ScreenController.updateImage();
			}
		});
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
				if (response.startsWith("SEND_SCREEN#"))
				{
					updateImage(response);
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
			}

		}
	}
}