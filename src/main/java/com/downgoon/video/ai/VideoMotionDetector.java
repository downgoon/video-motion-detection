package com.downgoon.video.ai;

import java.util.ArrayList;
import java.util.List;

import com.downgoon.video.image.MatrixImage;
import com.downgoon.video.util.Rect;

/**
 * 一个朴素的视频运动检测算法
 *
 * @author downgoon@qq.com
 * @since 2016-06-09
 */
public class VideoMotionDetector {

    /**
     * 区块粒度：将一张图片，切成很多个区块。每个区块大小为10像素*10像素。
     */
    private int blockSizeThreshold = 10;

    /**
     * 色差阈值：相邻的两帧，在同一像素点位置，如果色差大于阈值，则判定为运动像素。
     */
    private int colorDiffThreshold = 30;


    public VideoMotionDetector() {
        this(10, 30);
    }

    public VideoMotionDetector(int blockSizeThreshold, int colorDiffThreshold) {
        super();
        this.blockSizeThreshold = blockSizeThreshold;
        this.colorDiffThreshold = colorDiffThreshold;
    }

    /**
     * 区块运动计数器：记录指定区块内，有多少个像素被判定为运动像素了。
     */
    private int[][] blockMotionCount;

    /**
     * 区块运动判决器：记录指定区块，是否已经被判定为运动区块了。
     */
    private boolean[][] blockMotionJudge;

    private int pixelWidth;
    private int pixelHeight;

    private boolean initialized = false;

    public List<Rect> detect(MatrixImage currImage, MatrixImage diffImage) {

        pixelWidth = currImage.getWidth();
        pixelHeight = currImage.getHeight();

        if (!initialized) {
            // 划分区块：将一张图片，按照设定的区块大小，切分成若干个区块，并统计各个区块的运动像素
            blockMotionCount = new int[pixelWidth / blockSizeThreshold][pixelHeight / blockSizeThreshold];
            blockMotionJudge = new boolean[pixelWidth / blockSizeThreshold][pixelHeight / blockSizeThreshold];
            initialized = true;
        }

        // 初始化 blockMotionCount 和 blockMotionJudge 两个矩阵
        resetBlockMotionMatrix();

        // 计算每个Block区块的运动像素个数，并将结果保存到 blockMotionCount 矩阵
        doBlockMotionCount(currImage, diffImage);


        // 找出所有的运动区块，并登记为 Rect
        List<Rect> motionRects = new ArrayList<>();
        Rect rect = doBlockMotionJudge();
        while (rect != null) {
            motionRects.add(rect);
            // 每次发现一个新的运动区块及其周围邻居
            rect = doBlockMotionJudge();
        }
        return motionRects;
    }

    private void doBlockMotionCount(MatrixImage currImage, MatrixImage diffImage) {
        for (int y = 0; y < currImage.getHeight(); y++) {
            for (int x = 0; x < currImage.getWidth(); x++) {

                // 当前图在(x,y)点的RGB值
                int cR = currImage.rgbR(x, y);
                int cG = currImage.rgbG(x, y);
                int cB = currImage.rgbB(x, y);

                // 对比图在相同点的RGB值
                int dR = diffImage.rgbR(x, y);
                int dG = diffImage.rgbG(x, y);
                int dB = diffImage.rgbB(x, y);

                // 两个点在RGB的任一通道色差大于色差阈值，则判定为"运动像素"
                if (Math.abs(cR - dR) > colorDiffThreshold
                        || Math.abs(cG - dG) > colorDiffThreshold
                        || Math.abs(cB - dB) > colorDiffThreshold) {

                    // 区块内"运动像素"计数器累加1
                    int blockX = x / blockSizeThreshold;
                    int blockY = y / blockSizeThreshold;
                    blockMotionCount[blockX][blockY]++;
                }

            }
        }
    }

    /**
     * 查找并合并运动区块： 从左往右，从上到下，找到第一个运动区块，并扩充到它的邻居区块。
     *
     * @return 只要找到任何运动区块，都返回True；否则，返回False
     */
    private Rect doBlockMotionJudge() {
        int blockWidth = pixelWidth / blockSizeThreshold;
        int blockHeight = pixelHeight / blockSizeThreshold;

        Rect pixelRect = null;
        for (int bx = 0; bx < blockWidth; bx++) {
            for (int by = 0; by < blockHeight; by++) {

                if (!blockMotionJudge[bx][by] && isMotionBlock(bx, by)) {

                    blockMotionJudge[bx][by] = true;

                    if (pixelRect == null) {
                        // "运动区块"对应图片中像素矩形坐标: [(X1, Y1), (X2,Y2)]
                        pixelRect = new Rect(bx * blockSizeThreshold,
                                by * blockSizeThreshold,
                                bx * blockSizeThreshold + blockSizeThreshold,
                                by * blockSizeThreshold + blockSizeThreshold
                        );
                    }

                    // 每当标记一个"运动区块"，立即找出它的邻居区块也是运动的，但是尚未标记的
                    mergeNeighborBlocks(bx, by, pixelRect);
                    return pixelRect;
                }
            }
        }

        // NO MORE MOTION BLOCK
        return null;
    }


    /**
     * 合并周围5格近邻的运动区块
     */
    private void mergeNeighborBlocks(int blockX, int blockY, Rect pixelRect) {
        for (int nbx = blockX - 5; nbx < blockX + 5; nbx++) {
            for (int nby = blockY - 5; nby < blockY + 5; nby++) {

                // 邻居区块：某个指定区块周围5个区块
                boolean isNear5 = (nbx > 0 && nbx < pixelWidth / blockSizeThreshold)
                        && (nby > 0 && nby < pixelHeight / blockSizeThreshold);

                if (isNear5) {

                    // 对于邻居区块是运动的，且尚未被标记的
                    if (isMotionBlock(nbx, nby) && !blockMotionJudge[nbx][nby]) {

                        // 跟邻居对比，左顶点往左靠
                        if (nbx * blockSizeThreshold < pixelRect.getX1()) {
                            pixelRect.setX1(nbx * blockSizeThreshold);
                        }
                        if (nby * blockSizeThreshold < pixelRect.getY1()) {
                            pixelRect.setY1(nby * blockSizeThreshold);
                        }

                        // 跟邻居对比，右底点往右靠
                        if (nbx * blockSizeThreshold > pixelRect.getX2()) {
                            pixelRect.setX2(nbx * blockSizeThreshold);
                        }
                        if (nby * blockSizeThreshold > pixelRect.getY2()) {
                            pixelRect.setY2(nby * blockSizeThreshold);
                        }

                        // 把"邻居区块"也标记为"运动区块"
                        blockMotionJudge[nbx][nby] = true;

                        // 每当标记一个运动区域时，立即找出它的邻居区域也是运动的，但是尚未标记的
                        mergeNeighborBlocks(nbx, nby, pixelRect);
                    }
                }
            }
        }
    }

    private void resetBlockMotionMatrix() {
        for (int x = 0; x < pixelWidth / blockSizeThreshold; x++) {
            for (int y = 0; y < pixelHeight / blockSizeThreshold; y++) {
                blockMotionCount[x][y] = 0;
                blockMotionJudge[x][y] = false;
            }
        }
    }


    /**
     * 判断一个区块是否是运动区块
     */
    private boolean isMotionBlock(int blockX, int blockY) {
        int halfBlockPixels = (blockSizeThreshold * blockSizeThreshold) / 2;
        // 如果区块内有超过半数是运动像素，则区块判别为"运动区块"
        return blockMotionCount[blockX][blockY] > halfBlockPixels;
    }

}
