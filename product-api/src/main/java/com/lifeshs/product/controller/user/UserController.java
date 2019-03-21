package com.lifeshs.product.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.lifeshs.product.common.constants.jsonAttribute.base.ValidCode;
import com.lifeshs.product.common.constants.jsonAttribute.user.User;
import com.lifeshs.product.domain.dto.common.aop.AppJSON;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.service.common.impl.AppNormalServiceImpl;
import com.lifeshs.product.service.user.IAppUserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 应用app个人设置
 *
 * @author yuhang.weng
 * @DateTime 2017年2月21日 下午4:33:49
 */
@RestController(value = "appUserController")
@RequestMapping(value = { "/app", "/app/user" })
public class UserController {

    @Resource(name = "appUserService")
    private IAppUserService userService;

   /* @Autowired
    private IMemberService memberService;*/

    /**
     * 绑定手机号码
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "sendVerifyCode", method = RequestMethod.POST)
    public JSONObject sendVerifyCode(@RequestBody  String json) throws Exception {

        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        Integer userId = appJSON.getData().getUserId();
        String mobile = mm_0.getString(User.MOBILE);
        String type = mm_0.getString(ValidCode.TYPE);
        String appType = appJSON.getType();
        userService.sendVerifyCode(mobile, type, userId,appType);
        return AppNormalServiceImpl.success();
    };


    /**
     * 修改手机号码
     *
     * @param
     * @return
     * @author dachang.luo
     * @DateTime 2016年6月23日 上午11:04:53
     */
    @RequestMapping(value = "modifyMobile", method = RequestMethod.POST)
    public JSONObject modifyMobile(@RequestBody  AppJSON appJSON) throws Exception {
        /*AppJSON appJSON = parseAppJSON(json);*/

        int userId = appJSON.getData().getUserId();// 用户id

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String newMobile = mm_0.getString(ValidCode.NEW_MOBILE);// 新手机号码
        String newVerifyCode = mm_0.getString(ValidCode.NEW_MOBILE_CODE);// 新手机验证码

        Object oM = mm_0.get(ValidCode.OLD_MOBILE);
        Object oV = mm_0.get(ValidCode.OLD_MOBILE_CODE);

        return userService.modifyMobile(userId,newMobile,newVerifyCode,oM,oV);
    }

    /**
     * 用户认证类型(第三方认证,帐号密码认证)
     *
     * @param
     * @return
     * @author liu
     * @时间 2018年12月26日 上午11:24:38
     * @remark
     */
    @RequestMapping(value = "userLoginType", method = RequestMethod.POST)
    public JSONObject userLoginType(@RequestBody String json) {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        return userService.userLoginType(userId);
    }

    /**
     * 修改密码
     *
     * @param
     * @return
     * @author dachang.luo
     * @DateTime 2016年6月23日 上午11:05:06
     */
    @RequestMapping(value = "modifyPassword", method = RequestMethod.POST)
    public JSONObject modifyPassword(@RequestBody AppJSON appJSON, HttpServletRequest request) throws Exception {
        String ip = request.getHeader("X-Real-IP");

        /*AppJSON appJSON = parseAppJSON(json);*/

        int userId = appJSON.getData().getUserId();

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String oldPassword = mm_0.getString(ValidCode.PASSWORD_OLD);
        String newPassword = mm_0.getString(ValidCode.PASSWORD_NEW);
        MemberUserDTO user = appJSON.getAopData().getUser();
        return userService.modifyPassword(user,userId,oldPassword,newPassword,ip);
    }

    /**
     * 获取用户信息
     *
     * @param
     * @return
     * @author dachang.luo
     * @DateTime 2016年6月23日 上午11:05:16
     */
    @RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
    public JSONObject getUserInfo(@RequestBody  String json) throws Exception {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);

        MemberUserDTO user = appJSON.getAopData().getUser();
        return userService.getUserInfo(user);
    }

    /**
     * 更新用户基本信息
     *
     * @param json
     * @return
     * @author dachang.luo
     * @DateTime 2016年6月23日 上午11:05:31
     */
    @RequestMapping(value = "modifyUserBaseInfo", method = RequestMethod.POST)
    public JSONObject modifyUserBaseInfo(@RequestBody String json) throws Exception {
        return userService.modifyUserBaseInfo(json);
    }

    /**
     * 更新用户基本信息 2
     *
     * @param json
     * @return
     * @author yuhang.weng
     * @DateTime 2016年12月24日 下午5:20:57
     */
    @RequestMapping(value = "modifyUserBaseInfo2", method = RequestMethod.POST)
    public JSONObject modifyUserBaseInfo2(@RequestBody String json) throws Exception {
        return userService.modifyUserBaseInfo2(json);
    }

    /**
     * 修改用户身体数据
     *
     * @param json
     * @return
     * @author dachang.luo
     * @DateTime 2016年6月23日 上午11:05:43
     */
    @RequestMapping(value = "modifyUserBodyInfo", method = RequestMethod.POST)
    public JSONObject modifyUserBodyInfo(@RequestBody String json) throws Exception {
        return userService.modifyUserBodyInfo(json);
    }

    /**
     * 修改用户身体数据 2
     *
     * @param json
     * @return
     * @author yuhang.weng
     * @DateTime 2016年12月24日 下午5:21:14
     */
    @RequestMapping(value = "modifyUserBodyInfo2", method = RequestMethod.POST)
    public JSONObject modifyUserBodyInfo2(@RequestBody String json) throws Exception {
        return userService.modifyUserBodyInfo2(json);
    }
}
