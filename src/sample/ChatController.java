package sample;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatController {

    @FXML
    VBox textArea;

    @FXML
    TextField textField;

    @FXML
    ScrollPane scrollPane;


    private static Stage secondStage;

    static String name="";

    public void initChat(Stage currStage){
        secondStage= currStage;

        textArea.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollPane.setVvalue(1.0);
            }
        });

    }

    public void sendMsg(){
        Controller.sendMsgToSrv(textField.getText());
        textField.clear();
        textField.requestFocus();
    }

    public void myMessage(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                textArea.setPrefWidth(scrollPane.getWidth()*0.95);
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_RIGHT);
                Label text = new Label(name + " " +sdf.format(cal.getTime())+ ":\n" + str);

                Font font = new Font("Courier New", 12 );
                text.setFont(font);
                text.setTextFill(Color.CHOCOLATE);
                text.setAlignment(Pos.CENTER_RIGHT);
                hbox.getChildren().add(text);
                hbox.setHgrow(text, Priority.ALWAYS);
                textArea.getChildren().add(hbox);

            }
        });

    }

    public void botMsg(String friendnick, String str){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Font font = new Font("Arial", 12 );
                Label text = new Label(friendnick + " " +sdf.format(cal.getTime())+ ":\n"  +  str + "\n");
                text.setFont(font);
                text.setTextFill(Color.BLACK);
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().add(text);

                text.setAlignment(Pos.CENTER_LEFT);
                textArea.getChildren().add(hbox);

            }
        });
    }

    public void systemMsg(String str){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Font font = new Font("Arial", 12 );
                Label text = new Label(  str );
                text.setFont(font);
                text.setTextFill(Color.BLACK);
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER);
                hbox.getChildren().add(text);

                text.setAlignment(Pos.CENTER);
                textArea.getChildren().add(hbox);

            }
        });
    }



}
