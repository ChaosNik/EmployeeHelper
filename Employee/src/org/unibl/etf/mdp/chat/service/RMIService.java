package org.unibl.etf.mdp.chat.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.unibl.etf.mdp.rmi.image.ServerInterface;

public class RMIService
{
	private static String RESOURCES = "resources";
	private static int TCP_PORT = 9000;
	private static int RMI_PORT = 1099;
	private static String POLICY_CLIENT = "client_policyfile.txt";
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
	    RESOURCES = p.getProperty("RESOURCES");
	    TCP_PORT = Integer.parseInt(p.getProperty("TCP_PORT"));
	    RMI_PORT = Integer.parseInt(p.getProperty("RMI_PORT"));
	    POLICY_CLIENT = p.getProperty("POLICY_CLIENT");
	}
	public static boolean working = true;
	
	public static void save(String fileName, byte[] data, String userFrom, String userTo)
	{
		System.setProperty("java.security.policy", RESOURCES + File.separator + POLICY_CLIENT);
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			Registry registry = LocateRegistry.getRegistry(RMI_PORT);

			ServerInterface server = (ServerInterface) registry.lookup("Server");
			server.save(fileName, data, userFrom, userTo);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static byte[] download(String fileName, String userFrom, String userTo)
	{
		byte[] data = null;
		System.setProperty("java.security.policy", RESOURCES + File.separator + POLICY_CLIENT);
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			Registry registry = LocateRegistry.getRegistry(RMI_PORT);

			ServerInterface server = (ServerInterface) registry.lookup("Server");
			data = server.download(fileName, userFrom, userTo);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return data;
	}

	public static List<String> view(String userFrom, String userTo)
	{
		List<String> list = null;
		System.setProperty("java.security.policy", RESOURCES + File.separator + POLICY_CLIENT);
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			Registry registry = LocateRegistry.getRegistry(RMI_PORT);

			ServerInterface server = (ServerInterface) registry.lookup("Server");
			list = server.view(userFrom, userTo);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		if(list == null) list = new ArrayList<String>();
		return list;
	}
}
