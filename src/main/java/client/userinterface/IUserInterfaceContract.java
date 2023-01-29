package client.userinterface;

import client.problemdomain.SudokuGame;


public interface IUserInterfaceContract {

    //Short is just a smaller version of an "int". Although computers have become very powerful,
    //it is still good practice to use the smallest possible data structure, unless legibility (such as an enum)
    //is a more important concern for the problem in front of you.
    interface EventListener {
        void onSudokuInput(int x, int y, int input);
        void onDialogClick(int difficulty);
    }


    //View refers to what the user can "View", or "See". In English, the word is both a noun and a verb.
    interface View {


        void setListener(IUserInterfaceContract.EventListener listener);
        //update a single square after user input
        void updateSquare(int x, int y, int input);

        //update the entire board, such as after game completion or initial execution of the program
        void updateBoard(SudokuGame game);
        void showDialog(String message);
        void showError(String message);
    }
}