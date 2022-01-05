/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.discord.command;

import java.time.Duration;
import java.util.Optional;

import org.bukkit.entity.Player;

import di.dicore.DIApi;
import di.dilogin.BukkitApplication;
import di.dilogin.controller.DILoginController;
import di.dilogin.controller.LangManager;
import di.dilogin.model.AuthmeHook;
import di.dilogin.model.CodeGenerator;
import di.dilogin.entity.DIUserEntity;
import di.dilogin.model.TmpMessage;
import di.dilogin.minecraft.cache.TmpCache;
import di.dilogin.repository.DIUserRepository;
import di.internal.entity.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command to register as a user.
 */
public class DiscordRegisterCommand implements DiscordCommand {

	/**
	 * DIUser repository.
	 */
	private static DIUserRepository diUserRepository = DIUserRepository.getInstance();

	/**
	 * Main api.
	 */
	private final DIApi api = BukkitApplication.getDIApi();

	@Override
	public void execute(String message, MessageReceivedEvent event) {

		event.getMessage().delete().delay(Duration.ofSeconds(20)).queue();

		// Check account limits.
		if (diUserRepository.findByDiscordId(event.getAuthor().getIdLong()).size() >= api.getInternalController().getConfigManager()
				.getInt("register_max_discord_accounts")) {
			event.getChannel().sendMessage(LangManager.getString("register_max_accounts")).delay(Duration.ofSeconds(20))
					.flatMap(Message::delete).queue();
			return;
		}

		// Check arguments.
		if (message.equals("") || message.isEmpty()) {
			event.getChannel().sendMessage(LangManager.getString("register_discord_arguments"))
					.delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
			return;
		}

		// Check code.
		Optional<TmpMessage> tmpMessageOpt = TmpCache.getRegisterMessageByCode(message);
		if (!tmpMessageOpt.isPresent()) {
			event.getChannel().sendMessage(LangManager.getString("register_code_not_found"))
					.delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
			return;
		}
		
		Player player = tmpMessageOpt.get().getPlayer();
		
		// Check if player exists.
		if (diUserRepository.findByMinecraftName(player.getName()).isPresent()) {
			event.getChannel().sendMessage(LangManager.getString("register_already_exists"))
					.delay(Duration.ofSeconds(20)).flatMap(Message::delete).queue();
			return;
		}
		
		// Create password.
		String password = CodeGenerator.getCode(8, api);
		player.sendMessage(LangManager.getString(event.getAuthor(), player, "register_success")
				.replace("%authme_password%", password));
		
		// Send message to discord.
		MessageEmbed messageEmbed = getEmbedMessage(player, event.getAuthor());
		event.getChannel().sendMessage(messageEmbed).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
		
		// Remove user from register cache.
		TmpCache.removeRegister(player.getName());
		
		// Add user to data base.
		DIUserEntity user = new DIUserEntity(player, event.getAuthor());
		diUserRepository.save(user);

		if (DILoginController.isAuthmeEnabled()) {
			AuthmeHook.register(player, password);
		} else {
			DILoginController.loginUser(player, event.getAuthor());
		}

	}

	@Override
	public String getAlias() {
		return api.getInternalController().getConfigManager().getString("register_command");
	}

	/**
	 * Create the log message according to the configuration.
	 * 
	 * @param player Bukkit player.
	 * @param user   Discord user.
	 * @return Embed message configured.
	 */
	private MessageEmbed getEmbedMessage(Player player, User user) {
		EmbedBuilder embedBuilder = DILoginController.getEmbedBase()
				.setTitle(LangManager.getString(player, "register_discord_title"))
				.setDescription(LangManager.getString(user, player, "register_discord_success"));
		return embedBuilder.build();
	}

}
