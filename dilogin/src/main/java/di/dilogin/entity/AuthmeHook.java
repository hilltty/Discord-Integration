/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.entity;

import org.bukkit.entity.Player;

import fr.xephi.authme.api.v3.AuthMeApi;

/**
 * Class that interacts with the authme api
 */
public class AuthmeHook {

	/**
	 * Prohibits instantiation of the class.
	 */
	private AuthmeHook() {
		throw new IllegalStateException();
	}

	/**
	 * Authme api.
	 */
	private static final AuthMeApi authmeApi = AuthMeApi.getInstance();

	/**
	 * Start the player session
	 * 
	 * @param player Bukkit player.
	 */
	public static void login(Player player) {
		if (authmeApi.isRegistered(player.getName()))
			authmeApi.forceLogin(player);
	}

	/**
	 * Register a player
	 * 
	 * @param player   Bukkit player.
	 * @param password Default password.
	 */
	public static void register(Player player, String password) {
		if (!authmeApi.isRegistered(player.getName()))
			authmeApi.forceRegister(player, password, true);
	}
}
