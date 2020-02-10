package net.etfbl.api;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import net.etfbl.model.Database;
import net.etfbl.model.Stat;
import net.etfbl.model.User;

@Path("/rest")
public class APIService
{
	
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	static
	{
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/rest.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}
//	@POST
//	@Path("/login/{username}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response login(@PathParam("username") String username, String passwordHash)
//	{
//		//Ovdje bi bilo potrebno implementirati dekripciju i poredjenje hesa
//		String password = passwordHash;
//		User user = Database.getUser(username);	
//		if(user == null)
//			return Response.status(500).entity("User doesn't exist!").build();
//		if(!user.getPassword().equals(password))
//			return Response.status(500).entity("Wrong password!").build();
//		Database.addStat(new Stat(username, new Date()));
//	   String sessionid = "" + new Random().nextInt();
//	    if(!Database.addSession(username, sessionid))
//	    	return Response.status(500).entity("Unable to login!").build();
//		return Response.status(200).entity(sessionid).build();
//		//Bilo bi najbolje da vraca kriptovan sessionid
//	}
	@POST
	@Path("/login/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@PathParam("username") String username, String password)
	{
		logger.log(Level.INFO, "login");
		User user = Database.getUser(username);
		if(user == null)
			return Response.status(204).entity("User doesn't exist!").build();
		if(user.isBlocked())
			return Response.status(204).entity("You are blocked by Admin!").build();
		if(!user.getPassword().equals(password))
			return Response.status(204).entity("Wrong password!").build();
		Database.addStat(new Stat(username, new Date()));
	    /*TEST*/Database.printusers();
		return Response.status(200).entity("SUCCESS").build();
	}
	@POST
	@Path("/loginadmin/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginadmin(@PathParam("username") String username, String password)
	{
		logger.log(Level.INFO, "loginadmin");
		User user = Database.getUser(username);	
		if(user == null)
			return Response.status(204).entity("User doesn't exist!").build();
		if(!user.admin)
			return Response.status(204).entity("User isn't admin!").build();
		if(!user.getPassword().equals(password))
			return Response.status(204).entity("Wrong password!").build();
		Database.addStat(new Stat(username, new Date()));
	    /*TEST*/Database.printusers();
		return Response.status(200).entity("SUCCESS").build();
	}
//	@GET
//	@Path("/logout/{username}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response logout(@PathParam("username") String username, String sessionidHash)
//	{
//		//Ovdje bi bilo potrebno implementirati dekripciju i poredjenje hesa
//		String sessionid = sessionidHash;
//		if
//		(
//				Database.getSession(username) != null &&
//				!Database.getSession(username).equals(sessionid)
//		)
//			return Response.status(500).entity("Wrong session!").build();
//		User user = Database.getUser(username);	
//		if(user == null)
//			return Response.status(500).entity("User doesn't exist!").build();
//		Database.addStat(new Stat(username, new Date()));
//	    if(!Database.deleteSession(username, sessionid))
//	    	return Response.status(500).entity("Unable to logout!").build();
//		return Response.status(200).entity("SUCCESS").build();
//	}
	@GET
	@Path("/logout/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam("username") String username)
	{
		logger.log(Level.INFO, "logout");
		User user = Database.getUser(username);	
		if(user == null)
			return Response.status(204).entity("User doesn't exist!").build();
		Database.addStat(new Stat(username, new Date()));
		return Response.status(200).entity("SUCCESS").build();
	}
	@GET
	@Path("/activity/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activity(@PathParam("username") String username)
	{
		logger.log(Level.INFO, "activity");
		ArrayList<Stat> result = Database.getStat(username);
		if(result == null)
			return Response.status(204).entity("User has no activity!").build();
		/*TEST*/Database.printusers();
		return Response.status(200).entity(result.toArray()).build();
	}
//	@POST
//	@Path("/pass/{username}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response changePassword(@PathParam("username") String username, String data)
//	{
//		if(!data.matches(".+;.+;.+"))
//			return Response.status(500).entity("Wrong data format!!").build();
//		String splitter = ";";
//		String oldPasswordHash = data.split(splitter)[0];
//		String newPasswordHash = data.split(splitter)[1];
//		String sessionid = data.split(splitter)[2];
//		//Ovdje bi bilo potrebno implementirati dekripciju i poredjenje hesa
//		if
//		(
//				Database.getSession(username) != null &&
//				!Database.getSession(username).equals(sessionid)
//		)
//			return Response.status(500).entity("Wrong session!").build();
//		User user = Database.getUser(username);
//		if(user.getPassword().equals(oldPasswordHash))
//		{
//			user.setPassword(newPasswordHash);
//			if(!Database.editUser(user))
//				return Response.status(500).entity("Database error!").build();
//		}
//		else
//			return Response.status(500).entity("Wrong password!").build();
//		return Response.status(200).entity("SUCCESS").build();
//	}
	@POST
	@Path("/pass/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@PathParam("username") String username, String passwords)
	{
		logger.log(Level.INFO, "changePassword");
		if(!passwords.matches(".+;.+"))
			return Response.status(204).entity("Wrong data format!!").build();
		String splitter = ";";
		String oldPassword = passwords.split(splitter)[0];
		String newPassword = passwords.split(splitter)[1];
		User user = Database.getUser(username);
		if(user.getPassword().equals(oldPassword))
		{
			user.setPassword(newPassword);
			if(!Database.editUser(user))
				return Response.status(503).entity("Database error!").build();
		}
		else
			return Response.status(204).entity("Wrong password!").build();
		/*TEST*/Database.printusers();
		return Response.status(200).entity("SUCCESS").build();
	}
}
