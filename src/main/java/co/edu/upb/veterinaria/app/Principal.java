package co.edu.upb.veterinaria.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Principal extends Application {

    private static final String LOGIN_FXML =
            "/co/edu/upb/veterinaria/views/Login/Login.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
        Scene scene = new Scene(loader.load(), 1200, 720); // igual a tu Login (Opción A)
        stage.setTitle("SOS Veterinaria");
        stage.setScene(scene);
        stage.setMaximized(true); // para mantener el look Opción A
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
