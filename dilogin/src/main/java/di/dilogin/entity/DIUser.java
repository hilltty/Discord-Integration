package di.dilogin.entity;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

/**
 * Represents a user registered in the system. Contains his discord and
 * minecraft information.
 */
@Getter
@Setter
@AllArgsConstructor
public class DIUser {

	/**
	 * Bukkit Player object.
	 */
	private Player playerBukkit;

	/**
	 * Discord Player object.
	 */
	private User playerDiscord;
}
