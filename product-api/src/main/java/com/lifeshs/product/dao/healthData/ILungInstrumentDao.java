package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.LungInstrumentPO;

import java.util.Date;
import java.util.List;

/**
 * 肺活仪
 */
@Repository
@Mapper
public interface ILungInstrumentDao {
    int deleteByPrimaryKey(Integer id);

    int insert(LungInstrumentPO record);

    int insertSelective(LungInstrumentPO record);

    LungInstrumentPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LungInstrumentPO record);

    int updateByPrimaryKey(LungInstrumentPO record);

    List<LungInstrumentPO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    LungInstrumentPO lunginstrumentDate(@Param("userId") Integer userId,@Param("date")String date);
}