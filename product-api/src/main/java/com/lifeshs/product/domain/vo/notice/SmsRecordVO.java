package com.lifeshs.product.domain.vo.notice;

/**
 * 短信记录
 * @author zizhen.huang
 * @DateTime 2018年1月23日19:53:47
 */
public class SmsRecordVO extends SmsRecordPO {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

