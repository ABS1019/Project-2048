package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import model.GameModel;
import model.GameTile;
import controller.ArrowKeys;
import controller.Location;
import controller.Movements;

public class GameBoard {

	public static final int ROWS_COLS = 4;
	
	private final int newTiles = 2;
	private GameTile[][] board;
	
	private Font scoreFont;
	private boolean gameLost,
					 gameWon;
	
	private boolean gameHasBegun;
	
	private BufferedImage background,
						  completeGameBoard;
	private int x,
				y,
				gameScore = 0;
	
	private String stopwatch = "00:00:000";
	private long timeStarted,
				    timeGone;
	
	private static int SPACING = 5;
	public static int BOARD_WIDTH = (ROWS_COLS + 1) * SPACING + ROWS_COLS * GameTile.W_H,
					  BOARD_HEIGHT = (ROWS_COLS + 1) * SPACING + ROWS_COLS * GameTile.W_H;

	public GameBoard(int x, int y){
		scoreFont = GameModel.standard.deriveFont(30f);
		
		this.x = x;
		this.y = y;
		board = new GameTile[ROWS_COLS][ROWS_COLS];
		
		background = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		completeGameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		timeStarted = System.nanoTime();
		
		createBoardImage();
		start();
	}
	
	private void createBoardImage(){
		Graphics2D g = (Graphics2D) background.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.LIGHT_GRAY);
		
		for(int row = 0; row < ROWS_COLS; row++){
			for(int column = 0; column < ROWS_COLS; column++){
				int x = SPACING + SPACING * column + GameTile.W_H * column;
				int y = SPACING + SPACING * row + GameTile.W_H * row;
				g.fillRoundRect(x, y, GameTile.W_H, GameTile.W_H, GameTile.ARC, GameTile.ARC);
			}
		}
	}
	
	private void start(){
		for(int i = 0; i < newTiles; i++){
			spawnRandom();
		}
	}
	
	private void spawnRandom(){
		Random random = new Random();
		boolean notValid = true;
		
		while(notValid){
			int location = random.nextInt(ROWS_COLS * ROWS_COLS);
			int row = location / ROWS_COLS;
			int column = location % ROWS_COLS;
			GameTile thisBoard = board[row][column];
			if(thisBoard == null){
				int value = random.nextInt(10) < 9 ? 2 : 4;
				GameTile tiles = new GameTile(value, getLocationX(column), getLocationY(row));
				board[row][column] = tiles;
				notValid = false;
			}
		}
	}
	
	public int getLocationX(int column){
		return SPACING + column * GameTile.W_H + column * SPACING;
	}
	
	public int getLocationY(int row){
		return SPACING + row * GameTile.W_H + row * SPACING;
	}
	
	/** Render Game Board
	 * This method draws the background for the game board onto the completeGameBoard image.
	 * From here it creates the q6 tiles needed to make up the playing tiles. These tiles are
	 * then drawn onto the same image, creating the complete game board.
	 * 
	 * @param g
	 */
	public void renderWindow(Graphics2D main){
		Graphics2D g = (Graphics2D)completeGameBoard.getGraphics();
		g.drawImage(background, 0, 0, null);
		
		for(int row = 0; row < ROWS_COLS; row++){
			for(int col = 0; col < ROWS_COLS; col++){
				GameTile thisBoard = board[row][col];
				if(thisBoard == null) continue;
				thisBoard.render(g);
			}
		}
		main.drawImage(completeGameBoard, x, y, null);
		g.dispose();
		
		main.setColor(Color.BLACK);
		main.setFont(scoreFont);
		main.drawString("" + gameScore, 40, 50);
		main.setColor(Color.GRAY);
		main.drawString("Game Time: " + stopwatch, 105, 100);
		}
	
	public void boardUpdate(){
		KeyPressUpdate();
		
		if(!gameWon && !gameLost) {
			if(gameHasBegun) {
				final int million = 1000000;
				long time = System.nanoTime() - timeStarted;
				
				timeGone = time / million;
				stopwatch = formatTime(timeGone);
			} else {
				timeStarted = System.nanoTime();
			}
		}
		
		for(int row = 0; row < ROWS_COLS; row++){
			for(int column = 0; column < ROWS_COLS; column++){
				GameTile thisBoard = board[row][column];
				if(thisBoard == null) continue;
				thisBoard.update();
				resetTileLocations(thisBoard, row, column);
				if(thisBoard.getValue() == 2048){
					gameWon = true;
				}
			}
		}
	}
	
	private void resetTileLocations(GameTile thisBoard, int row, int column){
		if(thisBoard == null) return;
		
		int x = getLocationX(column);
		int y = getLocationY(row);
		
		int spanX = thisBoard.getX() - x;
		int spanY = thisBoard.getY() - y;
		
		if(Math.abs(spanX) < GameTile.TILE_MOVEMENT){
			thisBoard.setX(thisBoard.getX() - spanX);
		}
		
		if(Math.abs(spanY) < GameTile.TILE_MOVEMENT){
			thisBoard.setY(thisBoard.getY() - spanY);
		}
		
		if(spanX < 0){
			thisBoard.setX(thisBoard.getX() + GameTile.TILE_MOVEMENT);
		}
		if(spanY < 0){
			thisBoard.setY(thisBoard.getY() + GameTile.TILE_MOVEMENT);
		}
		if(spanX > 0){
			thisBoard.setX(thisBoard.getX() - GameTile.TILE_MOVEMENT);
		}
		if(spanY > 0){
			thisBoard.setY(thisBoard.getY() - GameTile.TILE_MOVEMENT);
		}
	}
	
	private boolean move(int row, int column, int moveHorizontal, int moveVertical, Movements movement){
		boolean movePossible = false;
		
		GameTile thisBoard = board[row][column];
		if(thisBoard == null) return false;
		boolean move = true;
		int newColumn = column;
		int newRow = row;
		while(move){
			newColumn += moveHorizontal;
			newRow += moveVertical;
			if(restrictions(movement, newRow, newColumn)) break;
			if(board[newRow][newColumn] == null){
				board[newRow][newColumn] = thisBoard;
				board[newRow - moveVertical][newColumn - moveHorizontal] = null;
				board[newRow][newColumn].setSlideTo(new Location(newRow, newColumn));
				
				movePossible = true; 
			}
			else if(board[newRow][newColumn].getValue() == thisBoard.getValue() && board[newRow][newColumn].canCombine()){
				board[newRow][newColumn].setCanCombine(false);
				board[newRow][newColumn].setValue(board[newRow][newColumn].getValue() * 2);
				
				movePossible = true;
				
				board[newRow - moveVertical][newColumn - moveHorizontal] = null;
				board[newRow][newColumn].setSlideTo(new Location(newRow, newColumn));
			}
			else{
				move = false;
			}
		}
		return movePossible;
	}
	
	private boolean restrictions(Movements movement, int row, int column) {
		if(movement == Movements.LEFT){
			return column < 0;
		}
		else if(movement == Movements.RIGHT){
			return column > ROWS_COLS - 1;
		}
		else if(movement == Movements.UP){
			return row < 0;
		}
		else if(movement == Movements.DOWN){
			return row > ROWS_COLS - 1;
		}
		return false;
	}

	private void tileMovement(Movements move){
		boolean movePossible = false;
		int horizontalDirection = 0;
		int verticalDirection = 0;
		
		if(move == Movements.LEFT){
			horizontalDirection = -1;
			for(int row = 0; row < ROWS_COLS; row++){
				for(int column = 0; column < ROWS_COLS; column++){
					if(!movePossible ){
						movePossible  = move(row, column, horizontalDirection, verticalDirection, move);
					}
					else move(row, column, horizontalDirection, verticalDirection, move);
				}
			}
		}
		
		else if(move == Movements.RIGHT){
			horizontalDirection = 1;
			for(int row = 0; row < ROWS_COLS; row++){
				for(int column = ROWS_COLS - 1; column >= 0; column--){
					if(!movePossible){
						movePossible = move(row, column, horizontalDirection, verticalDirection, move);
					}
					else move(row, column, horizontalDirection, verticalDirection, move);
				}
			}
		}
		
		else if(move == Movements.UP){
			verticalDirection = -1;
			for(int row = 0; row < ROWS_COLS; row++){
				for(int column = 0; column < ROWS_COLS; column++){
					if(!movePossible){
						movePossible = move(row, column, horizontalDirection, verticalDirection, move);
					}
					else move(row, column, horizontalDirection, verticalDirection, move);
				}
			}
		}
		
		else if(move == Movements.DOWN){
			verticalDirection = 1;
			for(int row = ROWS_COLS - 1; row >= 0; row--){
				for(int column = 0; column < ROWS_COLS; column++){
					if(!movePossible){
						movePossible = move(row, column, horizontalDirection, verticalDirection, move);
					}
					else move(row, column, horizontalDirection, verticalDirection, move);
				}
			}
		}
		else{
			System.out.println(move + " is not a valid direction.");
		}
		
		for(int row = 0; row < ROWS_COLS; row++){
			for(int column = 0; column < ROWS_COLS; column++){
				GameTile thisBoard = board[row][column];
				if(thisBoard == null) continue;
				thisBoard.setCanCombine(true);
			}
		}
		
		if(movePossible){
			spawnRandom();
			checkDead();
		}
	}
	
	private void checkDead() {
		for(int row = 0; row < ROWS_COLS; row++){
			for(int col = 0; col < ROWS_COLS; col++){
				if(board[row][col] == null)
					return;
				
				if(checkSurroundingTiles(row, col, board[row][col]))
					return;
			}
		} gameLost = true;
	}
	
	private boolean checkSurroundingTiles(int row, int column, GameTile thisBoard) {
		if(row > 0){
			GameTile check = board[row - 1][column];
			if(check == null) return true;
			if(thisBoard.getValue() == check.getValue()) return true;
		}
		if(row < ROWS_COLS - 1){
			GameTile check = board[row + 1][column];
			if(check == null) return true;
			if(thisBoard.getValue() == check.getValue()) return true;
		}
		if(column > 0){
			GameTile check = board[row][column - 1];
			if(check == null) return true;
			if(thisBoard.getValue() == check.getValue()) return true;
		}
		if(column < ROWS_COLS - 1){
			GameTile check = board[row][column + 1];
			if(check == null) return true;
			if(thisBoard.getValue() == check.getValue()) return true;
		}
		return false;
	}
	
	private String formatTime(long milliSecs) {
	String formattedTime;
	final int msPerSecond = 1000,
			  msPerMinute = 60000,
			    msPerHour = 3600000;
	String hour = "";
	
	int hours = (int)(milliSecs / msPerHour);
	if(hours >= 1){
		milliSecs -= hours * msPerHour;
		if(hours < 10){
			hour = "0" + hours;
		}
		else{
			hour = "" + hours;
		}
		hour += ":";
	}
	
	String minute;
	int minutes = (int)(milliSecs / msPerMinute);
	if(minutes >= 1){
		milliSecs -= minutes * msPerMinute;
		if(minutes < 10){
			minute= "0" + minutes;
		}
		else{
			minute= "" + minutes;
		}
	}
	else{
		minute= "00";
	}
	
	String second;
	int seconds = (int)(milliSecs / msPerSecond);
	if(seconds >= 1){
		milliSecs -= seconds * msPerSecond;
		if(seconds < 10){
			second= "0" + seconds;
		}
		else{
			second= "" + seconds;
		}
	}
	else{
		second= "00";
	}
	
	String ms;
	if(milliSecs > 99){
		ms = "" + milliSecs;
	}
	else if(milliSecs > 9){
		ms = "0" + milliSecs;
	}
	else{
		ms = "00" + milliSecs;
	}
	
	formattedTime = hour + minute + ":" + second + ":" + ms;
	return formattedTime;
}
	
	private void KeyPressUpdate() {
		if(ArrowKeys.typed(KeyEvent.VK_LEFT)){
			tileMovement(Movements.LEFT);
			
			if(!gameHasBegun) gameHasBegun = true;
		}
		if(ArrowKeys.typed(KeyEvent.VK_RIGHT)){
			tileMovement(Movements.RIGHT);
			
			if(!gameHasBegun) gameHasBegun = true;
		}
		if(ArrowKeys.typed(KeyEvent.VK_UP)){
			tileMovement(Movements.UP);
			
			if(!gameHasBegun) gameHasBegun = true;
		}
		if(ArrowKeys.typed(KeyEvent.VK_DOWN)){
			tileMovement(Movements.DOWN);
			
			if(!gameHasBegun) gameHasBegun = true;
		}
	}

	public boolean gameIsLost() {

		return false;
	}

	public Object getGameScore() {

		return null;
	}

	public Object getEmptyTiles() {

		return null;
	}

	public Object getGameBoardArray() {

		return null;
	}
}