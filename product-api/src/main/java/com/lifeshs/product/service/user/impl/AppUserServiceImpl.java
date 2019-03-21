package com.lifeshs.product.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.lifeshs.product.common.constants.base.BaseDefine;
import com.lifeshs.product.common.constants.base.CacheType;
import com.lifeshs.product.common.constants.base.VcodeTerminalType;
import com.lifeshs.product.common.constants.jsonAttribute.base.Area;
import com.lifeshs.product.common.constants.jsonAttribute.base.Normal;
import com.lifeshs.product.common.constants.jsonAttribute.base.ValidCode;
import com.lifeshs.product.common.constants.jsonAttribute.user.User;
import com.lifeshs.product.common.constants.jsonAttribute.user.UserRecord;
import com.lifeshs.product.common.constants.promptInfo.ErrorInfo;
import com.lifeshs.product.common.constants.promptInfo.NormalMessage;
import com.lifeshs.product.common.exception.sms.SMSException;
import com.lifeshs.product.domain.dto.common.AddressDTO;
import com.lifeshs.product.domain.dto.common.ImageDTO;
import com.lifeshs.product.domain.dto.common.aop.AppJSON;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.dto.user.UserDTO;
import com.lifeshs.product.domain.dto.user.UserRecordDTO;
import com.lifeshs.product.service.common.ICacheService;
import com.lifeshs.product.service.common.impl.AppNormalServiceImpl;
import com.lifeshs.product.service.common.impl.ValidCodeServiceImpl;
import com.lifeshs.product.service.user.IAppUserService;
import com.lifeshs.product.utils.DateTimeUtilT;
import com.lifeshs.product.utils.MD5Utils;
import com.lifeshs.product.utils.RandCodeUtil;
import com.lifeshs.product.utils.Toolkits;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用app个人设置实现类
 *
 * @author yuhang.weng
 * @version 1.0
 * @DateTime 2017年3月6日 下午8:37:28
 */
@Service(value = "appUserService")
public class AppUserServiceImpl extends AppNormalServiceImpl implements IAppUserService {

    private static final Logger logger = Logger.getLogger(AppUserServiceImpl.class);

    @Autowired
    private ValidCodeServiceImpl validCodeUtil;

  /*  @Autowired
    private IContactsService contactsService;

    @Autowired
    private IPaperService paperService;*/

    @Autowired
    private ICacheService cacheService;


    @Override
    public JSONObject sendVerifyCode(String mobile,String type,Integer id,String appType) throws Exception {


        boolean verifyMobile = Toolkits.verifyPhone(mobile);
        if (!verifyMobile) {
            return error(ErrorInfo.MOBILE_UNVERIFIY);
        }

        String userId = "";
        String cacheKey = "";
        Integer sendId = id;
        CacheType cacheType = null;

        if (StringUtil.isBlank(type)) {
            return error(ErrorInfo.VALID_CODE_CACHE_TYPE_NULL);
        }
        switch (type) {
            case ValidCode.REGISTER:
                cacheType = CacheType.USER_REGISTERY_CACHE; // 注册 验证码
                cacheKey = mobile;
                userId = memberService.checkMobile(mobile);
                if (StringUtil.isNotBlank(userId)) {
                    return success(NormalMessage.MOBILE_OCCUPIED);
                }
                break;
            case ValidCode.SET_PASSWORD:
                cacheType = CacheType.USER_RESET_CACHE; // 重置密码 验证码
                if (StringUtils.isBlank(mobile)) {
                    return error(ErrorInfo.VALID_CODE_MOBILE_MISSING);
                }
                // 查找手机号是否登记在系统中
                userId = memberService.checkMobile(mobile);
                // 普通用户
                if (StringUtils.isBlank(userId)) {
                    return success(NormalMessage.NO_SUCH_ACCOUNT);
                }
                cacheKey = userId;
                break;
            case ValidCode.SET_MOBILE_EMAIL:
                cacheType = CacheType.APP_MOBILE_EMAIL_MODIFY; // 修改手机 验证码
                if (StringUtils.isBlank(mobile)) {
                    return error(ErrorInfo.VALID_CODE_MOBILE_MISSING);
                }
                // 查找手机号是否登记在系统中
                userId = memberService.checkMobile(mobile);
                // 普通用户
                if (StringUtils.isEmpty(userId)) {
                    return success(NormalMessage.NO_SUCH_ACCOUNT);
                }
                cacheKey = mobile;
                break;
            case ValidCode.SET_NEW_MOBILE_EAMIL:
                /**
                 * 设置新的手机，需要判断该手机是否已被占用 如果被占用了，直接返回错误信息，终止发送验证码请求
                 */
                cacheType = CacheType.APP_MOBILE_EMAIL_MODIFY; // 修改手机 验证码
                userId = memberService.checkMobile(mobile);
                if (StringUtils.isNotBlank(userId)) {
                    return success(NormalMessage.MOBILE_OCCUPIED);
                }
                cacheKey = mobile;
                break;
            case ValidCode.LOGIN:
                cacheType = CacheType.OAUTH_CACHE;  // 共用认证缓存，保存手机登录验证码
                /**
                 * 查找手机号是否已经注册，如果不存在，就注册一个新的账号，账号名为
                 */
                userId = memberService.checkMobile(mobile);
                if (StringUtil.isBlank(userId)) {
                    UserDTO user = new UserDTO();
                    String userName = "m_" + RandCodeUtil.randNumberCodeByCustom("1", 8);
                    String password = RandCodeUtil.randNumberCodeByCustom("1", 10);
                    user.setUserName(userName);
                    user.setPassword(password);
                    user.setMobile(mobile);
                    user.setMobileVerified(true);
                    /*memberService.registMember(user);*/
                }
                cacheKey = "mobile" + mobile;
                break;
        }
        if (StringUtils.isBlank(cacheKey) || cacheType == null) {
            return error(ErrorInfo.VALID_CODE_ILLEGAL_ACTION);
        }
        VcodeTerminalType vCodeTerminalType = null;
        switch (appType) {
            case Normal.ANDROID_TYPE:
                vCodeTerminalType = VcodeTerminalType.ANDROID;
                break;
            case Normal.IOS_TYPE:
                vCodeTerminalType = VcodeTerminalType.IOS;
                break;
            default:
                vCodeTerminalType = null;
                break;
        }
        try {
            // 发送验证码
            validCodeUtil.sendToMobile(sendId, mobile, cacheKey, cacheType, vCodeTerminalType, false);
        } catch (SMSException e) {
            return error(e.getMessage());
        }
        /*// TODO 测试关闭验证码是否发送校验
        if (StringUtils.isEmpty(code)) {
            // 发送失败
            return error(Error.VALID_CODE_SEND_FAILED);
        }*/
        // 发送成功
        return success();
    }

    @Override
    public JSONObject checkVerifyCode(String json) throws Exception {
        AppJSON appJSON = parseAppJSON(json);

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String mobile = mm_0.getString(User.MOBILE);
        String type = mm_0.getString(ValidCode.TYPE);
        String verifyCode = mm_0.getString(ValidCode.CODE);

        String cacheKey = "";

        CacheType cacheType = null;
        if (type.equals(ValidCode.REGISTER)) {
            cacheType = CacheType.USER_REGISTERY_CACHE;
            cacheKey = mobile;
        }
        if (type.equals(ValidCode.SET_PASSWORD)) {
            cacheType = CacheType.USER_RESET_CACHE;
            cacheKey = memberService.checkMobile(mobile);
            if (StringUtils.isBlank(cacheKey)) {
                return success(NormalMessage.NO_SUCH_ACCOUNT);
            }
        }
        if (type.equals(ValidCode.SET_MOBILE_EMAIL) || type.equals(ValidCode.SET_NEW_MOBILE_EAMIL)) {
            cacheType = CacheType.APP_MOBILE_EMAIL_MODIFY;
            cacheKey = mobile;
        }
        /*if (type.equals(ValidCode.LOGIN)) {
            cacheType = CacheType.OAUTH_CACHE;
            cacheKey = "mobile" + mobile;
        }*/

        // 验证不通过
        if (!validCodeUtil.valid(cacheKey, verifyCode, cacheType)) {
            return success(NormalMessage.CODE_UNRECOGNIZED);
        }
        // 验证通过
        return success();
    }

    @Override
    public JSONObject setPasswod(String json, String ip) throws Exception {
        AppJSON appJSON = parseAppJSON(json);

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String mobile = mm_0.getString(User.MOBILE);// 手机号
        String password = mm_0.getString(User.PASSWORD);// 密码
        String verifyCode = mm_0.getString(ValidCode.CODE);// 验证码

        String userId = memberService.checkMobile(mobile);
        if (StringUtil.isBlank(userId)) {
            return success(NormalMessage.NO_SUCH_ACCOUNT);
        }

        // 密码格式验证
        boolean isValidPassword = Toolkits.isVerifyPassword(password);
        if (!isValidPassword) {
            return error(ErrorInfo.PASSWORD_UNVERIFY);
        }

        boolean bool = validCodeUtil.valid(userId, verifyCode, CacheType.USER_RESET_CACHE);
        if (bool) {
            // 修改密码
            memberService.modifyPasswordByUserId(userId, password, ip);
            // 绑定手机号码
            memberService.updateMobile(Integer.valueOf(userId), mobile);
        } else {
            return success(NormalMessage.CODE_UNRECOGNIZED);
        }
        return success();
    }

    @Override
    public JSONObject modifyMobile(Integer userId,String newMobile,String newVerifyCode,Object oM,Object oV) throws Exception {


        if (oM != null && oV != null && StringUtils.isNotBlank((String) oM) && StringUtils.isNotBlank((String) oV)) {
            // 包含有原手机号码，以及原手机号码验证码的话，说明本次操作为修改绑定手机
            String oldMobile = (String) oM;
            String oldVerifyCode = (String) oV;

            if (!validCodeUtil.valid(oldMobile, oldVerifyCode, CacheType.APP_MOBILE_EMAIL_MODIFY)) {
                return success(NormalMessage.MODIFY_MOBILE_UNRECOGNIZED_OLD);
            }
            if (!validCodeUtil.valid(newMobile, newVerifyCode, CacheType.APP_MOBILE_EMAIL_MODIFY)) {
                return success(NormalMessage.MODIFY_MOBILE_UNRECOGNIZED_NEW);
            }

            // 修改手机号
            boolean isOk = memberService.updateMobile(userId, newMobile);
            if (isOk) {
                return success();
            } else {
                return error(ErrorInfo.FAIL_ACTION);
            }
        } else {
            if (!validCodeUtil.valid(newMobile, newVerifyCode, CacheType.APP_MOBILE_EMAIL_MODIFY)) {
                return success(NormalMessage.MODIFY_MOBILE_UNRECOGNIZED_NEW);
            }

            // 修改手机号
            boolean isOk = memberService.updateMobile(userId, newMobile);
            if (isOk) {
                return success();
            } else {
                return success(NormalMessage.MOBILE_OCCUPIED);
            }
        }
    }

    @Override
    public JSONObject userLoginType(Integer userId) {
        Map<String, Object> oauthUser = memberService.getUserByUserId(userId);
        Map<String, Object> retData = new HashMap<>();
        if(oauthUser != null) {
            retData.put("oauthLogin", 1); // 是否第三方认证登录
            retData.put("firstModify", MD5Utils.encryptPassword(Normal.OAUTH_LOGIN_FIX_PASSWORD)
                    .equals(oauthUser.get("password"))?1:0); // 是否第一次修改密码
            retData.put("oauthType", oauthUser.get("oauthType")); // 认证方式
        } else {
            retData.put("oauthLogin", 0);
            retData.put("firstModify", 0);
            retData.put("oauthType", "帐号密码认证");
        }
        return success(retData);
    }

    @Override
    public JSONObject modifyPassword(MemberUserDTO user,Integer userId,String oldPassword,String newPassword,String ip) throws Exception {


        boolean isNewPasswordValid = Toolkits.isVerifyPassword(newPassword);
        if (!isNewPasswordValid) {
            return error(ErrorInfo.PASSWORD_UNVERIFY);
        }


        if(MD5Utils.encryptPassword(Normal.OAUTH_LOGIN_FIX_PASSWORD).equals(user.getPassword())) { // 第三方认证登录用户,并且是第一次修改密码
            // 不做旧密码验证
        } else {

            if (StringUtil.isBlank(oldPassword)) {
                return error(ErrorInfo.MODIFY_PASSWORD_OLD_MISSING);
            }
            oldPassword = MD5Utils.encryptPassword(oldPassword);

            if (!user.getPassword().equals(oldPassword)) {
                return success(NormalMessage.MODIFY_PASSWORD_UNRECOGNIZED);
            }
        }

        if (StringUtil.isBlank(newPassword)) {
            return error(ErrorInfo.MODIFY_PASSWORD_NEW_MISSING);
        }

        String userName = memberService.modifyPasswordByUserId(String.valueOf(userId), newPassword, ip);
        if (StringUtils.isNotBlank(userName)) {
            return success();
        }
        return error(ErrorInfo.FAIL_ACTION);
    }

    @Override
    public JSONObject getUserInfo(MemberUserDTO user) throws Exception {

        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put(User.MOBILE, user.getMobile());
        userMap.put(User.MOBILE_VERIFIY, user.getMobileVerified());
        userMap.put(User.PHOTO, user.getPhoto());
        userMap.put(User.REALNAME, user.getRealName());
        userMap.put(UserRecord.HEIGHT, user.getRecordDTO().getHeight());
        userMap.put(UserRecord.WEIGHT, user.getRecordDTO().getWeight());
        userMap.put(UserRecord.WAIST, user.getRecordDTO().getWaist());
        userMap.put(UserRecord.BUST, user.getRecordDTO().getBust());
        userMap.put(UserRecord.HIP, user.getRecordDTO().getHip());
        /*
         * 性别与生日默认值为空，如果是空
         */
        boolean sexs = BaseDefine.SEX;
        if (user.getRecordDTO().getGender() != null) {
            sexs = user.getRecordDTO().getGender();
        }
        userMap.put(User.SEX, sexs);

        String birthday = "";
        if (user.getRecordDTO().getBirthday() != null) {
            birthday = DateTimeUtilT.date(user.getRecordDTO().getBirthday());
        }
        userMap.put(User.BIRTHDAY, birthday);

        Map<String, Object> address = new HashMap<String, Object>();
        address.put(Area.PROVINCE, user.getProvince());
        address.put(Area.CITY, user.getCity());
        address.put(Area.COUNTY, user.getCounty());
        address.put(Area.STREET, user.getStreet());
        userMap.put(Area.ADDRESS, address);

        /*List<UserHobbyPO> userHobbyList = hobbyService.listUserHobby(user.getId());
        List<Map<String, Object>> hobbyList = new ArrayList<>();
        for (UserHobbyPO h : userHobbyList) {
            Map<String, Object> hobby = new HashMap<>();
            hobby.put(Hobby.ID, h.getHobbyId());
            hobby.put(Hobby.NAME, h.getHobbyName());
            hobbyList.add(hobby);
        }
        userMap.put(UserHobby.HOBBY, hobbyList);*/

        UserRecordDTO recordDTO = user.getRecordDTO();
        String corporeityResult = recordDTO.getCorporeityResult();
        Integer strokeRiskScore = recordDTO.getStrokeRiskScore();
        Integer subHealthSymptomScore = recordDTO.getSubHealthSymptomScore();

        userMap.put(UserRecord.CORPOREITY_RESULT, corporeityResult);
        userMap.put(UserRecord.STROKE_RISK_SCORE, strokeRiskScore);
        userMap.put(UserRecord.SUB_HEALTH_SYMPTOM_SCORE, subHealthSymptomScore);

        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("method", "getUserInfo");

        return success(userMap, extraMap);
    }

    @Override
    public JSONObject modifyUserBaseInfo(String json) throws Exception {
        Map<String, Object> healthArea = modifyUserBaseInfo(json, 1);
        if (healthArea == null) {
            return error(ErrorInfo.FAIL_ACTION);
        }
        return success(healthArea);
    }

    @Override
    public JSONObject modifyUserBaseInfo2(String json) {
        Map<String, Object> healthArea = modifyUserBaseInfo(json, 2);
        if (healthArea == null) {
            return error(ErrorInfo.FAIL_ACTION);
        }
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("healthArea", healthArea);
        return success(returnData, true);
    }

    /**
     * 修改用户基本信息
     *
     * @author yuhang.weng
     * @DateTime 2017年3月17日 下午5:30:35
     *
     * @param json
     * @param ver
     * @return
     */
    private Map<String, Object> modifyUserBaseInfo(String json, int ver) {
        AppJSON appJSON = parseAppJSON(json);
        int userId = appJSON.getData().getUserId();// 用户id
        MemberUserDTO user = appJSON.getAopData().getUser();

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        JSONObject address = mm_0.getJSONObject(Area.ADDRESS);
        String photo = mm_0.getString(User.PHOTO);
        String name = mm_0.getString(User.REALNAME);
        String sex = mm_0.getString(User.SEX);
        String birthday = mm_0.getString(User.BIRTHDAY);

        AddressDTO addressDTO = new AddressDTO();
        if (address != null) {
            String province = address.getString(Area.PROVINCE);
            String city = address.getString(Area.CITY);
            String county = address.getString(Area.COUNTY);
            String street = address.getString(Area.STREET);
            addressDTO.setProvince(province);
            addressDTO.setCity(city);
            addressDTO.setCountry(county);
            addressDTO.setStreet(street);
        }

        // // 修改用户个人信息
        Boolean gender = null;
        if (Normal.TRUE.equals(sex)) {
            gender = true;
        }
        if (Normal.FALSE.equals(sex)) {
            gender = false;
        }
        user.getRecordDTO().setGender(gender);

        String netPath = null;
        if (photo != null && StringUtil.isNotBlank(photo)) {
            String old_photo = user.getPhoto();
            ImageDTO imageVO = uploadPhoto(photo, old_photo, "head", false);
            if (imageVO.getUploadSuccess()) {
                netPath = imageVO.getNetPath();
            } else {
                return null;
            }
        }

        Date userBirthday = null;
        if (StringUtils.isNotBlank(birthday)) {
            userBirthday = DateTimeUtilT.date(birthday);
            user.getRecordDTO().setBirthday(userBirthday);
        }

        memberService.updateUserBaseInfo(userId, userBirthday, gender, name, netPath, addressDTO);
        return success(); /*getSystemCalculateHealthArea(user, ver)*/
    }

    @Override
    public JSONObject modifyUserBodyInfo(String json) {
        Map<String, Object> healthArea = modifyUserBodyInfo(json, 1);
        if (healthArea == null) {
            return success(NormalMessage.NO_DATA);
        }
        return success(healthArea);
    }

    @Override
    public JSONObject modifyUserBodyInfo2(String json) {
        Map<String, Object> healthArea = modifyUserBodyInfo(json, 2);
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("healthArea", healthArea);
        return success(returnData, true);
    }

    private Map<String, Object> modifyUserBodyInfo(String json, int ver) {
        AppJSON appJSON = parseAppJSON(json);
        int userId = appJSON.getData().getUserId();// 用户id
        MemberUserDTO user = appJSON.getAopData().getUser();

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        Float height = mm_0.getFloat(UserRecord.HEIGHT);
        Float weight = mm_0.getFloat(UserRecord.WEIGHT);
        Float waist = mm_0.getFloat(UserRecord.WAIST);
        Float bust = mm_0.getFloat(UserRecord.BUST);
        Float hip = mm_0.getFloat(UserRecord.HIP);

        // 保存用户的身体信息
        memberService.updateUserRecord(userId, height, weight, waist, bust, hip);

        if (height != null) {
            user.getRecordDTO().setHeight(height);
        }
        if (weight != null) {
            user.getRecordDTO().setWeight(weight);
        }
        if (waist != null) {
            user.getRecordDTO().setWaist(waist);
        }
        if (bust != null) {
            user.getRecordDTO().setBust(bust);
        }
        if (hip != null) {
            user.getRecordDTO().setHip(hip);
        }
        return success();/*getSystemCalculateHealthArea(user, ver);*/
    }

}
