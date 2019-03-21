package com.lifeshs.product.service.common.impl;

import com.lifeshs.product.common.constants.base.UserStatus;
import com.lifeshs.product.common.IBaseUserDao;
import com.lifeshs.product.dao.user.IMemberDao;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.dto.user.SensitiveOperationLogDTO;
import com.lifeshs.product.domain.po.member.TUser;
import com.lifeshs.product.domain.vo.user.UserPO;
import com.lifeshs.product.service.common.IBaseUser;
import com.lifeshs.product.service.common.transform.ICommonTrans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户的基类
 *
 * @author dengfeng 封装通用操作方法
 */
@Component("baseUser")
public abstract class BaseUserImpl implements IBaseUser {

    @Autowired
    protected ICommonTrans commonTrans;

    @Autowired
    private IBaseUserDao baseUserDao;

    @Autowired
    private IMemberDao memberDao;

    @Override
    public boolean addMember(MemberUserDTO user) {
        int effectRowCount = memberDao.addUser(user);
        if (effectRowCount > 0) {
            return true;
        }
        return false;
    }

    @Override
    public <T> boolean basicInformation(T entity) {
        int result = commonTrans.updateEntitie(entity);
        if (result == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkEmail(String email) {

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("emailVerified", true);

        List<Map<String, Object>> users = commonTrans.findByMap(TUser.class, params);
        if(!users.isEmpty() && users.size() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean checkMobile(String mobile) {

        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("mobileVerified", true);

        List<Map<String, Object>> users = commonTrans.findByMap(TUser.class, params);
        if(!users.isEmpty() && users.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 用户是否已存在（和机构用户一起判断）
     *  @author dengfeng
     *  @DateTime 2016-6-2 上午10:44:39
     *
     *  @param userName
     *  @return
     * @throws Exception
     */
    public boolean userIsExist(String userName) {
        // 用户
        UserPO user = memberDao.findUserByUserName(userName);
        if (user == null) {
            // 机构用户
            /*OrgUserPO orgUser = orgUserDao.findUserByUserName(userName);
            if (orgUser == null) {
                return false;
            }*/
        }
        return true;
    }

    /**
     * 邮箱是否已存在（和机构用户邮箱一起判断）
     *  @author dengfeng
     *  @DateTime 2016-6-2 上午10:44:39
     *
     *  @param
     *  @return
     * @throws Exception
     */
    public boolean emailIsExist(String email) throws Exception {
        return isExist("email", email);
    }

    /**
     * 判断指定的属性的值的用户是否在系统中已存在
     *  @author duosheng.mo , dengfeng update @20160602
     *  @DateTime 2016年5月20日 上午11:19:23
     *
     *  @param property 属性项
     *  @param propertyValue 属性值
     *  @return
     *  @throws Exception
     */
    private boolean isExist(String property, String propertyValue) {
        boolean bool = false;
        List<Map<String, Object>> userList = commonTrans.findByPropertyByMap(TUser.class, property, propertyValue);
        //若用户状态不是注销，则用户存在
        if(userList != null && userList.size() > 0){
            for(Map<String, Object> user : userList){
                int status = (int) user.get("status");
                if(status != UserStatus.logoff.value()){
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    public String checkMobileReturnUserId(String mobile) {
        String userId = null;

        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("mobileVerified", true);

        boolean get = false;
        List<Map<String, Object>> users = commonTrans.findByMap(TUser.class, params);
        for (Map<String, Object> map : users) {
            if ((Integer) map.get("status") != UserStatus.logoff.value()) {
                userId = String.valueOf((Integer) map.get("id"));
                get = true;
                break;
            }
        }

        return userId;
    }

    public String checkEmailReturnUserId(String email) {
        String userId = "";

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("emailVerified", true);

        boolean get = false;

        List<Map<String, Object>> users = commonTrans.findByMap(TUser.class, params);
        for (Map<String, Object> user : users) {
            int status = (int) user.get("status");
            if (status != UserStatus.logoff.value()) {
                userId = String.valueOf(user.get("id"));
                get = true;
                break;
            }
        }

        return userId;
    }

    @Override
    public void saveSensitiveLog(SensitiveOperationLogDTO log) {
        baseUserDao.insertSensitiveLog(log);
    }
}

