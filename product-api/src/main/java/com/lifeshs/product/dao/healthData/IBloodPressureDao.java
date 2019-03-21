package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.BloodPressurePO;

import java.util.Date;
import java.util.List;

/**
 * 血压仪
 *
 */
@Repository
@Mapper
public interface IBloodPressureDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BloodPressurePO record);

    int insertSelective(BloodPressurePO record);

    BloodPressurePO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BloodPressurePO record);

    int updateByPrimaryKey(BloodPressurePO record);

    List<BloodPressurePO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    BloodPressurePO currentPressureDate(@Param("userId")Integer userId,@Param("date") String date);
}