package x.mvmn.tankfillgame;

import java.awt.Color;

import x.mvmn.tankfillgame.swing.LineComponent;
import x.mvmn.tankfillgame.swing.ImageOverlayJComponent;

public class Line {

	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	private final LineComponent lineGraphicComponent;
	private final ImageOverlayJComponent canvas;

	public Line(int x1, int x2, int y1, int y2, ImageOverlayJComponent canvas) {
		this.x1 = x1 > x2 ? x2 : x1;
		this.x2 = x1 > x2 ? x1 : x2;
		this.y1 = y1 > y2 ? y2 : y1;
		this.y2 = y1 > y2 ? y1 : y2;
		this.canvas = canvas;

		this.lineGraphicComponent = new LineComponent(x1, x2, y1, y2, Color.BLUE);
		add();
	}

	public void delete() {
		canvas.removeSubComponent(lineGraphicComponent);
	}

	public void add() {
		canvas.addSubComponent(lineGraphicComponent);
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
}
