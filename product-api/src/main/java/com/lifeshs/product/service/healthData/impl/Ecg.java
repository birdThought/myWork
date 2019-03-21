package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.common.constants.healthData.HealthType;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.dao.healthData.IEcgDao;
import com.lifeshs.product.domain.dto.common.PaginationDTO;
import com.lifeshs.product.domain.dto.common.QueryPageData;
import com.lifeshs.product.domain.dto.healthData.EcgDTO;
import com.lifeshs.product.domain.dto.healthData.EcgDetailDTO;
import com.lifeshs.product.domain.dto.healthData.EcgHistoryDTO;
import com.lifeshs.product.domain.dto.healthData.HealthStandardValue;
import com.lifeshs.product.domain.dto.user.BaseMemberDo;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.dto.user.UserRecordDTO;
import com.lifeshs.product.domain.po.device.TMeasureEcg;
import com.lifeshs.product.service.healthData.IMeasureDevice;
import com.lifeshs.product.utils.DateTimeUtilT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Ecg extends MeasureDevice<EcgDTO> implements IMeasureDevice<EcgHistoryDTO> {
    @Autowired
    IEcgDao ecgDao;

    private final DeviceType deviceType = DeviceType.ECG;

    @Override
    protected boolean checkStatus(EcgDTO entity) {
        List<EcgDetailDTO> detailList = entity.getDetailList();
        // 如果没有携带详细信息就直接返回正常
        if (detailList == null || detailList.isEmpty()) {
            return false;
        }
        // 遍历携带的详细信息，如果找到status异常的数据就直接返回异常，否则返回正常
        for (EcgDetailDTO detail : detailList) {
            if (detail.getStatus() > 0) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    protected boolean checkStatusIsNull(EcgDTO entity) {
        return true;
    }

    @Override
    protected void deviantHandling(EcgDTO entity) throws OperationException {
        int userId = entity.getUserId();
        // 设置 用户为 未处理异常 状态
        MemberUserDTO user = memberService.getUser(userId);
        // 注意，这里的测量日期只精确到天
        super.updateUserIsOverstep(userId, user.getHasWarning(), deviceType, entity.getDate());
    }

    @Override
    protected void perfectData(EcgDTO entity) {
        // 心电只判断心率是否符合健康标准
        // 获取用户信息
        int userId = entity.getUserId();
        UserRecordDTO recordDTO = memberService.getRecord(userId);

        BaseMemberDo baseMemberDo = getMemberBaseData(recordDTO);
        boolean userSex = baseMemberDo.isSex();
        int userAge = baseMemberDo.getAge();
        // 获取心率健康范围
        HealthStandardValue<Integer> heartRateStandard = HealthStandard.getHeartRate(userSex, userAge);
        Float heartRateMin = heartRateStandard.getMin().floatValue();
        Float heartRateMax = heartRateStandard.getMax().floatValue();
        
        for (EcgDetailDTO ed : entity.getDetailList()) {
            // 默认是正常的
            Long statusDetail = 0L;
            Integer heartRate = ed.getHeartRate();
            // heartRate可能为null，需要做非null校验
            if (heartRate != null) {
                statusDetail = getStatus(heartRateMin, heartRateMax, heartRate.floatValue(), statusDetail, HealthType.heartRate.value());
            }
            ed.setStatus(statusDetail.intValue());
        }
    }

    @Override
    public List<EcgHistoryDTO> getMeasureDataWithDate(Integer userId, String deviceType, String dateType) {
        String date = "";
        if (DateTimeUtilT.valiDateTimeWithFormat(dateType)) {
            date = dateType;
            dateType = "SPECIAL_DAY";
        }
        List<EcgDTO> list = new ArrayList<>();
        switch (dateType) {
        case "DAY":
            list = deviceDao.listEcgWithLatestDay(userId, deviceType);
            break;
        case "WEEK":
            list = deviceDao.listEcgWithLatestWeek(userId, deviceType);
            break;
        case "MONTH":
            list = deviceDao.listEcgWithLatestMonth(userId, deviceType);
            break;
        case "THREEMONTH":
            list = deviceDao.listEcgWithLatestThreeMonth(userId, deviceType);
            break;
        case "SPECIAL_DAY":
            list = deviceDao.listEcgWithSpecialDate(userId, deviceType, DateTimeUtilT.date(date));
        default:
            break;
        }

        List<EcgHistoryDTO> historyList = toHistoryDTO(list);
        return historyList;
    }

    @Override
    public PaginationDTO<EcgHistoryDTO> getMeasureDataWithSplit(Integer userId, String deviceType, int nowPage, int pageSize) {
        PaginationDTO<EcgHistoryDTO> pagination = new PaginationDTO<>();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        List<EcgHistoryDTO> data = new ArrayList<>();
        conditionMap.put("userId", userId);
        conditionMap.put("deviceType", deviceType);
        int totalSize = commonTrans.getCount(TMeasureEcg.class, conditionMap);

        QueryPageData queryPageData = PaginationDTO.getQueryPageData(nowPage, pageSize, totalSize);
        int startIndex = queryPageData.getStartIndex();
        int totalPage = queryPageData.getTotalPage();

        pagination.setTotalPage(totalPage);
        pagination.setNowPage(nowPage);
        if (PaginationDTO.isDataOverFlow(nowPage, pageSize, totalSize)) {
            pagination.setData(data);
            return pagination;
        }

        List<EcgDTO> list = deviceDao.listEcgWithPageSplit(userId, deviceType, startIndex, pageSize);
        data = toHistoryDTO(list);
        
        // 封装数据
        pagination.setData(data);
        return pagination;
    }

    @Override
    public void save(EcgDTO entity) throws OperationException {
        perfectData(entity);
        int result = deviceDao.addEcg(entity);
        if (result == 0) {
            throw new OperationException("添加心电数据失败", ErrorCodeEnum.FAILED);
        }
        // 如果数据内容为空就不保存操作
        if (entity.getDetailList() == null || entity.getDetailList().isEmpty()) {
            return;
        }
        int ecgMeasureId = entity.getId();
        result = deviceDao.addEcgDetail(ecgMeasureId, entity.getDetailList());
        if (result != entity.getDetailList().size()) {
            throw new OperationException("部分心电数据保存失败", ErrorCodeEnum.NOT_COMPLETE);
        }
    }
    
    /**
     *  转换为历史记录类型
     *  @author yuhang.weng 
     *	@DateTime 2017年5月4日 下午2:00:42
     *
     *  @param list
     *  @return
     */
    private List<EcgHistoryDTO> toHistoryDTO(List<EcgDTO> list) {
        List<EcgHistoryDTO> historyList = new ArrayList<>();
        
        // 按照日期封装数据到map中
        Map<String, LinkedList<EcgDTO>> dateDevideMap = new HashMap<>();
        for (EcgDTO l : list) {
            String measureDate = DateTimeUtilT.date(l.getDate());
            if (!dateDevideMap.containsKey(measureDate)) {
                LinkedList<EcgDTO> root = new LinkedList<>();
                dateDevideMap.put(measureDate, root);
            }
            dateDevideMap.get(measureDate).add(l);
        }
        // 将数据添加到列表中
        for (String date : dateDevideMap.keySet()) {
            EcgHistoryDTO h = new EcgHistoryDTO();
            h.setMeasureDate(DateTimeUtilT.date(date));
            h.setDatas(dateDevideMap.get(date));
            historyList.add(h);
        }
        return historyList;
    }
    
    public PaginationDTO<EcgDTO> listEcgWithDateAndType(Integer userId, String measureDate,
            String deviceType, Integer signType, Integer rhythmId, Integer curPage, Integer pageSize) {
        PaginationDTO<EcgDTO> pagination = new PaginationDTO<>();
        int totalSize = deviceDao.countEcgWithDateAndSignType(userId, deviceType, DateTimeUtilT.date(measureDate), signType, rhythmId);

        QueryPageData queryPageData = PaginationDTO.getQueryPageData(curPage, pageSize, totalSize);
        int startIndex = queryPageData.getStartIndex();
        int totalPage = queryPageData.getTotalPage();
        pagination.setTotalPage(totalPage);
        pagination.setNowPage(curPage);
        if (PaginationDTO.isDataOverFlow(curPage, pageSize, totalSize)) {
            pagination.setData(new ArrayList<>());
            return pagination;
        }

        List<EcgDTO> list = deviceDao.listEcgWithDateAndSignType(userId, deviceType, measureDate, signType, rhythmId, startIndex, pageSize);
        
        // 封装数据
        pagination.setData(list);
        pagination.setTotalSize(totalSize);
        return pagination;
    }
    
    public Integer countActiveEcgWithDate(Integer userId, String deviceType, String measureDate) {
        int totalSize = deviceDao.countEcgWithDateAndSignType(userId, deviceType, DateTimeUtilT.date(measureDate), 1, null);
        return totalSize;
    }

    public EcgDTO selectByUserIdAndDate (int userId, Date date) {
        return ecgDao.selectByUserIdAndDate(userId, date);
    }

    public EcgDTO getLastestData(Integer userId) {
        return ecgDao.getLastestData(userId);
    }
}
