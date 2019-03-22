package com.lifeshs.product.service.doctor;



import com.lifeshs.product.domain.vo.record.DiseasesPO;
import com.lifeshs.product.domain.vo.record.UserRecordSortPO;
import com.lifeshs.product.domain.vo.user.UserInfoPO;
import com.lifeshs.product.domain.vo.user.UserMeasurePO;
import com.lifeshs.product.service.common.impl.Paging;

import java.util.List;

public interface UserInfoService {

    /**
     * 获取用户信息
     * @param idList
     * @return
     */
    List<UserInfoPO> getUserInfo(String idList);

    /**
     * 根据医生id获取科室
     * @param userId
     * @return int
     */
    int getDepartmentById(int userId);

    /**
     * 根据科室id获取测量用户数据
     * @param id
     */
    Paging<UserMeasurePO> getUserMeasureList(int id, int pageIndex, int pageSize);

    /**
     * 根据用户id更换病种
     * @param userId
     */
    int updateUserDisease(int userId, int diseasesId, String diseasesName);

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
    List<Integer> getDepartmentUserById(int userId);

    /**
     * 根据医生集合
     * @param
     */
    Paging<UserRecordSortPO> getUserRecord(List<Integer> userList, Integer pageIndex, Integer pageSize);

    /**
     * 根据医生id获取同科室用户的病历更新
     * @param userId
     */
    void getUserMedicalToUpdate(int userId);
}
