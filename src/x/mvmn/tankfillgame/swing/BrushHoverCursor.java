package x.mvmn.tankfillgame.swing;

import java.awt.Color;
import java.awt.Graphics;

public class BrushHoverCursor implements GraphicComponent {

	private volatile int x;
	private volatile int y;
	private volatile int radius;

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

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawOval(x, y, radius - 1, radius - 1);
		g.drawOval(x, y, radius + 1, radius + 1);
		g.setColor(Color.BLACK);
		g.drawOval(x, y, radius, radius);
	}
}
