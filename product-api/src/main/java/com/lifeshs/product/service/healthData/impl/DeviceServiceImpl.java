package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.healthData.HealthType;
import com.lifeshs.product.common.constants.jsonAttribute.healthData.HealthPackage;
import com.lifeshs.product.dao.healthData.IDeviceDao;
import com.lifeshs.product.dao.user.IMemberDao;
import com.lifeshs.product.domain.dto.user.UserRecordDTO;
import com.lifeshs.product.domain.po.device.TSportLocation;
import com.lifeshs.product.domain.po.device.TUserTerminal;
import com.lifeshs.product.domain.po.member.TUserMonitorTrack;
import com.lifeshs.product.service.healthData.IDeviceService;
import com.lifeshs.product.utils.DateTimeUtilT;
import com.lifeshs.product.utils.ReflectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DeviceServiceImpl implements IDeviceService {

    @Autowired
    private IMemberDao memberDao;
    @Autowired
    private IDeviceDao deviceDao;

    @Override
    public <T> Integer selectDeviceDataCountByMeasureDate(Class<T> entityClass, Map<String, Object> params) {
        String tableName = ReflectUtils.reflectTableName(entityClass);
        params.put("tableName", tableName);
        Integer amount = deviceDao.selectDeviceDataCountByMeasureDate(params);
        return amount;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T selectDeviceDataLastestDate(Class<T> entityName, int userId, String terminalType, String measureDate) {
        Object object = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);

        Map<String, Object> entity = ReflectUtils.queryEntityEnclosureParamsWholeColumn(entityName, params);
        entity.put("date", measureDate);
        entity.put("deviceType", terminalType);

        Map<String, Object> resMap = deviceDao.selectDeviceDataLastestDate(entity);
        object = ReflectUtils.getBean(resMap, entityName);
        return (T) object;
    }



    @Override
    public List<TSportLocation> findLatestGpsMessage(int userId, String terminalType, int limit) {
        return deviceDao.selectLatestLocation(userId, terminalType, limit);
    }

    @Override
    public List<TSportLocation> findLocationByDateTime(int userId, String terminalType, String startTime, String endTime) {
        return deviceDao.selectLocationByDate(userId, terminalType, startTime, endTime);
    }

    @Override
    public TUserTerminal selectDeviceIsBinding(String imei, String terminalType) {
        return null;
    }

    @Override
    public List<TUserMonitorTrack> findTracksOrderByParam(int deviceId, String orderParam, int type) {
        String order = "ASC";
        if (type == 1)
            order = "DESC";
        return deviceDao.selectTracksOrderByParam(deviceId, orderParam, order);
    }

    @Override
    public Map<String, Object> getHealthStandardValueByHealthType(Integer userId, List<HealthType> healthTypes) {
        List<String> healthTypeNames = new ArrayList<>();
        for (HealthType healthType : healthTypes) {
            healthTypeNames.add(healthType.name());
        }

        /** 获取用户的数据，可以考虑修改为从缓存中获取，但是要用ehcache中的，因为保存在session中的缓存更换的速度不够快 */
        UserRecordDTO recordDTO = memberDao.getUserRecord(userId);

        Integer age = null;
        if (recordDTO.getBirthday() != null) {
            age = DateTimeUtilT.calculateAge(recordDTO.getBirthday());
        }

        Integer height = recordDTO.getHeight() == null ? null : recordDTO.getHeight().intValue();
        Integer weight = recordDTO.getWeight() == null ? null : recordDTO.getWeight().intValue();

        Map<String, Object> hs = HealthStandard.getAllByUserNullCheck(recordDTO.getGender(), age, height, weight);

        Map<String, Object> hs_n = new HashMap<>();
        for (String key1 : hs.keySet()) {

            if (!healthTypeNames.contains(key1)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, String> cMap = (Map<String, String>) hs.get(key1);
            switch (key1) {
                case "vitalCapacity":

                    String va = cMap.get("min") + "-" + cMap.get("max");
                    hs_n.put(key1, va);

                    List<Map<String, String>> vb = new ArrayList<>();

                    Map<String, String> data1 = new LinkedHashMap<>();
                    data1.put("area", 0 + "-" + cMap.get("less"));
                    data1.put("score", "1分");
                    vb.add(data1);

                    Map<String, String> data2 = new LinkedHashMap<>();
                    data2.put("area", cMap.get("less") + "-" + cMap.get("min"));
                    data2.put("score", "2分");
                    vb.add(data2);

                    Map<String, String> data3 = new LinkedHashMap<>();
                    data3.put("area", cMap.get("min") + "-" + cMap.get("max"));
                    data3.put("score", "3分");
                    vb.add(data3);

                    Map<String, String> data4 = new LinkedHashMap<>();
                    data4.put("area", cMap.get("max") + "-" + cMap.get("more"));
                    data4.put("score", "4分");
                    vb.add(data4);

                    Map<String, String> data5 = new LinkedHashMap<>();
                    data5.put("area", cMap.get("more") + "-" + 99999);
                    data5.put("score", "5分");
                    vb.add(data5);

                    hs_n.put("vitalCapacityArea", vb);

                    break;
                case "bloodSugar":

                    String bm = cMap.get("less") + "-" + cMap.get("more");
                    String am = cMap.get("min") + "-" + cMap.get("max");
                    Map<String, String> bs = new HashMap<>();
                    bs.put("beforeMeal", bm);
                    bs.put("afterMeal", am);

                    hs_n.put(key1, bs);

                    break;
                default:

                    String da = cMap.get("min") + "-" + cMap.get("max");
                    hs_n.put(key1, da);

                    break;
            }
        }

        return hs_n;
    }

    @Override
    public Map<String, Object> getHealthStandardValueByHealthType2(Integer userId, List<HealthType> healthTypes) {
        List<String> healthTypeNames = new ArrayList<>();
        for (HealthType healthType : healthTypes) {
            healthTypeNames.add(healthType.name());
        }
        // 如果是心率手环，需要额外添加一个运动模式
        if (healthTypeNames.contains(HealthPackage.HEARTRATE)) {
            healthTypeNames.add(HealthPackage.HEARTRATE_SPORT_MODE);
        }

        UserRecordDTO recordDTO = memberDao.getUserRecord(userId);

        Integer age = null;
        if (recordDTO.getBirthday() != null) {
            age = DateTimeUtilT.calculateAge(recordDTO.getBirthday());
        }

        Integer height = recordDTO.getHeight() == null ? null : recordDTO.getHeight().intValue();
        Integer weight = recordDTO.getWeight() == null ? null : recordDTO.getWeight().intValue();

        Map<String, Object> hs = HealthStandard.getAllByUserNullCheck(recordDTO.getGender(), age, height, weight);

        Map<String, Object> healthArea = new HashMap<>();
        List<Map<String, String>> body = new ArrayList<>();
        List<Map<String, String>> vb = new ArrayList<>();

        for (String key : hs.keySet()) {
            if (!healthTypeNames.contains(key)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, String> cMap = (Map<String, String>) hs.get(key);
            switch (key) {
                case "vitalCapacity":

                    Map<String, String> data1 = new LinkedHashMap<>();
                    data1.put("area", 0 + "-" + cMap.get("less"));
                    data1.put("score", "1");
                    vb.add(data1);

                    Map<String, String> data2 = new LinkedHashMap<>();
                    data2.put("area", cMap.get("less") + "-" + cMap.get("min"));
                    data2.put("score", "2");
                    vb.add(data2);

                    Map<String, String> data3 = new LinkedHashMap<>();
                    data3.put("area", cMap.get("min") + "-" + cMap.get("max"));
                    data3.put("score", "3");
                    vb.add(data3);

                    Map<String, String> data4 = new LinkedHashMap<>();
                    data4.put("area", cMap.get("max") + "-" + cMap.get("more"));
                    data4.put("score", "4");
                    vb.add(data4);

                    Map<String, String> data5 = new LinkedHashMap<>();
                    data5.put("area", cMap.get("more") + "-" + 99999);
                    data5.put("score", "5");
                    vb.add(data5);

                    Map<String, String> va = new HashMap<>();
                    va.put("param", key);
                    va.put("min", cMap.get("min"));
                    va.put("max", cMap.get("max"));
                    va.put("less", cMap.get("less"));
                    va.put("more", cMap.get("more"));

                    body.add(va);
                    break;
                case "bloodSugar":

                    Map<String, String> ba = new HashMap<>();
                    ba.put("param", key);
                    ba.put("min", cMap.get("min"));
                    ba.put("max", cMap.get("max"));
                    ba.put("less", cMap.get("less"));
                    ba.put("more", cMap.get("more"));

                    body.add(ba);
                    break;
                default:

                    Map<String, String> da = new HashMap<>();
                    da.put("param", key);
                    da.put("min", cMap.get("min"));
                    da.put("max", cMap.get("max"));
                    da.put("less", "");
                    da.put("more", "");

                    body.add(da);
                    break;
            }
        }

        healthArea.put("commonHealthArea", body);
        healthArea.put("vitalCapacityArea", vb);

        return healthArea;
    }

    @Override
    public <T> T selectDeviceDataLastest(Class<T> entityName, int userId, String terminalType, String measureDate) {
        return null;
    }
}
