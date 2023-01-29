package server;

import javafx.application.Application;
import javafx.stage.Stage;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server extends Application {
    public static final String SERVICE = "Service";
    @Override
    public void start(Stage primaryStage) throws AlreadyBoundException, RemoteException {
        IServer serverInterface = new StartServer();

        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind(SERVICE, serverInterface);

    }


    public static void main(String[] args) {
        launch(args);
    }

}
