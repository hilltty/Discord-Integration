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
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import net.dv8tion.jda.api.entities.User;

/**
 * Represents a user registered in the system. Contains his discord and
 * minecraft information.
 */
@Entity
@Table(name = "user")
@NoArgsConstructor
public class DIUserEntity {

	@Id
	@Column(nullable = false, unique = true, name = "username")
	private String userName;

	@Column(nullable = false, name = "discord_id")
	private long discordId;
	
	public DIUserEntity(Player player, User user) {
		this.discordId = user.getIdLong();
		this.userName = player.getName();
	}
	
	/**
	 * Obtain the player from the user name.
	 * @return Bukkit player. Can be null if player is offline.
	 */
	public Optional<Player> getPlayerBukkit(){
		return Optional.empty();
	}

	/**
	 * Obtain the discord user from discordId.
	 * @return Discord Player object.
	 */
	public User getPlayerDiscord() {
		return null;
	}

}
