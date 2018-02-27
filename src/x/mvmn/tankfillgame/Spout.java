package x.mvmn.tankfillgame;

import java.util.Comparator;
import java.util.TreeSet;

public class Spout {
	public static final Comparator<Line> H_LINE_COMPARATOR_HEIGHT_PRIORITY = (l1, l2) -> (l2.getY1() - l1.getY1()) * 10000 + Math.abs(l2.getX1() - l1.getX1())
			+ Math.abs(l2.getX2() - l1.getX2());

	public int[] backgroundColor = new int[3];
	public final TreeSet<Line> linesToProcessUpwards = new TreeSet<>(H_LINE_COMPARATOR_HEIGHT_PRIORITY);
	public final TreeSet<Line> linesToProcessDownwards = new TreeSet<>(H_LINE_COMPARATOR_HEIGHT_PRIORITY);
	public int id;
	public SpoutPositionIndicator indicator;
}
