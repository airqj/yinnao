package com.example.qjb.yinnao

import java.net.*
import java.util.Arrays



/**
 * Created by qinjianbo on 17-11-27.
 */

class UDP() {
    private val ds = DatagramSocket()
//    private val localIp = InetAddress.getLocalHost().getHostAddress()
    private val remoteIp = "192.168.0.133"
    fun send(args: FloatArray) {
        //DatagramSocket类表示用来发送和接收(udp)数据报包的套接字。

        //获取本机ip地址

        //需要发送的数据
        val buffer = Arrays.toString(args).toByteArray()
        //DatagramPacket(byte[] buf, int length, InetAddress address, int port)
        //构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
        //将数据传送到本地ip，端口为9999
        val dp = DatagramPacket(buffer,buffer.size, InetAddress.getByName(remoteIp), 9999)
        //发送数据报包
        ds.send(dp)
    }
}
