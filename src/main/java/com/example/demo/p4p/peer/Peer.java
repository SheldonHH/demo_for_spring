package com.example.demo.p4p.peer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Peer {

    public static void main(String args[]) throws IOException,
            InterruptedException{

        while(true){
            ServerSocket ss=new ServerSocket(8801);
            System.out.println("Peer is Awaiting");
            Socket s=ss.accept();
            Multi t=new Multi(s);
            t.start();

            Thread.sleep(2000);
            ss.close();
        }

    }
}
class Multi extends Thread{
    private Socket s=null;
    DataInputStream infromClient;
    Multi() throws IOException{


    }
    Multi(Socket s) throws IOException{
        this.s=s;
        infromClient = new DataInputStream(s.getInputStream());
    }
    public void run(){

        String SQL=new String();
        try {
            SQL = infromClient.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(Multi.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Query: " + SQL);
        try {
            System.out.println("Socket Closing");
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Multi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

//import java.io.*;
//import java.net.*;
//
//// Server class
//class Server {
//    public static void main(String[] args)
//    {
//        ServerSocket server = null;
//
//        try {
//
//            // server is listening on port 1234
//            server = new ServerSocket(1234);
//            server.setReuseAddress(true);
//
//            // running infinite loop for getting
//            // client request
//            while (true) {
//
//                // socket object to receive incoming client
//                // requests
//                Socket client = server.accept();
//
//                // Displaying that new client is connected
//                // to server
//                System.out.println("New client connected"
//                        + client.getInetAddress()
//                        .getHostAddress());
//
//                // create a new thread object
//                ClientHandler clientSock
//                        = new ClientHandler(client);
//
//                // This thread will handle the client
//                // separately
//                new Thread(clientSock).start();
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (server != null) {
//                try {
//                    server.close();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // ClientHandler class
//    private static class ClientHandler implements Runnable {
//        private final Socket clientSocket;
//
//        // Constructor
//        public ClientHandler(Socket socket)
//        {
//            this.clientSocket = socket;
//        }
//
//        public void run()
//        {
//            PrintWriter out = null;
//            BufferedReader in = null;
//            try {
//
//                // get the outputstream of client
//                out = new PrintWriter(
//                        clientSocket.getOutputStream(), true);
//
//                // get the inputstream of client
//                in = new BufferedReader(
//                        new InputStreamReader(
//                                clientSocket.getInputStream()));
//
//                String line;
//                while ((line = in.readLine()) != null) {
//
//                    // writing the received message from
//                    // client
//                    System.out.printf(
//                            " Sent from the client: %s\n",
//                            line);
//                    out.println(line);
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//            finally {
//                try {
//                    if (out != null) {
//                        out.close();
//                    }
//                    if (in != null) {
//                        in.close();
//                        clientSocket.close();
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}