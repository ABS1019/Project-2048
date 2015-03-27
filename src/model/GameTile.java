package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import controller.Location;

public class GameTile {

	public static final int W_H = 100;
	public static final int TILE_MOVEMENT = 30;
	public static final int ARC = 15;

	private int x,
				y,
				value;
	private BufferedImage tileImage;
	private Color background;
	private Color text;
	private Font font;
	private Location tileMove;

	private boolean canCombine = true;

	public GameTile(int value, int x, int y) {
		this.value = value;
		this.x = x;
		this.y = y;
		tileMove = new Location(x, y);
		tileImage = new BufferedImage(W_H, W_H, BufferedImage.TYPE_INT_ARGB);
		drawImage();
	}

	private void drawImage() {
		Graphics2D g = (Graphics2D) tileImage.getGraphics();
		
		
		
		if (value == 2) {
			background = new Color(0xe9e9e9);
			text = new Color(0x000000);
		} else if (value == 4) {
			background = new Color(0xe6daab);
			text = new Color(0x000000);
		} else if (value == 8) {
			background = new Color(0xf79d3d);
			text = new Color(0xffffff);
		} else if (value == 16) {
			background = new Color(0xf28007);
			text = new Color(0xffffff);
		} else if (value == 32) {
			background = new Color(0xf55e3b);
			text = new Color(0xffffff);
		} else if (value == 64) {
			background = new Color(0xff0000);
			text = new Color(0xffffff);
		} else if (value == 128) {
			background = new Color(0xe9de84);
			text = new Color(0xffffff);
		} else if (value == 256) {
			background = new Color(0xf6e873);
			text = new Color(0xffffff);
		} else if (value == 512) {
			background = new Color(0xf5e455);
			text = new Color(0xffffff);
		} else if (value == 1024) {
			background = new Color(0xf7e12c);
			text = new Color(0xffffff);
		} else if (value == 2048) {
			background = new Color(0xffe400);
			text = new Color(0xffffff);
		} else {
			background = Color.BLACK;
			text = Color.WHITE;
		}

		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, W_H, W_H);

		g.setColor(background);
		g.fillRoundRect(0, 0, W_H, W_H, ARC, ARC);

		g.setColor(text);

		if (value <= 64) {
			font = GameModel.standard.deriveFont(36f);
		} else {
			font = GameModel.standard;
		}
		g.setFont(font);

		int pointX = W_H / 2 - TextPosition.textWidth("" + value, font, g) / 2;
		int pointY = W_H / 2 + TextPosition.textHeight("" + value, font, g) / 2;
		g.drawString("" + value, pointX, pointY);
		g.dispose();
	}

	public void update() {

	}

	public void render(Graphics2D g) {
		g.drawImage(tileImage, x, y, null);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		drawImage();
	}

	public boolean canCombine() {
		return canCombine;
	}

	public void setCanCombine(boolean canCombine) {
		this.canCombine = canCombine;
	}

	public Location getSlideTo() {
		return tileMove;
	}

	public void setSlideTo(Location tileMove) {
		this.tileMove = tileMove;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
