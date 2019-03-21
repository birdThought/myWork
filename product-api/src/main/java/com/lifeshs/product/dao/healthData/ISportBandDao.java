package com.lifeshs.product.dao.healthData;

import com.lifeshs.product.domain.vo.healthData.SportBandPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 运动腕带
 */
@Repository
@Mapper
public interface ISportBandDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SportBandPO record);

    int insertSelective(SportBandPO record);

    SportBandPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SportBandPO record);

    int updateByPrimaryKey(SportBandPO record);

    /**
     * 返回指定月份存在测量数据的日期集合
     *
     * @param userId
     * @param queryDate 查询月的第一天,例如:2017-05-01
     * @return
     */
    List<SportBandPO> selectDatesByUserId(@Param("userId")Integer userId, @Param("queryDate") String queryDate);

    Date getLastDateByUserId(@Param("userId") Integer userId);
}