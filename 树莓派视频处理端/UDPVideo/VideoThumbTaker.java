/*
@Time    :2019/6/5 0005 下午 2:10
@Author  :喜欢二福的沧月君（necydcy@gmail.com）
@FileName: VideoThumbTaker.java
@Software: IntelliJ IDEA
*/
package com.gao.UDPVideo;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;


class VideoThumbTaker
{
    String ffmpegApp;

    VideoThumbTaker(String ffmpegApp)
    {
        this.ffmpegApp = ffmpegApp;
    }

    @SuppressWarnings("unused")
    /****
     * 获取指定时间内的图片
     * @param videoFilename:视频路径
     * @param thumbFilename:图片保存路径
     * @param width:图片长
     * @param height:图片宽
     * @param hour:指定时
     * @param min:指定分
     * @param sec:指定秒
     * @throws IOException
     * @throws InterruptedException
     */ void getThumb(String videoFilename, String thumbFilename, int width,
                      int height, int hour, int min, float sec)throws IOException,
            InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegApp,"-y",
                "-i", videoFilename, "-vframes", "1", "-ss", hour + ":" + min
                + ":" + sec, "-f", "mjpeg", "-s", width + "*" + height,
                "-an", thumbFilename);

        Process process = processBuilder.start();

        InputStream stderr = process.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) !=null)
            ;
        process.waitFor();

        br.close();
        isr.close();
        stderr.close();
    }
//
//    public static void main(String[] args)
//    {
//        VideoThumbTaker videoThumbTaker = new VideoThumbTaker("E:\\ffmpeg-4.1.3-win64-static\\bin\\ffmpeg.exe");
//        try
//        {
//            videoThumbTaker.getThumb("D:\\2019-06-05_13：29：17——2019-06-05_13：29：17.mp4", "d:\\first.png",    1080, 896, 0, 0, 0);
////            System.out.println("over");
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
}

