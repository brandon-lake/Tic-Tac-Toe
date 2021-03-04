package application;

import java.awt.Point;

/**
 * This class handles all game logic for the Tic Tac Toe application
 *
 * @author  Brandon Lake
 * @date    Nov 30th, 2020
 */
public class Engine {
    public Engine() {}

    /**
     * A Function to determine whether the game has been won, and to return all relevant information for updating the GUI
     * @param board A 2D String array containing the values at each place on the board - either "", "X", or "O"
     * @return A String array containing 3 pieces of information - whether the game has been won (1), and if so by who (2) and where (3).
     * 1 = "yes"/"no"
     * 2 = "Player"/"Computer"
     * 3 = "vertical0"/"horizontal2" The word is the direction and the number is the row/col.
     *      For diagonal, "diagonal0" = top left -> bot right, "diagonal1" = bot left -> top right
     */
    public String[] isGameWon(String[][] board) {
        // check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0].compareTo("X") == 0 && board[i][1].compareTo("X") == 0 && board[i][2].compareTo("X") == 0) {
                return new String[] {"yes", "Player", "horizontal" + i};
            }
            else if (board[i][0].compareTo("O") == 0 && board[i][1].compareTo("O") == 0 && board[i][2].compareTo("O") == 0) {
                return new String[] {"yes", "Computer", "horizontal" + i};
            }
        }

        // check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i].compareTo("X") == 0 && board[1][i].compareTo("X") == 0 && board[2][i].compareTo("X") == 0) {
                return new String[] {"yes", "Player", "vertical" + i};
            }
            else if (board[0][i].compareTo("O") == 0 && board[1][i].compareTo("O") == 0 && board[2][i].compareTo("O") == 0) {
                return new String[] {"yes", "Computer", "vertical" + i};
            }
        }

        // check diagonals
        if (board[0][0].compareTo("X") == 0 && board[1][1].compareTo("X") == 0 && board[2][2].compareTo("X") == 0) {
            return new String[] {"yes", "Player", "diagonal0"};
        }
        else if (board[0][0].compareTo("O") == 0 && board[1][1].compareTo("O") == 0 && board[2][2].compareTo("O") == 0) {
            return new String[] {"yes", "Computer", "diagonal0"};
        }
        else if (board[2][0].compareTo("X") == 0 && board[1][1].compareTo("X") == 0 && board[0][2].compareTo("X") == 0) {
            return new String[] {"yes", "Player", "diagonal1"};
        }
        else if (board[2][0].compareTo("O") == 0 && board[1][1].compareTo("O") == 0 && board[0][2].compareTo("O") == 0) {
            return new String[] {"yes", "Computer", "diagonal1"};
        }

        return new String[] {"no", "", ""};
    }

    /**
     * A Function to check whether the board is full
     * @param board A 2D String array containing the values at each place on the board - either "", "X", or "O"
     * @return  True if the board is full, false otherwise
     */
    public boolean isBoardFull(String[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].compareTo("") == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * A Function to simulate the computer AI and calculate where to place its next move, based on the current state of
     * the board.
     *
     * Algorithm found at: https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-3-tic-tac-toe-ai-finding-optimal-move/
     *
     * @param board A 2D String array containing the values at each place on the board - either "", "X", or "O"
     * @return  A Point containing the x and y coordinates of the computers calculated next move
     */
    public Point makeComputerMove(String[][] board) {
        int topScore = -1000;
        Point bestMove = new Point(0, 0);

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {
                if (board[i][j].compareTo("") == 0) {
                    // for all available moves on the board...
                    board[i][j] = "O";
                    int score = minimax(board, 0, false);
                    board[i][j] = "";

                    if (score > topScore) {
                        bestMove = new Point(i, j);
                        topScore = score;
                    }
                }
            }
        }
        return bestMove;
    }

    /**
     * A Function to recursively step through every available combination of moves, alternating player and AI until either
     * a loss or a win has been found, then return +10 - depth for a found win, and -10 + depth for a found loss.  The
     * best found value for each recursive search is assigned to the "head" of that recursive search, aka the first
     * move of that chain of moves, aka the computers next move.
     *
     * Algorithm found at: https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-3-tic-tac-toe-ai-finding-optimal-move/
     *
     * @param board A 2D String array containing the values at each place on the board - either "", "X", or "O"
     * @param depth The depth of the recursive search for a win/loss, to be used to optimize the best decision of
     *              multiple winning decisions
     * @param isAiTurn A boolean variable to represent whose turn it currently is within the recursive search
     * @return  An integer value containing the weighted value for the computer to make its move at the given square,
     *          where +10 means the computer wins in 1 turn, and -10 means the computer loses in 1 turn.
     */
    private int minimax(String[][] board, int depth, boolean isAiTurn) {
        String[] gameWon = isGameWon(board);
        if (gameWon[0].compareTo("yes") == 0) {
            if (gameWon[1].compareTo("Computer") == 0) {
                return 10;
            } else {
                return -10;
            }
        }

        if (isBoardFull(board)) {
            return 0;
        }

        int bestScore;
        if (isAiTurn) {
            bestScore = -1000;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].compareTo("") == 0) {
                        board[i][j] = "O";
                        int score = minimax(board, depth + 1, false);
                        if (score > bestScore) {
                            bestScore = score - depth;
                        }
                        board[i][j] = "";
                    }
                }
            }
        } else {
            bestScore = 1000;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].compareTo("") == 0) {
                        board[i][j] = "X";
                        int score = minimax(board, depth + 1, true);
                        if (score < bestScore) {
                            bestScore = score + depth;
                        }
                        board[i][j] = "";
                    }
                }
            }
        }
        return bestScore;
    }
}
