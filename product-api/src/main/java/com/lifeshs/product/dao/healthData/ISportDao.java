package com.lifeshs.product.dao.healthData;

import com.lifeshs.product.domain.po.data.TDataSportKind;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("sportDao")
@Mapper
public interface ISportDao {

    public List<TDataSportKind> getAllSport();
}
