package server;

import client.computationlogic.GameLogic;
import client.problemdomain.SudokuGame;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
    GameLogic startTheGame(int difficulty) throws RemoteException;
    boolean isSolution(SudokuGame board);
}
