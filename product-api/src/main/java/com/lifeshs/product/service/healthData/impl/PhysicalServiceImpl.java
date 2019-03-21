package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.exception.base.BaseException;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.common.exception.base.ParamException;
import com.lifeshs.product.dao.record.IPhysicalDao;
import com.lifeshs.product.dao.record.IPhysicalImgDao;
import com.lifeshs.product.domain.vo.record.PhysicalImgPO;
import com.lifeshs.product.domain.vo.record.PhysicalPO;
import com.lifeshs.product.domain.vo.record.PhysicalVO;
import com.lifeshs.product.service.common.IPagingQueryProc;
import com.lifeshs.product.service.common.impl.Paging;
import com.lifeshs.product.service.healthData.IPhysicalAnalysisService;
import com.lifeshs.product.service.healthData.IPhysicalService;
import com.lifeshs.product.utils.DateTimeUtilT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service(value = "physicalService")
public class PhysicalServiceImpl implements IPhysicalService {

    @Resource(name = "physicalDao")
    private IPhysicalDao physicalDao;

    @Resource(name = "physicalImgDao")
    private IPhysicalImgDao physicalImgDao;

    @Resource(name = "physicalAnalysisServiceImpl")
    private IPhysicalAnalysisService analysisService;

    @Override
    public Paging<PhysicalVO> listPhysical(int userId, int curPage, int pageSize) {
        Paging<PhysicalVO> p = new Paging<>(curPage, pageSize);
        p.setQueryProc(new IPagingQueryProc<PhysicalVO>() {

            @Override
            public int queryTotal() {
                return physicalDao.countPhysical(userId);
            }

            @Override
            public List<PhysicalVO> queryData(int startRow, int pageSize) {
                return physicalDao.findPhysicalListByUserId(userId, startRow, pageSize);
            }
        });

        return p;
    }

    @Override
    public PhysicalVO getPhysical(int id, int userId) {
        return physicalDao.findPhysicalByIdAndUserId(id, userId);
    }

    @Override
    public PhysicalVO getPhysical(int id) {
        return physicalDao.getPhysical(id);
    }

    @Override
    public void addPhysical(PhysicalVO physical) throws BaseException {
        Integer userId = physical.getUserId();
        String physicalOrg = physical.getPhysicalsOrg();
        Date physicalDate = physical.getPhysicalsDate();
        String description = physical.getDescription();

        if (userId == null) {
            throw new ParamException("用户id不能为空", ErrorCodeEnum.MISSING);
        }
        if (physicalDate == null) {
            throw new ParamException("体检日期不能为空", ErrorCodeEnum.MISSING);
        }
        if (StringUtils.isBlank(physicalOrg)) {
            throw new ParamException("体检机构不能为空", ErrorCodeEnum.MISSING);
        }

        if (physicalOrg.length() > 30) {
            throw new ParamException("体检机构字数不能超过30个字符", ErrorCodeEnum.FORMAT);
        }

        if (description.length() > 150) {
            throw new ParamException("体检报告描述内容不能超过150个字符", ErrorCodeEnum.FORMAT);
        }

        // 设置体检报告的信息
        String title = DateTimeUtilT.dateCN(physicalDate) + "体检报告";

        PhysicalPO physicalPO = new PhysicalPO();
        physicalPO.setUserId(userId);
        physicalPO.setPhysicalsOrg(physicalOrg);
        physicalPO.setPhysicalsDate(physicalDate);
        physicalPO.setDescription(description);
        physicalPO.setTitle(title);
        int resultPhysical = physicalDao.addPhysical(physicalPO);
        if (resultPhysical == 0) {
            throw new OperationException("添加体检报告失败", ErrorCodeEnum.FAILED);
        }

        physical.setId(physicalPO.getId());

        List<PhysicalImgPO> imgList = physical.getImgList();
        // 对于没有图片的体检报告就不作保存图片的处理
        if (imgList == null || imgList.isEmpty()) {
            return;
        }

        List<String> imgStrList = new ArrayList<>();
        for (PhysicalImgPO img : imgList) {
            imgStrList.add(img.getImg());
        }
        int resultImg = physicalImgDao.addPhysicalImgList(imgStrList, physicalPO.getId());
        if (resultImg != imgList.size()) {
            throw new OperationException("添加体检报告图片失败", ErrorCodeEnum.NOT_COMPLETE);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = OperationException.class)
    public void deletePhysical(int id, int userId) throws OperationException {
        int result = physicalDao.delPhysicalByIdAndUserId(id, userId);
        if (result == 0) {
            throw new OperationException("删除体检报告失败", ErrorCodeEnum.FAILED);
        }

        // 删除分析
        analysisService.deleteAnalysis(id);
        // 体检报告可能不包含图片，删除返回的effectRow不能作为是否删除成功的条件
        physicalImgDao.delImgByPhysicalIdList(id);
    }

    @Override
    public void updatePhysical(PhysicalVO physical) throws BaseException {
        boolean needUpdate = false; // 是否需要进行更新操作

        Integer userId = physical.getUserId();
        Integer physicalId = physical.getId();
        String physicalOrg = physical.getPhysicalsOrg();
        Date physicalDate = physical.getPhysicalsDate();
        String description = physical.getDescription();

        if (userId == null) {
            throw new ParamException("用户id不能为空", ErrorCodeEnum.MISSING);
        }
        if (physicalId == null) {
            throw new ParamException("体检报告id不能为空", ErrorCodeEnum.MISSING);
        }
        if (physicalOrg != null && physicalOrg.length() > 30) {
            throw new ParamException("体检机构字数不能超过30个字符", ErrorCodeEnum.FORMAT);
        }
        if (description != null && description.length() > 150) {
            throw new ParamException("体检报告描述内容不能超过150个字符", ErrorCodeEnum.FORMAT);
        }

        // 设置体检报告的信息
        String title = null;
        if (physicalDate != null) {
            title = DateTimeUtilT.dateCN(physicalDate) + "体检报告";
        }

        // 判断是否需要进行图片修改操作
        List<PhysicalImgPO> imgList = physical.getImgList();
        if (imgList != null && !imgList.isEmpty()) {
            needUpdate = true;  // 需要更新图片操作

            // 需要删除的id
            List<Integer> deleteIdList = new ArrayList<>();
            // 需要更新的图片
            List<PhysicalImgPO> updateImgList = new ArrayList<>();
            // 需要添加的图片
            List<PhysicalImgPO> addImgList = new ArrayList<>();

            Iterator<PhysicalImgPO> iterator = imgList.iterator();
            while (iterator.hasNext()) {
                PhysicalImgPO img = iterator.next();
                // 删除id不为空，img值为null的记录
                if (img.getId() != null && img.getImg() == null) {
                    deleteIdList.add(img.getId());
                    iterator.remove();
                    continue;
                }
                // 更新id不为空，img值不为null的记录
                if (img.getId() != null && img.getImg() != null) {
                    updateImgList.add(img);
                    iterator.remove();
                    continue;
                }
                // 添加id为空的记录
                if (img.getId() == null) {
                    addImgList.add(img);
                    iterator.remove();
                    continue;
                }
            }

            // 删除图片
            if (!deleteIdList.isEmpty()) {
                int resultImg = physicalImgDao.delImgList(deleteIdList);
                if (resultImg != deleteIdList.size()) {
                    throw new OperationException("删除体检报告图片失败", ErrorCodeEnum.NOT_COMPLETE);
                }
            }

            // 更新图片
            if (!updateImgList.isEmpty()) {
                int resultImg = physicalImgDao.updateImgList(updateImgList, physicalId);
                if (resultImg != updateImgList.size()) {
                    throw new OperationException("更新体检报告图片失败", ErrorCodeEnum.NOT_COMPLETE);
                }
            }

            // 添加图片
            if (!addImgList.isEmpty()) {
                List<String> imgStrList = new ArrayList<>();
                for (PhysicalImgPO img : addImgList) {
                    imgStrList.add(img.getImg());
                }
                int resultImg = physicalImgDao.addPhysicalImgList(imgStrList, physicalId);
                if (resultImg != addImgList.size()) {
                    throw new OperationException("添加体检报告图片失败", ErrorCodeEnum.NOT_COMPLETE);
                }
            }
        }
        // title org date description
        if (title != null || physicalOrg != null || physicalDate != null || description != null) {
            needUpdate = true;  // 需要更新体检报告操作
        }

        if (needUpdate) {
            PhysicalPO physicalPO = new PhysicalPO();
            physicalPO.setId(physicalId);
            physicalPO.setUserId(userId);
            physicalPO.setPhysicalsOrg(physicalOrg);
            physicalPO.setPhysicalsDate(physicalDate);
            physicalPO.setDescription(description);
            physicalPO.setTitle(title);
            int result = physicalDao.updatePhysical(physicalPO);
            if (result == 0) {
                throw new OperationException("更新体检报告失败", ErrorCodeEnum.FAILED);
            }
        }
    }
}

