package de.bananaco.aznbans;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import de.bananaco.aznbans.handler.BanHandler;
import de.bananaco.aznbans.handler.BanListener;
import de.bananaco.aznbans.net.QueryServer;
import de.bananaco.aznbans.sync.SyncBans;

public class AznBans extends JavaPlugin {
	private Configuration c;
	private int queryPort;
	private String queryIp;
	private QueryServer qs;
	private static BanHandler bh;
	
	public static BanHandler getBanHandler() {
		return bh;
	}
	public void aznMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.AQUA + "[AznBans] " + ChatColor.GREEN + message);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length==0)
			return false;
		
		/*
		 * Can't spell banana without "ban"
		 */
		if(command.getName().equalsIgnoreCase("ban") || command.getName().equalsIgnoreCase("unban")) {
			
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(!p.hasPermission("aznbans.ban")) {
					aznMessage(sender, "You don't have permission to do that!");
					return true;
				}
			}
			
			
			String playername;
				playername = args[0];
				
				if(command.getName().equalsIgnoreCase("unban"))
				{
					bh.unban(playername);
					aznMessage(sender, playername+" was unbanned");
					return true;
				}
				
			if(args.length==1) {
				bh.ban(playername);
				aznMessage(sender, playername + " was banned");
				Player p = getServer().getPlayer(playername);
				if(p!=null)
					p.kickPlayer("You were banned!");
			return true;
			}
			else if(args.length>1) {
				String banreason = "";
				
				for(int i=1; i<args.length; i++)
				banreason = banreason + args[i] + " ";
			banreason.trim();
			bh.ban(playername, banreason);
			aznMessage(sender, playername + " was banned with reason: " + banreason);
			Player p = getServer().getPlayer(playername);
			if(p!=null)
				p.kickPlayer(banreason);
			}
			return true;
		}
		/*
		 * Kicking off to a good start 
		 */
		if(command.getName().equalsIgnoreCase("kick")) {
			
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(!p.hasPermission("aznbans.kick")) {
					aznMessage(sender, "You don't have permission to do that!");
					return true;
				}
			}
			
			String playername;
			playername = args[0];
		if(args.length==1) {
			Player p = getServer().getPlayer(playername);
			if(p!=null) {
				p.kickPlayer("You were kicked!");
				aznMessage(sender, "kicked " + p.getName());
				return true;
			}
			else {
				aznMessage(sender, playername + " is not online to be kicked!");
				return true;
			}
		}
		else if(args.length>1) {
			String kickreason = "";
			
			for(int i=1; i<args.length; i++)
			kickreason = kickreason + args[i] + " ";
		kickreason.trim();
		Player p = getServer().getPlayer(playername);
		if(p!=null) {
			p.kickPlayer(kickreason);
			aznMessage(sender, "kicked " + p.getName() + " with reason: " + kickreason);
			return true;
		}
		else {
			aznMessage(sender, playername + " is not online to be kicked!");
			return true;
		}
		}
		}
		return false;
	}

	@Override
	public void onDisable() {
		try {
		qs.getListener().close();
		qs.interrupt();
		} catch (Exception e) {
			System.err.println("[AznBans] Error disabling plugin.");
		}
		
		System.out.println("[AznBans] Disabled.");
	}
	
	@Override
	public void onEnable() {
		setupConfig();
		try {
		getServer().getPluginManager().addPermission(new Permission("azbans.kick",PermissionDefault.OP));
		getServer().getPluginManager().addPermission(new Permission("azbans.ban",PermissionDefault.OP));
		getServer().getPluginManager().addPermission(new Permission("azbans.bypass",PermissionDefault.OP));
		} catch (Exception e) {
			System.err.println("[AznBans] Permissions error. Blame Dinnerbone.");
		}
		bh = new BanHandler();
		qs = new QueryServer(queryIp, queryPort);
		try {

		qs.startListener();

		qs.start();

		new SyncBans(c).start();

		} catch(Exception e) {
		System.err.println("[AznBans] Socket error!");
		}
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, new BanListener(), Priority.Normal, this);
		System.out.println("[AznBans] Enabled.");
	}
	public void setupConfig() {
		// What would you say my defining characteristic is?
		this.c = getConfiguration();
		// Loads of people say it's my eyes.
		c.load();
		// Glass of port?
		c.setProperty("query-port", c.getInt("query-port",495));
		this.queryPort = c.getInt("query-port", 495);
		// Where are you localhosting this party from anyway?
		c.setProperty("query-ip", c.getString("query-ip","ANY"));
		this.queryIp = c.getString("query-ip","ANY");
		// Save your fear, I have already eaten.
		c.save();
	}
}
