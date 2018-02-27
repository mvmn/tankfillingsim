package x.mvmn.tankfillgame;

import java.awt.image.WritableRaster;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JSlider;

import x.mvmn.tankfillgame.swing.ImageOverlayJComponent;

public class Spout implements Runnable {

	private final int startX;
	private final int startY;
	private final ImageOverlayJComponent canvas;
	private final WritableRaster raster;
	int[] backgroundColor = new int[3];
	final Comparator<Line> hLineComparatorHeightPriority = (l1, l2) -> (l2.getY1() - l1.getY1()) * 10000 + Math.abs(l2.getX1() - l1.getX1())
			+ Math.abs(l2.getX2() - l1.getX2());
	private final JSlider jSliderSpeed;

	@Override
	public void run() {
		final TreeSet<Line> linesToProcessUpwards = new TreeSet<>(hLineComparatorHeightPriority);
		final TreeSet<Line> linesToProcessDownwards = new TreeSet<>(hLineComparatorHeightPriority);

		int[] pixelBuff = new int[3];
		try {
			{
				int x = startX;
				int y = startY;
				while (isBackground(x, y, pixelBuff)) {
					y += 1;
				}
				y--;
				// new Line(startX, startX, startY, y, canvas);
				{
					Line line = new Line(startX, startX, y, y, canvas);
					linesToProcessDownwards.add(line);
				}
			}
			while (linesToProcessDownwards.size() > 0 || linesToProcessUpwards.size() > 0) {
				Thread.sleep(jSliderSpeed.getValue());
				// System.out.println("D" + linesToProcessDownwards.size() + ", U" + linesToProcessUpwards.size());
				if (linesToProcessDownwards.size() > 0) {
					Line currentLine = linesToProcessDownwards.pollFirst();
					int clx1 = currentLine.getX1();
					int y = currentLine.getY1();
					// While not wall to the left and wall below
					while (isBackground(clx1 - 1, currentLine.getY1(), pixelBuff) && !isBackground(clx1, y + 1, pixelBuff)) {
						clx1--;
					}
					if (isBackground(clx1, y + 1, pixelBuff)) {
						// Hole below - go down and spawn new 1 pixel line there
						int cy = y + 1;
						while (isBackground(clx1, cy, pixelBuff)) {
							cy++;
						}
						cy--;
						linesToProcessDownwards.add(new Line(clx1, clx1, cy, cy, canvas));
					} else {
						// We can't move more left - can we move more right?
						int clx2 = currentLine.getX2();
						// While not wall to the right and wall below
						while (isBackground(clx2 + 1, currentLine.getY1(), pixelBuff) && !isBackground(clx2, y + 1, pixelBuff)) {
							clx2++;
						}

						if (isBackground(clx2, y + 1, pixelBuff)) {
							// Hole below - go down and spawn new 1 pixel line there
							int cy = y + 1;
							while (isBackground(clx2, cy, pixelBuff)) {
								cy++;
							}
							cy--;
							linesToProcessDownwards.add(new Line(clx2, clx2, cy, cy, canvas));
						} else {
							// Filled left and right - update current line, if already up to date - go up
							if (clx1 < currentLine.getX1() || clx2 > currentLine.getX2()) {
								linesToProcessDownwards.add(new Line(clx1, clx2, currentLine.getY1(), currentLine.getY1(), canvas));
							} else {
								currentLine.delete();
								int[] blue = new int[] { 0, 0, 255 };
								for (int i = currentLine.getX1(); i <= currentLine.getX2(); i++) {
									raster.setPixel(i, currentLine.getY1(), blue);
								}
								linesToProcessDownwards.addAll(findLinesAbove(currentLine));
							}
						}
					}
				} else {
					linesToProcessDownwards.addAll(findLinesAbove(linesToProcessUpwards.pollFirst()));
				}
			}
			System.out.println("Spout finished");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private List<Line> findLinesAbove(Line line) {
		List<Line> results = new LinkedList<>();

		int y = line.getY1() - 1;
		int scanXPoint = line.getX1();

		int pixelBuff[] = new int[3];
		while (scanXPoint <= line.getX2()) {
			if (isBackground(scanXPoint, y, pixelBuff)) {
				int start = scanXPoint;
				int end = scanXPoint;
				while (isBackground(start, y, pixelBuff) && !isBackground(start, y + 1, pixelBuff)) {
					start--;
				}
				start++;
				while (isBackground(end, y, pixelBuff) && !isBackground(end, y + 1, pixelBuff)) {
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

	public Spout(int startX, int startY, ImageOverlayJComponent canvas, JSlider jSliderSpeed) {
		this.startX = startX;
		this.startY = startY;
		this.canvas = canvas;
		this.jSliderSpeed = jSliderSpeed;

		raster = canvas.getImage().getRaster();
		raster.getPixel(startX, startY, backgroundColor);

	}

	protected boolean isBackground(int x, int y, int[] pixelBuff) {
		if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
			return false;
		}
		raster.getPixel(x, y, pixelBuff);
		return Math.abs(pixelBuff[0] - backgroundColor[0]) + Math.abs(pixelBuff[1] - backgroundColor[1]) + Math.abs(pixelBuff[2] - backgroundColor[2]) < 10;
	}
}
