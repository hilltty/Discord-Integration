/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.discord.event;

import di.dicore.DIApi;
import di.dilogin.BukkitApplication;
import di.dilogin.controller.DILoginController;
import di.dilogin.controller.LangManager;
import di.dilogin.entity.DIUserEntity;
import di.dilogin.minecraft.cache.TmpCache;
import di.dilogin.minecraft.util.Util;
import di.dilogin.model.AuthmeHook;
import di.dilogin.model.CodeGenerator;
import di.dilogin.model.TmpMessage;
import di.dilogin.repository.DIUserRepository;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Class for handling discord login or registration events.
 */
public class UserReactionMessageEvent extends ListenerAdapter {

	/**
	 * DIUser repository.
	 */
	private static final DIUserRepository diUserRepository = DIUserRepository.getInstance();
	
	/**
	 * DIApi api.
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
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		if (event.getUser().isBot())
			return;

		String reactionEmoji = event.getReactionEmote().getAsCodepoints();

		if (reactionEmoji.equalsIgnoreCase(EMOJI)) {

			Optional<TmpMessage> registerOpt = TmpCache.getRegisterMessage(event.getMessageIdLong());
			if (registerOpt.isPresent()) {
				registerUser(event, registerOpt.get());
				return;
			}

			Optional<TmpMessage> loginOpt = TmpCache.getLoginMessage(event.getMessageIdLong());
			loginOpt.ifPresent(tmpMessage -> loginUser(event, tmpMessage));

		} else if (reactionEmoji.equalsIgnoreCase(EMOJI_REJECTED)){

			Optional<TmpMessage> tmpMessageOptional = TmpCache.getRegisterMessage(event.getMessageIdLong());
			tmpMessageOptional = !tmpMessageOptional.isPresent() ? TmpCache.getLoginMessage(event.getMessageIdLong()) : tmpMessageOptional;

			System.out.println("OPTIONAL IS PRESENT: "+tmpMessageOptional.isPresent());
			if(!tmpMessageOptional.isPresent())
				return;

			rejectRequest(event,tmpMessageOptional.get());

		}
	}

	/**
	 *  In case of reject a request.
	 * @param event     Reaction event.
	 * @param tmpMessage Process message.
	 */
	private void rejectRequest(MessageReactionAddEvent event, TmpMessage tmpMessage){

		Message message = tmpMessage.getMessage();
		Player player = tmpMessage.getPlayer();
		User user = tmpMessage.getUser();

		// It is not necessary to delete the message, since when a user disconnects from the server
		// with an active request, it is automatically deleted.
		DILoginController.kickPlayer(player, LangManager.getString(user, player, "request_rejected"));
	}

	/**
	 * In case of being present in a registration process, this is carried out.
	 * 
	 * @param event     Reaction event.
	 * @param tmpMessage Process message.
	 */
	private void registerUser(MessageReactionAddEvent event, TmpMessage tmpMessage) {
		Message message = tmpMessage.getMessage();
		Player player = tmpMessage.getPlayer();
		User user = tmpMessage.getUser();

		if (!Objects.equals(event.getUser(), user))
			return;

		if (event.getMessageIdLong() != message.getIdLong())
			return;

		String password = CodeGenerator.getCode(8, api);
		player.sendMessage(
				LangManager.getString(user, player, "register_success").replace("%authme_password%", password));
		TmpCache.removeRegister(player.getName());
		message.editMessage(getRegisterEmbed(user, player)).delay(Duration.ofSeconds(60)).flatMap(Message::delete)
				.queue();
		diUserRepository.save(new DIUserEntity(player, user));

		if (DILoginController.isAuthmeEnabled()) {
			AuthmeHook.register(player, password);
		} else {
			if (!Util.isWhiteListed(user)) {
				player.sendMessage(LangManager.getString(player, "login_without_role_required"));
			} else {
				DILoginController.loginUser(player, user);
			}
		}

	}

	/**
	 * In case of being present in a login process, this is carried out.
	 * 
	 * @param event     Reaction event.
	 * @param tmpMessage Process message.
	 */
	private void loginUser(MessageReactionAddEvent event, TmpMessage tmpMessage) {
		Message message = tmpMessage.getMessage();
		Player player = tmpMessage.getPlayer();
		User user = tmpMessage.getUser();

		if (!Objects.equals(event.getUser(), user))
			return;

		if (event.getMessageIdLong() != message.getIdLong())
			return;

		message.editMessage(getLoginEmbed(user, player)).delay(Duration.ofSeconds(60)).flatMap(Message::delete).queue();
		DILoginController.loginUser(player, user);

	}

	/**
	 * @param user   Discord user.
	 * @param player Bukkit player.
	 * @return Registration completed message.
	 */
	private MessageEmbed getRegisterEmbed(User user, Player player) {
		return DILoginController.getEmbedBase().setTitle(LangManager.getString(user, player, "register_discord_title"))
				.setDescription(LangManager.getString(user, player, "register_discord_success")).build();
	}

	/**
	 * @param user   Discord user.
	 * @param player Bukkit player.
	 * @return Login completed message.
	 */
	private MessageEmbed getLoginEmbed(User user, Player player) {
		return DILoginController.getEmbedBase().setTitle(LangManager.getString(user, player, "login_discord_title"))
				.setDescription(LangManager.getString(user, player, "login_discord_success")).build();
	}

}
