package com.gao.UDPVideo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPClient {


    public static void send(File SEND_FILE_PATH, String serverIp,String clientIp){
        long startTime = System.currentTimeMillis();

        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
        byte[] receiveBuf = new byte[1];
        System.out.println();
        RandomAccessFile accessFile = null;
        DatagramPacket dpk = null;
        DatagramSocket dsk = null;
        int readSize = -1;
        try {
            accessFile = new RandomAccessFile(SEND_FILE_PATH,"r");
            dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName(clientIp), UDPUtils.PORT + 1));
            System.out.println(dpk);
            dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName(serverIp));
            int sendCount = 0;
            while((readSize = accessFile.read(buf,0,buf.length)) != -1){
                dpk.setData(buf, 0, readSize);
                dsk.send(dpk);
                System.out.println("223");
                // wait server response
                {
                    while(true){
                        dpk.setData(receiveBuf, 0, receiveBuf.length);
                        dsk.receive(dpk);

                        // confirm server receive
                        if(!UDPUtils.isEqualsByteArray(UDPUtils.successData,receiveBuf,dpk.getLength())){
                            System.out.println("resend ...");
                            dpk.setData(buf, 0, readSize);
                            dsk.send(dpk);
                        }else
                            break;
                    }
                }

                System.out.println("send count of "+(++sendCount)+"!");
            }
            // send exit wait server response
            while(true){
                System.out.println("client send exit message ....");
                dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);
                dsk.send(dpk);

                dpk.setData(receiveBuf,0,receiveBuf.length);
                dsk.receive(dpk);
                // byte[] receiveData = dpk.getData();
                if(!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())){
                    System.out.println("client Resend exit message ....");
                    dsk.send(dpk);
                }else
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(accessFile != null)
                    accessFile.close();
                if(dsk != null)
                    dsk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("time:"+(endTime - startTime));
    }
}
