package com.downgoon.video.camera;


/**
 * 摄像头连接或抓图异常等
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class CameraException extends Exception {

    private static final long serialVersionUID = 1040154087524461664L;

    public CameraException(String message, Throwable cause) {
        super(message, cause);
    }

}
