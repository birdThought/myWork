package com.lifeshs.product.service.healthData;

import com.lifeshs.product.domain.dto.record.EnergyAnalyzeDTO;
import com.lifeshs.product.domain.dto.record.SportsDTO;
import com.lifeshs.product.domain.po.data.TDataSportKind;
import com.lifeshs.product.domain.po.record.TRecordSport;

import java.util.List;

public interface ISportService {
    public List<TRecordSport> selectTRecordSportWithDate(int userId, String recordDate);

    public List<TDataSportKind> selectAllSports();

    public Integer addSport(TRecordSport sport);

    public List<TRecordSport> selectSportEnergyByUserIdWithDate(Integer userId, boolean customDate,
                                                                String startDate, String endDate);

    public boolean delSportByRecordSportId(Integer recordSportId);

    public boolean delSportDetail(Integer detailId);

    /**
     * @author wenxian.cai
     * @DateTime 2017年2月24日上午10:48:02
     * @serverComment 修改运动记录
     * @param
     */
    public boolean updateSport(SportsDTO sportsDTO);

    /**
     * @author wenxian.cai
     * @DateTime 2017年2月20日上午9:39:37
     * @serverComment 获取饮食能量分析
     * @param userId
     * @param recordDate
     */
    public EnergyAnalyzeDTO getEnergyAnalyze(int userId, String recordDate);
}

