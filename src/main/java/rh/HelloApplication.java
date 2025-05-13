package rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import rh.Models.User;
import rh.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class
HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        User user=new User("admin@admin.com","12345678","admin","rh", User.Role.valueOf("Admin"),true,null);
        UserService userService =new UserService();
        if(userService.admin()==0)
            userService.addUser(user);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/rh/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Clock&Go");
        stage.getIcons().add(new Image(getClass().getResource("/rh/img/logo.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}