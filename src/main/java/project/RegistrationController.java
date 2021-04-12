package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class RegistrationController implements Initializable {

    public TextField login;
    public TextField password;
    public TextField nickName;
    public Client client;


    @Autowired
    public void setClient(Client client) {
        this.client = client;
    }

    public void enter(ActionEvent actionEvent) throws IOException {

        if(!login.getText().trim().isEmpty() && !password.getText().trim().isEmpty() && !nickName.getText().trim().isEmpty()){
            client.write("/auth " + login.getText() + " " + password.getText());
        } else {
            login.clear();
            login.setPromptText("Empty Login or Password");
            password.clear();
            return;
        }

        if (!client.isAuthSuccess()) {
            client.write("/reg " + login.getText() + "*" + password.getText() + "*" + nickName.getText());

            Parent chat = FXMLLoader.load(getClass().getResource("fxml/chat.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.getIcons().add(new Image("project/icon.png"));
            stage.setScene(new Scene(chat));
            stage.setResizable(false);
            stage.show();
            login.getScene().getWindow().hide();
        } else {
            login.clear();
            login.setPromptText("This name already exists");
            password.clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            client = Client.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}