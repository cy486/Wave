package com.gao.UDPVideo;/*
@Time    :2019/5/9 0009 上午 8:15
@Author  :喜欢二福的沧月君（necydcy@gmail.com）
@FileName: UDPEchoClientTimeout.java
@Software: IntelliJ IDEA
*/
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClientTimeout {
    /*
    * Upd发送报文的客户端
    * */
    private static final int TIMEOUT = 3000;
    private static final int MAXTRIES = 5;

    public static void sendmessage(String serverIp) throws IOException {
//        if ((args.length < 2)||(args.length>3)){
//            throw new IllegalArgumentException("Parameter:<Server><Word>[<Port>]");
//        }
        InetAddress serverAddress = InetAddress.getByName(serverIp);//服务器地址
        System.out.println(serverAddress);
        byte[] bytesToSend = "无变化,平安无事".getBytes();//数据
        int servPort = 50000;//端口号
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);//接收的最长时间
        DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length,serverAddress,servPort);//要发送的报文
        DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length],bytesToSend.length);//接收信息的data
        int tries = 0;
        boolean receivedResponse = false;
        do {
            socket.send(sendPacket);
            try {
                socket.receive(receivePacket);
                if (!receivePacket.getAddress().equals(serverAddress)){
                    throw new IOException("Received packet from an unknow source");
                }
                receivedResponse = true;
            }catch (InterruptedIOException e){
                tries+=1;
                System.out.println("Time out,"+(MAXTRIES-tries)+",more tries");
            }
        }while ((!receivedResponse)&&(tries<MAXTRIES));
        if (receivedResponse){
            System.out.println("Received:"+ new String(receivePacket.getData()));
        }else {
            System.out.println("No response -- give up.");
        }
        socket.close();
    }
}

