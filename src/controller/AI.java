
package controller;

import controller.GameManager;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cell;
import model.GameState;
import model.Player;
import view.Board;
import view.UserInterface;

public class AI implements Runnable {
    
    private Thread thread;
    private String threadName;
    
    public static double maxPly;
    public static int bestPosition;
    public static int iteration = 0; // For testing purpose
    public int negativeInfinity = Integer.MIN_VALUE;
    public int positiveInfinity = Integer.MAX_VALUE;
 
    
    
    /**
     * 
     * @param board The board of the game.
     * @param player The current player.
     * @param depth The depth of the node tree.
     * @return int Evaluated value of th
     */
    public int findBestMoveMiniMax(Board board, String player, int depth) {
        return minimax(board, player, depth);
    }
    
    
    public void findBestMoveAlphaBeta(Board board, String player, int depth) {
        alphaBetaPruning(board, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    
    public int minimax(Board board, String player, int depth) {
        iteration++;
        ArrayList<Cell> availableCells = board.getAvailableCells();
        
        
        /**
         * Check terminal state and evaluated score.
         * If X player win, return 1. If O player win,
         * return -1. If it's a draw, return 0.
         */
        if (GameManager.checkWinner("X")) {
            return 1;
        } else if (GameManager.checkWinner("O")) {
            return -1;
        } else if (availableCells.isEmpty()) {
            return 0;
        }
        
        
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        
        
        for (int i = 0; i < availableCells.size(); ++i) {
            iteration++;
            int position = availableCells.get(i).position;
            
            
            if (player.equalsIgnoreCase(Player.O)) {
                GameManager.board.selectCell(position, Player.O);
                System.out.println("Place O in position: " + position + " depth = " + depth);
                int currentScore = minimax(board, Player.X, depth+1);
                min = Math.min(currentScore, min);
                
                
                if (depth == 0) {
                    System.out.println(currentScore);
                }
                
                
                if (currentScore <= 0) {
                    if (depth == 0) {
                        bestPosition = position;
                    }
                }
                
                if (currentScore == -1) {
                    GameManager.board.undoMove(position);
                    break;
                }
                
                /**
                 * Check before the last move complete, if the
                 * last move guarantee O will win, then game ends.
                 * 
                 * if (i == availableCells.size() -1 && min > 0) {
                        if (depth == 0) {
                            bestPosition = position;
                        }
                    }
                 */
            } else {
                GameManager.board.selectCell(position, Player.X);
                int currentScore = minimax(board, Player.O, depth+1);
                max = Math.max(currentScore, max);
                
                if (max == 1) {
                    GameManager.board.undoMove(position);
                    break;
                }
                
            } // End if-else check player X or player O
            
            // Undo move
            GameManager.board.undoMove(position);
            
        } // End for
        
        
        return player.equalsIgnoreCase(Player.X) ? max : min;
    } // End minimax

    
    
    
public int alphaBetaPruning(Board board, String player, int depth, int alpha, int beta) {
        iteration++;
        ArrayList<Cell> availableCells = board.getAvailableCells();
        
        
        /**
         * Evaluate terminal state. 
         * When the last possible move is place,
         * check for the winner. If X win, returns 10,
         * if O win returns -10, otherwise returns 0.
         */
        if (GameManager.checkWinner(Player.X)) {
            return 10;
        } else if (GameManager.checkWinner(Player.O)) {
            return -10;
        } else if (availableCells.isEmpty()) {
            return 0;
        }

        
        /**
         * If the player is player X
         */
        if (player.equalsIgnoreCase(Player.X)) {
            
            for (int i = 0; i < availableCells.size(); i++) {
                iteration++;
                int position = availableCells.get(i).position;
                GameManager.board.selectCell(position, Player.X);
                int score = alphaBetaPruning(board, Player.O, depth+1, alpha, beta);
                GameManager.board.undoMove(position);
                
                if (score > alpha) {
                    alpha = score;
                    if (depth == 0)
                        bestPosition = position;
                }

                if (beta <= alpha) {
                    return alpha;
                }
            } // End for
            
            
            return alpha;
            
            
        } else {
            /**
             * If the player is O
             */
            for (int i = 0; i < availableCells.size(); i++) {
                iteration++;
                int position = availableCells.get(i).position;
                GameManager.board.selectCell(position, Player.O);
                int score = alphaBetaPruning(board, Player.X, depth+1, alpha, beta);
                GameManager.board.undoMove(position);
                
                if (score < beta) {
                    beta = score;
                    if (depth == 0)
                        bestPosition = position;
                } else if (beta <= alpha) {
                    return beta;
                }

            } // End for
            
            
            return beta;
            
            
        } // End if-else

        
    } // End alphaBetaPruning


    @Override
    public void run() {
        alphaBetaPruning(GameManager.board, Player.O, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public void start() {
        System.out.println("AI thread started");
    }
    
    
} // End AI
