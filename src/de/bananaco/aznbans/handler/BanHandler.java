package de.bananaco.aznbans.handler;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class BanHandler {
	private final Configuration bans;
	private final String banpath;
	private final String reasons;
	public BanHandler() {
		this.bans = new Configuration(new File("plugins/AznBans/bans.yml"));
		this.banpath = "players.";
		this.reasons = "reasons.";
		this.bans.load();
	}
	public boolean isBanned(Player player) {
		return isBanned(player.getName());
	}
	public boolean isBanned(String player) {
		return bans.getBoolean(banpath+player, false);
	}
	public String getBanReason(Player player) {
	return getBanReason(player.getName());	
	}
	public String getBanReason(String player) {
	return bans.getString(reasons+player, "You are banned from this server!");
	}
	public void ban(Player player) {
	ban(player.getName());
	}
	public void ban(String player) {
	bans.setProperty(banpath+player, true);
	bans.save();
	}
	public void ban(Player player, String reason) {
	ban(player.getName(),reason);
	}
	public void queryBan(String player, String reason, String from) {
		if(bans.getBoolean(banpath+player, false))
			return;
		
		bans.setProperty(banpath+player, true);
		bans.setProperty(reasons+player, reason);
		bans.save();
		System.out.println(player + " was banned added to the banlist due to a ban on " + from);
	}
	public void ban(String player, String reason) {
	bans.setProperty(banpath+player, true);
	bans.setProperty(reasons+player, reason);
	bans.save();
	}
	public void unban(Player player) {
	unban(player.getName());
	}
	public void unban(String player) {
	bans.removeProperty(banpath+player);
	bans.removeProperty(reasons+player);
	bans.save();
	}
	public void reload() {
	bans.load();
	bans.save();
	}
	public String getAllBans() {
		List<String> keys = bans.getKeys("players");
		String[] players = new String[keys.size()];
		for(int i=0; i<keys.size(); i++) {
		players[i] = keys.get(i)+"="+getBanReason(keys.get(i));
		}
		return Arrays.toString(players);
	}
	public static Map<String, String> parseBans(String bans) {
		String b = bans.replace("[", "").replace("]", "");
		String[] bls = b.split(", ");
		if(bls==null)
			return new HashMap<String, String>();
		HashMap<String, String> hm = new HashMap<String, String>();
		for(String br : bls) {
			String[] bt = br.split("=");
			hm.put(bt[0], bt[1]);
		}
		return hm;
	}
}