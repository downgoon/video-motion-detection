package com.downgoon.video.camera;

import com.downgoon.video.image.MatrixImage;

/**
 * 摄像头操作接口，通常包括连接摄像头，并抓图等操作。
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public interface CameraInterface {


    /**
     * 连接本地的摄像头设备。有多个摄像头时，通过编号区分。
     *
     * @param deviceIndex 摄像头编号。Mac的 FaceTime 摄像头编号为0.
     * @throws CameraException 连接失败时，抛相关异常。
     */
    void connect(int deviceIndex) throws CameraException;


    /**
     * 连接本地的摄像头设备。有多个摄像头时，通过编号区分。
     *
     * @param deviceIndex 摄像头编号。Mac的 FaceTime 摄像头编号为0.
     * @param width       设置抓取画面的宽度
     * @param height      设置抓取画面的高度
     * @throws CameraException 连接失败时，抛相关异常。
     */
    void connect(int deviceIndex, int width, int height) throws CameraException;

    /**
     * 是否已经连接上摄像头
     *
     * @return 返回是否连接成功
     */
    boolean isConnected();


    /**
     * 断开摄像头的连接
     *
     * @throws CameraException 断开失败时，抛出相关异常。
     */
    void disconnect() throws CameraException;

    /**
     * extract one frame from camera device
     *
     * @return 返回帧
     * @throws CameraException 抓图失败时，抛出相关异常。
     */
    MatrixImage getFrame() throws CameraException;

    /**
     * 摄像头抓图的宽度
     *
     * @return 返回摄像头抓图的宽度
     */
    int getWidth();

    /**
     * 摄像头抓图的高度
     *
     * @return 返回摄像头抓图的高度
     */
    int getHeight();

}
