package com.icbc.ftp.schedule;


import cn.hutool.extra.ftp.Ftp;
import com.icbc.ftp.dao.FtpConfigDao;
import com.icbc.ftp.entity.FtpConfig;
import com.icbc.ftp.enums.BankCodeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @Scheduled(cron = "0/5 * * * * ? ")
    public void downLoadDetail() {
        String ftpHost = "";
        int ftpPort = 0;
        String ftpUser = "";
        String ftpPass = "";
        String remotePath = "";
        String accNo = "";
        String bankCode = "";
        try {
            //获取昨天日期
            String lastDay = LocalDateTime.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            logger.info("昨天日期[" + lastDay + "]");
            List<FtpConfig> list = ftpConfigDao.queryAll();
            if (list.size() == 0) {
                logger.info("ftp参数表无数据");
                return;
            }
            for (FtpConfig ftpConfig : list) {
                ftpHost = ftpConfig.getFtpHost();
                ftpPort = Integer.parseInt(ftpConfig.getFtpPort());
                ftpUser = ftpConfig.getFtpUser();
                ftpPass = ftpConfig.getFtpPass();
                remotePath = ftpConfig.getRemotePath();
                accNo = ftpConfig.getAccNo();
                bankCode = ftpConfig.getBankCode();
                logger.info("ftpHost[" + ftpHost + "]ftpPort[" + ftpPort + "]ftpUser[" + ftpUser + "]ftpPass[" + ftpPass + "]remotePath[" + remotePath + "]localPath[" + localPath + "]");
                //工商银行
                if (bankCode.equals(BankCodeEnum.ICBC.getCode())) {
                    Ftp ftpClient = null;
                    try {
                        ftpClient = new Ftp(ftpHost, ftpPort, ftpUser, ftpPass);
                        ftpClient.init();
                        logger.info("ftp初始化完成");
                        if (ftpClient.existFile(remotePath)) {
                            List<String> fileNameList = ftpClient.ls(remotePath).stream().filter(m -> m.contains(lastDay)).filter(m -> m.endsWith(".zip")).collect(Collectors.toList());
                            logger.info("文件列表" + fileNameList);
                            if (fileNameList.size() > 0) {
                                //下载文件
                                for (String fileName : fileNameList) {
                                    File file = new File(localPath + File.separator + lastDay + File.separator + fileName);
                                    ftpClient.download(remotePath, fileName, file);
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
                        if (ftpClient != null) {
                            ftpClient.close();
                        }
                    }

                }
                //邮政储蓄银行
                if (bankCode.equals(BankCodeEnum.PSBC.getCode())) {
                    Ftp ftpClient = null;
                    try {
                        ftpClient = new Ftp(ftpHost, ftpPort, ftpUser, ftpPass);
                        ftpClient.init();
                        logger.info("ftp初始化完成");
                        if (ftpClient.existFile(remotePath)) {
                            String fName = accNo + lastDay + ".zip";
                            List<String> fileNameList = ftpClient.ls(remotePath).stream().filter(fName::equals).collect(Collectors.toList());
                            logger.info("文件列表" + fileNameList);
                            if (fileNameList.size() > 0) {
                                //下载文件
                                for (String fileName : fileNameList) {
                                    File file = new File(localPath + File.separator + lastDay + File.separator + fileName);
                                    ftpClient.download(remotePath, fileName, file);
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
                        if (ftpClient != null) {
                            ftpClient.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("ftp下载失败[" + e.getMessage() + "]");
        }
    }

    public static void main(String[] args) {
        Ftp ftpClient = new Ftp("192.168.3.56", 21, "ftpuser", "zxc45018");
        ftpClient.init();

        ftpClient.download("/20201012","2345haozip_v6.2.0.11032.exe", new File("H:\\qq\\1.exe"));
    }
}
