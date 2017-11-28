package com.example.qjb.yinnao

import java.net.*
import java.util.Arrays



/**
 * Created by qinjianbo on 17-11-27.
 */

class UDP() {
    private val ds = DatagramSocket()
    private val remoteIp = "192.168.0.133"
    fun send(args: FloatArray) {
        val buffer = Arrays.toString(args).toByteArray()
        val dp = DatagramPacket(buffer,buffer.size, InetAddress.getByName(remoteIp), 9999)
        ds.send(dp)
    }
}
