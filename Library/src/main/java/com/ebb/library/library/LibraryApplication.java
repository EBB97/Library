package com.ebb.library.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

public class LibraryApplication extends Application {
    // This variable stores the application's 'SessionFactory'.
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        // Instantiate 'SessionFactory'.
        sessionFactory = new Configuration().configure().buildSessionFactory();
        launch();
    }

    // Get 'SessionFactory'.
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("library-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 452);
        stage.setTitle("Library!");
        stage.setScene(scene);
        stage.show();
    }
}