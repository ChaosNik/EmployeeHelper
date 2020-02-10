package org.unibl.etf.mdp.chat.service;

import java.util.ArrayList;

import org.unibl.etf.mdp.chat.model.User;
import org.unibl.etf.mdp.chat.view.Main;

public class ChatService
{

	public static void sendMessage(String to, String message)
	{
		Main.service.out.println("MESSAGE#" + to + "#" + message);
	}
	public static void sendBroadcast(String message)
	{
		Main.service.out.println("BROADCAST#" + message);
	}
	public static void sendStartScreen(String username)
	{
		Main.service.out.println("START_SCREEN#" + username);
	}
	public static void sendStopScreen(String username)
	{
		Main.service.out.println("STOP_SCREEN#" + username);
	}
	
	public static void requestUserList()
	{
		Main.service.out.println("LIST");
	}

	public static ArrayList<User> getUsers(String response)
	{
		ArrayList<User> users = new ArrayList<>();
		String[] params = response.split("\\$\\$");
		for (String user : params)
		{
			String[] properties = user.trim().split("\\|");
			if (properties.length == 1)
			{
				users.add(new User(properties[0]));
			}
		}
		return users;
	}
}
