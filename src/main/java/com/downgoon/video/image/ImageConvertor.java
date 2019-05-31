package com.downgoon.video.image;

import java.nio.ByteBuffer;

import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * 图片格式转换器:
 * <p>
 * {@link Frame}  -> {@link MatrixImage }
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class ImageConvertor {

    public static MatrixImage toMatrix(Frame frame, int width, int height) {

        // convert frame to IplImage
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage iplImage = converter.convert(frame);

        // convert IplImage to MatrixImage
        int[] rgbArray = new int[width * height];
        extractAndTransferRgb(iplImage, rgbArray);
        return new MatrixImage(width, height, rgbArray);

    }

    @SuppressWarnings("deprecation")
    private static void extractAndTransferRgb(IplImage iplImage, int[] rgbArray) {
        ByteBuffer iplArray = iplImage.getByteBuffer();
        for (int i = 0, iplIdx = 0; iplIdx < iplArray.limit() - 3; i++, iplIdx += 3) {
            rgbArray[i] = 0xFF000000 + (iplArray.get(iplIdx + 2) << 16) + (iplArray.get(iplIdx + 1) << 8)
                    + iplArray.get(iplIdx);
        }
    }

}
