package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.BodyFatScalePO;

import java.util.Date;
import java.util.List;

/**
 * 体脂称
 */
@Repository
@Mapper
public interface IBodyFatScaleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BodyFatScalePO record);

    int insertSelective(BodyFatScalePO record);

    BodyFatScalePO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BodyFatScalePO record);

    int updateByPrimaryKey(BodyFatScalePO record);

    List<BodyFatScalePO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    BodyFatScalePO currrntDodyfatsDate(@Param("userId")Integer userId,@Param("date") String date);
}