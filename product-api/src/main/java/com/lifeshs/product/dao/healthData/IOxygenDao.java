package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.OxygenPO;

import java.util.Date;
import java.util.List;

/**
 * 血氧仪
 */
@Repository
@Mapper
public interface IOxygenDao {
    int deleteByPrimaryKey(Integer id);

    int insert(OxygenPO record);

    int insertSelective(OxygenPO record);

    OxygenPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OxygenPO record);

    int updateByPrimaryKey(OxygenPO record);

    List<OxygenPO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    OxygenPO oxygenDate(@Param("userId")Integer userId, @Param("date") String date);
}