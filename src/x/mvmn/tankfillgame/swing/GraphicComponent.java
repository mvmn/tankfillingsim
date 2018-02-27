package x.mvmn.tankfillgame.swing;

import java.awt.Graphics;

public interface GraphicComponent {

	public void paint(Graphics g);

	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();
}
