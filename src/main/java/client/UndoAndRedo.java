package client;


import client.problemdomain.Coordinates;

import java.util.Stack;

public class UndoAndRedo {
    private Stack<Coordinates> undo;
    private Stack<Coordinates> redo;

    public UndoAndRedo() {
        undo = new Stack<>();
        redo = new Stack<>();
    }

    public void addMove(Coordinates current) {
        undo.push(current);
        redo.clear();
    }

    public Coordinates Undo() {
        if (undo.isEmpty()) return null;
        Coordinates cell = undo.pop();
        redo.push(cell);

        return new Coordinates(cell.getX(), cell.getY());
    }

    public Coordinates Redo() {
        if (redo.isEmpty()) return null;
        Coordinates cell = redo.pop();
        undo.push(cell);

        return new Coordinates(cell.getX(), cell.getY());
    }
}
