package fr.lygaen.advancedvanish.commands;

import fr.lygaen.advancedvanish.AdvancedVanish;
import fr.lygaen.advancedvanish.Constants;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static fr.lygaen.advancedvanish.AdvancedVanish.vanishList;
import static fr.lygaen.advancedvanish.Constants.ERROR;
import static fr.lygaen.advancedvanish.Constants.INFO;

public class Vanish implements CommandExecutor {
    private final AdvancedVanish advancedVanish;

    public Vanish(AdvancedVanish advancedVanish) {
        this.advancedVanish = advancedVanish;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender || sender instanceof ConsoleCommandSender) {
                Player target = null;
                int rank = 10;
                boolean hasRank = false;
                try {
                    target = Bukkit.getPlayerExact(args[0]);
                    if(target != null) {
                        rank = Integer.parseInt(args[1]);
                    } else {
                        rank = Integer.parseInt(args[0]);
                    }
                    hasRank = true;
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {/**/}
                if(target == null) {
                    sender.sendMessage(ERROR+"Please provide a Target !");
                    return false;
                }
                return CMD(sender, target, hasRank, rank);
            }
            sender.sendMessage(ERROR+"Only players, console or commandblocks can use this command !");
            return false;
        }
        Player player = (Player)sender;
        boolean hasTarget = false;
        Player target = null;
        int rank = 0;
        boolean hasRank = false;
        try {
            target = Bukkit.getPlayerExact(args[0]);
            if(target != null) {
                hasTarget = true;
                rank = Integer.parseInt(args[1]);
            } else {
                rank = Integer.parseInt(args[0]);
            }
            hasRank = true;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {/**/}

        AtomicReference<Integer> PermsRank = new AtomicReference<>(10);
        if(hasTarget) {
            if(hasRank) {
                target.getEffectivePermissions().forEach((a) -> {
                    String key = a.getPermission();
                    boolean value = a.getValue();
                    if (key.matches("vanish\\.power\\.[0-9]") && value) {
                        if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                            PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                        }
                    }
                });
                if(PermsRank.get() > rank) {
                    player.sendMessage(ERROR + "The player don't have the permissions");
                    return false;
                } else if(rank > 9) {
                    player.sendMessage(ERROR + "The limit is 9");
                    return false;
                }
                player.getEffectivePermissions().forEach((a) -> {
                    String key = a.getPermission();
                    boolean value = a.getValue();
                    if (key.matches("vanish\\.power\\.[0-9]") && value) {
                        if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                            PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                        }
                    }
                });
                if(PermsRank.get() > rank) {
                    player.sendMessage(ERROR + "You don't have the permissions !");
                    return false;
                }
                if(!vanishList.containsKey(target)) {
                    hide(target, rank);
                    player.sendMessage(INFO + "Vanished " + target.getDisplayName() +" !");
                } else {
                    if(vanishList.get(target) < rank) {
                        player.sendMessage(ERROR + "You don't have the permissions to unvanish this player with a higher rank !");
                        return false;
                    }
                    unhide(target);
                    player.sendMessage(INFO + "Unvanished " + target.getDisplayName() +" !");
                }
            } else {
                AtomicReference<Integer> oldPermsRank = new AtomicReference<>(10);
                target.getEffectivePermissions().forEach((a) -> {
                    String key = a.getPermission();
                    boolean value = a.getValue();
                    if (key.matches("vanish\\.power\\.[0-9]") && value) {
                        if (oldPermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                            oldPermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                        }
                    }
                });
                if(oldPermsRank.get().equals(10)) {
                    player.sendMessage(ERROR + "The player don't have the permissions");
                    return false;
                }
                player.getEffectivePermissions().forEach((a) -> {
                    String key = a.getPermission();
                    boolean value = a.getValue();
                    if (key.matches("vanish\\.power\\.[0-9]") && value) {
                        if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                            PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                        }
                    }
                });
                if(PermsRank.get() > oldPermsRank.get()) {
                    player.sendMessage(ERROR + "You don't have the permissions !");
                    return false;
                }
                if(!vanishList.containsKey(target)) {
                    hide(target, oldPermsRank.get());
                    player.sendMessage(INFO + "Vanished " + target.getDisplayName() +" !");
                } else {
                    if(vanishList.get(target) < PermsRank.get()) {
                        player.sendMessage(ERROR + "You don't have the permissions to unvanish this player with a higher rank !");
                        return false;
                    }
                    unhide(target);
                    player.sendMessage(INFO + "Unvanished " + target.getDisplayName() +" !");
                }
            }
        } else {
            player.getEffectivePermissions().forEach((a) -> {
                String key = a.getPermission();
                boolean value = a.getValue();
                if (key.matches("vanish\\.power\\.[0-9]") && value) {
                    if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                        PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                    }
                }
            });
            if (hasRank) {
                if(PermsRank.get() > rank) {
                    player.sendMessage(ERROR + "You don't have the permissions !");
                    return false;
                } else if(rank > 9) {
                    player.sendMessage(ERROR + "The limit is 9");
                    return false;
                }
                if(!vanishList.containsKey(player)) {
                    hide(player, rank);
                } else {
                    unhide(player);
                }
            } else {
                if(!vanishList.containsKey(player)) {
                    hide(player, PermsRank.get());
                } else {
                    unhide(player);
                }
            }
        }
        return true;

    }

    private boolean CMD(CommandSender sender, Player target, boolean hasRank, int rank) {
        AtomicInteger PermsRank = new AtomicInteger(10);
        if(hasRank) {
            target.getEffectivePermissions().forEach((a) -> {
                String key = a.getPermission();
                boolean value = a.getValue();
                if (key.matches("vanish\\.power\\.[0-9]") && value) {
                    if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                        PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                    }
                }
            });
            if (PermsRank.get() > rank || PermsRank.get() == 10) {
                sender.sendMessage(ERROR + "The player don't have the permissions");
                return false;
            } else if (rank > 9) {
                sender.sendMessage(ERROR + "The limit is 9");
                return false;
            }
            if (!vanishList.containsKey(target)) {
                hide(target, rank);
                sender.sendMessage(INFO + "Vanished " + target.getDisplayName() + " !");
            } else {
                if (vanishList.get(target) < rank) {
                    sender.sendMessage(ERROR + "You don't have the permissions to unvanish this player with a higher rank !");
                    return false;
                }
                unhide(target);
                sender.sendMessage(INFO + "Unvanished " + target.getDisplayName() + " !");
            }
        } else {
            target.getEffectivePermissions().forEach((a) -> {
                String key = a.getPermission();
                boolean value = a.getValue();
                if (key.matches("vanish\\.power\\.[0-9]") && value) {
                    if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                        PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                    }
                }
            });
            if (PermsRank.get() == 10) {
                sender.sendMessage(ERROR + "The player don't have the permissions");
                return false;
            }
            if (!vanishList.containsKey(target)) {
                hide(target, PermsRank.get());
                sender.sendMessage(INFO + "Vanished " + target.getDisplayName() + " !");
            } else {
                unhide(target);
                sender.sendMessage(INFO + "Unvanished " + target.getDisplayName() + " !");
            }
        }
        return true;
    }

    private void unhide(Player player) {
        player.sendMessage(Constants.INFO + "Unvanished you !");
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            onlinePlayer.showPlayer(advancedVanish, player);
        }
        vanishList.remove(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }

    private void hide(Player player, Integer integer) {
        player.setGameMode(GameMode.CREATIVE);
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if(vanishList.containsKey(onlinePlayer) && vanishList.get(onlinePlayer) <= integer) continue;
            AtomicReference<Integer> PermsRank = new AtomicReference<>(10);
            onlinePlayer.getEffectivePermissions().forEach((a) -> {
                String key = a.getPermission();
                boolean value = a.getValue();
                if (key.matches("vanish\\.power\\.[0-9]") && value) {
                    if (PermsRank.get() > Integer.parseInt(key.split("\\.", 3)[2])) {
                        PermsRank.set(Integer.parseInt(key.split("\\.", 3)[2]));
                    }
                }
            });
            if((PermsRank.get() <= integer)) continue;
            onlinePlayer.hidePlayer(advancedVanish, player);
        }
        vanishList.put(player, integer);
        player.sendMessage(Constants.INFO + "Vanished you with a power of "+integer+" !");
    }


}
