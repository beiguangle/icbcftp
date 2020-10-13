package com.icbc.ftp.dao;

import com.icbc.ftp.entity.FtpConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (FtpConfig)表数据库访问层
 *
 * @author Bgl
 * @since 2020-09-28 10:22:19
 */
@Mapper
public interface FtpConfigDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FtpConfig queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<FtpConfig> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param -ftpConfig- 实例对象
     * @return 对象列表
     */
    List<FtpConfig> queryAll();

    /**
     * 新增数据
     *
     * @param ftpConfig 实例对象
     * @return 影响行数
     */
    int insert(FtpConfig ftpConfig);

    /**
     * 修改数据
     *
     * @param ftpConfig 实例对象
     * @return 影响行数
     */
    int update(FtpConfig ftpConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}
