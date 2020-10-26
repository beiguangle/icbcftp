package com.icbc.ftp.util;

import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @program: ftp
 * @description: SocketClient
 * @author: bgl
 * @create: 2020-10-22 15:14
 **/
public class SocketClient {
    private static String strCharSet;

    private static String bankcode = "";

    private Socket client;

    private PrintWriter out;

    private BufferedInputStream bis;

    public static byte[] ReadStreamByte(InputStream inStream) throws Exception {
        byte[] strRet = new byte[1024];
        int retNum = -1;
        int totalrc = 0;
        int pakLenght = 0;
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            while ((retNum = inStream.read(strRet)) != -1) {
                outStream.write(strRet, 0, retNum);
                if (bankcode.equals("xingye")) {
                    if ((new String(outStream.toByteArray(), strCharSet))
                            .endsWith("</FOX>") || (
                            new String(outStream.toByteArray(), strCharSet))
                            .endsWith("</ERROR>") || (
                            new String(outStream.toByteArray(), strCharSet))
                            .indexOf("Error:") >= 0) {
                        break;
                    }
                    continue;
                }
                if (bankcode.equals("shyh")) {
                    if ((new String(outStream.toByteArray(), strCharSet))
                            .endsWith("</BOSEBankData>") || (
                            new String(outStream.toByteArray(), strCharSet))
                            .endsWith("</ERROR>") || (
                            new String(outStream.toByteArray(), strCharSet))
                            .indexOf("Error:") >= 0) {
                        break;
                    }
                    continue;
                }
                if (bankcode.equals("gzrcb")) {
                    if (pakLenght == 0) {
                        pakLenght = getPakLen(strRet, 6);
                    }
                    totalrc += retNum;
                    if (totalrc >= pakLenght) {
                        break;
                    }
                    continue;
                }
                if (bankcode.equals("cme")) {
                    if ((new String(outStream.toByteArray(), strCharSet))
                            .endsWith("</ap>")) {
                        break;
                    }
                    continue;
                }
                if (bankcode.equals("gxnxs")) {
                    if (pakLenght == 0) {
                        pakLenght = getPakLen(strRet, 8);
                    }
                    totalrc += retNum;
                    if (totalrc >= pakLenght + 8) {
                        break;
                    }
                }
            }
            byte[] outByt = outStream.toByteArray();
            return outByt;
        } finally {
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    private static int getPakLen(byte[] buff, int num) {
        byte[] pakLenByte = new byte[num];
        for (int i = 0; i < num; i++)
            pakLenByte[i] = buff[i];
        return Integer.valueOf(new String(pakLenByte)).intValue();
    }

    public SocketClient() {
        strCharSet = "GBK";
    }

    public SocketClient(String charSet) {
        strCharSet = charSet;
    }

    public String Submit(String Host, String Port, String strSend) throws Exception {
        int iPort = Integer.parseInt(Port);
        String strRetData = "";
        try {
            this.client = new Socket(Host, iPort);
            if (!this.client.isConnected()) {
                throw new Exception("+ Host + " + iPort );
            }
            this.client.setSoTimeout(6000);
            System.out.println("soTimeout=" + 6000);
            this.bis = new BufferedInputStream(this.client.getInputStream());
            this.out = new PrintWriter(this.client.getOutputStream(), true);
            this.out.write(strSend);
            this.out.flush();
            byte[] retByt = ReadStreamByte(this.bis);
            strRetData = new String(retByt, strCharSet);
            CloseAll();
        } catch (UnknownHostException e) {
            CloseAll();
            throw new Exception("+ Host + " + iPort + " + e.toString()]");
        } catch (ConnectException e) {
            CloseAll();
            throw new Exception("+ Host + " + iPort + " + e.toString()]");
        } catch (NoRouteToHostException e) {
            CloseAll();
            throw new Exception("+ Host + " + iPort + " + e.toString()]");
        } catch (InterruptedIOException e) {
            CloseAll();
            throw new Exception("+ Host + " + iPort + " + e.toString()]");
        } catch (Exception e) {
            CloseAll();
            throw new Exception("+ Host + " + iPort + " + e.toString()]");
        } finally {
            CloseAll();
        }
        if (strRetData != null && !strRetData.equals("")) {
            return strRetData;
        }
        throw new Exception("+ Host + " + iPort );
    }

    private void CloseAll() {
        try {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
            if (this.bis != null) {
                this.bis.close();
                this.bis = null;
            }
            if (this.client != null) {
                this.client.close();
                this.client = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStrCharSet() {
        return strCharSet;
    }

    public void setStrCharSet(String strCharSet) {
        SocketClient.strCharSet = strCharSet;
    }

    public void setBank(String bank) {
        bankcode = bank;
    }
}
