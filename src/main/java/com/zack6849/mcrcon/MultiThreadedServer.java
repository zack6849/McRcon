package com.zack6849.mcrcon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;

public class MultiThreadedServer implements Runnable {

    public Socket socket;
    public String user;
    public BufferedReader in;
    public BufferedWriter out;
    public static List<Socket> users;
    public static List<String> usernames;
    public static boolean on = true;
    public static ServerSocket server;

    MultiThreadedServer(Socket s) {
        this.socket = s;
    }

    public static void start() throws IOException {
        server = new ServerSocket(1337);
        users = new ArrayList<Socket>();
        usernames = new ArrayList<String>();
        BukkitWrapper.log("Waiting for connections");
        while (on) {
            Socket sock = server.accept();
            BukkitWrapper.log("Connection from " + sock.getInetAddress().toString().replaceFirst("/", ""));
            users.add(sock);
            new Thread(new MultiThreadedServer(sock)).start();
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new BufferedWriter(new PrintWriter(this.socket.getOutputStream()));
            boolean loggedin = false;
            while (!loggedin) {
                String user;
                String pass;
                write(out, "Enter your username");
                user = in.readLine();
                write(out, "Enter your password!");
                pass = in.readLine();
                if (user != null && pass != null) {
                    if (BukkitWrapper.users.contains(Utils.encrypt(user) + ":" + Utils.encrypt(pass))) {
                        usernames.add(user + " = " + socket.getInetAddress().toString().replaceFirst("/", ""));
                        BukkitWrapper.log("User " + user + " connected from IP " + socket.getInetAddress().toString().replaceFirst("/", ""));
                        loggedin = true;
                        new Thread(new ClientHandler(socket, user)).start();
                    } else {
                        write(out, "unknown username or password!");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(BufferedWriter out, String s) throws IOException {
        out.write(s);
        out.newLine();
        out.flush();
    }

    public static void WriteToAll(BufferedWriter out, String s)
            throws IOException {
        out.write(s);
        out.newLine();
    }

    public static void close() throws IOException {
        server.close();
        for (Socket s : users) {
            s.close();
        }
    }
}
