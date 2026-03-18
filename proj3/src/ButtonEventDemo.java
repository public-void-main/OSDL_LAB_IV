import javafx.application.Application; 
import javafx.scene.Scene; 
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.layout.VBox; 
import javafx.stage.Stage; 
 
public class ButtonEventDemo extends Application { 
 
    @Override 
    public void start(Stage stage) { 
 
        // Creating controls 
        Button btnClick = new Button("Click Here"); 
        Label lblMessage = new Label("Waiting for button click..."); 
 
        // Event handling for button click 
        btnClick.setOnAction(e -> { 
            lblMessage.setText("Button Clicked!"); 
        }); 
 
        // Layout 
        VBox root = new VBox(15); 
        root.getChildren().addAll(btnClick, lblMessage); 
 
        // Scene and Stage 
        Scene scene = new Scene(root, 300, 200); 
        stage.setTitle("Button Click Event Handling"); 
        stage.setScene(scene); 
        stage.show(); 
    } 
 
    public static void main(String[] args) { 
        launch(args); 
    } 
}