package com.downgoon.apps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;

import com.downgoon.ui.MatrixImagePanel;
import com.downgoon.video.ai.VideoMotionDetector;
import com.downgoon.video.camera.CameraCapture;
import com.downgoon.video.camera.CameraException;
import com.downgoon.video.image.MatrixImage;
import com.downgoon.video.util.Rect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 视频运动标记
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class MontionMarker extends JFrame {

    private static final long serialVersionUID = -1992437126562575898L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MontionMarker.class);

    private int imageWidth, imageHeight;

    /**
     * display images continuously
     */
    private MatrixImagePanel imagePanel;

    /**
     * capture camera continuously
     */
    private CameraCapture cameraCapturer;

    /**
     * Motion Detection
     */
    private VideoMotionDetector motionDetecor = new VideoMotionDetector();

    public MontionMarker(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public void start() throws CameraException {

        // 如果应用意外退出，释放摄像头资源
        startShutdownHook();

        // 启动摄像头
        startCamera();

        // 勾勒UI画板，
        startPanel();

        // 启动单独的线程抓取摄像头的图片，并推送到画板，快速、持续，人眼就会看为视频
        startCamera2PanelContinuously("video-motion-detection");
    }

    private void startCamera() throws CameraException {
        this.cameraCapturer = new CameraCapture();
        // Mac 电脑的摄像头编号为 0
        cameraCapturer.connect(0, this.imageWidth, this.imageHeight);
    }

    private void startPanel() {
        /* video Panel */
        this.imagePanel = new MatrixImagePanel();

        /* append into container of the JFrame */
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(imagePanel, BorderLayout.NORTH);

        // JFrame Settings
        setTitle("运动检测：对着摄像头摇摇头？");
        setSize(imageWidth, imageHeight);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void startCamera2PanelContinuously(String threadName) {
        Thread motionDetectThread = new Thread(new Runnable() {

            @Override
            public void run() {

                MatrixImage lastFrame = null;

                int failCnt = 0;
                while (cameraCapturer != null && cameraCapturer.isConnected()) {

                    try {

                        MatrixImage currFrame = null;
                        try {
                            currFrame = cameraCapturer.getFrame();
                            failCnt = 0;
                        } catch (CameraException ce) {
                            failCnt++;
                            LOGGER.error("camera capture exception: {}", ce.getMessage(), ce);
                            if (failCnt > 3) {
                                Thread.sleep(100L);
                                if (!cameraCapturer.isConnected()) {
                                    LOGGER.warn("camera not connected, break capture loop");
                                    break;
                                }
                            }

                        }

                        MatrixImage showFrame = currFrame;

                        if (lastFrame != null) {
                            // motion detection
                            List<Rect> motionRegions = motionDetecor.detect(currFrame, lastFrame);

                            for (int i = 0; i < motionRegions.size(); i++) {
                                Rect rect = motionRegions.get(i);

                                MatrixImage markFrame = new MatrixImage(imageWidth, imageHeight);
                                // deep copy and motion mark
                                MatrixImage.copyRgbArray(currFrame, markFrame);

                                // mark motion block with a tiffany blue rectangle
                                // NICE-RED: 0xF01D39   NICE-BLUE: 0x0F76C1  TIFFANY-BLUE: 0x81d8cf
                                markFrame.drawRect(rect.getX1(), rect.getY1(), rect.getWidth(), rect.getHeight(), 2,
                                        new Color(0x81d8cf));

                                showFrame = markFrame;
                            }

                        }

                        lastFrame = currFrame;

                        // push original current image or marked image into the panel
                        imagePanel.setMatrixImage(showFrame);

                    } catch (Exception e) {
                        // don't break from the loop
                        LOGGER.error("while true job exception: {}", e.getMessage(), e);
                    }

                } // end while (true)

            }
        }, threadName);
        motionDetectThread.start();
    }


    private void startShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("video-motion-hook") {

            @Override
            public void run() {
                LOGGER.info("shutdown hooking ...");
                try {
                    if (cameraCapturer != null && cameraCapturer.isConnected()) {
                        LOGGER.info("camera capture stopping ...");
                        cameraCapturer.disconnect();
                        LOGGER.info("camera capture stopped !!!");
                    }
                } catch (CameraException e) {
                    LOGGER.error("stop camera error: {} ", e.getMessage(), e);
                }

            }

        });
    }


    public static void main(String args[]) throws CameraException {
        MontionMarker montionMarker = new MontionMarker(1280, 720);
        montionMarker.start();
        LOGGER.info("motion marker started ...");
    }

}
