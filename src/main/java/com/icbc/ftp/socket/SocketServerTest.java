package com.icbc.ftp.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: ftp
 * @description: SocketServerTest
 * @author: bgl
 * @create: 2020-09-28 17:12
 **/
public class SocketServerTest extends Thread  {
    private final Log logger = LogFactory.getLog(SocketServerTest.class);
    @Override
    public void run() {
        InputStream inputStream = null;
		OutputStream out = null;
		byte[] retData = null;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(14784);
			logger.info("----------服务器开始------------");
			while (true) {
				// 监听开始
				logger.info("----------监听开始开始------------");
				Socket socket = null;
				try {
					socket = serverSocket.accept();
					inputStream = socket.getInputStream();
					logger.info("----------读取数据------------");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    BufferedInputStream bufferIn = new BufferedInputStream(inputStream);
                    byte[] buf = new byte[1024];

                    int len;
                    //先读取10位报文头
                    byte[] lenBytes = new byte[10];
                    bufferIn.read(lenBytes);
                    //获取数据长度
//                    Integer dataLen = Integer.valueOf(new String(lenBytes).substring(0, 5));
//                    while ((len = bufferIn.read(buf)) != -1) {
//                        byteArrayOutputStream.write(buf, 0, len);
//                        dataLen = dataLen - len;
//                        if (dataLen <= 0) {
//                            break;
//                        }
//                    }

					len = Integer.valueOf(new String(lenBytes).substring(0, 5));;
					int tInt = 0;
					int has = 0;
					System.out.println("len:" + len);
					while (has < len) {
						if ((tInt = bufferIn.read(buf)) == -1) {
							break;
						}
						has = has + tInt;
						System.out.println("has:" + has + "--t:" + tInt);
						byte[] h = new byte[tInt];
						System.arraycopy(buf, 0, h, 0, tInt);
						byteArrayOutputStream.write(h);
					}

					byteArrayOutputStream.flush();
					byteArrayOutputStream.toByteArray();
                    String msg =  new String(byteArrayOutputStream.toByteArray(), "UTF-8");
					logger.info("----------读取到的数据------------" + msg);

					String result = "0036000000<?xml version=\"1.0\" encoding=\"GBK\"?>\n" +
                            "<root>\n" +
                            "<Head>\n" +
                            "<OpName>2000</OpName>\n" +
                            "<OpRetCode>00</OpRetCode>\n" +
                            "<OpRetMsg>交易成功:A2006341935</OpRetMsg>\n" +
                            "</Head>\n" +
                            "<Param>\n" +
                            "<Balance>0085993114969</Balance>\n" +
                            "<Usable_Balance>0085993114969</Usable_Balance>\n" +
                            "<Reserved1>0085993114969</Reserved1>\n" +
                            "<Reserved2>0000000000000</Reserved2>\n" +
                            "<Reserved3></Reserved3>\n" +
                            "<Reserved4></Reserved4>\n" +
                            "</Param>\n" +
                            "</root>";
					retData = result.getBytes();
					out = socket.getOutputStream();
					logger.info("----------返回数据------------");
					out.write(retData);
					out.flush();
					inputStream.close();
					out.close();
					socket.close();
				} catch (Exception e) {
					logger.error("----------接收数据失败------------");
				} finally {
					if (socket != null) {
						socket.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
