package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.map.MapCursor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class TimingsCommand extends Command {
    public TimingsCommand(String name) {
        super(name);
        this.description = "Records timings for all plugin events";
        this.usageMessage = "/timings <function>";
        this.setPermission("bukkit.command.timings");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length != 1) return false;

        if ("flush".equals(args[0])) {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                plugin.flushTimings();
            }
        }
        else if("merged".equals(args[0])) {
            int index = 0;
            File timingFolder = new File("timings");
            timingFolder.mkdirs();
            File timings = new File(timingFolder, "timings.txt");
            while (timings.exists()) timings = new File(timingFolder, "timinigs" + (++index) + ".txt");
            try {
                PrintStream printer = new PrintStream(timings);
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    printer.println(plugin.getDescription().getFullName());
                    for (Event.Type type : Event.Type.values()) {
                        long time = plugin.getTiming(type);
                        if (time > 0) {
                            printer.println("    "  + type.name() + " " + time);
                        }
                    }
                }
                sender.sendMessage("Timings written to " + timings.getPath());
            } catch (IOException e) {
            }
        }
        else if("seperate".equals(args[0])) {
            int index = 0;
            File timingFolder = new File("timings");
            timingFolder.mkdirs();
            File timings = new File(timingFolder, "timings.txt");
            while (timings.exists()) timings = new File(timingFolder, "timinigs" + (++index) + ".txt");
            File names = new File(timingFolder, "names" + index + ".txt");
            int pluginIdx = 0;
            try {
                PrintStream fileTimings = new PrintStream(timings);
                PrintStream fileNames = new PrintStream(names);
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    pluginIdx++;
                    fileNames.println(pluginIdx + " " + plugin.getDescription().getFullName());
                    fileTimings.println("Plugin " + pluginIdx);
                    for (Event.Type type : Event.Type.values()) {
                        long time = plugin.getTiming(type);
                        if (time > 0) {
                            fileTimings.println("    " + type.name() + " " + time);
                        }
                    }
                }
                sender.sendMessage("Timings written to " + timings.getPath());
                sender.sendMessage("Names written to " + names.getPath());
            } catch (IOException e) {
            }
        }

        return true;
    }
}
