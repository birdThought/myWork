package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.dao.record.IPhysicalAnalysisDao;
import com.lifeshs.product.domain.vo.record.PhysicalAnalysisPO;
import com.lifeshs.product.domain.vo.record.PhysicalVO;
import com.lifeshs.product.service.healthData.IPhysicalAnalysisService;
import com.lifeshs.product.service.healthData.IPhysicalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service(value = "physicalAnalysisServiceImpl")
public class PhysicalAnalysisServiceImpl implements IPhysicalAnalysisService {

    @Resource(name = "physicalAnalysisDao")
    private IPhysicalAnalysisDao analysisDao;

    @Resource(name = "physicalService")
    private IPhysicalService physicalService;

    @Override
    public PhysicalAnalysisPO getAnalysis(int id) {
        return analysisDao.getAnalysis(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = OperationException.class)
    public void replyAnalysis(int id, String reply, int customerUserId, String doctorSign) throws OperationException {
        PhysicalAnalysisPO analysis = new PhysicalAnalysisPO();
        analysis.setId(id);
        analysis.setReply(reply);
        analysis.setCustomerUserId(customerUserId);
        analysis.setDoctorSign(doctorSign);
        int result = analysisDao.updateAnalysis(analysis);
        if (result == 0) {
            throw new OperationException("更新失败", ErrorCodeEnum.FAILED);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = OperationException.class)
    public void addAnalysis(int physicalId, String reply, int customerUserId, String doctorSign)
            throws OperationException {
        PhysicalVO physical = physicalService.getPhysical(physicalId);
        if (physical == null) {
            throw new OperationException("找不到该体检报告信息", ErrorCodeEnum.NOT_FOUND);
        }

        PhysicalAnalysisPO analysis = new PhysicalAnalysisPO();
        analysis.setRecordPhysicalId(physicalId);
        analysis.setReply(reply);
        analysis.setCustomerUserId(customerUserId);
        analysis.setDoctorSign(doctorSign);
        analysis.setUserId(physical.getUserId());
        int result = analysisDao.addAnalysis(analysis);
        if (result == 0) {
            throw new OperationException("添加失败", ErrorCodeEnum.FAILED);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = OperationException.class)
    public void addAnalysisRecord(int physicalId) throws OperationException {
        PhysicalVO physical = physicalService.getPhysical(physicalId);
        if (physical == null) {
            throw new OperationException("找不到该体检报告信息", ErrorCodeEnum.NOT_FOUND);
        }

        PhysicalAnalysisPO analysis = new PhysicalAnalysisPO();
        analysis.setRecordPhysicalId(physicalId);
        analysis.setUserId(physical.getUserId());
        int result = analysisDao.addAnalysis(analysis);
        if (result == 0) {
            throw new OperationException("添加失败", ErrorCodeEnum.FAILED);
        }
    }

    @Override
    public int countUnReadAnalysis(int userId) {
        int count = analysisDao.countAnalysisWithCondition(userId, null, null, null, null, false);
        return count;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = OperationException.class)
    public void readAnalysis(int id) throws OperationException {
        PhysicalAnalysisPO analysisPO = analysisDao.getAnalysis(id);
        boolean isReply = StringUtils.isNotBlank(analysisPO.getReply());
        if (isReply) {
            PhysicalAnalysisPO analysis = new PhysicalAnalysisPO();
            analysis.setId(id);
            analysis.setRead(true);
            int result = analysisDao.updateAnalysis(analysis);
            if (result == 0) {
                throw new OperationException("更新失败", ErrorCodeEnum.FAILED);
            }
        }
    }

    @Override
    public void readAnalysisList(int physicalId) {
        // 批量修改不做effectRow判断
        analysisDao.updateAnalysisListByRecordPhysicalId(physicalId, true, null);
    }

    @Override
    public void deleteAnalysis(int physicalId) {
        // 批量删除不做effectRow判断
        analysisDao.updateAnalysisListByRecordPhysicalId(physicalId, null, true);
    }
}

