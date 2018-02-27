package x.mvmn.tankfillgame;

import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

							JSlider jSliderContrast = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
							jSliderContrast.setBorder(BorderFactory.createTitledBorder("Image contrast"));
							mainWindow.getContentPane().add(jSliderContrast, BorderLayout.NORTH);

							JSlider jSliderSpeed = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
							jSliderSpeed.setBorder(BorderFactory.createTitledBorder("Simulation step delay"));
							mainWindow.getContentPane().add(jSliderSpeed, BorderLayout.SOUTH);

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

									// scrollPanel.repaint();
								}
							});

							canvas.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(MouseEvent e) {
									if (e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
										Spout spout = new Spout(e.getX(), e.getY(), canvas, jSliderSpeed);
										new Thread(spout).start();
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
}
