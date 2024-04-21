package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;

    public void run(){

        try {
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();
            ConnectionHandler handler = new ConnectionHandler(client);

            connections.add(handler);
        } 
        catch (IOException e){

            e.printStackTrace();
        }
    }
    /**
     * connectionHandler implements Run
     */
    public class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String usuario;

        public void connectionHandler(Socket client) {
            this.client = client;
        }
    
        public void run(){

            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()), 0);
                out.println("Por favor insira seu nome de usuario:");
                usuario = in.readLine();
                System.out.println(usuario + "Conectado!");
            } 
            catch (IOException e){
    
                e.printStackTrace();
            }
        }
    }
}
