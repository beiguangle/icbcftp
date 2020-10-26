package com.icbc.ftp.service.impl;

import cn.hutool.core.codec.Base64;
import com.icbc.ftp.dao.FtpConfigDao;
import com.icbc.ftp.entity.FtpConfig;
import com.icbc.ftp.service.FtpConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (FtpConfig)表服务实现类
 *
 * @author Bgl
 * @since 2020-09-28 10:22:19
 */
@Service("ftpConfigService")
public class FtpConfigServiceImpl implements FtpConfigService {
    @Resource
    private FtpConfigDao ftpConfigDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public FtpConfig queryById(Integer id) {
        return this.ftpConfigDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param -pageNumber 查询起始位置
     * @param pageSize  查询条数
     * @return 对象列表
     */
    @Override
    public Map<String, Object> queryAllByLimit(int pageNumber, int pageSize) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<FtpConfig> list = ftpConfigDao.queryAllByLimit(pageNumber,pageSize);
        map.put("total", ftpConfigDao.queryAll().size());
        map.put("rows", list);
        return map;
    }

    /**
     * 新增数据
     *
     * @param ftpConfig 实例对象
     * @return 实例对象
     */
    @Override
    public int insert(FtpConfig ftpConfig) {
        return this.ftpConfigDao.insert(ftpConfig);
    }

    /**
     * 修改数据
     *
     * @param ftpConfig 实例对象
     * @return 实例对象
     */
    @Override
    public int update(FtpConfig ftpConfig) {

        return this.ftpConfigDao.update(ftpConfig);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(Long id) {
        return this.ftpConfigDao.deleteById(id);
    }
}
