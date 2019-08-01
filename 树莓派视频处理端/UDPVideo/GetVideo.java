package com.gao.UDPVideo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Scanner;

public class GetVideo implements Runnable{
    private InputStream in ; // 正常输出流
    private boolean stop ; // 结束进程
    private Process pro ;
    private GetVideo(Process process, InputStream in){
        this.in = in;
        this.pro = process;
    }
    private void setStop() {
        this.stop = true;
    }
//    public static void main(String[] args) throws Exception{
//        System.out.println("###使用进程的方式进行编码###");
//        // 1. 视频转换视频
//        String rtspUrl = "rtmp://rtmp01open.ys7.com/openlive/7e4dc3e3a4144178827a90789fc3cf47.hd";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH：mm：ss");
//        Date now = new Date();
//        Date afterDate = new Date(now .getTime() + 300);
//        String dest = "D:/"+sdf.format(now)+"——"+sdf.format(afterDate)+".mp4";
//        System.out.println("+++++++++++++++++++"+dest);
//        convert(rtspUrl, dest);
//
//        System.out.println("###end###");
//    }

    static void convert(String src, String dest)throws Exception{
        String ffcmdpath = "ffmpeg";
        StringBuilder cmd = new StringBuilder();
        cmd.append(ffcmdpath)// 使用tcp的命令，默认是udp
//                .append(" -rtsp_transport tcp ")
                .append(" -i ").append(src)
//                	.append(" -vcodec copy ")
                .append(" -vcodec h264 ")
                //	.append(" -acodec copy ") // 音频，不设置好像也有。
                //	.append(" -s 1280*720 ")   // 设置分辨率，关系到视频的大小或者为 -vf scale=iw/2:ih/2
                //	.append(" -vf scale=iw/2:ih/2 ")
                .append(" -y ") // 覆盖
                .append(dest);
        System.out.println("cmd="+cmd.toString());
        System.out.println("###start cmd="+ new Date().toString());
        Process process = Runtime.getRuntime().exec(cmd.toString());
        // 输出内容
        GetVideo twffIn = new GetVideo(process, process.getInputStream());
        GetVideo twffInErr = new GetVideo(process,process.getErrorStream());
        Thread t = new Thread(twffIn);
        Thread t1 = new Thread(twffInErr);
        t.start();t1.start();
        // 停止指令,10秒后停止 ,一定要发送这个，要不然视频保存不下来
        //
        int i = process.waitFor(); // 一定要配合2个 inputstream ，要不然会一直阻塞
        System.out.println(i+"###end cmd="+ new Date().toString());
        twffIn.setStop();twffInErr.setStop(); // 停止 线程

    }


    private static void stopConvert(Process process){
        System.out.println("###send q cmd ");
        try{
            OutputStream os = process.getOutputStream();
            os.write("q".getBytes());
            os.flush(); // 一定要
        }catch(Exception err){
            err.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(in);
        while(!stop){
            if(scanner.hasNext()){
                String s = scanner.nextLine();
                System.out.println(s);
                // 判断停止录像的条件
                if(s.startsWith("frame=") && s.indexOf("fps=") > 6){
                    String frameCountStr = s.substring(6
                            ,s.indexOf("fps="));
//					System.out.println(s.indexOf("fps=") + ",frameCountStr="+frameCountStr);
                    // 获得当前解析到的帧数，退出
                    int frameCount = Integer.parseInt(frameCountStr.trim());
                    System.out.println("======================"+frameCount);
                    int maxFrameCount = 30000;
                    if(frameCount >= maxFrameCount){
                        System.out.println("##maxFrameCount="+ maxFrameCount +",frameCount="+frameCount);
                        stopConvert(pro);
                    }
                }
            }else{
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
        System.out.println("###读取线程结束啦###");
    }
}