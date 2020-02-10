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
	
	public static void sendScreen(String data)
	{
		Main.service.out.println("SEND_SCREEN#" + data);
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
				if(!"admin".equals(properties[0]))
					users.add(new User(properties[0]));
			}
		}
		return users;
	}
}
