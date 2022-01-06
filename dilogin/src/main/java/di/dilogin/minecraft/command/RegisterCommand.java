/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.minecraft.command;

import di.dicore.DIApi;
import di.dilogin.BukkitApplication;
import di.dilogin.controller.DILoginController;
import di.dilogin.controller.LangManager;
import di.dilogin.minecraft.cache.TmpCache;
import di.dilogin.model.TmpMessage;
import di.dilogin.repository.DIUserRepository;
import di.internal.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

/**
 * Command to register as a user.
 */
public class RegisterCommand implements CommandExecutor {

	/**
	 * DIUser repository.
	 */
	private static final DIUserRepository diUserRepository = DIUserRepository.getInstance();

	/**
	 * Main api.
	 */
	private static final DIApi api = BukkitApplication.getDIApi();

	/**
	 * Accept register emoji reaction.
	 */
	private static final String EMOJI = api.getInternalController().getConfigManager().getString("discord_embed_emoji");

	/**
	 * Reject register emoji reaction.
	 */
	private static final String EMOJI_REJECTED = api.getInternalController().getConfigManager().getString("discord_embed_emoji_rejected");

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (diUserRepository.findByMinecraftName(player.getName()).isPresent()) {
				player.sendMessage(LangManager.getString(player, "register_already_exists"));
				return false;
			}

			if (args.length == 0) {
				player.sendMessage(LangManager.getString(player, "register_arguments"));
				return false;
			}

			String id = arrayToString(args);
			if (!idIsValid(id)) {
				player.sendMessage(LangManager.getString(player, "register_user_not_detected")
						.replace("%user_discord_id%", id.replace(" ", "")));
				return false;
			}

			Optional<User> userOpt = Utils.getDiscordUserById(api.getCoreController().getDiscordApi(),
					Long.parseLong(id));
			if (!userOpt.isPresent()) {
				player.sendMessage(
						LangManager.getString(player, "register_user_not_detected").replace("%user_discord_id%", id));
				return false;
			}

			User user = userOpt.get();

			if (diUserRepository.findByDiscordId(user.getIdLong()).size() >= api.getInternalController().getConfigManager()
					.getInt("register_max_discord_accounts")) {
				player.sendMessage(LangManager.getString(player, "register_max_accounts").replace("%user_discord_id%",
						id.replace(" ", "")));
				return false;
			}

			player.sendMessage(LangManager.getString(user, player, "register_submit"));

			MessageEmbed messageEmbed = getEmbedRegisterMessage(player, user);

			sendMessage(user, player, messageEmbed);

		}
		return true;
	}

	/**
	 * Send message to user register.
	 * 
	 * @param user         Discord user.
	 * @param player       Bukkit player.
	 * @param messageEmbed Embed message.
	 */
	private void sendMessage(User user, Player player, MessageEmbed messageEmbed) {
		String code = TmpCache.getRegisterMessage(player.getName()).get().getCode();
		user.openPrivateChannel().submit()
				.thenAccept(channel -> channel.sendMessage(messageEmbed).submit().thenAccept(message -> {
					message.addReaction(EMOJI).and(message.addReaction(EMOJI_REJECTED)).queue();
					TmpCache.addRegister(player.getName(), new TmpMessage(player, user, message, code));
				}).whenComplete((message, error) -> {
					if (error == null)
						return;

					TextChannel serverChannel = api.getCoreController().getDiscordApi()
							.getTextChannelById(api.getInternalController().getConfigManager().getLong("channel"));

					serverChannel.sendMessage(user.getAsMention()).delay(Duration.ofSeconds(10))
							.flatMap(Message::delete).queue();

					Message serverMessage = serverChannel.sendMessage(messageEmbed).submit().join();
					serverMessage.addReaction(EMOJI).queue();
					TmpCache.addRegister(player.getName(), new TmpMessage(player, user, serverMessage, code));

				}));
	}

	/**
	 * Create the log message according to the configuration.
	 * 
	 * @param player Bukkit player.
	 * @param user   Discord user.
	 * @return Embed message configured.
	 */
	private MessageEmbed getEmbedRegisterMessage(Player player, User user) {
		return DILoginController.getEmbedBase().setTitle(LangManager.getString(player, "register_discord_title"))
				.setDescription(LangManager.getString(user, player, "register_discord_desc")).setColor(
						Utils.hex2Rgb(api.getInternalController().getConfigManager().getString("discord_embed_color"))).build();
	}

	/**
	 * @param string Array of string.
	 * @return Returns a string from array string.
	 */
	private static String arrayToString(String[] string) {
		String respuesta = "";
		for (int i = 0; i < string.length; i++) {
			if (i != string.length - 1) {
				respuesta += string[i] + " ";
			} else {
				respuesta += string[i];
			}
		}
		return respuesta;
	}

	/**
	 * Check if the user entered exists.
	 *
	 * @param id Discord user id.
	 * @return True if user exists.
	 */
	private static boolean idIsValid(String id) {
		try {
			Long.parseLong(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}