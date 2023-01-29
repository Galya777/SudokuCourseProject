package client.e;

import javafx.application.Platform;


public class ClockUsingThread extends ClockPaneWithProperty
        implements Runnable {

    public ClockUsingThread() {
        Thread t =   new Thread(this);
        t.setDaemon(true);// stop the thread when JavaFX exits
        t.start();
    }

    public void run() {
        while (true) {
            // run on the UI thread etCurrentTime()
            Platform.runLater(() -> setCurrentTime());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

}
