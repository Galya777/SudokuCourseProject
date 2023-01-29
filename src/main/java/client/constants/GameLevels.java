package client.constants;

public enum GameLevels {
    EASY(15),
    MEDIUM(30),
    HARD(50);

    private final int level;
    GameLevels(int i) {
        this.level=i;
    }

    public int getLevel() {
        return level;
    }
}
