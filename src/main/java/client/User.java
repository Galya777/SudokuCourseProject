package client;

import client.constants.GameLevels;
import client.constants.Result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class User {

    private String username;
    private  GameLevels level;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setLevel(GameLevels level) {
        this.level = level;
    }

    public void setScore(Result score) {
        this.score = score;
    }

    private  Result score;

    public User(String username, GameLevels level, Result score) {
        time=0;
        this.username = username;
        this.level = level;
        this.score = score;
    }
    public String getUsername() {
        return this.username;
    }


    public GameLevels getLevel() {
        return level;
    }



    public Result getScore() {
        return score;
    }


    @Override
    public String toString() {
        return "username=\n'" + username + '\'' +
                ", level=\n" + level +
                ", score=\n" + score;
    }
    public void writeNewUser() {
        PrintWriter fw = null;
        try {
            fw = new PrintWriter("users.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(username);
            bw.write(String.valueOf(score));
            bw.newLine();
            bw.write(time);
        } catch (IOException e) {
            e.printStackTrace();
            fw.close();
        }
    }
}
