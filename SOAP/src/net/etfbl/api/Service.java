package net.etfbl.api;

import net.etfbl.model.Database;
import net.etfbl.model.User;

public class Service
{
	public boolean newUser(String username, User user)
	{
		return Database.addUser(user);
	}
	public boolean blockUser(String adminUsername, String username)
	{
		User user = Database.getUser(username);
		user.block();
		return Database.editUser(user);
	}
}