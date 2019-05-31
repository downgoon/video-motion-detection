
package com.downgoon.video.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * a rgb image supporting some editing features such as
 * {@link #drawRect(int, int, int, int, Color)} and
 * {@link #fillRect(int, int, int, int, Color)}
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class MatrixImage implements Cloneable {

    /**
     * inner image holder
     */
    private BufferedImage bufferedImage;

    /**
     * buffered rgb array for the inner {@link BufferedImage}
     */
    private int[] rgbArray;

    private int width;

    private int height;

    /**
     * Constructor using a matrixImage in memory
     *
     * @param image Image
     */
    public MatrixImage(BufferedImage image) {
        this.bufferedImage = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        loadRgbArrayBuffer();
    }

    public MatrixImage(int width, int height, int[] rgbArray) {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;
        this.rgbArray = rgbArray;
        flushRgbArrayBuffer();
    }

    /**
     * Constructor to blank matrixImage, passing the size of matrixImage
     *
     * @param width  width
     * @param height height
     */
    public MatrixImage(int width, int height) {
        this(width, height, new int[width * height]);
    }


    /**
     * @return integer color array for the entire matrixImage.
     */
    public int[] getRgbArray() {
        return rgbArray;
    }

    /**
     * Set the integer color array for the entire matrixImage.
     **/
    public void setRgbArray(int[] arr) {
        rgbArray = arr;
    }

    /**
     * load rgb array from the inner {@link BufferedImage} and buffer it in
     * memory in order to enhance performances on subsequent image editing
     * actions such as {@link #drawRect(int, int, int, int, Color)} and
     * {@link #fillRect(int, int, int, int, Color)}
     */
    private void loadRgbArrayBuffer() {
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0,
                bufferedImage.getWidth());
    }

    /**
     * flush the buffered rgb array into the inner {@link BufferedImage}, it is
     * often called in image editing actions such as
     * {@link #drawRect(int, int, int, int, Color)}
     */
    private void flushRgbArrayBuffer() {
        int w = bufferedImage.getWidth();
        bufferedImage.setRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), rgbArray, 0, w);
    }

    public static void copyRgbArray(MatrixImage sourceImg, MatrixImage targetImg) {
        System.arraycopy(sourceImg.getRgbArray(), 0, targetImg.getRgbArray(), 0,
                sourceImg.getWidth() * sourceImg.getHeight());
        // targetImg.flushRgbArrayBuffer();
    }

    /**
     * Returns the width
     *
     * @return int width
     */
    public int getWidth() {
        return bufferedImage.getWidth();
    }

    /**
     * Returns the height
     *
     * @return int height
     */
    public int getHeight() {
        return bufferedImage.getHeight();
    }


    /**
     * Gets the integer color composition for x, y position
     *
     * @param x coordinate x
     * @param y coordinate y
     * @return int color
     */
    public int getColor(int x, int y) {
        return rgbArray[y * width + x];
    }

    /**
     * Gets alpha component for x, y position
     *
     * @param x coordinate x
     * @param y coordinate y
     * @return alpha component
     */
    public int alpha(int x, int y) {
        return (rgbArray[((y * width + x))] & 0xFF000000) >>> 24;
    }

    /**
     * Gets the integer color component 0 in the x and y position
     *
     * @param x coordinate x
     * @param y coordinate y
     * @return int color component 0
     */
    public int rgbR(int x, int y) {
        return (rgbArray[((y * width + x))] & 0x00FF0000) >>> 16;
    }

    /**
     * Gets the integer color component 1 in the x and y position
     *
     * @param x x position
     * @param y y position
     * @return int color component 1
     */
    public int rgbG(int x, int y) {
        return (rgbArray[((y * width + x))] & 0x0000FF00) >>> 8;
    }

    /**
     * Gets the integer color component 2 in the x and y position
     *
     * @param x coordinate x
     * @param y coordinate y
     * @return int blue color
     */
    public int rgbB(int x, int y) {
        return (rgbArray[((y * width + x))] & 0x000000FF);
    }

    /**
     * Sets the integer color composition in X an Y position
     *
     * @param x     position
     * @param y     position
     * @param color color value
     */
    public void setColor(int x, int y, int color) {
        rgbArray[((y * bufferedImage.getWidth() + x))] = color;
    }

    /**
     * Sets the integer color in X an Y position
     *
     * @param x     position
     * @param y     position
     * @param red   component 0
     * @param green component 1
     * @param blue  component 2
     */
    public void setAlphaRGB(int x, int y, int alpha, int red, int green, int blue) {
        rgbArray[((y * bufferedImage.getWidth() + x))] = (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

    /**
     * set alpha component value at the point (x,y)
     *
     * @param x     coordinate x
     * @param y     coordinate y
     * @param alpha alpha component value
     */
    public void setAlpha(int x, int y, int alpha) {
        int color = rgbArray[((y * width + x))];
        color = (alpha << 24) + (color & 0x00FFFFFF);
        rgbArray[((y * width + x))] = color;
    }

    /**
     * Sets the integer color in X an Y position
     *
     * @param x     position
     * @param y     position
     * @param red   component 0
     * @param green component 1
     * @param blue  component 2
     */
    public void setRGB(int x, int y, int red, int green, int blue) {
        int alpha = (rgbArray[((y * width + x))] & 0xFF000000) >>> 24;
        setAlphaRGB(x, y, alpha, red, green, blue);
    }

    /**
     * Sets a new matrixImage
     *
     * @param image a buffered matrixImage
     */
    public void setBufferedImage(BufferedImage image) {
        this.bufferedImage = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        loadRgbArrayBuffer();
    }

    /**
     * get the inner {@link BufferedImage}
     */
    public BufferedImage getBufferedImage() {
        return getBufferedImage(true);
    }

    /**
     * get the inner {@link BufferedImage}
     *
     * @param includeAlpha including alpha component or not
     * @return return the inner {@link BufferedImage} associated with
     * {@link MatrixImage}
     */
    public BufferedImage getBufferedImage(boolean includeAlpha) {
        if (includeAlpha) {
            return bufferedImage;
        }
        int[] rgbFiltered = new int[width * height];
        for (int i = 0; i < rgbFiltered.length; i++) {
            // remove alpha component
            rgbFiltered[i] = rgbArray[i] & 0x00FFFFFF;
        }
        BufferedImage imageFiltered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        imageFiltered.setRGB(0, 0, width, height, rgbFiltered, 0, width);
        return imageFiltered;
    }

    /*
     * image editor toolkit including drawRect and fillRect
     */

    /**
     * draw a rectangle into the image at the specified position marked (x,y)
     */
    public void drawRect(int x, int y, int w, int h, Color c) {
        drawRect(x, y, w, h, c, true);
    }

    /**
     * draw a thick rectangle
     */
    public void drawRect(int x, int y, int w, int h, int thickness, Color c) {
        for (int i = 0; i < thickness; i++) {
            drawRect(x + i, y + i, w - (i * 2), h - (i * 2), c, false);
        }
        flushRgbArrayBuffer();
    }

    private void drawRect(int x, int y, int w, int h, Color c, boolean autoFlush) {
        int color = c.getRGB();
        for (int i = x; i < x + w; i++) {
            setColor(i, y, color);
            setColor(i, y + (h - 1), color);
        }

        for (int i = y; i < y + h; i++) {
            setColor(x, i, color);
            setColor(x + (w - 1), i, color);
        }
        if (autoFlush) {
            flushRgbArrayBuffer();
        }

    }

    /**
     * fill a rectangle
     */
    public void fillRect(int x, int y, int w, int h, Color c) {
        int color = c.getRGB();
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                if (i < width && j < height) {
                    setColor(i, j, color);
                }
            }
        }
        flushRgbArrayBuffer();
    }

    /**
     * Compare two MatrixImage objects
     *
     * @param obj object to be compared. MatrixImage object is expected.
     */
    @Override
    public boolean equals(Object obj) {
        MatrixImage img = (MatrixImage) obj;
        int[] l_arrColor = img.getRgbArray();

        if (getWidth() != img.getWidth() || getHeight() != img.getHeight()) {
            return false;
        }

        for (int l_cont = 0; l_cont < getHeight(); l_cont++) {
            if (rgbArray[l_cont] != l_arrColor[l_cont]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clones the {@link MatrixImage}
     */
    @Override
    public MatrixImage clone() {
        MatrixImage newMarvinImg = new MatrixImage(getWidth(), getHeight());
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        newMarvinImg.setBufferedImage(newImage);
        MatrixImage.copyRgbArray(this, newMarvinImg);
        newMarvinImg.flushRgbArrayBuffer();
        return newMarvinImg;
    }
}
