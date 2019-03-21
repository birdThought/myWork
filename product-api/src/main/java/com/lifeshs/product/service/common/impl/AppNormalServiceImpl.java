package com.lifeshs.product.service.common.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.constants.healthData.HealthType;
import com.lifeshs.product.common.constants.jsonAttribute.base.Normal;
import com.lifeshs.product.common.constants.promptInfo.NormalMessage;
import com.lifeshs.product.domain.dto.common.aop.AppJSON;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.service.user.IMemberService;
import com.lifeshs.product.service.user.HobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * APP通用的service，主要用于存放通用方法
 *
 * @author yuhang.weng
 * @DateTime 2016年10月27日 下午5:35:44
 */
@Service(value = "appNormalService")
public class AppNormalServiceImpl extends AppBaseService {

    public static final String SUCCESS_STATUS = "0";

    public static final String ERROR_NORMAL_STATUS = "1";

    /** 健康标准值必要参数缺失 */
    public static final String ERROR_HEALTH_STANDAR_NECESSARY_PARAM_MISSIING = "400";

    @Autowired
    protected IMemberService memberService;

    @Resource(name = "userHobbyService")
    protected HobbyService hobbyService;
    /**
     * 错误返回
     * </p>
     * 如果不能够准确定义到错误码，就使用这个错误返回方法
     * </p>
     *
     * @author dachang.luo
     * @DateTime 2016年6月16日下午2:24:12
     *
     * @param errorMsg
     *            服务端错误信息
     * @return
     */
    public static JSONObject error(String errorMsg) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(Normal.STATUS, ERROR_NORMAL_STATUS);
        resultMap.put(Normal.MESSAGE, errorMsg);

        JSONObject jsonObj = new JSONObject();
        jsonObj.putAll(resultMap);
        return jsonObj;
    }

    /**
     * 错误返回
     * </p>
     *
     * @author yuhang.weng
     * @DateTime 2017年7月10日 上午10:13:40
     *
     * @param errorMsg
     * @param status
     *            错误码，具体参考http状态码
     * @return
     */
    public static JSONObject error(String errorMsg, Integer status) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(Normal.STATUS, String.valueOf(status));
        resultMap.put(Normal.MESSAGE, errorMsg);

        JSONObject jsonObj = new JSONObject();
        jsonObj.putAll(resultMap);
        return jsonObj;
    }

    /**
     * 错误返回
     * </p>
     *
     * @author yuhang.weng
     * @DateTime 2017年7月10日 上午10:32:40
     *
     * @param errorMsg
     *            错误信息
     * @param code
     *            错误码
     * @return
     */
    public static JSONObject error(String errorMsg, ErrorCodeEnum code) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(Normal.STATUS, String.valueOf(code.value()));
        resultMap.put(Normal.MESSAGE, errorMsg);

        JSONObject jsonObj = new JSONObject();
        jsonObj.putAll(resultMap);
        return jsonObj;
    }

    private static JSONObject returnSuccessWithExtraMap(JSONObject root, Map<String, String> extraMap) {
        for (String key : extraMap.keySet()) {
            if (root.containsKey(key)) {
                // 包含相同的key的话，优先保留原数据
                continue;
            }
            root.put(key, extraMap.get(key));
        }
        return root;
    }

    public static JSONObject success() {
        JSONObject root = new JSONObject();
        root.put(Normal.STATUS, "0");

        JSONArray array = new JSONArray();
        root.put(Normal.DATA, array);

        return root;
    }



    public static JSONObject success(Object data, boolean emptyCheck) {
        if (emptyCheck) {
            if (data instanceof Map) {
                Map<?, ?> castMap = (Map<?, ?>) data;
                if (castMap.isEmpty()) {
                    return success(NormalMessage.NO_DATA);
                }
            }

            if (data instanceof List) {
                List<?> castList = ((List<?>) data);
                if (castList.size() == 0) {
                    return success(NormalMessage.NO_DATA);
                }
            }

            // 这段检测是为了避免null情况的产生
            if (data == null) {
                System.out.println("通用接入点");
                return success(NormalMessage.NO_DATA);
            }
        }
        return success(data);
    }

    /**
     * 返回正确的app请求，并且在root层携带更多的参数
     *
     * @author yuhang.weng
     * @DateTime 2016年11月11日 下午2:28:58
     *
     * @param data
     * @param extraMap
     * @return
     */
    public static JSONObject success(Object data, Map<String, String> extraMap) {
        JSONObject json = success((String)data);
        return returnSuccessWithExtraMap(json, extraMap);
    }

    public static JSONObject success(Object data, Map<String, String> extraMap, boolean emptyCheck) {
        JSONObject root = success(data, emptyCheck);
        return returnSuccessWithExtraMap(root, extraMap);
    }

    /**
     *
     * @author yuhang.weng
     * @DateTime 2016年12月19日 下午2:59:20
     *
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONObject success(Object object) {
        JSONObject root = new JSONObject();
        root.put(Normal.STATUS, "0");

        JSONArray array = new JSONArray();

        boolean ok = false;
        if (object instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) object;
            array.add(value2String(data));
            ok = true;
        }
        if (object instanceof List) {
            List<Object> datas = (List<Object>) object;
            array.addAll(value2String(datas));
            ok = true;
        }
        if (!ok) {
            array.add(JSONObject.toJSON(object));
            ok = true;
        }

        root.put(Normal.DATA, array);

        return root;
    }

    /**
     * 返回正确的app请求，携带返回信息
     *
     * @author yuhang.weng
     * @DateTime 2016年11月15日 下午7:19:10
     *
     * @param message
     * @return
     */
    public static JSONObject success(String message) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(Normal.STATUS, SUCCESS_STATUS);
        resultMap.put(Normal.MESSAGE, message);

        JSONObject jsonObj = new JSONObject();
        jsonObj.putAll(resultMap);
        return jsonObj;
    }

    /**
     * 返回正确的app请求，携带返回信息,并且在root层携带更多的参数
     *
     * @author yuhang.weng
     * @DateTime 2016年11月11日 下午2:28:58
     *
     * @param
     * @param extraMap
     * @return
     */
    public static JSONObject success(String message, Map<String, String> extraMap) {
        JSONObject json = success(message);

        for (String key : extraMap.keySet()) {
            if (json.containsKey(key)) {
                // 包含相同的key的话，优先保留原数据
                continue;
            }
            json.put(key, extraMap.get(key));
        }
        return json;
    }

    /**
     * 对饮食记录一天内的数据进行排序（按照早餐，早餐加餐，午餐，午餐加餐，晚餐，晚餐加餐排列）
     *
     * @author yuhang.weng
     * @DateTime 2016年12月19日 上午11:22:53
     *
     * @param dietDatas
     * @return
     */
    public static List<Map<String, Object>> dietTimeSort(List<Map<String, Object>> dietDatas) {

        Collections.sort(dietDatas, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // Date d1 = DateTimeUtilT.time((String) o1.get("dietTime"));
                // Date d2 = DateTimeUtilT.time((String) o2.get("dietTime"));
                Date d1 = (Date) o1.get("dietTime");
                Date d2 = (Date) o2.get("dietTime");

                if (d1.before(d2)) {
                    return -1;
                }

                if (d1.equals(d2)) {
                    String type1 = (String) o1.get("dietType");
                    String type2 = (String) o2.get("dietType");
                    return type1.length() - type2.length();
                }
                return 0;
            }
        });

        return dietDatas;
    }

    /**
     * 将接收到的string转换为AppJSON对象
     *
     * @author yuhang.weng
     * @DateTime 2017年2月21日 下午7:33:12
     *
     * @param json
     * @return
     */
    public static AppJSON parseAppJSON(String json) {
        JSONObject root = JSONObject.parseObject(json);
        AppJSON appJSON = JSONObject.toJavaObject(root, AppJSON.class);
        return appJSON;
    }

    /**
     * 获取系统计算的健康标准范围值 根据type转换获取的结果(1是第一套标准， 建议填值2) 备注：
     * user对象只需要填写user.id，user.recordDTO.birthday，user.recordDTO.gender的值
     *
     * @author yuhang.weng
     * @DateTime 2016年11月10日 下午1:39:12
     *
     * @param
     * @param type
     * @return
     */
    public Map<String, Object> getSystemCalculateHealthArea(MemberUserDTO user, int type) {
        Map<String, Object> map = new HashMap<>();

        user.getRecordDTO().getBirthday();
        user.getRecordDTO().getGender();
        if (user.getRecordDTO().getBirthday() == null || user.getRecordDTO().getGender() == null) {
            return map;
        }
        int userId = user.getId();

        List<HealthType> healthTypes = new ArrayList<>();

        healthTypes.add(HealthType.heartRate);
        healthTypes.add(HealthType.systolic);
        healthTypes.add(HealthType.diastolic);
        healthTypes.add(HealthType.saturation);
        healthTypes.add(HealthType.bloodSugar);
        healthTypes.add(HealthType.vitalCapacity);
        healthTypes.add(HealthType.temperature);
        healthTypes.add(HealthType.ECG);
        // 体脂秤
        healthTypes.add(HealthType.weight);
        healthTypes.add(HealthType.axungeRatio);
        healthTypes.add(HealthType.WHR);
        healthTypes.add(HealthType.BMI);
        healthTypes.add(HealthType.fatFreeWeight);
        healthTypes.add(HealthType.muscle);
        healthTypes.add(HealthType.moisture);
        healthTypes.add(HealthType.boneWeight);
        healthTypes.add(HealthType.bodyage);
        healthTypes.add(HealthType.baseMetabolism);
        healthTypes.add(HealthType.proteide);
        healthTypes.add(HealthType.visceralFat);
        healthTypes.add(HealthType.pH);
        // TODO 添加新设备的健康标准
        healthTypes.add(HealthType.SG);
        healthTypes.add(HealthType.LDL);
        healthTypes.add(HealthType.TG);
        healthTypes.add(HealthType.TC);
        healthTypes.add(HealthType.UA);
        healthTypes.add(HealthType.HDL);
        healthTypes.add(HealthType.BloodLipidRation);

        Map<String, Object> healthStandardValues = new HashMap<>();

        if (type == 1) {
            healthStandardValues = getHealthAreaData(userId, healthTypes);
        }
        if (type == 2) {
            healthStandardValues = getHealthAreaData2(userId, healthTypes);
        }

        return healthStandardValues;
    }

    private Map<String, Object> getHealthAreaData(int userId, List<HealthType> healthTypes) {
        return terminal.getDeviceService().getHealthStandardValueByHealthType(userId, healthTypes);
    }

    private Map<String, Object> getHealthAreaData2(int userId, List<HealthType> healthTypes) {
        return terminal.getDeviceService().getHealthStandardValueByHealthType2(userId, healthTypes);
    }

}
