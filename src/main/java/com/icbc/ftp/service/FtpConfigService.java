package com.icbc.ftp.service;

import com.icbc.ftp.entity.FtpConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * (FtpConfig)表服务接口
 *
 * @author Bgl
 * @since 2020-09-28 10:22:19
 */
@Component
public interface FtpConfigService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FtpConfig queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    Map<String, Object> queryAllByLimit(int offset, int limit);


    /**
     * 新增数据
     *
     * @param ftpConfig 实例对象
     * @return 实例对象
     */
    int insert(FtpConfig ftpConfig);

    /**
     * 修改数据
     *
     * @param ftpConfig 实例对象
     * @return 实例对象
     */
    int update(FtpConfig ftpConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Long id);

}
