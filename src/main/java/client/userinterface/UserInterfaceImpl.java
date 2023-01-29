package client.userinterface;

import client.UndoAndRedo;
import client.User;
import client.buildlogic.SudokuBuildLogic;
import client.constants.GameLevels;
import client.constants.GameState;
import client.constants.Messages;
import client.constants.Result;
import client.e.ClockWithLabelApp;
import client.problemdomain.Coordinates;
import client.problemdomain.SudokuGame;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.IServer;
import server.Server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.scene.paint.Color.TURQUOISE;

/**
 * Manages the window, and displays a pop up notification when the user completes the puzzle.
 */
public class UserInterfaceImpl implements IUserInterfaceContract.View,
        EventHandler<KeyEvent> {
    private final Stage stage;
    private final Group root;
    private IServer sudokuServer;

    private User user;

    private SudokuGame game;


    private GameLevels level;
    @FXML
    private final Button btnNewGame = new Button("New Game");
    @FXML
    private final Button btnShowSolution = new Button("Show");
    private final Button btnCheck = new Button("Check");
    @FXML
    private final Button btnExit = new Button("Exit");
    @FXML
    private final Button btnUndo = new Button("Undo");
    @FXML
    private final Button btnRedo = new Button("Redo");

    @FXML
    private Button lstart = new Button("Start");


    private Scene scene1, scene2;

    @FXML
    private RadioButton level1 = new RadioButton("Easy");

    @FXML
    private RadioButton level2 =new RadioButton("Medium");;

    @FXML
    private RadioButton level3 = new RadioButton("Hard");

    //This HashMap stores the Hash Values (a unique identifier which is automatically generated;
    // see java.lang.object in the documentation) of each TextField by their Coordinates. When a SudokuGame
    //is given to the updateUI method, we iterate through it by X and Y coordinates and assign the values to the
    //appropriate TextField therein. This means we don't need to hold a reference variable for every god damn
    //text field in this app; which would be awful.
    //The Key (<Key, Value> -> <Coordinates, Integer>) will be the HashCode of a given InputField for ease of lookup
    private HashMap<Coordinates, SudokuTextField> textFieldCoordinates;
    UndoAndRedo movesManager;

    private IUserInterfaceContract.EventListener listener;

    private boolean clicked;


    //Size of the window
    protected static final double WINDOW_X = 700;
    protected static final double WINDOW_Y = 700;
    //distance between window and board
    private static final double BOARD_PADDING = 50;

    private static final double BOARD_X_AND_Y = 576;
    private static final Color BACKGROUND = Color.rgb(224, 10, 136);
    private static final Color BACKGROUND_BOARD = Color.rgb(0, 150, 136);
    private static final String SUDOKU = "Sudoku";

    public UserInterfaceImpl(Stage stage) throws NotBoundException, IOException {
        this.stage=stage;
        this.root=new Group();
        this.textFieldCoordinates = new HashMap<>();
        clicked=false;
        TextInputDialog inputDialog=new TextInputDialog();
        inputDialog.setContentText("Enter your username: ");
        Optional<String> enteredUsername = inputDialog.showAndWait();

        if(enteredUsername.isPresent()) {
            user=new User(enteredUsername.get(), GameLevels.EASY, Result.LOOSE);
        }else{
            Platform.exit();
            System.exit(0);
        }

        //Scene 1
        Label label1= new Label("Choose your level");
        lstart= new Button("Start the game!");

        ToggleGroup question1= new ToggleGroup();
        level1.setToggleGroup(question1);
        level2.setToggleGroup(question1);
        level3.setToggleGroup(question1);

        lstart.setDisable(true);

        level1.setOnAction(e -> lstart.setDisable(false) );
        level2.setOnAction(e -> lstart.setDisable(false) );
        level3.setOnAction(e -> lstart.setDisable(false) );

        lstart.setOnAction(e-> {
            if(level1.isSelected()){
                level= GameLevels.EASY;
            }else if(level2.isSelected()){
                level = GameLevels.MEDIUM;
            }else if(level3.isSelected()){
                level=GameLevels.HARD;
            }
            stage.setScene(scene2);
        });
        VBox layout1 = new VBox(20);
        layout1.getChildren().addAll(label1, level1, level2, level3, lstart);
        scene1= new Scene(layout1, 300, 250);


//Scene 2
        Label label2= new Label();
        VBox layout2= new VBox(20);
        layout2.getChildren().addAll(label2);
        scene2= new Scene(layout2, WINDOW_X,WINDOW_Y);
        //Get SudokuGame object for a new game


    ///buttons are disabled to prevent errors
        Start();
        SoledItOrNot();
        btnCheck.setDisable(true);
        Solution();
        btnShowSolution.setDisable(true);
        Exit();
        Undo();
        Redo();
        btnUndo.setDisable(true);
        btnRedo.setDisable(true);




        initializeUserInterface();
        stage.setScene(scene1);
        connectToServer();

    }


    @Override
    public void setListener(IUserInterfaceContract.EventListener listener) {
        this.listener = listener;
    }

    public void initializeUserInterface() {

        drawTitle(root);
        drawBackground(root);
        drawSudokuBoard(root);
        drawTextFields(root);
        drawGridLines(root);

        stage.show();
    }
    public void connectToServer() throws IOException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(1099);
        sudokuServer = (IServer) registry.lookup(Server.SERVICE);


    }
    private void drawTextFields(Group root) {
        //where to start drawing the numbers
        final int xOrigin = 50;
        final int yOrigin = 50;
        //how much to move the x or y value after each loop
        final int xAndYDelta = 64;


        for (int xIndex = 0; xIndex < 9; xIndex++) {
            for (int yIndex = 0; yIndex < 9; yIndex++) {
                int x = xOrigin + xIndex * xAndYDelta;
                int y = yOrigin + yIndex * xAndYDelta;
                //draw it
                SudokuTextField tile = new SudokuTextField(xIndex, yIndex);

                //encapsulated style information
                styleSudokuTile(tile, x, y);

                //Note: Note that UserInterfaceImpl implements EventHandler<ActionEvent> in the class declaration.
                //By passing "this" (which means the current instance of UserInterfaceImpl), when an action occurs,
                //it will jump straight to "handle(ActionEvent actionEvent)" down below.
                tile.setOnKeyPressed(this);

                textFieldCoordinates.put(new Coordinates(xIndex, yIndex), tile);

                root.getChildren().add(tile);
            }
        }
    }

    private void styleSudokuTile(SudokuTextField tile, double x, double y) {
        Font numberFont = new Font(32);
        tile.setFont(numberFont);
        tile.setAlignment(Pos.CENTER);

        tile.setLayoutX(x);
        tile.setLayoutY(y);
        tile.setPrefHeight(64);
        tile.setPrefWidth(64);

        tile.setBackground(Background.EMPTY);
    }

    private void drawGridLines(Group root) {
        //draw vertical lines starting at 114x and 114y:
        int xAndY = 114;
        int index = 0;
        while (index < 8) {
            int thickness;
            if (index == 2 || index == 5) {
                thickness = 3;
            } else {
                thickness = 2;
            }

            Rectangle verticalLine = getLine(
                    xAndY + 64 * index,
                    BOARD_PADDING,
                    BOARD_X_AND_Y,
                    thickness
                    );

            Rectangle horizontalLine = getLine(
                    BOARD_PADDING,
                    xAndY + 64 * index,
                    thickness,
                    BOARD_X_AND_Y
            );

            root.getChildren().addAll(
                    verticalLine,
                    horizontalLine
            );

            index++;
        }
    }

    /**
     * Convenience method to reduce repetitious code.
     *
     * X, Y, Height, Width,
     * @return A Rectangle to specification
     */
    public Rectangle getLine(double x, double y, double height, double width){
        Rectangle line = new Rectangle();

        line.setX(x);
        line.setY(y);

        line.setHeight(height);
        line.setWidth(width);

        line.setFill(Color.BLACK);
        return line;

    }

    private void drawBackground(Group root) {
         scene2 = new Scene(root, WINDOW_X, WINDOW_Y);
        scene2.setFill(BACKGROUND);
        stage.setScene(scene2);
    }

    private void drawSudokuBoard(Group root) {
        Rectangle boardBackground = new Rectangle();
        boardBackground.setX(BOARD_PADDING);
        boardBackground.setY(BOARD_PADDING);
        boardBackground.setWidth(BOARD_X_AND_Y);
        boardBackground.setHeight(BOARD_X_AND_Y);
        boardBackground.setFill(BACKGROUND_BOARD);
        root.getChildren().add(boardBackground);
    }

    private void drawTitle(Group root) {
        Text title = new Text(200, 50, SUDOKU);
        title.setFill(TURQUOISE);
        Font font = new Font(50);
        title.setFont(font);
        root.getChildren().add(title);

    }

    /**
     * Each time the user makes an input (which can be 0 to delete a number), we update the user
     * interface appropriately.
     */
    @Override
    public void updateSquare(int x, int y, int input) {
        SudokuTextField tile = textFieldCoordinates.get(new Coordinates(x, y));
        String value = Integer.toString(
                input
        );

        if (value.equals("0")) value = "";

        tile.textProperty().setValue(value);
    }

    @Override
    public void updateBoard(SudokuGame game) {
        for (int xIndex = 0; xIndex < 9; xIndex++) {
            for (int yIndex = 0; yIndex < 9; yIndex++) {
                TextField tile = textFieldCoordinates.get(new Coordinates(xIndex, yIndex));

                String value = Integer.toString(
                        game.getCopyOfGridState()[xIndex][yIndex]
                );

                if (value.equals("0")) value = "";
                tile.setText(
                        value
                );

                //If a given tile has a non-zero value and the state of the game is GameState.NEW, then mark
                //the tile as read only. Otherwise, ensure that it is NOT read only.
                if (game.getGameState() == GameState.NEW){
                    if (value.equals("")) {
                        tile.setStyle("-fx-opacity: 1;");
                        tile.setDisable(false);
                    } else {
                        tile.setStyle("-fx-opacity: 0.8;");
                        tile.setDisable(true);
                    }
                }
            }
        }
    }

    @Override
    public void showDialog(String message) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
        dialog.showAndWait();

        if (dialog.getResult() == ButtonType.OK) listener.onDialogClick(level.getLevel());
    }

    @Override
    public void showError(String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        dialog.showAndWait();
    }


    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getText().equals("0")
                    || event.getText().equals("1")
                    || event.getText().equals("2")
                    || event.getText().equals("3")
                    || event.getText().equals("4")
                    || event.getText().equals("5")
                    || event.getText().equals("6")
                    || event.getText().equals("7")
                    || event.getText().equals("8")
                    || event.getText().equals("9")
            ) {
                int value = Integer.parseInt(event.getText());
                handleInput(value, event.getSource());
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                handleInput(0, event.getSource());
            } else {
                ((TextField)event.getSource()).setText("");
            }
        }

        event.consume();
    }

    /**
     * @param value  expected to be an integer from 0-9, inclusive
     * @param source the textfield object that was clicked.
     */
    private void handleInput(int value, Object source) {
        listener.onSudokuInput(
                ((SudokuTextField) source).getX(),
                ((SudokuTextField) source).getY(),
                value
        );
    }

    public void Start() {
        btnNewGame.setMaxSize(300, 500);
        btnNewGame.setStyle("fx-background-color:  #0000ff");
        btnNewGame.setLayoutX(50);
        btnNewGame.setLayoutY(650);
        btnCheck.setDisable(false);
        btnShowSolution.setDisable(false);
        btnUndo.setDisable(false);
        btnRedo.setDisable(false);
        btnNewGame.setOnAction(e-> {
            /*try {
                timer.setBtnStart(btnNewGame);
                timer.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
            if(clicked){
                stage.setScene(scene1);
                listener.onDialogClick(level.getLevel());
            }
            try {
                SudokuBuildLogic.build(this, level.getLevel());
                game=SudokuBuildLogic.getInitialState();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            clicked=true;
        });



        root.getChildren().add(btnNewGame);
    }
    public void Exit(){
        btnExit.setMaxSize(300, 500);
        btnExit.setStyle("fx-background-color:  #0000ff");
        btnExit.setLayoutX(300);
        btnExit.setLayoutY(650);
        btnExit.setOnAction(value->{
            user.writeNewUser();
            Platform.exit();
            System.exit(0);
        });
        root.getChildren().add(btnExit);
    }
    public void Undo(){
        btnUndo.setMaxSize(300, 500);
        btnUndo.setStyle("fx-background-color:  #0000ff");
        btnUndo.setLayoutX(400);
        btnUndo.setLayoutY(650);
        btnUndo.setOnAction(value->{
            Coordinates undoneCell = new Coordinates(movesManager.Undo());
            if(undoneCell.getPreviousValue() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Nothing to undo");
                System.out.println("Nothing to undo");
            }
            else {
                updateSquare(undoneCell.getX(), undoneCell.getY(), undoneCell.getPreviousValue());
            }
        });
        root.getChildren().add(btnUndo);
    }
    public void Redo(){
        btnRedo.setMaxSize(300, 500);
        btnRedo.setStyle("fx-background-color:  #0000ff");
        btnRedo.setLayoutX(500);
        btnRedo.setLayoutY(650);
        btnRedo.setOnAction(value->{
            Coordinates redoneCell = movesManager.Redo();
            if(redoneCell == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Nothing to redo");
                System.out.println("Nothing to redo");
                return;
            }
            updateSquare(redoneCell.getX(), redoneCell.getY(), redoneCell.getValue());
        });
        root.getChildren().add(btnRedo);
    }
    public void Solution(){
        btnShowSolution.setMaxSize(300, 500);
        btnShowSolution.setStyle("fx-background-color:  #0000ff");
        btnShowSolution.setLayoutX(150);
        btnShowSolution.setLayoutY(650);
        btnShowSolution.setOnAction(value->{
            try {
                printSolution();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        root.getChildren().add(btnShowSolution);
    }
    public void SoledItOrNot(){
        btnCheck.setMaxSize(300, 500);
        btnCheck.setStyle("fx-background-color:  #0000ff");
        btnCheck.setLayoutX(200);
        btnCheck.setLayoutY(650);
        btnCheck.setOnAction(value->{
           if(game.getGameState()==GameState.COMPLETE){
               showDialog(Messages.GAME_COMPLETE);
               user.setScore(Result.WIN);
           }
           else{
               Alert a=new Alert(Alert.AlertType.WARNING);
               a.setContentText("Wrong answer. Sorry you lost!");
               a.show();
               user.setScore(Result.LOOSE);
               try {
                   printSolution();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });
        root.getChildren().add(btnCheck);
    }
  void printSolution() throws IOException {
        game=SudokuBuildLogic.solved(this);
       updateBoard(game);
  }
}
