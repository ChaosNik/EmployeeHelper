package org.unibl.etf.mdp.chat.server;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

public class Server
{

	public static int TCP_PORT = 9000;
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
	    TCP_PORT = Integer.parseInt(p.getProperty("TCP_PORT"));
	}
	public static ArrayList<User> users = new ArrayList<>();

	public static void main(String[] args)
	{

		try
		{
			ServerSocket ss = new ServerSocket(TCP_PORT);
			System.out.println("Chat server running...");
			while (true)
			{
				Socket sock = ss.accept();
				new ServerThread(sock).start();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
