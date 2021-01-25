package com.icbc.ftp.schedule;

import com.icbc.ftp.dao.FtpConfigDao;
import com.icbc.ftp.entity.FtpConfig;
import com.icbc.ftp.enums.BankCodeEnum;
import com.icbc.ftp.util.Sftp;
import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @program: ftp
 * @description: FtpDownTask
 * @author: bgl
 * @create: 2020-09-28 14:52
 **/
@Component
public class FtpDownTask {
    private static final Log logger = LogFactory.getLog(FtpDownTask.class);

    @Value("${download.path}")
    private String localPath;

    @Resource
    private FtpConfigDao ftpConfigDao;

    /**
     * @Description: 工商银行
     * @Param:
     * @return:
     * @Exception":
     * @Author: bgl
     * @Date: 16:48
     */
    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void downLoadGHDetail() {
        String ftpHost = "";
        int ftpPort = 0;
        String ftpUser = "";
        String priKeyPath = "";
        String remotePath = "";
        try {
            //获取昨天日期
            String lastDay = LocalDateTime.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            logger.info("昨天日期[" + lastDay + "]");
            List<FtpConfig> list = ftpConfigDao.queryByBankCode(BankCodeEnum.ICBC.getCode());
            if (list.size() == 0) {
                logger.info("ftp参数表无数据");
                return;
            }
            for (FtpConfig ftpConfig : list) {
                ftpHost = ftpConfig.getFtpHost();
                ftpPort = Integer.parseInt(ftpConfig.getFtpPort());
                ftpUser = ftpConfig.getFtpUser();
                priKeyPath = ftpConfig.getPriKeyPath();
                remotePath = ftpConfig.getRemotePath();
                logger.info("ftpHost[" + ftpHost + "]ftpPort[" + ftpPort + "]ftpUser[" + ftpUser + "]priKeyPath[" + priKeyPath + "]remotePath[" + remotePath + "]localPath[" + localPath + "]");


                Sftp sftpClient = new Sftp(ftpUser, "", ftpPort, ftpHost, priKeyPath, "");
                ChannelSftp channelSftp = sftpClient.priKeyConnect();
                try {
                    logger.info("sftp连接成功");
                    if (sftpClient.isExist(remotePath, channelSftp)) {
                        Vector<ChannelSftp.LsEntry> fileNameList = channelSftp.ls(remotePath);
                        logger.info("文件列表" + fileNameList);
                        if (fileNameList.size() > 0) {
                            //下载文件
                            String fileName = "";
                            for (ChannelSftp.LsEntry f : fileNameList) {
                                fileName = f.getFilename();
                                if (fileName.contains(lastDay) && fileName.endsWith(".zip")) {
                                    File dir = new File(localPath + File.separator + lastDay);
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }
                                    sftpClient.download(remotePath, fileName, localPath + "\\" + lastDay + "\\" + fileName, channelSftp);
                                }
                            }
                            logger.info("下载工行电子回单完成");
                        } else {
                            logger.info("文件列表为空");
                        }
                    } else {
                        logger.info("远程文件夹不存在");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new Exception(e.getMessage());
                } finally {
                    //关闭连接
                    sftpClient.disconnected(channelSftp);
                }

            }

        } catch (Exception e) {
            logger.error("ftp下载失败[" + e.getMessage() + "]");
        }
    }

    /**
     * @Description: 邮储
     * @Param:
     * @return:
     * @Exception":
     * @Author: bgl
     * @Date: 17:05
     */
    //@PostConstruct
    //@Scheduled(cron = "0 0 7 * * ? ")
    public void downLoadYCDetail() {
        String ftpHost;
        int ftpPort;
        String ftpUser;
        String priKeyPath;
        String remotePath;
        String accNo;
        try {
            //获取昨天日期
            String lastDay = LocalDateTime.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            logger.info("昨天日期[" + lastDay + "]");
            List<FtpConfig> list = ftpConfigDao.queryByBankCode(BankCodeEnum.PSBC.getCode());
            if (list.size() == 0) {
                logger.info("ftp参数表无数据");
                return;
            }
            for (FtpConfig ftpConfig : list) {
                ftpHost = ftpConfig.getFtpHost();
                ftpPort = Integer.parseInt(ftpConfig.getFtpPort());
                ftpUser = ftpConfig.getFtpUser();
                priKeyPath = ftpConfig.getPriKeyPath();
                remotePath = ftpConfig.getRemotePath();
                accNo = ftpConfig.getAccNo();
                logger.info("ftpHost[" + ftpHost + "]ftpPort[" + ftpPort + "]ftpUser[" + ftpUser + "]priKeyPath[" + priKeyPath + "]remotePath[" + remotePath + "]localPath[" + localPath + "]");

                //邮政储蓄银行

                Sftp sftpClient = new Sftp(ftpUser, "", ftpPort, ftpHost, priKeyPath, "");
                ChannelSftp channelSftp = sftpClient.priKeyConnect();
                try {
                    logger.info("ftp连接成功");
                    if (sftpClient.isExist(remotePath, channelSftp)) {
                        String fName = accNo + lastDay + ".zip";
                        Vector<ChannelSftp.LsEntry> fileNameList = channelSftp.ls(remotePath);
                        if (fileNameList.size() > 0) {
                            //下载文件
                            String fileName = "";
                            for (ChannelSftp.LsEntry f : fileNameList) {
                                fileName = f.getFilename();
                                if (fileName.equals(fName)) {
                                    File dir = new File(localPath + File.separator + lastDay);
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }
                                    sftpClient.download(remotePath, fileName, localPath + File.separator + lastDay + File.separator + fileName, channelSftp);
                                }
                            }
                            logger.info("下载邮储电子回单完成");
                        } else {
                            logger.info("文件不存在");
                        }
                    } else {
                        logger.info("远程文件夹不存在");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new Exception(e.getMessage());
                } finally {
                    //关闭连接
                    sftpClient.disconnected(channelSftp);
                }

            }
        } catch (Exception e) {
            logger.error("ftp下载失败[" + e.getMessage() + "]");
        }
    }

}
