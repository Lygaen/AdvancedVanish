package fr.lygaen.advancedvanish;

import fr.lygaen.advancedvanish.commands.Vanish;
import fr.lygaen.advancedvanish.commands.VanishList;
import fr.lygaen.advancedvanish.events.Events;
import fr.lygaen.advancedvanish.events.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

import static fr.lygaen.advancedvanish.Constants.WARN;

public final class AdvancedVanish extends JavaPlugin {

    public static HashMap<Player, Integer> vanishList = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(
                "\n==============================================================================\n" +
                "    _       _                               _  __     __          _     _\n" +
                "   / \\   __| |_   ____ _ _ __   ___ ___  __| | \\ \\   / /_ _ _ __ (_)___| |__\n" +
                "  / _ \\ / _` \\ \\ / / _` | '_ \\ / __/ _ \\/ _` |  \\ \\ / / _` | '_ \\| / __| '_ \\\n" +
                " / ___ \\ (_| |\\ V / (_| | | | | (_|  __/ (_| |   \\ V / (_| | | | | \\__ \\ | | |\n" +
                "/_/   \\_\\__,_| \\_/ \\__,_|_| |_|\\___\\___|\\__,_|    \\_/ \\__,_|_| |_|_|___/_| |_|\n" +
                "==============================================================================");
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Scheduler(), 1, 3);
        Objects.requireNonNull(Bukkit.getPluginCommand("vanish")).setExecutor(new Vanish(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("vanishlist")).setExecutor(new VanishList(this));
    }

    @Override
    public void onDisable() {
        vanishList.forEach((player, rank) -> player.sendMessage(WARN + "Vanish Plugin is disabled, you will unvanish"));
        vanishList.clear();
    }
}
