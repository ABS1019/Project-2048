package model;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class TextPosition {

	private TextPosition() {
	}

	public static int textWidth(String number, Font text, Graphics2D g) {
		g.setFont(text);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(number, g);
		return (int) bounds.getWidth();
	}

	public static int textHeight(String number, Font text, Graphics2D g) {
		g.setFont(text);
		if (number.length() == 0)
			return 0;
		TextLayout layout = new TextLayout(number, text, g.getFontRenderContext());
		return (int) layout.getBounds().getHeight();
	}
}
