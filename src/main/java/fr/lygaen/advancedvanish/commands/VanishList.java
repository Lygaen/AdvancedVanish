package fr.lygaen.advancedvanish.commands;

import fr.lygaen.advancedvanish.AdvancedVanish;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.lygaen.advancedvanish.AdvancedVanish.vanishList;
import static fr.lygaen.advancedvanish.Constants.ERROR;
import static fr.lygaen.advancedvanish.Constants.INFO;

public class VanishList implements CommandExecutor {
    private AdvancedVanish advancedVanish;

    public VanishList(AdvancedVanish advancedVanish) {
        this.advancedVanish = advancedVanish;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(args.length == 0)) {
            return false;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ERROR + "Only players can use this command.");
            return false;
        }
        LinkedList<Player> playersList = new LinkedList<>();
        AtomicInteger PermsRank = new AtomicInteger(10);
        for (PermissionAttachmentInfo a : sender.getEffectivePermissions()) {
            String key = a.getPermission();
            boolean value = a.getValue();
            if (key.matches("vanish\\.power\\.[0-9]") && value) {
                if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                    PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                }
            } else if (key.equals("vanish.list.all")) {
                PermsRank.set(0);
                break;
            }
        }
        vanishList.forEach(((player, integer) -> {
            if(PermsRank.get() <= integer) {
                playersList.add(player);
            }
        }));
        if(playersList.isEmpty()) {
            sender.sendMessage(INFO + "Nobody is vanished !");
            return true;
        }
        showGUI((Player)sender, playersList);
        return true;
    }

    private void showGUI(Player sender, LinkedList<Player> playersList) {
        int size = playersList.size() % 9 == 0 ? playersList.size() : playersList.size() + (9 - (playersList.size() % 9));
        Inventory inv = Bukkit.createInventory(null, size, "Vanished Players List");

        for (int i = 0; i < playersList.size(); i++) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            Objects.requireNonNull(skullMeta).setOwningPlayer(playersList.get(i));
            Objects.requireNonNull(skullMeta).setDisplayName(ChatColor.YELLOW + playersList.get(i).getName());
            Objects.requireNonNull(skullMeta).setLore(Collections.singletonList(ChatColor.GREEN + "Power : " + ChatColor.DARK_GREEN +vanishList.get(playersList.get(i))));
            skull.setItemMeta(skullMeta);
            inv.setItem(i, skull);
        }

        sender.openInventory(inv);
    }
}
