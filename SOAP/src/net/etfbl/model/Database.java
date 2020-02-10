package net.etfbl.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Database
{
	private static String REDIS_ADDRESS = "127.0.0.1";
	private static int REDIS_PORT = 6379;
	
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
	    REDIS_ADDRESS = p.getProperty("REDIS_ADDRESS");
	    REDIS_PORT = Integer.parseInt(p.getProperty("REDIS_PORT"));
		
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/db.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}
	public static User getUser(String username)
	{
		logger.log(Level.INFO, "getUser");
//		User user = null;
//		for(User x : users)
//			if(x.getUsername().equals(username))
//				user = x;
//		return user;
		User user = null;
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try
		{
//			/*TEST*/System.out.println("----------------");
			Jedis jedis = pool.getResource();
//			/*TEST*/System.out.println(username);
			String password = jedis.get("database:user:" + username + ":password");
//			/*TEST*/System.out.println(password);
			boolean blocked = "true".equals(jedis.get("database:user:" + username + ":blocked"));
//			/*TEST*/System.out.println(blocked);
			boolean admin = "true".equals(jedis.get("database:user:" + username + ":admin"));
//			/*TEST*/System.out.println(admin);
			user = new User(username, password, blocked, admin);
//			/*TEST*/System.out.println("----------------");
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		finally
		{
			pool.close();
		}
		return user;
	}
	public static boolean addUser(User user)
	{
		logger.log(Level.INFO, "addUser");
//		return users.add(user);
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.set(("database:user:" + user.getUsername()), user.getUsername());
			jedis.set(("database:user:" + user.getUsername() + ":password"), user.getPassword());
			jedis.set(("database:user:" + user.getUsername() + ":blocked"), "" + user.isBlocked());
			jedis.set(("database:user:" + user.getUsername() + ":admin"), "" + user.isAdmin());
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
		finally
		{
			pool.close();
		}
		return true;
	}
	public static boolean editUser(User user)
	{
		logger.log(Level.INFO, "editUser");
//		User oldUser = null;
//		for(User x : users)
//			if(x.getUsername().equals(user.getUsername()))
//				oldUser = x;
//		users.remove(oldUser);
//		return users.add(user);
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.del("database:user:" + user.getUsername() + ":password");
			jedis.del("database:user:" + user.getUsername() + ":blocked");
			jedis.del("database:user:" + user.getUsername() + ":admin");
			
			jedis.set(("database:user:" + user.getUsername() + ":password"), user.getPassword());
			jedis.set(("database:user:" + user.getUsername() + ":blocked"), "" + user.isBlocked());
			jedis.set(("database:user:" + user.getUsername() + ":admin"), "" + user.isAdmin());
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
		finally
		{
			pool.close();
		}
		return true;
	}
	public static ArrayList<Stat> getStat(String username)
	{
		logger.log(Level.INFO, "getStat");
//		ArrayList<Stat> result = new ArrayList<Stat>();
//		for(Stat x : stats)
//			if(x.getUsername().equals(username))
//				result.add(x);
//		return result;
		ArrayList<Stat> stats = new ArrayList<Stat>();
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
//			/*TEST*/System.out.println("START");
			Set<String> set = jedis.smembers("datetimes-" + username);
			for (String item : set)
			{
				String[] datetime = item.split(":");
				int year = Integer.parseInt(datetime[0]);
				int month = Integer.parseInt(datetime[1]);
				int day = Integer.parseInt(datetime[2]);
				int hour = Integer.parseInt(datetime[3]);
				int minute = Integer.parseInt(datetime[4]);
				int second = Integer.parseInt(datetime[5]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, day, hour, minute, second);
				stats.add(new Stat(username, calendar.getTime()));
//				/*TEST*/System.out.println("GET " + year + "." + month + "." + day + ". " + hour + ":" + minute + ":" + second);
			}
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		finally
		{
			pool.close();
		}
		return stats;
	}
	//Upitno je da li je potrebna ova metoda ispod
//	public static Stat getStat(String username, Date datetime)
//	{
//		Stat stat = null;
//		for(Stat x : stats)
//			if(x.getUsername().equals(username) && x.getDateTime().equals(datetime))
//				stat = x;
//		return stat;
//	}
	public static boolean addStat(Stat stat)
	{
		logger.log(Level.INFO, "addStat");
//		return stats.add(stat);
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			Calendar calendar= Calendar.getInstance();
			calendar.setTime(stat.getDateTime());
			String datetime = "";
			datetime += calendar.get(Calendar.YEAR) + ":";
			datetime += calendar.get(Calendar.MONTH) + ":";
			datetime += calendar.get(Calendar.DAY_OF_MONTH) + ":";
			datetime += calendar.get(Calendar.HOUR) + ":";
			datetime += calendar.get(Calendar.MINUTE) + ":";
			datetime += calendar.get(Calendar.SECOND);
//			/*TEST*/System.out.println("ADD " + datetime);
			jedis.sadd("datetimes-" + stat.getUsername(), datetime);
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
		finally
		{
			pool.close();
		}
		return true;
	}
//	public static boolean addSession(String username, String sessionid)
//	{
//		return sessions.putIfAbsent(username, sessionid) == null;
//	}
//	public static String getSession(String username)
//	{
//		return sessions.get(username);
//	}
//	public static boolean deleteSession(String username, String sessionid)
//	{
//		return sessions.remove(username, sessionid);
//	}
	/*test*/public static void printusers()
	{
//		for(User u : users)
//			System.out.println(u.username + " " + u.password + " " + u.isAdmin() + " " + u.isBlocked());
	}
}
