/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.minecraft.command;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import di.dicore.DIApi;
import di.dilogin.BukkitApplication;
import di.dilogin.controller.LangManager;
import di.dilogin.entity.DIUserEntity;
import di.dilogin.repository.DIUserRepository;

/**
 * Command to unregister the account.
 */
public class UnregisterCommand implements CommandExecutor {

	/**
	 * DIUser repository.
	 */
	private static DIUserRepository diUserRepository = DIUserRepository.getInstance();

	/**
	 * Main plugin.
	 */
	public final Plugin plugin = BukkitApplication.getPlugin();

	/**
	 * Main api.
	 */
	public final DIApi api = BukkitApplication.getDIApi();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 0) {
			sender.sendMessage(api.getCoreController().getLangManager().getString("no_args"));
			return false;
		}

		String nick = args[0];
		Optional<DIUserEntity> optUser = diUserRepository.findByMinecraftName(nick);

		if (!optUser.isPresent()) {
			sender.sendMessage(LangManager.getString("no_player").replace("%nick%", nick));
			return true;
		}
		DIUserEntity user = optUser.get();
		Player player = sender.getServer().getPlayer(nick);
		sender.sendMessage(LangManager.getString(nick, "unregister_success"));
		
		if(player!=null)
		player.kickPlayer(LangManager.getString(player, "unregister_kick"));
		
		diUserRepository.delete(user);
		
		return true;
	}

}
