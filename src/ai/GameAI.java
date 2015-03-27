package ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import view.GameBoard;
import controller.Movements;

public class GameAI {
	
	public enum Participant {
		PLAYER_ONE,
		PLAYER_TWO;
	}
	
    /**
     * Method that finds the best next move.
     * 
     * @param theBoard
     * @param depth
     * @return
     * @throws CloneNotSupportedException 
     */
    public static Movements findBestMove(GameBoard gameBoard, int searchDepth) throws CloneNotSupportedException {
        Map<String, Object> minimax = minimaxAlgorithm(gameBoard, searchDepth, Participant.PLAYER_ONE);
        
//        Map<String, Object> result = alphabetaAlgorithm(gameBoard, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, Participant.PLAYER_ONE);
        
        return (Movements)minimax.get("Direction");
    }

	private static Map<String, Object> minimaxAlgorithm(GameBoard gameBoard,
			int searchDepth, Participant player) {
		
		Map<String, Object> decision = new HashMap<>();
        
        Movements bestMove = null;
        int highestScore;
        
        if(searchDepth == 0 || gameBoard.gameIsLost()) {
            highestScore=heuristicScore(gameBoard.getGameScore(), gameBoard.getEmptyTiles(), calculateCluster(gameBoard.getClonedGameBoard()));
        }
        else {
            if(player == Participant.PLAYER_ONE) {
                highestScore = Integer.MIN_VALUE;

                for(Movements moveMade : Movements.values()) {
                    GameBoard clonedBoard = (GameBoard) gameBoard.clone();

                    int points = clonedBoard.move(moveMade);
                    
                    if(points == 0 && gameBoard.isEqual(gameBoard.getGameBoardArray(), clonedBoard.getGameBoardArray()))
                    	continue;

                    Map<String, Object> thisResult = minimaxAlgorithm(gameBoard, searchDepth - 1, Participant.PLAYER_TWO);
                    int thisScore=((Number)thisResult.get("Score")).intValue();
                    if(thisScore > highestScore) {
                        highestScore = thisScore;
                        bestMove = moveMade;
                    }
                }
            }
            else {
                highestScore = Integer.MAX_VALUE;

                List<Integer> moves = GameBoard.getEmptyTiles();
                if(moves.isEmpty()) {
                    highestScore = 0;
                }
                int[] availableTiles = {2, 4};

                int row,
                	column;
                int[][] boardArray;
                for(Integer location : moves) {
                    row = location / GameBoard.BOARD_WIDTH;
                    column = location % GameBoard.BOARD_HEIGHT;

                    for(int value : availableTiles) {
                        GameBoard newBoard = (GameBoard) gameBoard.clone();
                        newBoard.setEmptyTiles(row, column, value);

                        Map<String, Object> currentResult = minimaxAlgorithm(newBoard, searchDepth - 1, Participant.PLAYER_ONE);
                        int thisScore = ((Number)currentResult.get("Score")).intValue();
                        if(thisScore < highestScore)
                        	highestScore = thisScore;
                    }
                }
            }
        }
        
        decision.put("Score", highestScore);
        decision.put("Direction", bestMove);
        
        return decision;
    }
	
    /**
     * Calculates a heuristic variance-like score that measures how clustered the
     * board is.
     * 
     * @param boardArray
     * @return 
     */
    private static int calculateCluster(int[][] boardArray) {
        int cluster = 0;
        
        int[] surroundingTile = {-1,0,1};
        
        for(int row = 0; row < boardArray.length; ++row) {
            for(int column = 0; column < boardArray.length; ++column) {
                if(boardArray[row][column] == 0) {
                    continue;
                }
                
                //for every pixel find the distance from each neightbors
                int numberOfotherTiles = 0;
                int total = 0;
                for(int eachTile : surroundingTile) {
                    int x = row + eachTile;
                    if(x<0 || x>=boardArray.length) { continue; }
                    
                    for(int eachTileY : surroundingTile) {
                        int y = column + eachTileY;
                        
                        if(y < 0 || y >= boardArray.length) { continue;
                        }
                        if(boardArray[x][y] > 0) {
                            ++numberOfotherTiles;
                            total += Math.abs(boardArray[row][column] - boardArray[x][y]);
                        }  
                    }
                }
                cluster += total / numberOfotherTiles;
            }
        }   
        return cluster;
    }
}