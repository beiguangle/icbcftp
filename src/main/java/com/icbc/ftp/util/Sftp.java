package com.icbc.ftp.util;

import com.jcraft.jsch.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @program: ftp
 * @description: Sftp
 * @author: bgl
 * @create: 2020-11-03 14:01
 **/
public class Sftp {
    private final static Log logger = LogFactory.getLog(Sftp.class);
    private String userName;
    private String password;
    private int port;
    private String hostName;
    private String priKeyFile;
    private String passphrase;

    Session session = null;
    Channel channel = null;

    /**
     * @param userName
     * @param password
     * @param port
     * @param hostName
     */
    public Sftp(String userName, String password, int port, String hostName) {
        super();
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.hostName = hostName;
    }

    /**
     * @param userName
     * @param password
     * @param port
     * @param hostName
     * @param priKeyFile
     * @param passphrase
     */
    public Sftp(String userName, String password, int port, String hostName,
                String priKeyFile, String passphrase) {
        super();
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.hostName = hostName;
        this.priKeyFile = priKeyFile;
        this.passphrase = passphrase;
    }

    public ChannelSftp connect() {

        JSch jsch = new JSch();
        try {
            if (port > 0) {
                session = jsch.getSession(userName, hostName, port);
            } else {
                session = jsch.getSession(userName, hostName);
            }
            if (session == null) {
                return null;
                //throw new Exception("session is null");
            }
            //设置登陆主机的密码
            session.setPassword(password);//设置密码
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.setTimeout(20000);
            session.connect();
            //创建sftp通信通道
            channel = (Channel) session.openChannel("sftp");
            channel.connect();
            return (ChannelSftp) channel;
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    /**
     * 密钥文件连接
     *
     * @return
     */

    public ChannelSftp priKeyConnect() {
        JSch jsch = new JSch();
        try {
            if (priKeyFile != null && !"".equals(priKeyFile)) {
                if (passphrase != null && !"".equals(passphrase)) {
                    jsch.addIdentity(priKeyFile, passphrase);
                } else {
                    jsch.addIdentity(priKeyFile);
                }
            }
            if (port > 0) {
                session = jsch.getSession(userName, hostName, port);
            } else {
                session = jsch.getSession(userName, hostName);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            //session.setTimeout(20000);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            return (ChannelSftp) channel;
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param is   要上传的本地文件流
     * @param dsc  目标目录或文件，若是目录则上传的目标文件名和本地文件名一样
     * @param sftp
     * @return
     */
    public boolean upload(InputStream is, String dsc, ChannelSftp sftp) {
        try {
            sftp.put(is, dsc);
            return true;
        } catch (SftpException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }

    /**
     * @param remotedir  要上传到的目录
     * @param uploadFile 上传的文件
     * @param sftp
     * @return
     */
    public boolean upload(String remotedir, String uploadFile, ChannelSftp sftp) {
        try {
            sftp.cd(remotedir);
            File file = new File(uploadFile);
            sftp.put(new FileInputStream(file), file.getName());
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return false;
        }
    }


    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     * @param sftp
     */
    public void download(String directory, String downloadFile,
                         String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.get(downloadFile, saveFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param sftp
     */
    public void delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnected(ChannelSftp sftp) {
        if (sftp != null) {
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
            sftp.disconnect();
        }
    }

    /**
     * 判断路径是否存在
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public boolean isExist(String remotePath, ChannelSftp sftp) throws SftpException {
        boolean flag = false;
        try {
            sftp.cd(remotePath);
            logger.info("存在路径：" + remotePath);
            flag = true;
        } catch (SftpException sException) {

        } catch (Exception Exception) {
        }
        return flag;
    }
}
