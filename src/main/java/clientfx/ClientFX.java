import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public final static String REGISTER_COMMAND = "REGISTER";

    private static final String SERVER_ADDRESS = "localhost";

    private static int SERVER_PORT = 1337;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("client_fx.fxml"));
        primaryStage.setTitle("Inscription aux cours");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
