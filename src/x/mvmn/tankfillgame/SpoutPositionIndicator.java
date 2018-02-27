package x.mvmn.tankfillgame;

import java.awt.Color;
import java.awt.Graphics;

import x.mvmn.tankfillgame.swing.GraphicComponent;

public class SpoutPositionIndicator implements GraphicComponent {
	private final int spoutId;
	private final int y;
	private final int x;

	SpoutPositionIndicator(int spoutId, int y, int x) {
		this.spoutId = spoutId;
		this.y = y;
		this.x = x;
	}

	@Override
	public void paint(Graphics g) {
		g.setFont(TankFillGame.spoutFont);
		g.setColor(Color.WHITE);
		g.drawString("" + spoutId, x + 2, y);
		g.drawString("" + spoutId, x - 2, y);
		g.drawString("" + spoutId, x, y + 2);
		g.drawString("" + spoutId, x, y - 2);
		g.setColor(Color.RED);
		g.drawString("" + spoutId, x, y);
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getX() {
		return x;
	}
}