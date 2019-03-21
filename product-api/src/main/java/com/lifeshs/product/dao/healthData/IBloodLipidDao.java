package com.lifeshs.product.dao.healthData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.BloodLipidPO;

import java.util.Date;
import java.util.List;

/**
 * 血脂仪
 *
 */
@Repository
@Mapper
public interface IBloodLipidDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BloodLipidPO record);

    int insertSelective(BloodLipidPO record);

    BloodLipidPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BloodLipidPO record);

    int updateByPrimaryKey(BloodLipidPO record);

    List<BloodLipidPO> selectMeasureDatesByUserId(@Param("userId") Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);

    /**
     * 获取最后一条数据
     * @param userId
     * @return
     */
    BloodLipidPO getLastData(@Param("userId") int userId);
}