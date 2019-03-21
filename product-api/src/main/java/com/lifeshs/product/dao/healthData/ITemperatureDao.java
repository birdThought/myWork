package com.lifeshs.product.dao.healthData;

import com.lifeshs.product.domain.vo.healthData.TemperaturePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 体温
 */
@Repository
@Mapper
public interface ITemperatureDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TemperaturePO record);

    int insertSelective(TemperaturePO record);

    TemperaturePO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TemperaturePO record);

    int updateByPrimaryKey(TemperaturePO record);

    List<TemperaturePO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);
}