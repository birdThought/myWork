package com.lifeshs.product.service.user.impl;

import com.lifeshs.product.common.constants.base.UserStatus;
import com.lifeshs.product.domain.po.member.TUser;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.service.alipay.config.AgentConstant;
import com.lifeshs.product.service.common.impl.BaseUserImpl;
import com.lifeshs.product.utils.MD5Utils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 普通用户（会员）操作类
 * @author dengfeng
 * 增加会员特有方法
 */
@Component("member")
public class Member extends BaseUserImpl {

    public Integer register(String userName, String password, String userCode) {
        MemberUserDTO user = new MemberUserDTO();
        user.setUserName(userName);
        user.setPassword(MD5Utils.encryptPassword(password));
        user.setUserCode(userCode);
        user.setStatus(UserStatus.normal.value());
        user.setHealthProduct(0);
        user.setHealthWarning(0);
        user.setHasWarning(0);
        user.setParentId(AgentConstant.AGENT_DEFUALT_PARENT_ID_A2);
        //user.setSex(false);   默认性别取消
        super.addMember(user);
        return user.getId();
    }

    public Integer register(MemberUserDTO memberUserDTO) {
        memberUserDTO.setStatus(UserStatus.normal.value());

        MemberUserDTO user = new MemberUserDTO();
        user.setUserName(memberUserDTO.getUserName());
        user.setPassword(MD5Utils.encryptPassword(memberUserDTO.getPassword()));
        user.setMobile(memberUserDTO.getMobile());
        user.setMobileVerified(memberUserDTO.getMobileVerified());
        user.setRealName(memberUserDTO.getRealName());
        user.setStatus(memberUserDTO.getStatus());
        user.setHealthProduct(0);
        user.setHealthWarning(0);
        user.setHasWarning(0);
        user.setUserCode(memberUserDTO.getUserCode());
        user.setPhoto(memberUserDTO.getPhoto());
        user.setParentId(memberUserDTO.getParentId());
        super.addMember(user);
        return user.getId();
    }

    /**
     * 返回map形式的user信息
     *  @author dachang.luo
     *  @DateTime 2016年6月30日 上午10:14:57
     *  @serverComment 服务注解
     *
     *  @param userId
     *  @return
     *  @throws Exception
     */
    public Map<String , Object> getUserMap(int userId)throws Exception{
        return commonTrans.findUniqueByPropertyByMap(TUser.class, "id", userId);
    }
}

