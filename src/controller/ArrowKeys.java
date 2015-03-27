package controller;

import java.awt.event.KeyEvent;

public class ArrowKeys {

	public static boolean[] KeyPressed = new boolean[256];
	public static boolean[] previousKey = new boolean[256];
	
	private ArrowKeys() { }
	
	public static int update(){
		int keyValue = 0;
		
		switch(keyValue) {
		case 0:
			previousKey[KeyEvent.VK_LEFT] = KeyPressed[KeyEvent.VK_LEFT];
		case 1:
			previousKey[KeyEvent.VK_RIGHT] = KeyPressed[KeyEvent.VK_RIGHT];
		case 2:
			previousKey[KeyEvent.VK_UP] = KeyPressed[KeyEvent.VK_UP];
		case 3: 
			previousKey[KeyEvent.VK_DOWN] = KeyPressed[KeyEvent.VK_DOWN];
			break;
		}
		return keyValue;
	}

	
	public static void keyPressed(KeyEvent e){ KeyPressed[e.getKeyCode()] = true; }
	
	public static void keyReleased(KeyEvent e){ KeyPressed[e.getKeyCode()] = false; }
	
	public static boolean typed(int keyEvent){ return !KeyPressed[keyEvent] && previousKey[keyEvent]; }
}
