package com.lifeshs.product.service.doctor.impl;


import com.lifeshs.product.dao.doctor.UserInfoDao;
import com.lifeshs.product.domain.vo.record.DiseasesPO;
import com.lifeshs.product.domain.vo.record.UserRecordSortPO;
import com.lifeshs.product.domain.vo.user.UserInfoPO;
import com.lifeshs.product.domain.vo.user.UserMeasurePO;
import com.lifeshs.product.service.common.IPagingQueryProc;
import com.lifeshs.product.service.common.impl.Paging;
import com.lifeshs.product.service.doctor.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public List<UserInfoPO> getUserInfo(String idList) {
        String[] id = idList.split(",");
        return userInfoDao.getUserInfo(id);
    }

    @Override
    public int getDepartmentById(int userId) {
        return userInfoDao.getDepartmentById(userId);
    }

    @Override
    public Paging<UserMeasurePO> getUserMeasureList(int id, int curPage, int pageSize) {
        Paging<UserMeasurePO> p = new Paging<>(curPage, pageSize);
        p.setQueryProc(new IPagingQueryProc<UserMeasurePO>() {

            @Override
            public int queryTotal() {
                return userInfoDao.countMeasureList(id);
            }

            @Override
            public List<UserMeasurePO> queryData(int startRow, int pageSize) {
                return  userInfoDao.getUserMeasureList(id,startRow,pageSize);
            }
        });

        return p;


    }

    @Override
    public int updateUserDisease(int userId,int diseasesId,String diseasesName) {
        return userInfoDao.updateUserDisease(userId,diseasesId,diseasesName);
    }

    @Override
    public List<DiseasesPO> getDiseases() {
        return userInfoDao.getDiseases();
    }

    @Override
    public List<Integer> getDepartmentUserById(int userId) {
        return userInfoDao.getDepartmentUserById(userId);
    }

    @Override
    public Paging<UserRecordSortPO> getUserRecord(List<Integer>userList, Integer curPage, Integer pageSize) {

        Paging<UserRecordSortPO> p = new Paging<>(curPage, pageSize);
        p.setQueryProc(new IPagingQueryProc<UserRecordSortPO>() {

            @Override
            public int queryTotal() {
                return userList.size();
            }

            @Override
            public List<UserRecordSortPO> queryData(int startRow, int pageSize) {
                String st = "";
                for (Integer integer : userList) {
                    st += integer+",";
                }
                return  userInfoDao.getUserRecord(st.substring(0,st.length()-1),startRow,pageSize);
            }
        });

        return p;
    }
}
