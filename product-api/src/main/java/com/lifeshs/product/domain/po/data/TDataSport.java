package com.lifeshs.product.domain.po.data;

import java.io.Serializable;

/**
 * t_data_sport
 */
@SuppressWarnings("serial")
public class TDataSport implements Serializable {

    private Integer id;

    private String name;

    private Integer kind;

    /** 能量（/min） */
    private Double kcal;

    public TDataSport() {
        super();
    }

    public TDataSport(Integer id, String name, Integer kind, Double kcal) {
        super();
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.kcal = kcal;
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


    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }


    public Double getKcal() {
        return kcal;
    }

    public void setKcal(Double kcal) {
        this.kcal = kcal;
    }

}
