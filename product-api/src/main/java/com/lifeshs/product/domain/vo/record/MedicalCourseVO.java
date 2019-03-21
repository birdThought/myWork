package com.lifeshs.product.domain.vo.record;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *  病程
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年9月14日 上午11:05:56
 */
public @Data class MedicalCourseVO {

    private Integer id;
    /** 病历id */
    private Integer medicalId;
    /** 病程类型 */
    private String courseType;
    /** 备注 */
    private String remark;
    /** 就诊日期 */
    private Date visitingDate;
    /** 病程图片 */
    private List<MedicalCourseImgPO> imgList;
    /** 创建日期 */
    private Date createDate;
    /** 修改日期 */
    private Date modifyDate;
}

