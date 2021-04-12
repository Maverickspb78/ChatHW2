package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class ChatApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ClientConfig.class);
        ChatApplication chatApplication = context.getBean("chatApplication", ChatApplication.class);
        chatApplication.init(primaryStage);
    }

    public void init(Stage primaryStage) {
       try {
            Parent auth = FXMLLoader.load(getClass().getResource("fxml/auth.fxml"));
            primaryStage.setTitle("Welcome to the GeekChat");
            primaryStage.getIcons().add(new Image("project/icon.png"));
            primaryStage.setScene(new Scene(auth));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}