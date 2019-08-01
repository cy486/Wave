package com.gao.UDPVideo;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoInfo {
    //视频路径
    private String ffmpegApp;
    //视频时
    private int hours;
    //视频分
    private int minutes;
    //视频秒
    private float seconds;
    //视频width
    private int width;
    //视频height
    private int heigt;


    public VideoInfo() {}

    VideoInfo(String ffmpegApp)
    {
        this.ffmpegApp = ffmpegApp;
    }

    public String toString()
    {
        return "time: " +hours +":" + minutes +":" + seconds +", width = " + width + ", height= " + heigt;
    }

    public void getInfo(String videoFilename)throws IOException,
            InterruptedException
    {
        String tmpFile = videoFilename + ".tmp.png";
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegApp,"-y",
                "-i", videoFilename, "-vframes", "1", "-ss", "0:0:0", "-an",
                "-vcodec", "png", "-f", "rawvideo", "-s", "100*100", tmpFile);

        Process process = processBuilder.start();

        InputStream stderr = process.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        String line;
        //打印 sb，获取更多信息。如bitrate、width、heigt
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) !=null)
        {
            sb.append(line);
        }

        new File(tmpFile).delete();

        System.out.println("video info:\n" + sb);
        Pattern pattern = Pattern.compile("Duration: (.*?),");
        Matcher matcher = pattern.matcher(sb);

        if (matcher.find())
        {
            String time = matcher.group(1);
            calcTime(time);
        }

        pattern = Pattern.compile("w:\\d+ h:\\d+");
        matcher = pattern.matcher(sb);

        if (matcher.find())
        {
            String wh = matcher.group();
            //w:100 h:100
            String[] strs = wh.split("\\s+");
            if(strs.length == 2)
            {
                width = Integer.parseInt(strs[0].split(":")[1]);
                heigt = Integer.parseInt(strs[1].split(":")[1]);
            }
        }

        process.waitFor();
        br.close();
        isr.close();
        stderr.close();
    }

    private void calcTime(String timeStr)
    {
        String[] parts = timeStr.split(":");
        hours = Integer.parseInt(parts[0]);
        minutes = Integer.parseInt(parts[1]);
        seconds = Float.parseFloat(parts[2]);
    }

    public String getFfmpegApp()
    {
        return ffmpegApp;
    }

    public void setFfmpegApp(String ffmpegApp)
    {
        this.ffmpegApp = ffmpegApp;
    }

    int getHours()
    {
        return hours;
    }

    public void setHours(int hours)
    {
        this.hours = hours;
    }

    int getMinutes()
    {
        return minutes;
    }

    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
    }

    float getSeconds()
    {
        return seconds;
    }

    public void setSeconds(float seconds)
    {
        this.seconds = seconds;
    }

    int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    int getHeigt()
    {
        return heigt;
    }

    public void setHeigt(int heigt)
    {
        this.heigt = heigt;
    }

//    public static void main(String[] args)
//    {
//        VideoInfo videoInfo = new VideoInfo("E:\\ffmpeg-4.1.3-win64-static\\bin\\ffmpeg.exe");
//        try
//        {
//            videoInfo.getInfo("D:\\2019-06-05_13：29：17——2019-06-05_13：29：17.mp4");
//            System.out.println(videoInfo);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

}