package utils.entity;

import javax.security.auth.login.LoginException;

import org.bukkit.plugin.Plugin;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * Contains the main information of the bot.
 **/
@Getter
@Setter
public class DiscordBot {

	/**
	 * Javacord Api.
	 */
	private JDA api;

	/**
	 * Bot prefix.
	 */
	private String prefix;

	/**
	 * Main server id.
	 */
	private long serverid;

	/**
	 * Bot CommandHandler.
	 */
	private CommandHandler commandHandler;

	/**
	 * Main Controller.
	 * 
	 * @param prefix   Bot prefix.
	 * @param serverid Main server id.
	 * @param token    Bot token.
	 * @param plugin   Bukkit plugin.
	 */
	public DiscordBot(String prefix, long serverid, String token, Plugin plugin) {
		this.prefix = prefix;
		this.serverid = serverid;
		initBot(token, plugin);
	}

	/**
	 * Init bot.
	 * 
	 * @param token  Bot token.
	 * @param plugin Bukkit plugin.
	 */
	public void initBot(String token, Plugin plugin) {
		try {
			this.api = JDABuilder.createDefault(token).build();
			onConnectToDiscord(plugin);
		} catch (LoginException e) {
			e.printStackTrace();
			plugin.getLogger().warning("The Bot failed to start.");
		}
	}

	/**
	 * When the bot starts successfully it passes here.
	 * 
	 * @param api Javacord Api.
	 */
	private void onConnectToDiscord(Plugin plugin) {
		
		plugin.getLogger().info("Bot started");
		this.commandHandler = new CommandHandler(prefix);
	}
}