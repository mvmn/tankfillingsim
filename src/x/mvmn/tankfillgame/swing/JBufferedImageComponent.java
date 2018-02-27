package x.mvmn.tankfillgame.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class JBufferedImageComponent extends JComponent {
	private static final long serialVersionUID = 8027247198208733399L;
	private final BufferedImage image;
	private final Dimension imageSize;

	public JBufferedImageComponent(BufferedImage image) {
		this.image = image;
		imageSize = new Dimension(image.getWidth(), image.getHeight());
	}

	public BufferedImage getImage() {
		return image;
	}

	public Dimension getImageSize() {
		return imageSize;
	}

	@Override
	public Dimension getPreferredSize() {
		return getImageSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getImageSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getImageSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	@Override
	public Dimension getSize() {
		return getImageSize();
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}
}
