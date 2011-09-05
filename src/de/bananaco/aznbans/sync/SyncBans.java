package de.bananaco.aznbans.sync;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.util.config.Configuration;

import de.bananaco.aznbans.AznBans;
import de.bananaco.aznbans.handler.BanHandler;

public class SyncBans extends Thread {
	public final Configuration c;
	public SyncBans(Configuration c) {
		this.c = c;
	}
	
	public void run() {
		syncBans();
		interrupt();
	}
	
	public void syncBans() {
		for(String host : c.getKeys("servers")!=null ? c.getKeys("servers") : new ArrayList<String>()) {
			int port = c.getInt("servers."+host, 495);
			try {
			String bans = SyncBans.performQuery(host, port);
			Map<String, String> banMap = BanHandler.parseBans(bans);
			for(String key : banMap.keySet())
				AznBans.getBanHandler().queryBan(key, banMap.get(key),host);
			banMap.clear();
			} catch (Exception e) {
			System.err.println("[AznBans] Error syncing banlist for " + host +":" + port + "!");
			}
		}
	}
	
	public static String performQuery(String host, int port) throws Exception {
	System.out.println("[AznBans] Checking bans on " + host + ":" + port);
	String query = "QUERY";
	Socket socket = new Socket(host, port);

	InputStreamReader isr = new InputStreamReader(socket.getInputStream());
	BufferedReader in = new BufferedReader(isr);

	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	out.println(query);

	String line = "";
	StringBuffer sb = new StringBuffer();
	while ((line = in.readLine()) != null) {
		sb.append(line);
	}
	return sb.toString();
	}
}
