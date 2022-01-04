/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.entity;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.entity.Player;

import di.dicore.DIApi;
import di.dilogin.BukkitApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;

/**
 * Represents a user registered in the system. Contains his discord and
 * minecraft information.
 */
@Entity
@Table(name = "user")
@NoArgsConstructor
@ToString
@Getter
@Setter
public class DIUserEntity {
	
	private static DIApi api = BukkitApplication.getDIApi();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(nullable = false, unique = true, name = "username")
	private String userName;

	@Column(nullable = false, name = "discord_id")
	private long discordId;

	public DIUserEntity(String userName, long discordId) {
		this.userName = userName;
		this.discordId = discordId;
	}
	
	public DIUserEntity(Player player, User user) {
		this.discordId = user.getIdLong();
		this.userName = player.getName();
	}
	
	/**
	 * Obtain the player from the user name.
	 * @return Bukkit player. Can be null if player is offline.
	 */
	public Optional<Player> getPlayerBukkit(){
		return Optional.ofNullable(api.getInternalController().getPlugin().getServer().getPlayer(userName));
	}

	/**
	 * Obtain the discord user from discordId.
	 * @return Discord Player object.
	 */
	public User getPlayerDiscord() {
		return api.getCoreController().getDiscordApi().getUserById(this.discordId);
	}

}
