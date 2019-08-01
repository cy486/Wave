/*
@Time    :2019/6/5 0005 下午 3:01
@Author  :喜欢二福的沧月君（necydcy@gmail.com）
@FileName: VideoFirstThumbTaker.java
@Software: IntelliJ IDEA
*/
package com.gao.UDPVideo;

import java.io.IOException;

/***
 *
 * 得到第一秒（也是第一帧）图片
 */
class VideoFirstThumbTaker extends VideoThumbTaker
{
    VideoFirstThumbTaker(String ffmpegApp)
    {
        super(ffmpegApp);
    }

    void getThumb(String videoFilename) throws IOException, InterruptedException
    {
        super.getThumb(videoFilename, "first.png", 1080, 720, 0, 0, 0);
    }
}

