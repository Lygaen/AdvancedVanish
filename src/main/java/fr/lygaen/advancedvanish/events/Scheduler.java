package fr.lygaen.advancedvanish.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;


import static fr.lygaen.advancedvanish.AdvancedVanish.vanishList;

public class Scheduler implements Runnable {
    @Override
    public void run() {
        vanishList.forEach((player, rank) -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Currently Vanished, Power : " + rank)));
    }
}
