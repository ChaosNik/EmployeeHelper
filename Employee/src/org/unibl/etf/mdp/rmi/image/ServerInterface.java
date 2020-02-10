package org.unibl.etf.mdp.rmi.image;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote
{
	//Save data sent by userFrom to userTo
	public boolean save(String fileName, byte[] data, String userFrom, String userTo) throws RemoteException;
	//List all files in <user>'s directory
	public List<String> view(String userFrom, String userTo) throws RemoteException;
	//Get file sent by userFrom to userTo
	public byte[] download(String fileName, String userFrom, String userTo) throws RemoteException;
}