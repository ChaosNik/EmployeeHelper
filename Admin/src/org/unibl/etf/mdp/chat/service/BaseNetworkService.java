package org.unibl.etf.mdp.chat.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class BaseNetworkService
{

	private static int TCP_PORT = 9000;
	private static String NETWORK_ADDRESS = "127.0.0.1";
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
	    NETWORK_ADDRESS = p.getProperty("NETWORK_ADDRESS");
	}
	BufferedReader in;
	PrintWriter out;
	Socket sock;

	public boolean connect()
	{
		try
		{
			InetAddress addr = InetAddress.getByName(NETWORK_ADDRESS);
			sock = new Socket(addr, TCP_PORT);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void disconnect()
	{
		try
		{
			out.println("END");
			in.close();
			out.close();
			sock.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
