package client;

import client.e.ClockWithLabelApp;
import client.userinterface.IUserInterfaceContract;
import client.userinterface.UserInterfaceImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the Root Container (the thing which attends to all of the primary objects which must communicate when
 * the program is running (a running program is called a "process").
 */
public class SudokuApplication extends Application {


    private IUserInterfaceContract.View uiImpl;

    @Override
    public void start(Stage stage) throws Exception {


        uiImpl = new UserInterfaceImpl(stage);
        Timer myTimer = new Timer();
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                System.out.println("Time: ");
                ClockWithLabelApp.launch();

            }
        };
        myTimer.schedule(task, 5000l);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
