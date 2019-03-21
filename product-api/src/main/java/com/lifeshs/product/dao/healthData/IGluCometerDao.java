package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.GluCometerPO;

import java.util.Date;
import java.util.List;

/**
 * 血糖仪
 */
@Repository
@Mapper
public interface IGluCometerDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GluCometerPO record);

    int insertSelective(GluCometerPO record);

    GluCometerPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GluCometerPO record);

    int updateByPrimaryKey(GluCometerPO record);

    List<GluCometerPO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    GluCometerPO getLastestData(@Param("userId") int userId);

    GluCometerPO glucometerDate(@Param("userId")Integer userId, @Param("date") String date);
}