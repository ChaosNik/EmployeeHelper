package org.unibl.etf.mdp.rmi.client;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.unibl.etf.mdp.rmi.image.ServerInterface;

public class RMIClient
{
	public static final String PATH = "resources";
	public static final int TCP_PORT = 9000;
	public static boolean working = true;

	/*public static void main(String[] args)
	{
		System.setProperty("java.security.policy", PATH + File.separator + "client_policyfile.txt");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			Registry registry = LocateRegistry.getRegistry(1099);

			ServerInterface server = (ServerInterface) registry.lookup("Server");
			byte[] data = server.download("image.jpg", "miki");
			System.out.println(data);
			server.save("slika.jpg", data, "muki");
			System.out.println("Client process done!!!");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}*/
}