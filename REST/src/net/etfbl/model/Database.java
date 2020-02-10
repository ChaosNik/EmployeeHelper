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
//	private static ArrayList<User> users = new ArrayList<User>();
//	private static ArrayList<Stat> stats = new ArrayList<Stat>();
//	private static HashMap<String, String> sessions = new HashMap<String, String>();
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	private static String REDIS_ADDRESS = "127.0.0.1";
	private static int REDIS_PORT = 6379;
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
		
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.flushAll();
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
		finally
		{
			pool.close();
		}
//		users.add(new User("admin", "admin", false, true));
//		users.add(new User("miki", "miki", false, false));
//		users.add(new User("muki", "muki", false, false));
//		users.add(new User("maki", "maki", false, false));
//		
//		stats.add(new Stat("admin", new Date(2019, 11, 10, 10, 0, 0)));
//		stats.add(new Stat("admin", new Date(2019, 11, 10, 10, 0, 0)));
//		stats.add(new Stat("admin", new Date(2019, 11, 10, 10, 0, 0)));
//		stats.add(new Stat("admin", new Date(2019, 11, 10, 10, 0, 0)));
//		stats.add(new Stat("admin", new Date(2019, 11, 10, 10, 0, 0)));
//		
//		stats.add(new Stat("miki", new Date(2019, 11, 10, 10, 0, 10)));
//		stats.add(new Stat("miki", new Date(2019, 11, 10, 10, 0, 10)));
//		stats.add(new Stat("miki", new Date(2019, 11, 10, 10, 0, 10)));
//		stats.add(new Stat("miki", new Date(2019, 11, 10, 10, 0, 10)));
//		stats.add(new Stat("miki", new Date(2019, 11, 10, 10, 0, 10)));
//		
//		stats.add(new Stat("muki", new Date(2019, 11, 10, 10, 0, 20)));
//		stats.add(new Stat("muki", new Date(2019, 11, 10, 10, 0, 20)));
//		stats.add(new Stat("muki", new Date(2019, 11, 10, 10, 0, 20)));
//		stats.add(new Stat("muki", new Date(2019, 11, 10, 10, 0, 20)));
//		stats.add(new Stat("muki", new Date(2019, 11, 10, 10, 0, 20)));
//		
//		stats.add(new Stat("maki", new Date(2019, 11, 10, 10, 0, 30)));
//		stats.add(new Stat("maki", new Date(2019, 11, 10, 10, 0, 30)));
//		stats.add(new Stat("maki", new Date(2019, 11, 10, 10, 0, 30)));
//		stats.add(new Stat("maki", new Date(2019, 11, 10, 10, 0, 30)));
//		stats.add(new Stat("maki", new Date(2019, 11, 10, 10, 0, 30)));
		
		addUser(new User("admin", "admin", false, true));
		addUser(new User("miki", "miki", false, false));
		addUser(new User("muki", "muki", false, false));
		addUser(new User("maki", "maki", false, false));
		
		/*addStat(new Stat("admin", new Date(2019, 11, 10, 10, 0, 0)));
		addStat(new Stat("admin", new Date(2019, 11, 10, 10, 5, 0)));
		addStat(new Stat("admin", new Date(2019, 11, 10, 10, 10, 0)));
		addStat(new Stat("admin", new Date(2019, 11, 10, 10, 15, 0)));
		addStat(new Stat("admin", new Date(2019, 11, 10, 10, 20, 0)));
		
		addStat(new Stat("miki", new Date(2019, 11, 10, 10, 0, 10)));
		addStat(new Stat("miki", new Date(2019, 11, 10, 10, 5, 10)));
		addStat(new Stat("miki", new Date(2019, 11, 10, 10, 10, 10)));
		addStat(new Stat("miki", new Date(2019, 11, 10, 10, 15, 10)));
		addStat(new Stat("miki", new Date(2019, 11, 10, 10, 20, 10)));
		
		addStat(new Stat("muki", new Date(2019, 11, 10, 10, 0, 20)));
		addStat(new Stat("muki", new Date(2019, 11, 10, 10, 5, 20)));
		addStat(new Stat("muki", new Date(2019, 11, 10, 10, 10, 20)));
		addStat(new Stat("muki", new Date(2019, 11, 10, 10, 15, 20)));
		addStat(new Stat("muki", new Date(2019, 11, 10, 10, 20, 20)));
		
		addStat(new Stat("maki", new Date(2019, 11, 10, 10, 0, 30)));
		addStat(new Stat("maki", new Date(2019, 11, 10, 10, 5, 30)));
		addStat(new Stat("maki", new Date(2019, 11, 10, 10, 10, 30)));
		addStat(new Stat("maki", new Date(2019, 11, 10, 10, 15, 30)));
		addStat(new Stat("maki", new Date(2019, 11, 10, 10, 20, 30)));*/
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2019, 11, 10, 10, 0, 0);
		addStat(new Stat("admin", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 0);
		addStat(new Stat("admin", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 0);
		addStat(new Stat("admin", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 0);
		addStat(new Stat("admin", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 0);
		addStat(new Stat("admin", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 0);
		addStat(new Stat("admin", calendar.getTime()));

		calendar.set(2019, 11, 10, 10, 0, 10);
		addStat(new Stat("miki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 10);
		addStat(new Stat("miki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 10);
		addStat(new Stat("miki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 10);
		addStat(new Stat("miki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 10);
		addStat(new Stat("miki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 10);
		addStat(new Stat("miki", calendar.getTime()));

		calendar.set(2019, 11, 10, 10, 0, 20);
		addStat(new Stat("muki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 20);
		addStat(new Stat("muki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 20);
		addStat(new Stat("muki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 20);
		addStat(new Stat("muki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 20);
		addStat(new Stat("muki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 20);
		addStat(new Stat("muki", calendar.getTime()));

		calendar.set(2019, 11, 10, 10, 0, 30);
		addStat(new Stat("maki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 30);
		addStat(new Stat("maki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 30);
		addStat(new Stat("maki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 30);
		addStat(new Stat("maki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 30);
		addStat(new Stat("maki", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 30);
		addStat(new Stat("maki", calendar.getTime()));

//		sessions.putIfAbsent("admin", "000000001");
//		sessions.putIfAbsent("miki", "000000002");
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
