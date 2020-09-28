package fr.lygaen.advancedvanish.events;

import fr.lygaen.advancedvanish.AdvancedVanish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.atomic.AtomicReference;

import static fr.lygaen.advancedvanish.AdvancedVanish.vanishList;

public class Events implements Listener {

    private final AdvancedVanish advancedVanish;

    public Events(AdvancedVanish advancedVanish) {
        this.advancedVanish = advancedVanish;
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e) {
        Player p = (Player) e.getEntity();
        if((e.getEntity() instanceof Player) && vanishList.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(vanishList.containsKey(e.getPlayer())) {
            vanishList.remove(e.getPlayer());
            Bukkit.getServer().getOnlinePlayers().forEach(player -> player.showPlayer(advancedVanish, e.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        AtomicReference<Integer> PermsRank = new AtomicReference<>(10);
        e.getPlayer().getEffectivePermissions().forEach((a) -> {
            String perm = a.getPermission();
            boolean hasPerm = a.getValue();
            if(perm.matches("vanish\\.power\\.[0-9]") && hasPerm) {
                if (PermsRank.get() > Integer.parseInt(perm.split("\\.", 3)[2])) {
                    PermsRank.set(Integer.parseInt(perm.split("\\.", 3)[2]));
                }
            }
        });
        if(!PermsRank.get().equals(10)) {
            vanishList.forEach((player, rank) -> {
                if(rank < PermsRank.get()) {
                    e.getPlayer().hidePlayer(advancedVanish, player);
                }
            });
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equals("Vanished Players List")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if(e.getView().getTitle().equals("Vanished Players List")) {
            e.setCancelled(true);
        }
    }
}
