package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import rh.Models.User;
import rh.Service.UserService;
import rh.Utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class users implements Initializable{

    @FXML
    private VBox vbox;
    @FXML
    private AnchorPane profilebox;
    @FXML
    private VBox vboxscroll;
    private Boolean profileStatus;
    public void setProfileStatus(Boolean profileStatus) {
        this.profileStatus = profileStatus;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXMLLoader fxl=new FXMLLoader();
        fxl.setLocation(getClass().getResource("/rh/sidebar.fxml"));
        Parent root= null;
        try {
            root = fxl.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        vbox.getChildren().add(root);
        setProfileStatus(false);
        List<User> users;
        UserService userService=new UserService();
        try {
            users=userService.getAllusers();
            for(int i=0;i<users.size();i++){
                FXMLLoader fxll=new FXMLLoader();
                fxll.setLocation(getClass().getResource("/rh/card.fxml"));
                Parent roott=fxll.load();
                Card c=fxll.getController();
                c.setdata(users.get(i));
                c.setUser(users.get(i));
                c.setId(users.get(i).getId());
                vboxscroll.getChildren().add(roott);
                VBox.setMargin(roott, new Insets(10, 0, 10, 0));
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        UserSession userSession =UserSession.getInstance();
        userSession.logout();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/rh/login.fxml"));
        Parent root=loader.load();
        vbox.getScene().setRoot(root);
    }

    public void OpenProfile(ActionEvent actionEvent) throws IOException {
        setProfileStatus(!this.profileStatus);
        profilebox.getChildren().clear();

        if (profileStatus) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rh/profilecard.fxml"));
            AnchorPane profilePane = loader.load();
            profilebox.getChildren().add(profilePane);
        }
    }
}
