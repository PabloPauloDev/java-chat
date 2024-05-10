package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server implements Runnable {

    private ArrayList<connectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public void server() {
        connections = new ArrayList<>();
        done = false;
    }

    public void run(){

        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                connectionHandler handler = new connectionHandler(client);
                connections.add(handler);
                pool.execute(handler);;
            }
        } 
        catch (IOException e){
            shutdown();
        }
    }

    public void broadcast(String message) {
        for (connectionHandler ch: connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown(){
        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }
            for (connectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {

        }
    }



    public class connectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String usuario;

        public connectionHandler(Socket client) {
            this.client = client;
        }
    
        public void run(){

            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()), 0);
                out.println("Por favor insira seu nome de usuario:");
                usuario = in.readLine();
                System.out.println(usuario + "Conectado!");
                broadcast(usuario + "entrou no chat!");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/usuario")){
                        String[] messageSplit = message.split("", 2);
                        if (messageSplit.length == 2){
                            broadcast(usuario + "alterou seu nome para:" + messageSplit[1]);
                            System.out.println(usuario + "alterou seu nome para:" + messageSplit[1]);
                            usuario = messageSplit[1];
                        } else {
                            out.println("Nome de usuario nao solicitado");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadcast(usuario + " Saiu do chat");
                        shutdown();
                    }else {
                        broadcast(usuario + ":  " + message);
                    }
                } 
            } 
            catch (IOException e){
                shutdown();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                } 
            } catch (IOException e) {
                //ignore
            }
        }
    }



    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
