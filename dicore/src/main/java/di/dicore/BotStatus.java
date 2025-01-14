package di.dicore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import di.internal.controller.CoreController;
import net.dv8tion.jda.api.entities.Activity;

/**
 * In this class, the flow of events related to the visible state of the bot in discord is controlled.
 */
public class BotStatus implements Listener {

    /**
     * The internal controller of the core plugin.
     */
    private static final CoreController controller = BukkitApplication.getInternalController();

    /**
     * The configuration type of bot status selected in the file (status_type).
     */
    private static int type;

    /**
     * The configuration message of bot status selected in the file (status_content).
     */
    private static String content;

    /**
     * Init the thread related to the configuration of the bot status events.
     */
    public static void init() {
        if (!controller.getConfigManager().contains("status_type")
                || !controller.getConfigManager().contains("status_content"))
            return;

        type = controller.getConfigManager().getInt("status_type");
        content = controller.getConfigManager().getString("status_content");

        initEvents();

    }

    /**
     * Init bot events.
     */
    private static void initEvents() {
        if (type == 1) {
            fire();
            controller.getPlugin().getServer().getPluginManager().registerEvents(new BotStatus(), controller.getPlugin());
        } else if (type == 2) {
            fireOnTime();
        }
    }

    /**
     * Fire the status of the bot every 5 mins.
     */
    private static void fireOnTime() {
        ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();
        threadPool.scheduleWithFixedDelay(BotStatus::fire, 0, 5, TimeUnit.MINUTES);
    }

    /**
     * Fire the status of the bot when player joins.
     *
     * @param e The event of player join.
     */
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        fire();
    }

    /**
     * Fire the status of the bot when player quits.
     *
     * @param e The event of player quit.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        fire();
    }

    /**
     * Fire the status of the bot.
     */
    private static void fire() {
        controller.getBot().getApi().getPresence().setActivity(Activity.playing(getContent()));
    }

    /**
     * Get the content of the status.
     *
     * @return The content of the status.
     */
    private static String getContent() {
        return content.replace("%minecraft_players%", String.valueOf(controller.getPlugin().getServer().getOnlinePlayers().size()));
    }

}
