package org.unibl.etf.mdp.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ServerThread extends Thread
{

	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private User user;
	
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	static
	{
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/chat.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}

	public ServerThread(Socket sock)
	{
		this.sock = sock;
		try
		{
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
		}
		catch (Exception e)
		{
			//logger.log(Level.INFO, e.toString(), e);
		}
	}

	public boolean login(String username)
	{
		logger.log(Level.INFO, "login");
		user = new User(username, out);

		if(!Server.users.contains(user))
		{
			Server.users.add(user);
			return true;
		}
		return false;
	}

	public void sendMessage(String from, String to, String message)
	{
		logger.log(Level.INFO, "sendMessage");
		int index = Server.users.indexOf(new User(to, null));
		if (index > -1)
		{
			User user = Server.users.get(index);
			PrintWriter out = user.getPrintWriter();
			out.println(Protocol.MESSAGE + from + Protocol.SEPARATOR + message);
		}
	}
	
	public void sendBroadcast(String from, String message)
	{
		logger.log(Level.INFO, "sendBroadcast");
		for(User user : Server.users)
		{
			PrintWriter out = user.getPrintWriter();
			out.println(Protocol.MESSAGE + from + Protocol.SEPARATOR + message);
		}
	}

	public void logout()
	{
		logger.log(Level.INFO, "logout");
		Server.users.remove(user);
	}
	
	public void sendStartScreen(String username)
	{
		logger.log(Level.INFO, "sendStartScreen");
		int index = Server.users.indexOf(new User(username, null));
		if (index > -1)
		{
			User user = Server.users.get(index);
			PrintWriter out = user.getPrintWriter();
			out.println(Protocol.START_SCREEN);
		}
	}
	
	public void sendStopScreen(String username)
	{
		logger.log(Level.INFO, "sendStopScreen");
		int index = Server.users.indexOf(new User(username, null));
		if (index > -1)
		{
			User user = Server.users.get(index);
			PrintWriter out = user.getPrintWriter();
			out.println(Protocol.STOP_SCREEN);
		}
	}
	
	public void sendScreen(String screen)
	{
		logger.log(Level.INFO, "sendScreen");
		int index = Server.users.indexOf(new User("admin", null));
		if (index > -1)
		{
			User user = Server.users.get(index);
			PrintWriter out = user.getPrintWriter();
			out.println(Protocol.SEND_SCREEN + Protocol.SEPARATOR + screen);
		}
	}

	public String getUserList()
	{
		logger.log(Level.INFO, "getUserList");
		return Server.users.stream().filter(user -> !user.getUsername().equals(this.user.getUsername()))
				.map(user -> user.toString()).collect(Collectors.joining("$$"));
	}

	@Override
	public void run()
	{
		logger.log(Level.INFO, "run");
		String request;
		try
		{
			while (!Protocol.END.equals(request = in.readLine()))
			{
				try
				{
					if (request == null)
					{
						request = "";
					}
					if (request.startsWith(Protocol.LOGIN))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						boolean status = false;
						if (params.length == 2) {
							status = login(params[1]);
						}

						if (!status)
						{
							out.println(ErrorMessage.INVALID_LOGIN);
						}
						else
						{
							out.println(Protocol.OK);
						}
					}
					if (request.startsWith(Protocol.MESSAGE))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 3)
						{
							sendMessage(user.getUsername(), params[1], params[2]);
						}
						else
						{
							out.println(ErrorMessage.INVALID_REQUEST);
						}
					}
					
					if (request.startsWith(Protocol.BROADCAST))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 2)
						{
							sendBroadcast(user.getUsername(), params[1]);
						}
						else
						{
							out.println(ErrorMessage.INVALID_REQUEST);
						}
					}
					
					if (request.startsWith(Protocol.START_SCREEN))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 2)
							sendStartScreen(params[1]);
						else
							out.println(ErrorMessage.INVALID_REQUEST);
					}
					
					if (request.startsWith(Protocol.STOP_SCREEN))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 2)
							sendStopScreen(params[1]);
						else
							out.println(ErrorMessage.INVALID_REQUEST);
					}
					
					if (request.startsWith(Protocol.SEND_SCREEN))
					{
						String[] params = request.split(Protocol.SEPARATOR);
						if (params.length == 2)
							sendScreen(params[1]);
						else
							out.println(ErrorMessage.INVALID_REQUEST);
					}

					if (Protocol.LIST.startsWith(request))
					{
						out.println(Protocol.LIST + Protocol.SEPARATOR + getUserList());
					}
				}
				catch (Exception e)
				{
					logger.log(Level.INFO, e.toString(), e);
				}
			}

			in.close();
			out.close();
			sock.close();
		}
		catch (IOException e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
		finally
		{
			logout();
		}

	}

}
