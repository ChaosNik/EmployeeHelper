package org.unibl.etf.mdp.rmi.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Server implements ServerInterface
{

	private static String ROOT = "files";
	private static String RESOURCES = "resources";
	private static int RMI_PORT = 1099;
	private static String POLICY_SERVER = "server_policyfile.txt";
	
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
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
	    ROOT = p.getProperty("ROOT");
	    RESOURCES = p.getProperty("RESOURCES");
	    RMI_PORT = Integer.parseInt(p.getProperty("RMI_PORT"));
	    POLICY_SERVER = p.getProperty("POLICY_SERVER");
		    
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/rmi.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}
	public Server()
	{
		super();
	}
	@Override
	public boolean save(String fileName, byte[] data, String userFrom, String userTo) throws RemoteException
	{
		logger.log(Level.INFO, "save");
		try
		{
			File root = new File(ROOT);
			if(!root.exists()) root.mkdir();
			File folderTo = new File(ROOT + File.separator + userTo);
			if(!folderTo.exists()) folderTo.mkdir();
			File folderFrom = new File(ROOT + File.separator + userTo + File.separator + userFrom);
			if(!folderFrom.exists()) folderFrom.mkdir();
			File file = new File(ROOT + File.separator + userTo + File.separator + userFrom + File.separator + fileName);
			if(!file.exists()) file.createNewFile();
			Files.write(file.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
			logger.log(Level.INFO, "File " + fileName + " sent from user " + userFrom + " to user " + userTo);
			return true;
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
	}

	@Override
	public List<String> view(String userFrom, String userTo) throws RemoteException
	{
		logger.log(Level.INFO, "view");
		File root = new File(ROOT);
		if(!root.exists()) root.mkdir();
		File folderTo = new File(ROOT + File.separator + userTo);
		if(!folderTo.exists()) folderTo.mkdir();
		File folderFrom = new File(ROOT + File.separator + userTo + File.separator + userFrom);
		if(!folderFrom.exists()) folderFrom.mkdir();
		ArrayList<String> result = new ArrayList<String>();
		for(File file : folderFrom.listFiles())result.add(file.getName());
		logger.log(Level.INFO, "Files send by user " + userFrom + " to user " + userTo + " were viewed");
		return result;
	}
	@Override
	public byte[] download(String fileName, String userFrom, String userTo) throws RemoteException
	{
		logger.log(Level.INFO, "download");
		try
		{
			File root = new File(ROOT);
			if(!root.exists()) root.mkdir();
			File folderTo = new File(ROOT + File.separator + userTo);
			if(!folderTo.exists()) folderTo.mkdir();
			File folderFrom = new File(ROOT + File.separator + userTo + File.separator + userFrom);
			if(!folderFrom.exists()) folderFrom.mkdir();
			File file = new File(ROOT + File.separator + userTo + File.separator + userFrom + File.separator + fileName);
			if(!file.exists()) file.createNewFile();
			logger.log(Level.INFO, "File " + fileName + " sent by user " + userFrom + " to user " + userTo + " was downloaded");
			return Files.readAllBytes(file.toPath());
		}
		catch (IOException e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
	}
	public static void main(String args[])
	{
		System.setProperty("java.security.policy", RESOURCES + File.separator + POLICY_SERVER);
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		try
		{
			Server server = new Server();
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(RMI_PORT);
			registry.rebind("Server", stub);
			System.out.println("RMI server started...");
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}
}
