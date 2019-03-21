package com.lifeshs.product.service.user;

import com.alibaba.fastjson.JSONObject;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;

/**
 * 应用app个人设置
 *
 * @author yuhang.weng
 * @version 1.0
 * @DateTime 2017年3月6日 下午8:37:08
 */
public interface IAppUserService {

    /**
     * 发送手机验证码
     *
     * @author dachang.luo
     * @DateTime 2016年6月20日 上午9:52:27
     *
     * @param
     * @return
     * @throws Exception
     */
    JSONObject sendVerifyCode(String mobile,String type,Integer userId,String appType) throws Exception;

    /**
     * 核对验证码
     *
     * @author dachang.luo
     * @DateTime 2016年6月16日下午4:55:26
     *
     * @param json
     * @return
     * @throws Exception
     */
    JSONObject checkVerifyCode(String json) throws Exception;

    /**
     * 忘记密码 (找回密码)
     *
     * @author dachang.luo
     * @DateTime 2016年6月20日 上午9:52:57
     *
     * @param json
     * @return
     * @throws Exception
     */
    JSONObject setPasswod(String json, String ip) throws Exception;

    /**
     * 修改手机号码
     *
     * @author dachang.luo
     * @DateTime 2016年6月16日下午5:51:47
     *
     * @param
     * @return
     * @throws Exception
     */
    JSONObject modifyMobile(Integer userId,String newMobile,String newVerifyCode,Object oM,Object oV) throws Exception;

    /**
     * 修改密码
     *
     * @author dachang.luo
     * @DateTime 2016年6月21日 下午8:22:58
     *
     * @param
     * @return
     * @throws Exception
     */
    JSONObject modifyPassword(MemberUserDTO user, Integer userId, String oldPassword, String newPassword, String ip) throws Exception;

    /**
     * 用户登录认证的方式,为了验证修改密码时是否要填写旧密码
     * @param
     * @return
     * @author liu
     * @时间 2018年12月26日 上午11:50:14
     * @remark
     */
    JSONObject userLoginType(Integer userId);

    /**
     * 获取用户信息
     *
     * @author dachang.luo
     * @DateTime 2016年5月27日上午9:52:01
     *
     * @param
     * @return
     * @throws Exception
     */
    JSONObject getUserInfo(MemberUserDTO user) throws Exception;

    /**
     * 更新用户信息
     *
     * @author dachang.luo
     * @DateTime 2016年5月27日上午9:51:29
     *
     * @param json
     * @return
     */
    JSONObject modifyUserBaseInfo(String json) throws Exception;

    /**
     * 更新用户信息 2
     *
     * @author yuhang.weng
     * @DateTime 2016年12月24日 下午5:18:31
     *
     * @param json
     * @return
     */
    JSONObject modifyUserBaseInfo2(String json);

    /**
     * 修改用户身体数据
     *
     * @author dachang.luo
     * @DateTime 2016年6月16日下午8:20:52
     *
     * @param json
     * @return
     */
    JSONObject modifyUserBodyInfo(String json);

    /**
     * 修改用户身体数据2
     *
     * @author yuhang.weng
     * @DateTime 2016年12月24日 下午5:18:50
     *
     * @param json
     * @return
     */
    JSONObject modifyUserBodyInfo2(String json);


}
