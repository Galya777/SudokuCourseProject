package client.buildlogic;

import client.computationlogic.GameLogic;
import client.persistence.LocalStorageImpl;
import client.problemdomain.IStorage;
import client.problemdomain.SudokuGame;
import client.userinterface.IUserInterfaceContract;
import client.userinterface.logic.ControlLogic;

import java.io.IOException;

public class SudokuBuildLogic {



    /**
     * This class takes in the uiImpl object which is tightly-coupled to the JavaFX framework,
     * and binds that object to the various other objects necessary for the application to function.
     */
   private static SudokuGame initialState;
    public static void build(IUserInterfaceContract.View userInterface, int difficulty) throws IOException {

        IStorage storage = new LocalStorageImpl();

        try {
            //will throw if no game data is found in local storage

            initialState = storage.getGameData();
        } catch (IOException e) {

            initialState = GameLogic.getNewGame(difficulty);
            //this method below will also throw an IOException
            //if we cannot update the game data. At this point
            //the application is considered unrecoverable
            storage.updateGameData(initialState);
        }

        IUserInterfaceContract.EventListener uiLogic = new ControlLogic(storage, userInterface);
        userInterface.setListener(uiLogic);
        userInterface.updateBoard(initialState);
    }

    public static SudokuGame solved(IUserInterfaceContract.View userInterface) throws IOException {

        IStorage storage = new LocalStorageImpl();

        try {
            //will throw if no game data is found in local storage

            initialState = storage.getGameData();
        } catch (IOException e) {

            initialState = GameLogic.getSolved();
            //this method below will also throw an IOException
            //if we cannot update the game data. At this point
            //the application is considered unrecoverable
            storage.updateGameData(initialState);
        }

        IUserInterfaceContract.EventListener uiLogic = new ControlLogic(storage, userInterface);
        userInterface.setListener(uiLogic);
        userInterface.updateBoard(initialState);

        return initialState;
    }
    public static SudokuGame getInitialState() {
        return initialState;
    }
}
