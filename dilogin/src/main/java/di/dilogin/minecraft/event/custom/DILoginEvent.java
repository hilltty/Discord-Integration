package di.dilogin.minecraft.event.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This event is generated when logging into DILogin
 */
@Getter @AllArgsConstructor
public final class DILoginEvent extends Event {

	/**
	 * Default handler list.
	 * @return the handler list.
	 */
	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	/**
	 * The handler list.
	 */
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	/**
	 * The player who is logging in
	 */
	private Player player;

	/**
	 * Get the handlers of this event.
	 * @return the handlers of this event.
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
}