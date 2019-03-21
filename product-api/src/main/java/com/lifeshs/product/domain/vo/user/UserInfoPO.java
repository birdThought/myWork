package com.lifeshs.product.domain.vo.user;

public class UserInfoPO extends UserPO {

    private Integer diseasesId; //病种id
    private String diseasesName; //病种名字

    public Integer getDiseasesId() {
        return diseasesId;
    }

    public void setDiseasesId(Integer diseasesId) {
        this.diseasesId = diseasesId;
    }

    public String getDiseasesName() {
        return diseasesName;
    }

    public void setDiseasesName(String diseasesName) {
        this.diseasesName = diseasesName;
    }
}
