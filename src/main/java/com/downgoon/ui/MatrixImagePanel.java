package com.downgoon.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import com.downgoon.video.image.MatrixImage;


/**
 * 用于显示图片 {@link MatrixImage} 的画布
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class MatrixImagePanel extends JPanel {

	private static final long serialVersionUID = -2251129125464243732L;
	protected MatrixImage matrixImage;

	private boolean autoZoom;

	private int lastWidth = 0;
	private int lastHeight = 0;

	public MatrixImagePanel() {
		super();
		autoZoom = true;
	}

	/**
	 * set image associated with the panel
	 */
	public void setMatrixImage(MatrixImage image) {
		this.matrixImage = image;
		if (autoZoom && differentSize(image)) {
			this.lastWidth = image.getWidth();
			this.lastHeight = image.getHeight();
			Dimension d = new Dimension(this.lastWidth, this.lastHeight);
			setSize(d);
			setPreferredSize(d);
			validate();
		}
		repaint();
	}

	/**
	 * get image associated with the panel
	 */
	public MatrixImage getMatrixImage() {
		return matrixImage;
	}

	private boolean differentSize(MatrixImage image) {
		return this.lastWidth != image.getWidth() || this.lastHeight != image.getHeight();
	}

	@Override
	public Image createImage(int width, int height) {
		matrixImage = new MatrixImage(width, height);
		setPreferredSize(new Dimension(width, height));
		return matrixImage.getBufferedImage();
	}

	/**
	 * Overwrite the paint method
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (matrixImage != null) {
			// imageUpdate
			g.drawImage(matrixImage.getBufferedImage(), 0, 0, this);
		}
	}

}
