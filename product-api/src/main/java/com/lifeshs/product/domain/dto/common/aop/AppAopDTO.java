package com.lifeshs.product.domain.dto.common.aop;

import com.lifeshs.product.domain.dto.user.MemberUserDTO;

/**
 * 应用app切面新增数据
 *
 * @author yuhang.weng
 * @DateTime 2017年2月21日 下午7:21:32
 */
public class AppAopDTO {

    /** 用户信息 */
    private MemberUserDTO user;

    public MemberUserDTO getUser() {
        return user;
    }

    public void setUser(MemberUserDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AppAopDTO [user=" + user + "]";
    }
}
