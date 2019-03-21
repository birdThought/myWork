package com.lifeshs.product.dao.doctor;


import com.lifeshs.product.domain.vo.record.DiseasesPO;
import com.lifeshs.product.domain.vo.record.UserRecordSortPO;
import com.lifeshs.product.domain.vo.user.UserInfoPO;
import com.lifeshs.product.domain.vo.user.UserMeasurePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserInfoDao {

    /**
     * 获取用户信息
     * @param
     * @return
     */
    List<UserInfoPO> getUserInfo(@Param("id") String[] id);

    /**
     * 根据医生id获取科室
     * @param userId
     * @return
     */
    int getDepartmentById(@Param("id") int userId);

    /**
     * 根据科室id获取用户测量数据
     * @param id
     */
    List<UserMeasurePO> getUserMeasureList(@Param("id") int id, @Param("startRow") int startRow, @Param("pageSize") int pageSize);

    /**
     * 统计用户测量数据
     * @param id
     * @return
     */
    int countMeasureList(@Param("id") int id);

    /**
     * 根据用户id变更病种
     *
     * @param id
     * @param
     * @param diseasesName
     * @return
     */
    int updateUserDisease(@Param("id") int id, @Param("diseasesId") int diseasesId, @Param("diseasesName") String diseasesName);

    /**
     * 获取病种
     * @return
     */
    List<DiseasesPO> getDiseases();

    /**
     * 根据当前医生id获区科室用户
     * @param userId
     * @return
     */
    List<Integer> getDepartmentUserById(@Param("id") int userId);



    /**
     * 获取用户最新更新的档案数据
     * @param
     * @param startRow
     * @param pageSize
     * @return
     */
    List<UserRecordSortPO> getUserRecord(@Param("ids") String ids, @Param("startRow") int startRow, @Param("pageSize") int pageSize);
}
