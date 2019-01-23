package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try{
            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Server started.");
//            String sss = AuthService.getNickAuth("login2", "pass2");
//            System.out.println(sss);


//            DataInputStream in = new DataInputStream(socket.getInputStream());
//            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
//                subscribe(socket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            try{
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public void subscribe (ClientHandler client) {
        System.out.println("Client authorized");
        clients.add(client);
    }

    public void unsubscribe (ClientHandler client) {
        System.out.println("Client disconnected");
        clients.remove(client);
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler c:clients) {
            c.sendMsg(msg);
        }

    }

    public void privateMsg(ClientHandler client, String msg) {
        client.sendMsg(msg);
    }

    public ClientHandler findByNick(String nick) {
        for (ClientHandler c:clients) {
            if (c.getNick().equals(nick)) return c;
        }
        return null;
    }

}
