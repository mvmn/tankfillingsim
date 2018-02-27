package x.mvmn.tankfillgame.swing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ImageOverlayJComponent extends JBufferedImageComponent {
	private static final long serialVersionUID = 6483856720054697176L;

	protected List<GraphicComponent> subComponents = new ArrayList<>();
	protected ReadWriteLock rwLock = new ReentrantReadWriteLock();

	public ImageOverlayJComponent(BufferedImage image) {
		super(image);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Lock readLock = rwLock.readLock();
		try {
			readLock.lock();

			for (GraphicComponent subComponent : subComponents) {
				subComponent.paint(g);
			}
		} finally {
			readLock.unlock();
		}
	}

	public void addSubComponent(GraphicComponent subComponent) {
		Lock writeLock = rwLock.writeLock();
		try {
			writeLock.lock();
			subComponents.add(subComponent);
		} finally {
			writeLock.unlock();
		}
		this.repaint();
	}

	public void removeSubComponent(GraphicComponent subComponent) {
		Lock writeLock = rwLock.writeLock();
		try {
			writeLock.lock();
			subComponents.remove(subComponent);
		} finally {
			writeLock.unlock();
		}
		this.repaint();
	}

	public List<GraphicComponent> getSubComponentsCopy() {
		Lock readLock = rwLock.readLock();
		try {
			readLock.lock();
			return new ArrayList<>(subComponents);
		} finally {
			readLock.unlock();
		}
	}
}
