package com.lifeshs.product.dao.healthData;

import com.lifeshs.product.domain.vo.healthData.UaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 尿酸分析仪
 */
@Repository
@Mapper
public interface IUaDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UaPO record);

    int insertSelective(UaPO record);

    UaPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UaPO record);

    int updateByPrimaryKey(UaPO record);

    List<UaPO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);


    UaPO getLastestData(@Param("userId")int userId);
}