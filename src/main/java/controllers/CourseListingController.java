package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import noidea.App;
import model.*;

public class CourseListingController implements Initializable {

    @FXML
    private ScrollPane coursePane;
    
    @FXML
    private VBox courseBox;

    @FXML
    private TextField courseField;

    @FXML
    private Spinner<Integer> pageSpinner;

    @FXML @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCourses(1, "");
        pageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000));
        pageSpinner.valueProperty().addListener((o, oldPage, newPage) -> loadCourses(newPage, courseField.getText()));
    }
    
    @FXML
    void courseSearchButtonPressed(ActionEvent event) {
        pageSpinner.getValueFactory().setValue(1);
        loadCourses(1, courseField.getText());
    }

    private void loadCourses(int page, String search) {
        courseBox.getChildren().clear();
        List<Course> foundCourses = App.system.getCoursesFromSearch(page - 1, search);
        for(Course c : foundCourses) {
            Text t = new Text(c.toStringDetailed());
            t.setWrappingWidth(1200);
            t.setTextAlignment(TextAlignment.LEFT);
            VBox courseV = new VBox(t);
            
            if(App.system.isStudent()) {
                Button registerButton = new Button("Register for course");
                registerButton.setOnAction(event -> signupButton(c));
                courseV.getChildren().add(registerButton);
            } else if (App.system.hasActiveStudent()) {
                SplitMenuButton gradeButton = new SplitMenuButton();
                gradeButton.setText(String.format("Set Grade (Current: %s)", App.system.getStudent().getCourseGrade(c.getIdentifier())));
                gradeButton.setMinWidth(300);
                for(Grade g : Grade.values()) {
                    gradeButton.getItems().add(makeGradeItem(c.getIdentifier(), g, gradeButton));
                }
                courseV.getChildren().add(gradeButton);
            }

            TitledPane pane = new TitledPane(c.getIdentifier() + " - " + c.getName(), courseV);
            pane.setExpanded(false);
            courseBox.getChildren().add(pane);
        }
    }

    private void signupButton(Course c) {
        if(App.system.addCourseForStudent(c.getIdentifier())) {
            Alert a = new Alert(AlertType.INFORMATION);
            a.setHeaderText("Registration success!");
            a.setContentText("You have successfully signed up for " + c.getIdentifier() + " - " + c.getName() + "!");
            a.show(); 
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Registration failed!");
            a.setContentText("Course not found, or you may have already registered for this course.");
            a.show(); 
        }
    }
    
    private MenuItem makeGradeItem(String id, Grade grade, SplitMenuButton b) {
        MenuItem item = new MenuItem(grade.toString());
        item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if(App.system.addGrade(id, grade)) {
                    Alert al = new Alert(AlertType.INFORMATION);
                    al.setHeaderText("Grade updated!");
                    al.setContentText("Grade has successfully been updated to " + grade.toString());
                    al.show();
                    b.setText(String.format("Set Grade (Current: %s)", grade.toString()));
                } else {
                    Alert al = new Alert(AlertType.ERROR);
                    al.setHeaderText("Error");
                    al.setContentText("Grade could not be changed.");
                    al.show();
                }
            }
        });
        return item;
    }
}