package x.mvmn.tankfillgame;

import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JSlider;

import x.mvmn.tankfillgame.swing.ImageOverlayJComponent;

public class SpoutHandlingThread implements Runnable {

	private final ImageOverlayJComponent canvas;
	private final WritableRaster raster;
	private final JSlider jSliderSpeed;
	private final List<Spout> spouts = new ArrayList<>();

	protected final AtomicBoolean paused = new AtomicBoolean(true);
	protected static final AtomicInteger spoutCounter = new AtomicInteger(0);

	public SpoutHandlingThread(ImageOverlayJComponent canvas, JSlider jSliderSpeed) {
		this.canvas = canvas;
		this.jSliderSpeed = jSliderSpeed;
		raster = canvas.getImage().getRaster();
	}

	public void addSpout(int x, int y) {
		synchronized (spouts) {
			Spout spout = new Spout();
			spout.id = spoutCounter.incrementAndGet();
			spout.indicator = new SpoutPositionIndicator(spout.id, y, x);
			canvas.addSubComponent(spout.indicator);

			spout.backgroundColor = raster.getPixel(x, y, new int[3]);
			while (isBackground(x, y, new int[3], spout.backgroundColor)) {
				y += 1;
			}
			y--;
			{
				Line line = new Line(x, x, y, y, canvas);
				spout.linesToProcessDownwards.add(line);
			}
			spouts.add(spout);
		}
	}

	@Override
	public void run() {
		int[] pixelBuff = new int[3];
		try {
			while (true) {
				if (paused.get()) {
					Thread.sleep(1000);
				} else {
					synchronized (spouts) {
						for (Spout spout : spouts) {
							// while (linesToProcessDownwards.size() > 0 || linesToProcessUpwards.size() > 0) {
							if (spout.linesToProcessDownwards.size() > 0 || spout.linesToProcessUpwards.size() > 0) {
								// System.out.println("D" + linesToProcessDownwards.size() + ", U" + linesToProcessUpwards.size());
								if (spout.linesToProcessDownwards.size() > 0) {
									Line currentLine = spout.linesToProcessDownwards.pollFirst();
									int clx1 = currentLine.getX1();
									int y = currentLine.getY1();
									// While not wall to the left and wall below
									while (isBackground(clx1 - 1, currentLine.getY1(), pixelBuff, spout.backgroundColor)
											&& !isBackground(clx1, y + 1, pixelBuff, spout.backgroundColor)) {
										clx1--;
									}
									if (isBackground(clx1, y + 1, pixelBuff, spout.backgroundColor)) {
										// Hole below - go down and spawn new 1 pixel line there
										int cy = y + 1;
										while (isBackground(clx1, cy, pixelBuff, spout.backgroundColor)) {
											cy++;
										}
										cy--;
										spout.linesToProcessDownwards.add(new Line(clx1, clx1, cy, cy, canvas));
									} else {
										// We can't move more left - can we move more right?
										int clx2 = currentLine.getX2();
										// While not wall to the right and wall below
										while (isBackground(clx2 + 1, currentLine.getY1(), pixelBuff, spout.backgroundColor)
												&& !isBackground(clx2, y + 1, pixelBuff, spout.backgroundColor)) {
											clx2++;
										}

										if (isBackground(clx2, y + 1, pixelBuff, spout.backgroundColor)) {
											// Hole below - go down and spawn new 1 pixel line there
											int cy = y + 1;
											while (isBackground(clx2, cy, pixelBuff, spout.backgroundColor)) {
												cy++;
											}
											cy--;
											spout.linesToProcessDownwards.add(new Line(clx2, clx2, cy, cy, canvas));
										} else {
											// Filled left and right - update current line, if already up to date - go up
											if (clx1 < currentLine.getX1() || clx2 > currentLine.getX2()) {
												spout.linesToProcessDownwards.add(new Line(clx1, clx2, currentLine.getY1(), currentLine.getY1(), canvas));
											} else {
												currentLine.delete();
												int[] blue = new int[] { 0, 0, 255 };
												for (int i = currentLine.getX1(); i <= currentLine.getX2(); i++) {
													raster.setPixel(i, currentLine.getY1(), blue);
												}
												spout.linesToProcessDownwards.addAll(findLinesAbove(currentLine, spout.backgroundColor));
											}
										}
									}
								} else {
									spout.linesToProcessDownwards.addAll(findLinesAbove(spout.linesToProcessUpwards.pollFirst(), spout.backgroundColor));
								}
							} else {
								System.out.println("Spouts finished");
							}
						}
						Thread.sleep(jSliderSpeed.getValue());
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private List<Line> findLinesAbove(Line line, int[] backgroundColor) {
		List<Line> results = new LinkedList<>();

		int y = line.getY1() - 1;
		int scanXPoint = line.getX1();

		int pixelBuff[] = new int[3];
		while (scanXPoint <= line.getX2()) {
			if (isBackground(scanXPoint, y, pixelBuff, backgroundColor)) {
				int start = scanXPoint;
				int end = scanXPoint;
				while (isBackground(start, y, pixelBuff, backgroundColor) && !isBackground(start, y + 1, pixelBuff, backgroundColor)) {
					start--;
				}
				start++;
				while (isBackground(end, y, pixelBuff, backgroundColor) && !isBackground(end, y + 1, pixelBuff, backgroundColor)) {
					end++;
				}
				scanXPoint = end;
				end--;

				results.add(new Line(start, end, y, y, canvas));
			} else {
				scanXPoint++;
			}
		}

		return results;
	}

	protected boolean isBackground(int x, int y, int[] pixelBuff, int[] backgroundColor) {
		if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
			return false;
		}
		raster.getPixel(x, y, pixelBuff);
		return Math.abs(pixelBuff[0] - backgroundColor[0]) + Math.abs(pixelBuff[1] - backgroundColor[1]) + Math.abs(pixelBuff[2] - backgroundColor[2]) < 10;
	}

	public void setPaused(boolean pausedVal) {
		paused.set(pausedVal);
	}
}
