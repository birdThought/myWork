package com.lifeshs.product.domain.po.data;

import java.io.Serializable;
import java.util.List;

/**
 * t_data_sport_kind
 */
@SuppressWarnings("serial")
public class TDataSportKind implements Serializable {

    private Integer id;

    /** 运动种类名称 */
    private String name;

    private List<TDataSport> sports;

    public TDataSportKind() {
        super();
    }

    public TDataSportKind(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TDataSport> getSports() {
        return sports;
    }

    public void setSports(List<TDataSport> sports) {
        this.sports = sports;
    }

}
