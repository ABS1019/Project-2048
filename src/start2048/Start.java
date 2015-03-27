package start2048;

import javax.swing.JFrame;

import model.GameModel;

public class Start {

	public static void main(String[] args){
		GameModel game2048 = new GameModel();
		
		JFrame window = new JFrame("Aron Buckley-Smith - AI Solution for 2048!");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setResizable(false);
		window.add(game2048);
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		game2048.startNewGame();
	}
	
}
