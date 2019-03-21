package com.lifeshs.product.dao.healthData;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.dto.healthData.EcgDTO;
import com.lifeshs.product.domain.dto.healthData.EcgDetailDTO;

/**
 * 心电
 */
@Repository
@Mapper
public interface IEcgDao {
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(EcgPO record);
//
//    int insertSelective(EcgPO record);
//
//    EcgPO selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(EcgPO record);
//
//    int updateByPrimaryKey(EcgPO record);
//
    /**
     *  获取用户指定月份的测量记录日期与status值
     *  @author yuhang.weng 
     *  @DateTime 2017年11月29日 下午4:26:39
     *
     *  @param userId 用户id
     *  @param queryDate 指定月份（需要是一个月的第一天）
     *  @return
     */
    List<EcgDetailDTO> selectMeasureDatesByUserId(@Param("userId") int userId, @Param("queryDate") Date queryDate);

    /**
     * 查询用户最近一次测量心电的日期
     *
     * @param userId 用户id
     * @param date 日期
     * @return
     */
    Date getLastDateByUserId(@Param("userId") Integer userId);

    /**
     * 根据指定日期获取指定用户的数据
     * 只获取这一天的最后一条数据
     *
     * @param userId 用户id
     * @param date 日期
     * @return
     */
    EcgDTO selectByUserIdAndDate(@Param("userId") int userId, @Param("date") Date date);

    /**
     *  获取最后一条数据
     * @param userId
     * @return
     */
    EcgDTO getLastestData(@Param("userId") int userId);
}