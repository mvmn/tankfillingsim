package x.mvmn.tankfillgame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import x.mvmn.tankfillgame.swing.BrushHoverCursor;
import x.mvmn.tankfillgame.swing.ErrorHelper;
import x.mvmn.tankfillgame.swing.ImageOverlayJComponent;

public class TankFillGame {

	public static void main(String args[]) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		SwingUtilities.invokeLater(() -> {
			if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(null)) {
				new Thread(() -> {
					try {
						BufferedImage sourceImg = ImageIO.read(jfc.getSelectedFile());
						if (sourceImg != null) {
							BufferedImage img = new BufferedImage(sourceImg.getWidth(), sourceImg.getHeight(), BufferedImage.TYPE_INT_RGB);
							ColorConvertOp op = new ColorConvertOp(sourceImg.getColorModel().getColorSpace(), img.getColorModel().getColorSpace(),
									new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED));
							op.filter(sourceImg, img);

							BufferedImage sourceImgRgb = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
							img.copyData(sourceImgRgb.getRaster());

							JFrame mainWindow = new JFrame("Tank filling game");
							mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

							mainWindow.getContentPane().setLayout(new BorderLayout());
							ImageOverlayJComponent canvas = new ImageOverlayJComponent(img);
							canvas.setBorder(BorderFactory.createEtchedBorder());
							JScrollPane scrollPanel = new JScrollPane(canvas);
							mainWindow.getContentPane().add(scrollPanel, BorderLayout.CENTER);

							JRadioButton rbSpouts = new JRadioButton("Spout", true);
							JRadioButton rbPaintBlack = new JRadioButton("Paint black", false);
							JRadioButton rbPaintWhite = new JRadioButton("Paint white", false);
							JSlider paintRadius = new JSlider(JSlider.HORIZONTAL, 1, 500, 10);
							paintRadius.setBorder(BorderFactory.createTitledBorder("Paint brush radius"));

							ButtonGroup group = new ButtonGroup();
							group.add(rbSpouts);
							group.add(rbPaintBlack);
							group.add(rbPaintWhite);

							JSlider jSliderContrast = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
							jSliderContrast.setBorder(BorderFactory.createTitledBorder("Image contrast"));

							JPanel btnPanle = new JPanel(new GridLayout(1, 3));
							btnPanle.add(rbSpouts);
							btnPanle.add(rbPaintBlack);
							btnPanle.add(rbPaintWhite);
							JPanel topPanel = new JPanel(new BorderLayout());
							topPanel.add(jSliderContrast, BorderLayout.NORTH);
							topPanel.add(paintRadius, BorderLayout.SOUTH);
							topPanel.add(btnPanle, BorderLayout.CENTER);
							mainWindow.getContentPane().add(topPanel, BorderLayout.NORTH);

							JSlider jSliderSpeed = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
							jSliderSpeed.setBorder(BorderFactory.createTitledBorder("Simulation step delay"));

							JButton btnStart = new JButton("Start");
							JButton btnStop = new JButton("Pause");

							JPanel simControlPanel = new JPanel(new BorderLayout());
							simControlPanel.add(jSliderSpeed, BorderLayout.CENTER);
							simControlPanel.add(btnStart, BorderLayout.WEST);
							simControlPanel.add(btnStop, BorderLayout.EAST);
							mainWindow.getContentPane().add(simControlPanel, BorderLayout.SOUTH);

							BrushHoverCursor brushHoverCursor = new BrushHoverCursor();
							brushHoverCursor.setRadius(paintRadius.getValue());

							rbSpouts.addActionListener(e -> canvas.removeSubComponent(brushHoverCursor));
							rbPaintBlack.addActionListener(e -> {
								canvas.removeSubComponent(brushHoverCursor);
								canvas.addSubComponent(brushHoverCursor);
							});
							rbPaintWhite.addActionListener(e -> {
								canvas.removeSubComponent(brushHoverCursor);
								canvas.addSubComponent(brushHoverCursor);
							});
							paintRadius.addChangeListener(e -> {
								brushHoverCursor.setRadius(paintRadius.getValue());
							});

							SpoutHandlingThread spoutHandlingThread = new SpoutHandlingThread(canvas, jSliderSpeed);
							new Thread(spoutHandlingThread).start();
							btnStart.addActionListener(a -> spoutHandlingThread.setPaused(false));
							btnStop.addActionListener(a -> spoutHandlingThread.setPaused(true));

							jSliderContrast.addChangeListener(new ChangeListener() {
								@Override
								public void stateChanged(ChangeEvent e) {
									int value = jSliderContrast.getValue();
									double coefficient = value / 50d;
									for (int x = 0; x < sourceImgRgb.getWidth(); x++) {
										for (int y = 0; y < sourceImgRgb.getHeight(); y++) {
											int values[] = new int[3];
											sourceImgRgb.getRaster().getPixel(x, y, values);
											for (int i = 0; i < values.length; i++) {
												values[i] = (int) Math.min(255, values[i] * coefficient);
											}
											img.getRaster().setPixel(x, y, values);
										}
									}

									canvas.invalidate();
									canvas.revalidate();
									canvas.repaint();
								}
							});

							canvas.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(MouseEvent e) {
									int x = e.getX();
									int y = e.getY();
									if (x < canvas.getWidth() && y < canvas.getHeight()) {
										if (rbSpouts.isSelected()) {
											addSpout(x, y, canvas, spoutHandlingThread);
										}
									}
								}
							});
							canvas.addMouseMotionListener(new MouseAdapter() {
								@Override
								public void mouseMoved(MouseEvent e) {
									int x = e.getX();
									int y = e.getY();
									if (x < canvas.getWidth() && y < canvas.getHeight()) {
										if (rbPaintBlack.isSelected() || rbPaintWhite.isSelected()) {
											int radius = brushHoverCursor.getRadius();
											brushHoverCursor.setX(x - radius / 2);
											brushHoverCursor.setY(y - radius / 2);
											canvas.imageUpdate(canvas.getImage(), ImageObserver.SOMEBITS, x - 1, y - 1, radius + 2, radius + 2);
										}
									}

								}

								@Override
								public void mouseDragged(MouseEvent e) {
									int x = e.getX();
									int y = e.getY();
									if (x < canvas.getWidth() && y < canvas.getHeight()) {
										if (rbPaintBlack.isSelected() || rbPaintWhite.isSelected()) {
											int radius = paintRadius.getValue();
											int centerX = x - radius / 2;
											int centerY = y - radius / 2;
											brushHoverCursor.setX(centerX);
											brushHoverCursor.setY(centerY);
											Color color = rbPaintBlack.isSelected() ? Color.BLACK : Color.WHITE;
											Graphics g = canvas.getImage().getGraphics();
											g.setColor(color);
											g.fillOval(centerX, centerY, radius, radius);
											// g.dispose();
											canvas.imageUpdate(canvas.getImage(), ImageObserver.SOMEBITS, x - 1, y - 1, radius + 2, radius + 2);

											// Update source image too
											g = sourceImgRgb.getGraphics();
											g.setColor(color);
											g.fillOval(centerX, centerY, radius, radius);
										}
									}
								}
							});

							mainWindow.pack();
							// scrollPanel.setSize(canvas.getSize());
							canvas.setSize(scrollPanel.getSize());
							SwingUtilities.invokeLater(() -> {
								mainWindow.setVisible(true);
							});
						} else {
							ErrorHelper.showError("Bad input", "Not an image file");
						}
					} catch (final Throwable t) {
						t.printStackTrace();
						ErrorHelper.showError(t);
					}

				}).start();
			}
		});
	}

	protected static final Font spoutFont = new Font("Sanserif", Font.BOLD, 16);

	protected static void addSpout(int x, int y, ImageOverlayJComponent canvas, SpoutHandlingThread spoutHandlingThread) {
		spoutHandlingThread.addSpout(x, y);
	}
}
