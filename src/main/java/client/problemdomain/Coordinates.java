package client.problemdomain;

import java.util.Objects;

/**
 * Convenience class for storing the location of a given tile in the Sudoku puzzle in a Hashmap.
 */
public class Coordinates {
    private final int x;
    private final int y;
    private int previousValue;
    private int value;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
        value=0;
    }

    public Coordinates(Coordinates otherCell) {
        this.x = otherCell.x > 0 && otherCell.x < 9 ? otherCell.x : 0;
        this.y = otherCell.y > 0 && otherCell.y < 9 ? otherCell.y : 0;
        this.previousValue = otherCell.previousValue >= 1 && otherCell.previousValue <= 9 ? otherCell.previousValue : 0;
        this.value=otherCell.value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(int previousValue) {
        this.previousValue = previousValue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
