package com.lifeshs.product.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.lifeshs.product.common.constants.base.TerminalType;
import com.lifeshs.product.common.constants.promptInfo.TerminalErrorType;
import com.lifeshs.product.domain.po.data.TDataImei;
import com.lifeshs.product.domain.po.device.TUserTerminal;
import com.lifeshs.product.domain.po.member.TUser;
import com.lifeshs.product.service.common.transform.ICommonTrans;
import com.lifeshs.product.service.healthData.IDeviceService;
import com.lifeshs.product.service.healthData.impl.*;
import com.lifeshs.product.service.record.IMedicalService;
import com.lifeshs.product.service.record.IRecordService;
import com.lifeshs.product.service.user.family.IFamilyService;
import com.lifeshs.product.utils.DateTimeUtilT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 终端设备（Browser，APP、HL、C3）业务类（适配源，实现通用功能）
 * 注：device指可测量的类别，不一定是独立的终端设备，如HL包含几个device
 * 
 * @author dengfeng
 * @DateTime 2016-5-18 下午05:39:49
 */

@Component("terminalAdaptee")
public class TerminalAdaptee extends CommonServiceImpl {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private LocationService location;

    @Autowired
    private Band band;

    @Autowired
    private BandStep bandStep;

    @Autowired
    private BandSleep bandSleep;

    @Autowired
    private IRecordService recordService;

    @Autowired
    private IMedicalService medicalService;

    @Autowired
    private IFamilyService familyService;

    @Resource(name = "bloodPressure")
    private BloodPressure bloodPressure;

    @Resource(name = "heartRate")
    private HeartRate heartRate;

    @Resource(name = "lunginstrument")
    private Lunginstrument lunginstrument;

    @Resource(name = "oxygen")
    private Oxygen oxygen;

    @Resource(name = "bodyfatscale")
    private Bodyfatscale bodyfatscale;

    @Resource(name = "glucometer")
    private Glucometer glucometer;

    @Resource(name = "temperature")
    private Temperature temperature;
    
    @Resource(name = "ecg")
    private Ecg ecg;
    
    @Resource(name = "uran")
    private Uran uran;
    
    @Resource(name = "ua")
    private Ua ua;
    
    @Resource(name = "bloodLipid")
    private BloodLipid bloodLipid;

    public IDeviceService getDeviceService() {
        return deviceService;
    }

    /**
     * 血压业务对象
     * 
     * @author dengfeng
     * @DateTime 2016-5-20 下午03:21:36
     *
     * @return entity
     */
    public BloodPressure getBloodPressure() {
        return bloodPressure;
    }

    /**
     * 心率业务对象
     * 
     * @author dengfeng
     * @DateTime 2016-5-20 下午03:21:36
     *
     * @return entity
     */
    public HeartRate getHeartRate() {
        return heartRate;
    }

    /**
     * 定位业务对象
     * 
     * @author dengfeng
     * @DateTime 2016-5-20 下午03:21:36
     *
     * @return entity
     */
    public LocationService getLocation() {
        return location;
    }

    /**
     * 手环-计步睡眠日 业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月25日 下午3:31:07
     *
     * @return
     */
    public Band getBand() {
        return band;
    }

    /**
     * 手环-计步睡眠日 业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月26日 下午2:04:35
     *
     * @return
     */
    public BandSleep getBandSleep() {
        return bandSleep;
    }

    /**
     * 手环-计步 业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月26日 下午2:04:40
     *
     * @return
     */
    public BandStep getBandStep() {
        return bandStep;
    }

    /**
     * 公共操作对象
     * 
     * @author wenxian.cai
     * @DateTime 2016年8月9日上午9:47:15
     * @serverComment
     * @param
     */
    public ICommonTrans getCommonTrans() {
        return commonTrans;
    }

    /**
     * 健康档案操作对象
     * 
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午12:03:40
     * @serverComment
     * @param
     */
    public IRecordService getRecordService() {
        return recordService;
    }

    /**
     * 健康档案--病历操作对象
     * 
     * @author wenxian.cai
     * @DateTime 2016年8月23日上午10:49:10
     * @serverComment
     * @param
     */
    public IMedicalService getMedicalService() {
        return medicalService;
    }

    /**
     * 家庭组操作对象
     * 
     * @author wenxian.cai
     * @DateTime 2016年8月16日上午9:22:26
     * @serverComment
     * @param
     */
    public IFamilyService getFamilyService() {
        return familyService;
    }

    /**
     * 体温业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月25日 下午3:33:56
     *
     * @return entity
     */
    public Temperature getTemperature() {
        return temperature;
    }

    /**
     * 肺活量业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月26日 上午9:56:11
     *
     * @return
     */
    public Lunginstrument getLunginstrument() {
        return lunginstrument;
    }

    /**
     * 血氧计业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月26日 上午9:56:28
     *
     * @return
     */
    public Oxygen getOxygen() {
        return oxygen;
    }

    /**
     * 体脂秤业务对象
     * 
     * @author yuhang.weng
     * @DateTime 2016年5月26日 上午9:56:56
     *
     * @return
     */
    public Bodyfatscale getBodyfatscale() {
        return bodyfatscale;
    }

    /**
     * @author yuhang.weng
     * @DateTime 2016年6月20日 上午8:59:23
     * @serverComment 血糖设备服务对象
     *
     * @return
     */
    public Glucometer getGlucometer() {
        return glucometer;
    }
    
    public Ecg getEcg() {
        return ecg;
    }
    
    public BloodLipid getBloodLipid() {
        return bloodLipid;
    }
    
    public Uran getUran() {
        return uran;
    }
    
    public Ua getUa() {
        return ua;
    }

    /**
     * 验证，每次响应要先验证数据
     * 
     * @author dengfeng
     * @DateTime 2016-5-18 下午05:42:44
     *
     * @param json
     * @param terminalType
     * @return
     * @throws Exception
     */
    public TerminalErrorType verify(String json, TerminalType terminalType) throws Exception {

        if (StringUtils.isBlank(json))
            return TerminalErrorType.DataFormatError;

        // JSON合法性校验
        if (!baseFormat(json)) {
            return TerminalErrorType.DataFormatError;
        }

        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject("data");

        // 从json中取出imei和password元素
        String imei = data.getString("imei");
        String password = data.getString("password");

        TDataImei dataImei = this.commonTrans.findUniqueByProperty(TDataImei.class, "imei", imei);
        if (dataImei == null) {
            return TerminalErrorType.NotImei; // IMEI号不存在
        }

        if (dataImei.getStatus() != 2) {
            return TerminalErrorType.NotBound; // 设备尚未绑定
        }

        TUserTerminal terminal = deviceService.selectDeviceIsBinding(imei, terminalType.getName()); // 获取设备
        TUser user = this.commonTrans.getEntity(TUser.class, terminal.getUserId());

        String type = root.getString("type"); // 从json中取出type元素
        if (!type.equals(terminalType.getName())) {
            return TerminalErrorType.TypeMismatch; // 设备类型不匹配
        }

        if (!user.getToken().equals(password)) {
            return TerminalErrorType.IncorrectToken; // token不正确
        }

        if (tokenService.isTokenOverTime(imei, "", password))
            return TerminalErrorType.TokenOverTime; // token过期

        return TerminalErrorType.Normal;
    }

    /**
     * 返回错误信息的Json
     * 
     * @author dengfeng
     * @DateTime 2016-5-20 下午02:18:54
     *
     * @param errorType
     *            错误类型
     * @return
     */
    public static String returnErrorJson(TerminalErrorType errorType) {
        String datetime = DateTimeUtilT.dateTime(new Date()); // 同步时间
        String json = "{'command':'result','status':'" + errorType.value() + "','datetime':'" + datetime
                + "','action':[]}";
        return json;
    }

    /**
     * @author yuhang.weng
     * @DateTime 2016年6月2日 上午10:29:23
     * @serverComment JSON串最外层框架校验
     *
     * @param json
     * @return
     */
    protected boolean baseFormat(String json) {
        /* 第一段校验 */
        JSONObject root;
        try {
            root = JSONObject.parseObject(json);
        } catch (Exception e) { // 如果无法转换json则返回false
            return false;
        }
        if (!root.containsKey("type"))
            return false;
        if (!root.containsKey("data"))
            return false;
        /* 第二段校验 */
        JSONObject data = root.getJSONObject("data");
        if (!data.containsKey("imei"))
            return false;
        if (!data.containsKey("password"))
            return false;
        if (!data.containsKey("msg"))
            return false;
        if (!data.containsKey("msgsize"))
            return false;
        return true;
    }

}
