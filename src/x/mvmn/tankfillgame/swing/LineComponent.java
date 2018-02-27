package x.mvmn.tankfillgame.swing;

import java.awt.Color;
import java.awt.Graphics;

public class LineComponent implements GraphicComponent {

	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	private final Color color;

	public LineComponent(int x1, int x2, int y1, int y2, Color color) {
		super();
		this.x1 = x1 > x2 ? x2 : x1;
		this.x2 = x1 > x2 ? x1 : x2;
		this.y1 = y1 > y2 ? y2 : y1;
		this.y2 = y1 > y2 ? y1 : y2;
		this.color = color;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}

	public int getX1() {
		return x1;
	}

	public int getX2() {
		return x2;
	}

	public int getY1() {
		return y1;
	}

	public int getY2() {
		return y2;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public int getX() {
		return x1;
	}

	@Override
	public int getY() {
		return y1;
	}

	public int getWidth() {
		return x2 - x1;
	}

	public int getHeight() {
		return y2 - y1;
	}
}
