package com.zack6849.mcrcon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitWrapper extends JavaPlugin {

    public static Logger log;
    public static List<String> users;
    public static File USERS_FILE;
    static File datafolder;

    @Override
    public void onEnable() {
        BukkitWrapper.log = getLogger();
        USERS_FILE = new File(this.getDataFolder() + "/users.txt");
        users = new ArrayList<String>();
        users.clear();
        datafolder = this.getDataFolder();
        if (!USERS_FILE.exists()) {
            try {
                USERS_FILE.getParentFile().mkdirs();
                USERS_FILE.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(USERS_FILE));
                String line;
                while ((line = in.readLine()) != null) {
                    users.add(line);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultiThreadedServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onDisable() {

        try {
            MultiThreadedServer.close();
        } catch (IOException e) {
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("adduser")) {
            if (!sender.hasPermission("mcrcon.add")) {
                sender.sendMessage("nope.");
                return true;
            }
            if (args.length == 2) {
                String user = args[0];
                String password = args[1];
                try {
                    FileWriter fw = new FileWriter(BukkitWrapper.USERS_FILE, true);
                    BufferedWriter br = new BufferedWriter(fw);
                    String data = Utils.encrypt(user) + ":" + Utils.encrypt(password);
                    if (users.contains(data)) {
                        sender.sendMessage(ChatColor.RED + "That user already exists!");
                        return true;
                    }
                    br.write(data);
                    reloadUsers();
                    br.newLine();
                    br.flush();
                    br.close();
                    sender.sendMessage("User " + user + " set sucessfully!");
                    log(sender.getName() + " created user " + user + "!");
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("deluser")) {
            if (!sender.hasPermission("mcrcon.add")) {
                sender.sendMessage("nope.");
                return true;
            }
            if (args.length == 1) {
                String user = args[0];
                try {
                    FileWriter fw = new FileWriter(BukkitWrapper.USERS_FILE, true);
                    BufferedWriter br = new BufferedWriter(fw);
                    BufferedReader reader = new BufferedReader(new FileReader(BukkitWrapper.USERS_FILE));
                    String data = Utils.encrypt(user);
                    if (!users.contains(data)) {
                        sender.sendMessage(ChatColor.RED + "That user does not exist!");
                        return true;
                    }
                    File in = BukkitWrapper.USERS_FILE;
                    File temp = new File(this.getDataFolder(), "tmp.txt");
                    String remove = user;
                    String current;
                    while ((current = reader.readLine()) != null) {
                        String trimmed = current.trim();
                        if (!trimmed.equals(remove)) {
                            br.write(trimmed);
                        }
                    }
                    br.flush();
                    br.close();
                    if (temp.renameTo(in)) {
                        reloadUsers();
                        sender.sendMessage("Removed user " + user);
                        return true;
                    } else {
                        reloadUsers();
                        sender.sendMessage("An error occured while deleting that user! check the console!");
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("listusers")) {
            String users = "";
            for (int i = 0; i < MultiThreadedServer.usernames.size(); i++) {
                users += MultiThreadedServer.usernames.get(i) + " ";
            }
            sender.sendMessage("Currently connected users: " + users);
            return true;
        }
        return false;
    }

    public static void log(String s) {
        BukkitWrapper.log.info(s);
        try {
            File f = new File(datafolder, "log.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter br = new BufferedWriter(fw);
            br.write(Utils.getTime() + s);
            br.newLine();
            br.flush();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadUsers() {
        users.clear();
        BukkitWrapper.datafolder = DataFolder();
        {
            try {
                BufferedReader in = new BufferedReader(new FileReader(USERS_FILE));
                String line;
                while ((line = in.readLine()) != null) {
                    users.add(line);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File DataFolder() {
        return Bukkit.getPluginManager().getPlugin("RCON").getDataFolder();
    }
}
