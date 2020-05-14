package io.github.zoranac.itemincinerator;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Console;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ItemIncinerator extends JavaPlugin {

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            Player player = (Player) sender;

            if (command.getName().equals("incinerate") && player.hasPermission("command.incinerate")){
                double dist = 0;
                int distInt = 0;
                AtomicInteger itemCountDestroyed = new AtomicInteger();
                if (args[0] != null){
                    dist = Double.valueOf(args[0]);

                    if (Double.valueOf(args[0]) == Math.floor(Double.valueOf(args[0])) && !Double.isInfinite(Double.valueOf(args[0]))) {
                        distInt = Integer.valueOf(args[0]);
                    }

                }
                else {
                    sender.sendMessage("/incinerate [distance] [item name]");
                    return false;
                }
                List<String> ArgsList = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
                player.getNearbyEntities(dist,dist,dist).forEach((x)->{
                    if (x instanceof Item && x.getType().name() == "DROPPED_ITEM"){
                        if (args.length >= 2 && args[1] != null && args[1] != ""){
                            if (ArgsList.contains(((Item) x).getItemStack().getType().name())){
                                itemCountDestroyed.getAndIncrement();
                                x.remove();
                            }
                        }
                        else {
                            itemCountDestroyed.getAndIncrement();
                            x.remove();
                        }
                    }
                });

                String distString = "";

                if (distInt != 0){
                    distString = String.valueOf(distInt);
                }
                else{
                    distString = String.valueOf(dist);
                }

                if (itemCountDestroyed.intValue() == 0){
                    sender.sendMessage(ChatColor.RED + "No blocks were within range to be incinerated");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You have incinerated " + String.valueOf(itemCountDestroyed) +
                            " items on the ground within " + distString + " blocks of you.");
                }
            }

            return true;
        }
        catch (Exception e)
        {
            if (e instanceof NumberFormatException){
                sender.sendMessage("Distance must be a number.");
            }
            else
            {
                sender.sendMessage("An error occurred.");
            }

            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {
            Player player = (Player) sender;
            if (command.getName().equals("incinerate") && player.hasPermission("command.incinerate")) {
                Double dist = 0.0;
                if (args[0] != null && args.length >= 2) {
                    dist = Double.valueOf(args[0]);
                    List<String> ArgsList = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
                    List<String> itemList = new ArrayList<>();
                    for (Entity e : player.getNearbyEntities(dist,dist,dist)) {
                        if (e instanceof Item
                                && e.getType().name() == "DROPPED_ITEM"
                                && !ArgsList.contains(((Item) e).getItemStack().getType().name())) {
                            itemList.add(((Item) e).getItemStack().getType().name());
                        }
                    }

                    return itemList;
                }
            }
            return null;
        }
        catch (Exception e){
            return super.onTabComplete(sender, command, alias, args);
        }
    }
}
