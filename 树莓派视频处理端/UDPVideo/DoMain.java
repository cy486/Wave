/*
@Time    :2019/6/5 0005 下午 2:50
@Author  :喜欢二福的沧月君（necydcy@gmail.com）
@FileName: DoMain.java
@Software: IntelliJ IDEA
*/

package com.gao.UDPVideo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.gao.UDPVideo.UDPClient.send;

public class DoMain {

    private static Properties properties;
    /**/
    public static void main(String[] args) throws Exception {
        InputStream in = DoMain.class.getClassLoader().getResourceAsStream(
                "ip.properties");
        properties = new Properties();
        String rtspUrl = null;
        String clientIp =null;
        String serverIp = null;
        String sensitivity = null;
        try {
            properties.load(in);
           rtspUrl = getRTSP();
           serverIp = getServerIp();
           clientIp = getClientIp();
           sensitivity = getSensitivity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true){
            /*
             * 第一部分从rtmp流获取视频
             * */
            System.out.println("###使用进程的方式进行编码###");
            // 1. 视频转换视频
            String videoFile = "data.mp4";
            System.out.println("+++++++++++++++++++" + videoFile);
            GetVideo.convert(rtspUrl, videoFile);
            System.out.println("###end###");

            /*
             * 利用ffmpeg获取视频的第一帧和最后一帧的图片
             * */
            VideoFirstThumbTaker first = new VideoFirstThumbTaker("ffmpeg");
            VideoLastThumbTaker last = new VideoLastThumbTaker("ffmpeg");
            try {
                first.getThumb(videoFile);
                last.getThumb(videoFile);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            /*
             * 进行判断，如果有人闯入便发送视频，无人闯入就发送图片
             * */
            File picture1 = new File("first.png");
            File picture2 = new File("last.png");
            double sim = 0;
            try {
                sim = Dif.getSimilarity(picture1, picture2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File videoF = new File(videoFile);
            //发送
            int sens = Integer.parseInt(sensitivity);
            if (sim < sens) {
                send(videoF, clientIp, serverIp);
                System.out.println("相似度：" + sim);
                System.out.println("有人闯入");

            } else {
                UDPEchoClientTimeout.sendmessage(serverIp);
                System.out.println("相似度：" + sim);
                System.out.println("平安无事");
            }
        }
    }
    private static String getClientIp() {
        return "" + properties.get("clientip");
    }

    private static String getServerIp() {
        return "" + properties.get("serverip");
    }

    private static String getRTSP() {
        return "" + properties.get("rtsp");
    }
    private static String getSensitivity() {
        return "" + properties.get("sensitivity");
    }
}

