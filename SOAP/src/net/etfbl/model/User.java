package net.etfbl.model;

public class User
{
	public String username;
	public String password;
	public boolean blocked;
	public boolean admin;
	public User()
	{
		username = "";
		password = "";
		blocked = true;
		admin = false;
	}
	public User(String username, String password, boolean blocked, boolean admin)
	{
		this.username = username;
		this.password = password;
		this.blocked = blocked;
		this.admin = admin;
	}
	public void block()
	{
		blocked = true;
	}
	public void unblock()
	{
		blocked = false;
	}
	public String getUsername()
	{
		return username;
	}
	public String getPassword()
	{
		return password;
	}
	public boolean getBlocked()
	{
		return blocked;
	}
	public boolean getAdmin()
	{
		return admin;
	}
	public boolean isBlocked()
	{
		return blocked;
	}
	public boolean isAdmin()
	{
		return admin;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}
	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}
}
