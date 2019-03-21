package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.HeartRatePO;

import java.util.*;

/**
 * 心律(手环,hl)
 */
@Repository
@Mapper
public interface IHeartRateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(HeartRatePO record);

    int insertSelective(HeartRatePO record);

    HeartRatePO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HeartRatePO record);

    int updateByPrimaryKey(HeartRatePO record);

    List<HeartRatePO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    HeartRatePO heartrateDate(@Param("userId") Integer userId,@Param("date")String date);

}