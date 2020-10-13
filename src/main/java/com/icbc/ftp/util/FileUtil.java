package com.icbc.ftp.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @program: ftp
 * @description: FileUtil
 * @author: bgl
 * @create: 2020-09-29 14:29
 **/
public class FileUtil {
    private static final Log logger = LogFactory.getLog(FileUtil.class);
    /**
     * 文件下载
     */
    public static void downLoad(HttpServletResponse response, File file, String uploadFilename) throws UnsupportedEncodingException {
        logger.info("下载文件名[" + uploadFilename + "]");
        logger.info("下载文件地址[" + file.getAbsolutePath() + "]");
        response.setHeader("content-type","application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(uploadFilename,"utf-8"));
        byte[] buff=new byte[8192];
        BufferedInputStream bis=null;
        OutputStream os=null;
        try{
            os=response.getOutputStream();
            bis=new BufferedInputStream(new FileInputStream(file));
            int i=bis.read(buff);
            while (i!=-1){
                os.write(buff,0,i);
                os.flush();
                i=bis.read(buff);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (bis!=null){
                try {
                    bis.close();
                    os.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
