package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import server.FileHistoryService;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ChatController implements Initializable {

    public TextArea output;
    public TextField input;
    public Client client;
    public MenuItem m1;
    public MenuItem m2;
    public MenuItem m3;


    public void send(ActionEvent actionEvent) {
        if (!input.getText().isEmpty()) {
            client.write(input.getText());
            input.clear();
        }
    }

    public void change() {
        input.appendText(m1.getText());
    }

    public void change2() {
        input.appendText(m2.getText());
    }

    public void change3() {
        input.appendText(m3.getText());
    }


    public void quit(ActionEvent actionEvent) throws IOException {

        Parent chat = FXMLLoader.load(getClass().getResource("fxml/auth.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Welcome to the GeekChat");
        stage.getIcons().add(new Image("project/icon.png"));
        stage.setScene(new Scene(chat));
        stage.setResizable(false);
        stage.show();
        input.getScene().getWindow().hide();
        client.write("/quit");
        client.readThreadStop();
        FileHistoryService.getInstance().save(
                Arrays.asList(output.getText().split("\n").clone()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> history = FileHistoryService.getInstance().load();
        int count = history.size() - 100;
        if (count < 0) count = 0;
        for (int i = count; i < 100 + count && i < history.size(); i++) {
            output.appendText(history.get(i) + "\n");
        }

        try {
            client = Client.getInstance();
            client.read(output);
            client.readThreadStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}