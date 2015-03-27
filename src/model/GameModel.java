package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import controller.ArrowKeys;
import view.GameBoard;


public class GameModel extends JPanel implements KeyListener, Runnable {

	public static final int WIDTH = 500,
							HEIGHT = 650;
	private Thread game;
	private boolean gameIsRunning;
	
	public static final Font standard = new Font("Times New Roman", Font.PLAIN, 25);
	private BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private GameBoard mainBoard;

	private long startTime;
	private long elapsed;
	private boolean set;

	public GameModel() {
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		
		mainBoard = new GameBoard(WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, HEIGHT - GameBoard.BOARD_HEIGHT - 100);
	}

	private void gameUpdates() {
		mainBoard.boardUpdate();
		ArrowKeys.update();
	}

	private void createBackground() {
		Graphics2D main = (Graphics2D) canvas.getGraphics();
		main.setColor(Color.white);
		main.fillRect(0, 0, WIDTH, HEIGHT);
		mainBoard.renderWindow(main);
		main.dispose();

		Graphics2D g = (Graphics2D) getGraphics();
		g.drawImage(canvas, 0, 0, null);
		g.dispose();
	}

	@Override
	public void run() {
		final int TargetFPS = 60;
		double nsPerUpdate = 1000000000.0 / TargetFPS;
		
		long milliSecs = System.currentTimeMillis();
		int framesPerSecond = 0, totalUpdates = 0;

		// last update time in nanoseconds
		double lastUpdateTime = System.nanoTime();
		double updatesNeeded = 0;

		while (gameIsRunning) {

			boolean doRender = false;
			double currentTime = System.nanoTime();
			updatesNeeded += (currentTime - lastUpdateTime) / nsPerUpdate;
			lastUpdateTime = currentTime ;

			// update queue
			while (updatesNeeded >= 1) {
				totalUpdates++;
				gameUpdates();
				updatesNeeded--;
				doRender = true;
			}

			// render
			if (doRender) {
				framesPerSecond++;
				createBackground();
				doRender = false;
			} else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// FPS Timer
		if (System.currentTimeMillis() - milliSecs > 1000) {
			System.out.printf("Total game updates: %d, Total frames per second: %d", totalUpdates, framesPerSecond);
			System.out.println();
			framesPerSecond = 0;
			totalUpdates = 0;
			milliSecs += 1000;
		}
	}

	public synchronized void startNewGame() {
		if (gameIsRunning)
			return;
		gameIsRunning = true;
		game = new Thread(this, "game");
		game.start();
	}

	public synchronized void stopGame() {
		if (!gameIsRunning)
			return;
		gameIsRunning = false;
		System.exit(0);
	}

	@Override
	public void keyPressed(KeyEvent e) { ArrowKeys.keyPressed(e);	}

	@Override
	public void keyReleased(KeyEvent e) { ArrowKeys.keyReleased(e); }

	@Override
	public void keyTyped(KeyEvent e) {}
}
