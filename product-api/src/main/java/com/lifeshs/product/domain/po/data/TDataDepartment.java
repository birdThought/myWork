package com.lifeshs.product.domain.po.data;

import java.io.Serializable;

/**
 * t_data_department
 */

@SuppressWarnings("serial")
public class TDataDepartment implements Serializable {

    private Integer id;

    /**科室名称*/
    private String name;

    /**父类ID*/
    private Integer parentId;

    public TDataDepartment() {
        super();
    }

    public TDataDepartment(Integer id, String name, Integer parentId) {
        super();
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
