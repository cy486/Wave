/*
@Time    :2019/6/5 0005 下午 3:03
@Author  :喜欢二福的沧月君（necydcy@gmail.com）
@FileName: VideoLastThumbTaker.java
@Software: IntelliJ IDEA
*/
package com.gao.UDPVideo;

import java.io.IOException;

/**
 * 得到最后一秒（也是最后一帧）图片
 */

class VideoLastThumbTaker extends VideoThumbTaker
{
    VideoLastThumbTaker(String ffmpegApp)
    {
        super(ffmpegApp);
    }

    void getThumb(String videoFilename) throws IOException, InterruptedException
    {
        VideoInfo videoInfo = new VideoInfo(ffmpegApp);
        videoInfo.getInfo(videoFilename);
        System.out.println(videoInfo.getHours());
        super.getThumb(videoFilename, "last.png", 1080, 720,
                0, 0,
                15);
    }
}

