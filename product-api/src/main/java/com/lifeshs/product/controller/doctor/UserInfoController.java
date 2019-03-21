package com.lifeshs.product.controller.doctor;

import com.alibaba.fastjson.JSONObject;

import com.lifeshs.product.common.constants.healthData.HealthPackageType;
import com.lifeshs.product.common.constants.jsonAttribute.base.Page;
import com.lifeshs.product.domain.dto.common.aop.AppJSON;
import com.lifeshs.product.domain.vo.record.DiseasesPO;
import com.lifeshs.product.domain.vo.record.UserRecordSortPO;
import com.lifeshs.product.domain.vo.user.UserInfoPO;
import com.lifeshs.product.domain.vo.user.UserMeasurePO;
import com.lifeshs.product.service.common.impl.AppNormalServiceImpl;
import com.lifeshs.product.service.doctor.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.lifeshs.product.service.common.impl.AppNormalServiceImpl.parseAppJSON;
import static com.lifeshs.product.service.common.impl.AppNormalServiceImpl.success;


@RestController
@RequestMapping("app/doctor")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("getUserInfo")
    public JSONObject getUserInfo(@RequestBody String json){
        AppJSON appJSON = parseAppJSON(json);
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String idList = mm_0.getString("idList");
        List<UserInfoPO> userInfo = userInfoService.getUserInfo(idList);
        return success(userInfo);
    }

    @PostMapping("getUserMeasureList")
    public JSONObject getUserMeasureList(@RequestBody String json){
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        int pageIndex = mm_0.getIntValue(Page.INDEX);// 页码
        int pageSize = mm_0.getIntValue(Page.SIZE);// 每页行数
        int id = userInfoService.getDepartmentById(userId);//根据医生获取科室
        List<UserMeasurePO> userMeasureList = userInfoService.getUserMeasureList(id,pageIndex,pageSize).getData();
        for (UserMeasurePO userMeasure : userMeasureList) {
            Integer device = userMeasure.getDevice();
            Boolean item = true;
            for(HealthPackageType val : HealthPackageType.values()) {
                if (val.value() == device) {
                    userMeasure.setDeviceName(val.getName());
                    item = false;
                    break;
                }
            }
            if(item){
                for(HealthPackageType val : HealthPackageType.values()){
                    for(HealthPackageType val1 : HealthPackageType.values()){
                        if((val.value()|val1.value()) == device){
                            userMeasure.setDeviceName(val.getName()+","+val1.getName());
                        }
                    }
                }
            }

        }
        return success(userMeasureList);
    }


    @PostMapping("updateUserDisease")
    public JSONObject updateUserDisease(@RequestBody String json){
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        int userId = mm_0.getIntValue("userId");
        int diseasesId = mm_0.getIntValue("diseasesId");
        String diseasesName = mm_0.getString("diseasesName");
        int i = userInfoService.updateUserDisease(userId, diseasesId, diseasesName);
        return success();
    }

    @PostMapping("getDiseases")
    public JSONObject getDiseases(@RequestBody String json){
        List<DiseasesPO> diseasesList = userInfoService.getDiseases();
        return success(diseasesList);
    }

    @PostMapping("getUserRecord")
    public JSONObject getUserRecord(@RequestBody String json){
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        int pageIndex = mm_0.getIntValue(Page.INDEX);// 页码
        int pageSize = mm_0.getIntValue(Page.SIZE);// 每页行数
        List<Integer>userList = userInfoService.getDepartmentUserById(userId);

        List<UserRecordSortPO> data = userInfoService.getUserRecord(userList, pageIndex, pageSize).getData();
        return success(data);

    }




}
