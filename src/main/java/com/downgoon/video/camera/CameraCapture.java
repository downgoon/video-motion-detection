package com.downgoon.video.camera;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import com.downgoon.video.image.ImageConvertor;
import com.downgoon.video.image.MatrixImage;

/**
 * 通过OpenCV连接摄像头，并抓取图片。
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class CameraCapture implements CameraInterface {

    private volatile FrameGrabber frameGrabber;

    private volatile boolean connected;

    private int width = 640;

    private int height = 480;

    @Override
    public void connect(int deviceIndex) throws CameraException {
        connect(deviceIndex, width, height);
    }

    @Override
    public void connect(int deviceIndex, int width, int height) throws CameraException {
        this.width = width;
        this.height = height;

        try {
            frameGrabber = FrameGrabber.createDefault(deviceIndex);
            frameGrabber.setImageWidth(width);
            frameGrabber.setImageHeight(height);
            frameGrabber.start();
            // capture one frame to check camera started
            frameGrabber.grab();
            connected = true;
        } catch (Exception e) {
            throw new CameraException("camera connect failure", e);
        }

    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void disconnect() throws CameraException {
        try {
            frameGrabber.stop();
            connected = false;
        } catch (Exception e) {
            new CameraException("camera disconnect failure", e);
        }
    }

    @Override
    public MatrixImage getFrame() throws CameraException {
        if (!connected) {
            throw new IllegalStateException("camera not connected, no frame captured");
        }

        try {
            Frame frame = frameGrabber.grab();
            return ImageConvertor.toMatrix(frame, width, height);
        } catch (Exception e) {
            // FrameGrabber$Exception: retrieve() Error: Could not retrieve frame. (Has start() been called?)
            throw new CameraException("camera frame capture failure", e);
        }

    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }


}
