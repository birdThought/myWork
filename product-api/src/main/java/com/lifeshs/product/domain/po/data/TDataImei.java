package com.lifeshs.product.domain.po.data;

import java.io.Serializable;
import java.util.Date;

/**
 * t_data_imei
 */
@SuppressWarnings("serial")
public class TDataImei implements Serializable {

    /**设备入库表*/
    public Long id;

    /**串号*/
    public String imei;

    /**默认密码(保留)*/
    public String password;

    /**状态,在库_0,已卖出_1,已绑定_2*/
    public Integer status;

    /**入库时间*/
    public Date createDate;


    public TDataImei() {
        super();
    }

    public TDataImei(Long id, String imei, String password, Integer status, Date createDate) {
        super();
        this.id = id;
        this.imei = imei;
        this.password = password;
        this.status = status;
        this.createDate = createDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


}

