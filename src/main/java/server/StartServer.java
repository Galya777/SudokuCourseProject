package server;

import client.computationlogic.GameLogic;
import client.constants.GameState;
import client.problemdomain.SudokuGame;

import java.io.Serializable;
import java.rmi.RemoteException;

public class StartServer implements IServer, Serializable {

    @Override
    public GameLogic startTheGame(int difficulty) throws RemoteException {
        return new GameLogic(difficulty);
    }
    @Override
    public boolean isSolution(SudokuGame board) {
        return board.getGameState().equals(GameState.COMPLETE);
    }

}
