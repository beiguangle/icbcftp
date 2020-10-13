package com.icbc.ftp.controller;

import cn.hutool.core.util.ZipUtil;
import com.icbc.ftp.entity.FtpConfig;
import com.icbc.ftp.entity.PeyfiontResult;
import com.icbc.ftp.service.FtpConfigService;
import com.icbc.ftp.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Objects;

/**
 * (FtpConfig)表控制层
 *
 * @author Bgl
 * @since 2020-09-28 10:22:23
 */
@Controller
public class FtpConfigController {
    @Resource
    private FtpConfigService ftpConfigService;
    @Value("${download.path}")
    private String loaclPath;

    @RequestMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping("/ftpconfig")
    public String ftpconfigHtml() {
        return "ftpconfig";
    }

    @ResponseBody
    @RequestMapping("/ftpconfig/list")
    public Map<String, Object> ftpconfigList(@RequestParam(value = "pageNumber") Integer pageNumber,
                                             @RequestParam(value = "pageSize") Integer pageSize) {
        // 查询列表数据
        return ftpConfigService.queryAllByLimit(pageNumber, pageSize);
    }

    @ResponseBody
    @PostMapping("/ftpconfig/add")
    public PeyfiontResult ftpconfigAdd(FtpConfig ftpconfig) {
        if (ftpConfigService.insert(ftpconfig) > 0) {
            return PeyfiontResult.ok();
        }
        return PeyfiontResult.build(403, "新增配置失败！");
    }

    @ResponseBody
    @PostMapping("/ftpconfig/edit")
    public PeyfiontResult ftpconfigEdit(FtpConfig ftpconfig) {
        if (ftpConfigService.update(ftpconfig) > 0) {
            return PeyfiontResult.ok();
        }
        return PeyfiontResult.build(403, "修改配置失败！");
    }

    @ResponseBody
    @PostMapping("/ftpconfig/del")
    public PeyfiontResult ftpconfigDel(@RequestParam("id") Long id) {
        if (ftpConfigService.deleteById(id) > 0) {
            return PeyfiontResult.ok();
        }
        return PeyfiontResult.build(403, "删除配置失败！");
    }

    /**
     * 判断文件是否存在
     */
    @RequestMapping("/isFileExists")
    @ResponseBody
    public PeyfiontResult tempFileFlag(@RequestParam(value = "tranDate") String tranDate) {
        File file = new File(loaclPath +File.separator + tranDate);
        System.out.println("读取文件路径[" + file.getAbsolutePath() +"]");
        if (file.exists() && Objects.requireNonNull(file.listFiles()).length > 0) {
            return PeyfiontResult.ok();
        } else {
            return PeyfiontResult.build(999, "所选日期无文件可下载！");
        }
    }
    /**
     * 文件下载
     */
    @RequestMapping("/fileDownload")
    @ResponseBody
    public void tempFileDown(@RequestParam(value = "tranDate") String tranDate, HttpServletResponse response) throws UnsupportedEncodingException {
        System.out.println("下载日期时间[" + tranDate + "]");
        String filePath = loaclPath + File.separator + tranDate;
        File file = ZipUtil.zip(filePath);
        FileUtil.downLoad(response,file,file.getName());
    }
}

