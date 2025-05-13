package rh.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ChangerRoleController {

    private Runnable onEmployeSelected;
    private Runnable onComptableSelected;

    public void setOnEmployeSelected(Runnable onEmployeSelected) {
        this.onEmployeSelected = onEmployeSelected;
    }

    public void setOnComptableSelected(Runnable onComptableSelected) {
        this.onComptableSelected = onComptableSelected;
    }

    @FXML
    private void handleEmploye(ActionEvent event) {
        if (onEmployeSelected != null) onEmployeSelected.run();
        closeWindow(event);
    }

    @FXML
    private void handleComptable(ActionEvent event) {
        if (onComptableSelected != null) onComptableSelected.run();
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
