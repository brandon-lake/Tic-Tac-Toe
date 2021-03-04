package application;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import java.awt.Point;

/**
 * This class handles all functionality for the Tic Tac Toe application
 *
 * @author  Brandon Lake
 * @date    Nov 30th, 2020
 */
public class Controller {
    @FXML Label box00;
    @FXML Label box01;
    @FXML Label box02;
    @FXML Label box10;
    @FXML Label box11;
    @FXML Label box12;
    @FXML Label box20;
    @FXML Label box21;
    @FXML Label box22;
    @FXML Label messageBox;
    @FXML Button newGameButton;
    @FXML Line winnerLine;

    @FXML Button counterButton;
    @FXML Label counterLabel;
    private int counter;

    private Engine engine = new Engine();
    private Service<Void> backgroundThread;
    private enum states {
        PLAYER_TURN, COMPUTER_TURN, GAME_OVER
    }
    private states gameState = states.PLAYER_TURN;

    private String[][] board = new String[3][3];
    private int[] xBoxCenters = new int[] { 101, 250, 399 };
    private int[] yBoxCenters = new int[] { 104, 260, 416 };

    public Controller() {
        buildEmptyBoard();
    }

    /**
     * A Function which runs when the player clicks on a square to place an "X".  This function triggers all following
     * game logic to take place, including checking if the game has been won, checking if the board is full, and telling
     * the computer to take its turn
     * @param box
     * @param x
     * @param y
     */
    @FXML private void boxClicked(Label box, int x, int y) {
        if (gameState == states.PLAYER_TURN) {
            if (box.getText().compareTo("") == 0) {
                newGameButton.setDisable(false);
                board[x][y] = "X";
                box.setText("X");
                gameState = states.COMPUTER_TURN;
                messageBox.setText("Thinking.....");
                messageBox.setStyle("-fx-alignment: center; -fx-background-color: lightgray");

                backgroundThread = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                if (!isGameOver()) {
                                    // board is not full and game is not won - can make a computer move
                                    Thread.sleep(2000); // to demonstrate this thread doesn't hold up the whole application
                                    Point moveLocation = engine.makeComputerMove(board);
                                    Platform.runLater(() -> {
                                        placeComputerMove(moveLocation);
                                    });
                                }

                                return null;
                            }
                        };
                    }
                };

                backgroundThread.restart();
            }
        }
    }

    /**
     * A Function to determine if the game is over (either a win or a draw), and take appropriate action if so
     * @return  A boolean containing true if the game is over, false otherwise
     */
    private boolean isGameOver() {
        String[] gameWon = engine.isGameWon(board);
        if (gameWon[0].compareTo("yes") == 0) {
            Platform.runLater(() -> {
                handleGameWon(gameWon);
            });
            return true;
        } else {
            // game is not won
            if (engine.isBoardFull(board)) {
                Platform.runLater(() -> {
                    handleDraw();
                });
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * A Function to draw a "O" at the given Point location on the board
     * @param p A Point containing the target x and y coordinates to draw the "O"
     */
    private void placeComputerMove(Point p) {
        board[p.x][p.y] = "O";
        int target = p.x * 3 + p.y;

        switch (target) {
            case 0:
                box00.setText("O");
                break;
            case 1:
                box01.setText("O");
                break;
            case 2:
                box02.setText("O");
                break;
            case 3:
                box10.setText("O");
                break;
            case 4:
                box11.setText("O");
                break;
            case 5:
                box12.setText("O");
                break;
            case 6:
                box20.setText("O");
                break;
            case 7:
                box21.setText("O");
                break;
            case 8:
                box22.setText("O");
                break;
        }
        if (!isGameOver()) {
            gameState = states.PLAYER_TURN;
            messageBox.setText("Player Turn!");
            messageBox.setStyle("-fx-alignment: center; -fx-background-color: greenyellow");
        }
    }

    /**
     * A Function to run when the game has been won.  This function updates the message label below the board, and draws
     * a line to indicate where the game was won.
     * @param gameWon
     */
    private void handleGameWon(String[] gameWon) {
        // set game state and game message
        gameState = states.GAME_OVER;
        messageBox.setText("Game Over - " + gameWon[1] + " Win!");
        String color = gameWon[1].compareTo("Player") == 0 ? "greenyellow" : "red";
        messageBox.setStyle("-fx-alignment: center; -fx-background-color: " + color);

        // "draw" line based on given win position
        winnerLine.setStroke(Paint.valueOf(color));
        int strLen = gameWon[2].length();
        String direction = gameWon[2].substring(0, strLen - 1);
        int position = Integer.parseInt(gameWon[2].substring(strLen - 1, strLen));

        // draw the appropriate line based on the winning direction and position
        switch (direction) {
            case "vertical":
                winnerLine.setStartX(xBoxCenters[position]);
                winnerLine.setEndX(xBoxCenters[position]);
                winnerLine.setStartY(yBoxCenters[0]);
                winnerLine.setEndY(yBoxCenters[2]);
                break;
            case "horizontal":
                winnerLine.setStartX(xBoxCenters[0]);
                winnerLine.setEndX(xBoxCenters[2]);
                winnerLine.setStartY(yBoxCenters[position]);
                winnerLine.setEndY(yBoxCenters[position]);
                break;
            case "diagonal":
                winnerLine.setStartX(xBoxCenters[0]);
                winnerLine.setEndX(xBoxCenters[2]);
                switch (position) {
                    case 0:
                        winnerLine.setStartY(yBoxCenters[0]);
                        winnerLine.setEndY(yBoxCenters[2]);
                        break;
                    case 1:
                        winnerLine.setStartY(yBoxCenters[2]);
                        winnerLine.setEndY(yBoxCenters[0]);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        winnerLine.setOpacity(1);
    }

    /**
     * A Function to run if the game is a draw and the board is full
     */
    private void handleDraw() {
        gameState = states.GAME_OVER;
        messageBox.setText("Game Over - Draw!");
        messageBox.setStyle("-fx-alignment: center; -fx-background-color: lightgray");
    }

    /**
     * A Function to reset the internal board array to an empty board
     */
    private void buildEmptyBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    /**
     * A Function to run when the new game button has been clicked.  Resets the board state for the next game.
     */
    @FXML private void startNewGame() {
        // start new game
        buildEmptyBoard();
        box00.setText("");
        box01.setText("");
        box02.setText("");
        box10.setText("");
        box11.setText("");
        box12.setText("");
        box20.setText("");
        box21.setText("");
        box22.setText("");

        // hide line
        winnerLine.setStartX(100);
        winnerLine.setEndX(400);
        winnerLine.setStartY(13);
        winnerLine.setEndY(13);
        winnerLine.setOpacity(0);

        // disabled when board is empty
        newGameButton.setDisable(true);

        // reset any on page messages?
        gameState = states.PLAYER_TURN;
        messageBox.setText("Player Turn!");
        messageBox.setStyle("-fx-alignment: center; -fx-background-color: greenyellow");
    }

    /**
     * A Function to run when the "Click me?" button is clicked.  This will increment the counter just above it, and it
     * is used to demonstrate that the background thread (game engine) does not interfere with GUI updates
     */
    @FXML private void incrementCounter() {
        counter++;
        counterLabel.setText(String.valueOf(counter));
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box00Clicked() {
        boxClicked(box00, 0, 0);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box01Clicked() {
        boxClicked(box01, 0, 1);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box02Clicked() {
        boxClicked(box02, 0, 2);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box10Clicked() {
        boxClicked(box10, 1, 0);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box11Clicked() {
        boxClicked(box11, 1, 1);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box12Clicked() {
        boxClicked(box12, 1, 2);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box20Clicked() {
        boxClicked(box20, 2, 0);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box21Clicked() {
        boxClicked(box21, 2, 1);
    }

    /**
     * Sends appropriate parameters to the boxClicked method
     */
    @FXML private void box22Clicked() {
        boxClicked(box22, 2, 2);
    }
}
