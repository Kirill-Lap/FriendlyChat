package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller  {

    @FXML
    TextField nameF, nickF;

    @FXML
    GridPane grid;

    @FXML
    Button mainBtn;

    @FXML
    Label label2, label3, signin;

    @FXML
    PasswordField passwordField;

    @FXML
    private Text actiontarget;

    private static Stage firstStage;
    private ChatController chatController;
    static boolean authorized = false;
    static boolean signin_flag = false;

    /**Обработка кнопки идентификации*/
    public void handleAction ()  {

        if (socket == null || socket.isClosed()) {
            connect();
        }

        if (!signin_flag) {
            String login = nameF.getText();
            String pass = passwordField.getText();
            nameF.clear();
            passwordField.clear();
            sendMsgToSrv(login + " " + pass);
        } else {
            sendMsgToSrv("/new " + nameF.getText() + " " + nickF.getText() + " "  + passwordField.getText());
            nameF.clear();
            nickF.clear();
            passwordField.clear();
        }

    }
    /** переключение между формами log in и sign in */
    public void signInForm ()  {
        if (!signin_flag) {
            label2.setManaged(true);
            nickF.setManaged(true);
            label2.setVisible(true);
            nickF.setVisible(true);
            grid.setRowIndex(passwordField,4);
            grid.setRowIndex(label3,4);
            mainBtn.setText("Sign In");
            signin.setText("Log In");
//            signin.
            signin_flag = true;
        } else {
            label2.setManaged(false);
            nickF.setManaged(false);
            label2.setVisible(false);
            nickF.setVisible(false);
            grid.setRowIndex(passwordField,3);
            grid.setRowIndex(label3,3);
            mainBtn.setText("Log In");
            signin.setText("Sign In");
            signin_flag = false;

        }

    }


    /** запуск и переключение на окно чата*/
    private void runChat()  {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                firstStage.close();

                //** разобраться с объявлением Pane*/
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat_UI.fxml"));
                Pane pane = null;

                try {
                    pane = (Pane) loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                chatController = loader.<ChatController>getController();

                Stage secondStage = new Stage();

                secondStage.setTitle("Friendly Chat");
                secondStage.setScene(new Scene(pane, 350, 375));
                chatController.initChat(secondStage);

                secondStage.show();

            }
        });

    }



    public static void  initFirstStage(Stage stage) {
        firstStage=stage;
    }


    /**Блок подключения*/
    private Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    private final String IP_ADRESS = "localhost";
    private final int PORT = 8189;


    private void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while(true) {
                            String str = in.readUTF();
                            if (str.equals("/connectionClosed")) {
                                chatController.systemMsg(str);
                                break;
                            }
                            if (authorized) {
                                String[] meta = str.split("%",2);
                                if (meta[0].equals(ChatController.name)) chatController.myMessage(meta[1]);
                                else chatController.botMsg(meta[0], meta[1]);

                            } else {
                                String regex = "^/authok.*";
                                Pattern p =Pattern.compile(regex);
                                Matcher m = p.matcher(str);
                                if (m.matches()) {
                                    authorized = true;
                                    String meta[] = str.split(" ");
                                    ChatController.name = meta[1];
                                    runChat();
                                } else {
                                    printInfo(str);
                                }


                            }


                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**Ну тут понятно*/
    public static void sendMsgToSrv(String text){
        try{
            out.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Вывод сообщений о результатах идентификации*/
    private void printInfo(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                actiontarget.setFont(new Font("Arial",12));
                actiontarget.setStyle("-fx-text-fill: white;");
                actiontarget.setText(text);
            }
        });
    }

}
