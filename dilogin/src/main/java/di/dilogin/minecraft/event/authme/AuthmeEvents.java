/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.minecraft.event.authme;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import di.dilogin.repository.DIUserRepository;
import fr.xephi.authme.events.UnregisterByAdminEvent;
import fr.xephi.authme.events.UnregisterByPlayerEvent;

/**
 * AuthMe related events.
 */
public class AuthmeEvents implements Listener {

	/**
	 * DIUser repository.
	 */
	private static DIUserRepository diUserRepository = DIUserRepository.getInstance();

	@EventHandler
	void onUnregisterByAdminEvent(UnregisterByAdminEvent event) {
		unregister(event.getPlayerName());
	}

	@EventHandler
	void onUnregisterByPlayerEvent(UnregisterByPlayerEvent event) {
		Optional<Player> optPlayer = Optional.ofNullable(event.getPlayer());
		if (optPlayer.isPresent())
			unregister(optPlayer.get().getName());
	}

	/**
	 * Unregister user from DILogin.
	 */
	private void unregister(String playerName) {
		diUserRepository.deleteByUserName(playerName);
	}
}
