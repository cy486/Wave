# Wave
【大三下学期】中国第八届软件杯赛题-基于物联网视频传输系统现场决赛三等奖
# 1.作品简介： 
  此软件是基于中国第八届软件杯赛题--基于物联网的视频系统的实现而开发的系统。
  
  根据赛题内容：视频监控系统在各行各业得到广泛的应用。但是由于视频对网络带宽要求比较高，限制了视频系统在某些网络条件苛刻环境的应用。
  
  物联网采用的网络媒体，一般具有带宽窄，网络连接不安定的特点。很难满足通用视频监控系统对网络的要求。本课题的目标是实现物联网环境下的视频监控解决方案，面向监控对象变化缓慢，降低监控视频的帧率对不会影响监控效果的应用场景；同时满足当有异常事物闯入造成监控画面短时剧烈变化时提高视频帧数以达到监控目的应用场景。
  
  根据赛题要求和以上场景限制，我们小组把系统分成三个模块进行设计和编写，分别为树莓派端视频处理，UDP传输协议编写，windows服务器端展示。
树莓派处理端可以设置服务器ip，客户端ip，rtsp视频流地址，监控灵敏度阀值。

  Windows视频处理端可以设置服务器ip，客户端ip，发生异常时接收邮件的邮箱。
  
  树莓派视频处理模块：根据赛题要求和我们的理解讨论，我们决定根据一下步骤完成。第一步，进行萤石摄像头的rtsp流视频获取，并截取30秒的视频，储存在本地。第二步，对比此视频的第一帧和最后一帧的图片的相似度，对比图片采用汉明距离的比较的方法，把图像进行抽取灰度图，并缩放，计算两张图片的汉明距离[1]，从而计算出相似度。第三步，判断发送与否，如果相似度小于阀值，则发送此段视频，如果相似度大于阀值则不发送，继续进行下30秒的视频的监控。
  
  UDP传输协议编写：采用java语言编写，所以使用java的网络编程接口。使用UDP方式传输，所以使用DatagramSocket进行数据传输，由于UDP只能发送64k数据，所以我们将视频数据进行分割，读入后分段进行传输，视频流传输完毕后，发送结束符号，服务器端接收最后一组数据后，传回结束符号，从而结束此次传输。
  
   windows服务器端展示：由于此项目基于具体项目。所以我们对此模块进行了大程度的编写和优化，加入有异常时发出警报提示和邮件提醒的功能。此模块具体细分为两个具体的小模块，即视频实时展示界面模块和server端视频接收模块。
   
  实时展示界面模块，由于采用的是萤石网络摄像头，所以我们此模块运用萤石开放平台提供的视频接口来进行视频的实时监控，并把此前的监控到的视频的变化率生成数据文档，进行波形图展示。并展示异常列表和监控的场景。提供监控场景的自定义设置。并运用nodejs技术，实现轻量级的监控web端展示。
  
  Server端视频接收模块，采用与树莓派端对应的UDP协议，进行传输来的视频的解析和保存，并在视频接收时，发出警报提示，接收完成后向对应邮箱的邮件提醒。异常文件名称为异常的日期和时间。
  
[1] 在信息论中，两个等长字符串之间的汉明距离（英语：Hamming distance）是两个字符串对应位置的不同字符的个数。换句话说，它就是将一个字符串变换成另外一个字符串所需要替换的字符个数。

汉明重量是字符串相对于同样长度的零字符串的汉明距离，也就是说，它是字符串中非零的元素个数：对于二进制字符串来说，就是1的个数，所以11101的汉明重量是4。

# 二 详细设计文档：
## 2.1 用例图

![用例图.png](https://i.loli.net/2019/08/01/5d4245acefa3750524.png)

## 2.2 时序图：
 

## 2.3 类图：
 
图 3 系统类图

## 2.4 状态图：
 
图 4 系统状态图

# 三 UDP自定义应用层协议说明：

## 3.1 说明

采用UDP单播模式，进行树莓派端和服务器端的通信。

Java语言，客户端和客户机之间通DatagramSocket对象来相互发送数据。

DatagramSocket对象提供的发送报文的Send方法是依照UDP协议，根据储存在数据报文里的目的主机的IP地址和端口号，直接向目的主机发送数据报文。树莓派端传报文中包含视频数据和报文的总长度。

## 3.2 示意图
 
图 5 示意图

# 四 依赖的外部软件包说明：
## 4.1 ffmpeg：对摄像头的视频进行接受和抽帧处理

FFmpeg是一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序。采用LGPL或GPL许可证。它提供了录制、转换以及流化音视频的完整解决方案。它包含了非常先进的音频/视频编解码库libavcodec，为了保证高可移植性和编解码质量，libavcodec里很多code都是从头开发的。
FFmpeg在Linux平台下开发，但它同样也可以在其它操作系统环境中编译运行，包括Windows、Mac OS X等。这个项目最早由Fabrice Bellard发起，2004年至2015年间由Michael Niedermayer主要负责维护。许多FFmpeg的开发人员都来自MPlayer项目，而且当前FFmpeg也是放在MPlayer项目组的服务器上。项目的名称来自MPEG视频编码标准，前面的"FF"代表"Fast。
。
## 4.2 nodejs：提供服务器端视频播放服务支持依赖。

Node.js 是一个基于 Chrome V8 引擎的 JavaScript 运行环境。 Node.js 使用了一个事件驱动、非阻塞式 I/O 的模型。

Node 是一个让 JavaScript 运行在服务端的开发平台，它让 JavaScript 成为与PHP、Python、Perl、Ruby 等服务端语言平起平坐的脚本语言。发布于2009年5月，由Ryan Dahl开发，实质是对Chrome V8引擎进行了封装。

Node对一些特殊用例进行优化，提供替代的API，使得V8在非浏览器环境下运行得更好。V8引擎执行Javascript的速度非常快，性能非常好。 Node是一个基于Chrome JavaScript运行时建立的平台， 用于方便地搭建响应速度快、易于扩展的网络应用。Node 使用事件驱动， 非阻塞I/O 模型而得以轻量和高效，非常适合在分布式设备上运行数据密集型的实时应用。
# 五 安装配置说明：
### 5.1 树莓派端：

1. 需要安装java环境

2. 以及ffmpeg。

ffmpeg的安装如下：

sudo apt-get update
sudo add-apt-repository ppa:djcj/hybrid  
sudo apt-get update  
sudo apt-get install ffmpeg  

3. 修改ip.properties文件，写入服务器和客户端ip地址以及摄像头的rtsp流的地址。

4. 在程序目录下执行java -jar UdpDemo.jar 运行程序

## 5.2 服务器端：

1. 需要java环境。

2. 需要nodejs环境。

3. 修改ip. properties的客户端和服务器端的ip和接收短信的邮箱

4. 点击 startserver.bat 启动服务器

5. 点击start.bat运行监控端，进行视频监控和数据展示



