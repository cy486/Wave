package Udp;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class UDPServer {

//  private static final String SAVE_FILE_PATH = "F:\\"+new SimpleDateFormat("yyyy_MM-dd_HH_mm_ss").format(new Date())+".mp4";
    private static Properties properties;
    public static void main(String[] args) {
        InputStream in = UDPServer.class.getClassLoader().getResourceAsStream(
                "myip.properties");
        properties = new Properties();
        String email = null;
        String clientIp =null;

        String serverIp = null;
        try {
            properties.load(in);
            email = getemail();
            serverIp = getServerIp();
            clientIp = getClientIp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            File file2 = new File("F:/video");
            file2.mkdir();
            String SAVE_FILE_PATH="F:\\video\\"+new SimpleDateFormat("yyyy_MM-dd_HH_mm_ss").format(new Date())+".mp4";
            byte[] buf = new byte[UDPUtils.BUFFER_SIZE];

            DatagramPacket dpk = null;
            DatagramSocket dsk = null;
            BufferedOutputStream bos = null;
            try {

                dpk = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName(serverIp), UDPUtils.PORT));
                dsk = new DatagramSocket(UDPUtils.PORT + 1, InetAddress.getByName(clientIp));
                bos = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
                System.out.println("wait client ....");
                dsk.receive(dpk);

                int readSize = 0;
                int readCount = 0;
                int flushSize = 0;
                while ((readSize = dpk.getLength()) != 0) {
                    // validate client send exit flag
                    if (UDPUtils.isEqualsByteArray(UDPUtils.exitData, buf, readSize)) {
                        System.out.println("server exit ...");
                        // send exit flag
                        dpk.setData(UDPUtils.exitData, 0, UDPUtils.exitData.length);
                        dsk.send(dpk);
                        break;
                    }

                    bos.write(buf, 0, readSize);
                    if (++flushSize % 1000 == 0) {
                        flushSize = 0;
                        bos.flush();
                    }
                    dpk.setData(UDPUtils.successData, 0, UDPUtils.successData.length);
                    dsk.send(dpk);

                    dpk.setData(buf, 0, buf.length);
                    System.out.println("receive count of " + (++readCount) + " !");
                    dsk.receive(dpk);
                }

                // last flush
                bos.flush();
                {
                    try {
                        // 1.wav 文件放在java project 下面
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        MailUtils.sendMail(email, "时间" + df.format(new Date()) + "有人闯入");
                        FileInputStream fileau = new FileInputStream(
                                System.getProperty("user.dir") + "\\src\\sound.wav");
                        AudioStream as = new AudioStream(fileau);
                        AudioPlayer.player.start(as);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null)
                        bos.close();
                    if (dsk != null)
                        dsk.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static String getClientIp() {
        return "" + properties.get("clientip");
    }

    private static String getServerIp() {
        return "" + properties.get("serverip");
    }

    private static String getemail() {
        return "" + properties.get("email");
    }
    //
}
