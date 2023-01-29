package client.problemdomain;

import client.computationlogic.SudokuUtilities;
import client.constants.GameState;

import java.io.Serializable;

/**
 * Remember, a program contains representations of real world objects (i.e. Money, User, Game). These things
 * contain real world information (although they may also contain useless/stub/test information), and are necessary
 * for a program which does anything practical.
 *
 * A game of sudoku is a board, which contain 81 squares.
 *
 */
public class SudokuGame implements Serializable {
    private final GameState gameState;
    private final int[][] gridState;

    /**
     * To make it easier to work with Arrays (where the first index position is 0 instead of 1, and so on),
     * Grid coordinates will be represented with x and y index values ranging from 0 (inclusive) to 8 (inclusive).
     */
    public static final int GRID_BOUNDARY = 9;

    public SudokuGame(GameState gameState, int[][] gridState) {
        this.gameState = gameState;
        this.gridState = gridState;
    }



    public GameState getGameState() {
        return gameState;
    }

    public int[][] getCopyOfGridState() {
        return SudokuUtilities.copyToNewArray(gridState);
    }

}
