package com.zack6849.mcrcon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ClientHandler extends Thread {

    public Socket client;
    public String user;
    public BufferedReader in;
    public BufferedWriter out;

    public ClientHandler(Socket s, String u) {
        this.client = s;
        this.user = u;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            out = new BufferedWriter(new PrintWriter(this.client.getOutputStream()));
            Bukkit.getLogger().addHandler(new LoggerHandler(out));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                MultiThreadedServer.write(out, "Welcome " + user + "!");
                String temp;
                while ((temp = in.readLine()) != null) {
                    if (temp.startsWith("/")) {
                        BukkitWrapper.log(user + " executed command " + temp.replaceFirst("/", ""));
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), temp.replaceFirst("/", ""));
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "[" + ChatColor.YELLOW + "RCON: " + user + ChatColor.GREEN + "] " + temp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
