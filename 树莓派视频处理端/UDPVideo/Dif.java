package com.gao.UDPVideo;
/*
@Time    :2019/5/8 0008 下午 10:41
@Author  :喜欢二福的沧月君（necydcy@gmail.com）
@FileName: Dif.java
@Software: IntelliJ IDEA
*/

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Dif {
    // 全流程

    static double getSimilarity(File imageFile1, File file2) throws IOException {
        int[] pixels1 = getImgFinger(imageFile1);
        int[] pixels2 = getImgFinger(file2);
// 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）

        int hammingDistance = getHammingDistance(pixels1, pixels2);
// 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
        return calSimilarity(hammingDistance)*100;
    }

    private static int[] getImgFinger(File imageFile) throws IOException {
        Image image = ImageIO.read(imageFile);
// 转换至灰度
        image = toGrayscale(image);
// 缩小成32x32的缩略图
        image = scale(image);
// 获取灰度像素数组
        int[] pixels1 = getPixels(image);
// 获取平均灰度颜色
        int averageColor = getAverageOfPixelArray(pixels1);
// 获取灰度像素的比较数组（即图像指纹序列）
        pixels1 = getPixelDeviateWeightsArray(pixels1, averageColor);
        return pixels1;
    }

    // 将任意Image类型图像转换为BufferedImage类型，方便后续操作
    private static BufferedImage convertToBufferedFrom(Image srcImage) {
        BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(null),
                srcImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(srcImage, null, null);
        g.dispose();
        return bufferedImage;
    }

    // 转换至灰度图
    private static BufferedImage toGrayscale(Image image) {
        BufferedImage sourceBuffered = convertToBufferedFrom(image);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(sourceBuffered, null);
    }

    // 缩放至32x32像素缩略图
    private static Image scale(Image image) {
        image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return image;
    }

    // 获取像素数组
    private static int[] getPixels(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        return convertToBufferedFrom(image).getRGB(0, 0, width, height,
                null, 0, width);
    }

    // 获取灰度图的平均像素颜色值
    private static int getAverageOfPixelArray(int[] pixels) {
        Color color;
        long sumRed = 0;
        for (int pixel : pixels) {
            color = new Color(pixel, true);
            sumRed += color.getRed();
        }
        return (int) (sumRed / pixels.length);
    }

    // 获取灰度图的像素比较数组（平均值的离差）
    private static int[] getPixelDeviateWeightsArray(int[] pixels, final int averageColor) {
        Color color;
        int[] dest = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            color = new Color(pixels[i], true);
            dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
        }
        return dest;
    }

    // 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
    private static int getHammingDistance(int[] a, int[] b) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] == b[i] ? 0 : 1;
        }
        return sum;
    }

    // 通过汉明距离计算相似度
    private static double calSimilarity(int hammingDistance){
        int length = 32*32;
        double similarity = (length - hammingDistance) / (double) length;

// 使用指数曲线调整相似度结果
        similarity = Math.pow(similarity, 2);
        return similarity;
    }
}
