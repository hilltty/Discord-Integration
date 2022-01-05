/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.minecraft.event.authme;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import di.dilogin.controller.LangManager;
import di.dilogin.model.CodeGenerator;
import di.dilogin.entity.DIUserEntity;
import di.dilogin.model.TmpMessage;
import di.dilogin.minecraft.cache.TmpCache;
import di.dilogin.minecraft.event.UserLoginEvent;
import di.dilogin.minecraft.event.custom.DILoginEvent;
import di.dilogin.repository.DIUserRepository;
import fr.xephi.authme.events.LoginEvent;

public class UserLoginEventAuthmeImpl implements UserLoginEvent {

	/**
	 * DIUser repository.
	 */
	private static DIUserRepository diUserRepository = DIUserRepository.getInstance();
	
	@Override
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		String playerName = event.getPlayer().getName();
		if (!diUserRepository.findByMinecraftName(playerName).isPresent()) {
			initPlayerRegisterRequest(event, playerName);
		} else {
			initPlayerLoginRequest(event, playerName);
		}
	}

	@Override
	public void initPlayerLoginRequest(PlayerJoinEvent event, String playerName) {
		TmpCache.addLogin(playerName, null);
		Optional<DIUserEntity> userOpt = diUserRepository.findByMinecraftName(playerName);
		if (!userOpt.isPresent())
			return;

		DIUserEntity user = userOpt.get();

		event.getPlayer().sendMessage(LangManager.getString(user, "login_request"));
		sendLoginMessageRequest(user.getPlayerBukkit().get(), user.getPlayerDiscord());
	}

	@Override
	public void initPlayerRegisterRequest(PlayerJoinEvent event, String playerName) {
		String code = CodeGenerator
				.getCode(api.getInternalController().getConfigManager().getInt("register_code_length"), api);
		TmpCache.addRegister(playerName, new TmpMessage(event.getPlayer(), null, null, code));
	}

	/**
	 * Event when a user logs in with authme.
	 * 
	 * @param event LoginEvent.
	 */
	@EventHandler
	public void onAuth(final LoginEvent event) {
		String playerName = event.getPlayer().getName();

		if (!diUserRepository.findByMinecraftName(playerName).isPresent()) {
			initPlayerAuthmeRegisterRequest(event, playerName);
		}

		Bukkit.getScheduler().runTask(api.getInternalController().getPlugin(),
				() -> Bukkit.getPluginManager().callEvent(new DILoginEvent(event.getPlayer())));
	}

	/**
	 * If the user logged in with Authme is not registered with DILogin, it prompts
	 * him to register.
	 * 
	 * @param event      Authme login event.
	 * @param playerName Player's name.
	 */
	public void initPlayerAuthmeRegisterRequest(LoginEvent event, String playerName) {
		String code = CodeGenerator
				.getCode(api.getInternalController().getConfigManager().getInt("register_code_length"), api);
		String command = api.getCoreController().getBot().getPrefix() + api.getInternalController().getConfigManager().getString("register_command") + " " + code;
		TmpCache.addRegister(playerName, new TmpMessage(event.getPlayer(), null, null, code));
		event.getPlayer().sendMessage(LangManager.getString(event.getPlayer(), "register_opt_request")
				.replace("%register_command%", command));
	}
}
