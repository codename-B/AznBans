package de.bananaco.aznbans.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import de.bananaco.aznbans.AznBans;

public class BanListener extends PlayerListener {

	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if(p.hasPermission("aznbans.bypass"))
			return;
		BanHandler bh = AznBans.getBanHandler();
		if(bh.isBanned(p)) {
			event.setResult(Result.KICK_OTHER);
			event.setKickMessage(bh.getBanReason(p));
		}
	}
}
