package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler {
    Server server;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true) {
                        String str = in.readUTF();
                        String[] tokens = str.split(" ");
                        String newNick = null;
                        String msg = "Wrong login/password";

                        if (tokens[0].equals("/new")) {
                            msg = AuthService.registerNewLogin(tokens[1], tokens[2], tokens[3]);
                            if (msg.equals("New user added")){
                                newNick = tokens[2];
                            }
                        } else {

                                newNick = AuthService.getNickAuth(tokens[0],tokens[1]);
                                if (server.findByNick(newNick)!=null) {
                                    msg = "User " + newNick + " already logged in.";
                                    newNick = null;
                                }
                        }

                        if (newNick != null) {
                            nick = newNick;
                            server.subscribe(ClientHandler.this);
                            out.writeUTF("/authok " + nick);
                            break;
                        } else {
                            out.writeUTF(msg);
                        }

                    }

                    while(true){
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/connectionClosed");
                            break;
                        }
                        String regex = "^/w.*";
                        Pattern p =Pattern.compile(regex);
                        Matcher m = p.matcher(str);
                        if (m.matches()) {
                            String[] meta = str.split(" ",3);
                            if (server.findByNick(meta[1])!=null ) {
                                if (meta.length ==3)
                                    server.privateMsg(server.findByNick(meta[1]),nick + "%" +meta[2]);
                                else
                                    server.privateMsg(server.findByNick(meta[1]),nick + "% ");
                            } else
                                server.privateMsg(ClientHandler.this, "Server%Nobody with such nickname");

                        } else {
                            server.broadcastMsg(nick + "%" +str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(ClientHandler.this);
                }
            }
        }).start();
    }

    public String getNick() {
        return nick;
    }

    public void sendMsg(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
