import javafx.application.Application; 
import javafx.scene.Scene; 
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.TextField; 
import javafx.scene.layout.GridPane; 
import javafx.stage.Stage; 
 
public class SimpleFormDemo extends Application { 
 
    @Override 
    public void start(Stage stage) { 
 
        // Creating labels 
        Label lblName = new Label("Name:"); 
        Label lblEmail = new Label("Email:"); 
        Label lblResult = new Label(); 
 
        // Creating text fields 
        TextField txtName = new TextField(); 
        TextField txtEmail = new TextField(); 
 
        // Creating button 
        Button btnSubmit = new Button("Submit"); 
 
        // Button event handling 
        btnSubmit.setOnAction(e -> { 
            String name = txtName.getText(); 
            String email = txtEmail.getText(); 
            lblResult.setText("Submitted:\nName: " + name + "\nEmail: " + email); 
        }); 
 
        // Layout using GridPane 
        GridPane grid = new GridPane(); 
        grid.setHgap(10); 
        grid.setVgap(10); 
 
        grid.add(lblName, 0, 0); 
        grid.add(txtName, 1, 0); 
        grid.add(lblEmail, 0, 1); 
        grid.add(txtEmail, 1, 1); 
        grid.add(btnSubmit, 1, 2); 
        grid.add(lblResult, 1, 3); 
 
        // Scene and Stage 
        Scene scene = new Scene(grid, 350, 250); 
        stage.setTitle("Simple Form Application"); 
        stage.setScene(scene); 
        stage.show(); 
    } 
 
    public static void main(String[] args) { 
        launch(args); 
    } 
} 