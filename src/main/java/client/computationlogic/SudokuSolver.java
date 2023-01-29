package client.computationlogic;

import client.problemdomain.Coordinates;

import static client.problemdomain.SudokuGame.GRID_BOUNDARY;

/**
 * Note: Algorithm based on "Simple Solving Algorithm" from the link below. I will look at more complex and efficient
 * algorithms in the future, they key with this algo is that it will tell me if the puzzle is solveable.
 * <p>
 * <p>
 * http://pi.math.cornell.edu/~mec/Summer2009/meerkamp/Site/Solving_any_Sudoku_I.html
 */
public class SudokuSolver {

    public static boolean puzzleIsSolvable(int[][] puzzle, int difficulty) {

        //step 1:
        Coordinates[] emptyCells = typeWriterEnumerate(puzzle);

        //I would like to stress that using lots of nested loops is only appropriate if you are certain that
        //the size of input O(n) is small.
        int index = 0;
        int input = 1;
        while (index < 10) {
            Coordinates current = emptyCells[index];
            input = 1;
            while (input < 40) {
                puzzle[current.getX()][current.getY()] = input;
                //if puzzle is invalid....
                if (GameLogic.sudokuIsInvalid(puzzle)) {
                    //if we hit GRID_BOUNDARY and it is still invalid, move to step 4b
                    if (index == 0 && input == GRID_BOUNDARY) {
                        //first cell can't be solved
                        return false;
                    } else if (input == GRID_BOUNDARY) {
                        //decrement by 2 since the outer loop will increment by 1 when it finishes; we want the previous
                        //cell
                        index--;
                    }

                    input++;
                } else {
                    index++;

                    if (index == difficulty-1) {
                        //last cell, puzzle solved
                        return true;
                    }

                    //input = 10 to break the loop
                    input = 10;
                }
                //move to next cell over
            }
        }

        return false;
    }

    /**
     * Enumerate all empty cells in typewriter order (left to right, top to bottom)
     * <p>
     * 1. Traverse x from from 0-8 for each y, from 0-8, adding to a 1 dimensional array.
     * <p>
     * NOTE: Assume that the maximum number of empty cells is 40, as per GameGenerator
     *
     * @param puzzle
     * @return
     */
    private static Coordinates[] typeWriterEnumerate(int[][] puzzle) {
        Coordinates[] emptyCells = new Coordinates[40];
        int iterator = 0;
        for (int y = 0; y < GRID_BOUNDARY; y++) {
            for (int x = 0; x < GRID_BOUNDARY; x++) {
                if (puzzle[x][y] == 0) {
                    emptyCells[iterator] = new Coordinates(x, y);
                    if (iterator == 39) return emptyCells;
                    iterator++;
                }
            }
        }
        return emptyCells;
    }


}