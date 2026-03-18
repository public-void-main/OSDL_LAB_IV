import javafx.application.Application; 
import javafx.scene.Scene; 
import javafx.scene.control.Button; 
import javafx.scene.layout.StackPane; 
import javafx.stage.Stage; 
 
public class HelloFX extends Application { 
 
    @Override 
    public void start(Stage stage) { 
 
        // Creating a Button 
        Button btn = new Button("Click Me"); 
 
        // Adding button to layout 
        StackPane root = new StackPane(); 
        root.getChildren().add(btn); 
 
        // Creating a Scene 
        Scene scene = new Scene(root, 300, 200); 
 
        // Setting stage properties 
        stage.setTitle("JavaFX Button Example"); 
        stage.setScene(scene); 
        stage.show(); 
    } 
 
    public static void main(String[] args) { 
        launch(args); 
    } 
} 