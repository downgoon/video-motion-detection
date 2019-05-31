package com.downgoon.video.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.stream.ImageInputStream;

/**
 * 图片IO处理类：将图片保存到磁盘；或从磁盘加载图片。
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class MatrixImageIO {

    public static MatrixImage loadImage(String imageFileName) throws IOException {

        File imgFile = new File(imageFileName);

        if (!imgFile.exists()) {
            throw new FileNotFoundException("image not found: " + imageFileName);
        }

        String extName = parseExtName(imageFileName);
        Iterator<?> imgReaders = ImageIO.getImageReadersByFormatName(extName);
        ImageReader imgReader = (ImageReader) imgReaders.next();

        imgReader.addIIOReadWarningListener(new IIOReadWarningListener() {
            public void warningOccurred(ImageReader source, String warning) {
                // do nothing
            }
        });

        ImageInputStream imageInputStream = null;

        try {
            imageInputStream = ImageIO.createImageInputStream(imgFile);
            imgReader.setInput(imageInputStream);
            BufferedImage bufferedImage = imgReader.read(0);
            return new MatrixImage(bufferedImage);

        } finally {
            if (imageInputStream != null) {
                imageInputStream.close();
            }

        }

    }

    /**
     * Saves a ImageMatrix via file system path.
     *
     * @param imageMatrix   - ImageMatrix object
     * @param imageFileName - file path
     */
    public static void saveImage(MatrixImage imageMatrix, String imageFileName) throws IOException {

        File imageFile = new File(imageFileName);
        String extName = parseExtName(imageFileName);
        if (extName == null) {
            extName = ".jpeg";
            imageFileName += ".jpeg";
        }

        ImageIO.write(imageMatrix.getBufferedImage(), extName, imageFile);

    }

    private static String parseExtName(String imageFileName) {
        int idx = imageFileName.lastIndexOf('.');
        if (idx == -1) {
            return null;
        }
        return imageFileName.substring(idx + 1).toLowerCase();
    }

}
