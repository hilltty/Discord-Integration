package di.dilogin.minecraft.cache;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;

import di.dicore.BukkitApplication;
import di.dilogin.entity.UserSession;

/**
 * Contains the list of users with valid and active sessions.
 */
public class UserSessionCache {

	/**
	 * Prohibits instantiation of the class.
	 */
	private UserSessionCache() {
		throw new IllegalStateException();
	}

	/**
	 * List of user sessions.
	 */
	private static HashMap<UserSession, Long> sessions = new HashMap<>();

	/**
	 * Check if the user has a valid session.
	 * 
	 * @param name Bukkit User Name.
	 * @param ip   Bukkit User Ip.
	 * @return true if user has a valid session.
	 */
	public static boolean isValid(String name, String ip) {
		Optional<UserSession> userOpt = getSessionByUserName(name);

		if (!userOpt.isPresent())
			return false;

		UserSession user = userOpt.get();

		if (!user.getIp().equals(ip))
			return false;

		if (Calendar.getInstance().getTimeInMillis() > ((Long) sessions.get(user)).longValue())
			return false;

		return true;
	}

	/**
	 * Check if there is a session based on the user's name.
	 * 
	 * @param name User's name.
	 * @return True if there is a session.
	 */
	public static boolean contains(String name) {
		return getSessionByUserName(name).isPresent();
	}

	/**
	 * Gets a user's session based on their name.
	 * 
	 * @param name User's name.
	 * @return User's session.
	 */
	private static Optional<UserSession> getSessionByUserName(String name) {
		for (UserSession user : sessions.keySet()) {
			if (user.getName().equals(name))
				return Optional.of(user);
		}
		return Optional.empty();
	}

	/**
	 * Add new session.
	 * 
	 * @param name Player's name.
	 * @param ip   Player's ip.
	 */
	public static void addSession(String name, String ip) {
		int minutes = BukkitApplication.getInternalController().getConfigManager().getInt("session_time_min");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, minutes);
		sessions.put(new UserSession(name, ip), c.getTimeInMillis());
	}

}