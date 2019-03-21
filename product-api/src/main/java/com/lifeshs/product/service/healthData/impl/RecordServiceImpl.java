package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.exception.base.ParamException;
import com.lifeshs.product.dao.record.IRecordDao;
import com.lifeshs.product.domain.dto.common.PaginationDTO;
import com.lifeshs.product.domain.dto.common.QueryPageData;
import com.lifeshs.product.domain.dto.record.DietDetail;
import com.lifeshs.product.domain.po.record.*;
import com.lifeshs.product.service.common.transform.ICommonTrans;
import com.lifeshs.product.service.healthData.IRecordService;
import com.lifeshs.product.utils.DateTimeUtilT;
import com.lifeshs.product.utils.ImageUtilV2;
import com.lifeshs.product.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Time;
import java.util.*;

@Component("record1")
public class RecordServiceImpl implements IRecordService {

    @Autowired
    ICommonTrans commonTrans;

    @Autowired
    IRecordDao recordDao;

    @Override
    public Object selectPhysicalsById(int Id) {
        TRecordPhysicals recordPhysicals = commonTrans.getEntity(TRecordPhysicals.class, Id);
        return recordPhysicals;
    }

    @Override
    public PaginationDTO selectPhysicalsByUserIdPageSplit(Integer userId, int page, int pageSize) {
        //PaginationDTO pagination = getPhysicalPageBarData(userId, page, pageSize);
        PaginationDTO pagination = new PaginationDTO();

        int totalSize = selectPhysicalsCountByUserId(userId);

        QueryPageData queryPageData = PaginationDTO.getQueryPageData(page, pageSize, totalSize);

        if (PaginationDTO.isDataOverFlow(page, pageSize, totalSize)) {
            pagination.setData(new ArrayList<Map<String, Object>>());
            return pagination;
        }

        int startIndex = queryPageData.getStartIndex();
        int totalPage = queryPageData.getTotalPage();
        page = queryPageData.getCurPage();

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("startIndex", startIndex);
        map.put("pageSize", pageSize);
        List<Map<String, Object>> list = recordDao.selectPhysicalsByUserIdPageSplit(map);

        // 封装分页信息
        pagination.setData(list);
        pagination.setNowPage(page);
        pagination.setTotalPage(totalPage);
        pagination.setTotalSize(totalSize);

        return pagination;
    }

    @Override
    public <T> Integer selectPhysicalsCountByUserId(Integer userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        int result = recordDao.selectPhysicalsCountByUserId(map);
        return result;
    }

    /**
     * @param recordPhysical
     * @return
     * @throws Exception
     * @author zhiguo.lin
     * @DateTime 2016年8月18日 下午8:19:42
     * @serverCode 服务代码
     * @serverComment 添加体检报告
     */

    @Override
    public Boolean addPhysicals(TRecordPhysicals recordPhysical) throws Exception {
        if (recordPhysical.getPhysicalsDate() == null || StringUtil.isBlank(recordPhysical.getPhysicalsOrg())) {
            throw new ParamException("体检日期和体检机构不能为空", ErrorCodeEnum.MISSING);
        }

        if (recordPhysical.getPhysicalsOrg().length() > 30) {
            throw new ParamException("体检机构字数不能超过30个字符", ErrorCodeEnum.FORMAT);
        }

        if (recordPhysical.getDescription().length() > 150) {
            throw new ParamException("体检报告描述内容不能超过150个字符", ErrorCodeEnum.FORMAT);
        }

        // 设置体检报告的信息
        String title = DateTimeUtilT.dateCN(recordPhysical.getPhysicalsDate()) + "体检报告";
        recordPhysical.setTitle(title);
        recordPhysical.setCreateDate(new Date());

        if (recordPhysical.getImg1() != null) {
            String destination = ImageUtilV2.copyImgFileToUploadFolder(recordPhysical.getImg1(), "record");
            recordPhysical.setImg1(destination);
        }
        if (recordPhysical.getImg2() != null) {
            String destination = ImageUtilV2.copyImgFileToUploadFolder(recordPhysical.getImg2(), "record");
            recordPhysical.setImg2(destination);
        }
        if (recordPhysical.getImg3() != null) {
            String destination = ImageUtilV2.copyImgFileToUploadFolder(recordPhysical.getImg3(), "record");
            recordPhysical.setImg3(destination);
        }
        return commonTrans.save(recordPhysical) > 0 ? true : false;
    }

    /**
     * @param target
     * @return
     * @throws IOException
     * @throws Exception
     * @author zhiguo.lin
     * @DateTime 2016年8月18日 下午8:21:53
     * @serverCode 服务代码
     * @serverComment 更新体检报告

     */
    @Override
    public Boolean updatePhysicals(TRecordPhysicals target) throws ParamException, IOException {
        TRecordPhysicals source = commonTrans.getEntity(TRecordPhysicals.class, target.getId());
        if (target.getPhysicalsDate() == null || target.getPhysicalsOrg().isEmpty()) {
            throw new ParamException("体检日期和体检机构不能为空", ErrorCodeEnum.FORMAT);
        }

        if (target.getPhysicalsOrg().length() > 30) {
            throw new ParamException("体检机构字数不能超过30个字符", ErrorCodeEnum.FORMAT);
        }

        String img1 = target.getImg1();
        String img2 = target.getImg2();
        String img3 = target.getImg3();

        if (img1 != null) { // 为null不进行任何操作
            if (img1.equals("")) {
                ImageUtilV2.delImg(source.getImg1());
                source.setImg1("");
            }
            if (!img1.equals("") && !img1.equals(source.getImg1())) { // !img1.equals("") 避免空字符串copyImgFileToUploadFolder
                ImageUtilV2.delImg(source.getImg1()); // 删除原图
                String destination = ImageUtilV2.copyImgFileToUploadFolder(target.getImg1(), "record");
                source.setImg1(destination);
            }
        }
        if (img2 != null) {
            if (img2.equals("")) {
                ImageUtilV2.delImg(source.getImg2());
                source.setImg2("");
            }
            if (!img2.equals("") && !img2.equals(source.getImg3())) {
                ImageUtilV2.delImg(source.getImg2());
                String destination = ImageUtilV2.copyImgFileToUploadFolder(target.getImg2(), "record");
                source.setImg2(destination);
            }
        }
        if (img3 != null) {
            if (img3.equals("")) {
                ImageUtilV2.delImg(source.getImg2());
                source.setImg3("");
            }
            if (!img3.equals("") && !img3.equals(source.getImg3())) {
                ImageUtilV2.delImg(source.getImg3());
                String destination = ImageUtilV2.copyImgFileToUploadFolder(target.getImg3(), "record");
                source.setImg3(destination);
            }
        }

        String title = DateTimeUtilT.dateCN(target.getPhysicalsDate()) + "体检报告";
        source.setTitle(title);
        source.setPhysicalsDate(target.getPhysicalsDate());
        source.setPhysicalsOrg(target.getPhysicalsOrg());
        source.setDescription(target.getDescription());
        return commonTrans.saveOrUpdate(source) > 0 ? true : false;

    }

    @Override
    public Integer deletePhysicals(Integer userId, Integer reportId) {

        // 获取用户的体检报告
        TRecordPhysicals recordPhysical = commonTrans.findUniqueByProperty(TRecordPhysicals.class, "id", reportId);
        int result = 0;
        // 删除该记录关联的图片
        if (recordPhysical != null) {

            if (!recordPhysical.getUserId().equals(userId)) {
                return 0;
            }
            result = commonTrans.deleteEntityById(TRecordPhysicals.class, reportId);
            if (result < 1) {
                return 0;
            } else {
                ImageUtilV2.delImg(recordPhysical.getImg1());
                ImageUtilV2.delImg(recordPhysical.getImg2());
                ImageUtilV2.delImg(recordPhysical.getImg3());
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> selectDietEnergyByUserIdWithDate(Integer userId, boolean customDate,
                                                                      String startDate, String endDate) {
        return recordDao.selectDietEnergyByUserIdWithDate(userId, customDate, startDate, endDate);
    }

    @Override
    public List<DietDetail> selectDietSplitWithRecordDate(int userId, int curPage, int pageSize) {
        List<DietDetail> diets = new ArrayList<>();

        int totalSize = recordDao.selectCountOfDietGroupByDate(userId);

        if (PaginationDTO.isDataOverFlow(curPage, pageSize, totalSize)) {
            return diets;
        }

        QueryPageData queryPageData = PaginationDTO.getQueryPageData(curPage, pageSize, totalSize);
        int startIndex = queryPageData.getStartIndex();
        curPage = queryPageData.getCurPage();

        List<String> recordDates = recordDao.selectDietDateGroupByDateWithPageSplit(userId, startIndex, pageSize);
        diets = recordDao.selectDietsWithDates(userId, recordDates);

        return diets;
    }

    @Override
    public List<Map<String, Object>> selectDietByUserIdWithDate(Integer userId, String date) {
        List<Map<String, Object>> list = recordDao.selectDietByUserIdWithDate(userId, date);
        return list;
    }


    @Override
    public <T> Integer addDiet(HashMap<String, Object> map, Integer userId) throws Exception {
        TRecordDiet recordDiet = new TRecordDiet();
        recordDiet.setUserId(userId);
        recordDiet.setDietType((String) map.get("dietType"));
        recordDiet.setDietTime((Time) map.get("dietTime"));
        recordDiet.setRecordDate(DateTimeUtilT.date((String) map.get("recordDate")));
        recordDiet.setEnergy((Integer) map.get("energy"));
        recordDiet.setCreateDate(new Date());
        commonTrans.save(recordDiet);
        return recordDiet.getId();
    }

    @Override
    public <T> Integer addDietFood(HashMap<String, Object> map, Integer dietId) {
        TRecordDietFood recordDietFood = new TRecordDietFood();
        recordDietFood.setDietId(dietId);
        recordDietFood.setKcal((float) (Integer) map.get("kcal"));
        recordDietFood.setFoodID((Integer) map.get("foodID"));
        recordDietFood.setFoodWeight((Integer) map.get("foodWeight"));
        recordDietFood.setCreateDate(new Date());
        int result = commonTrans.save(recordDietFood);
        return result;
    }

    @Override
    public List<TDataFoodKind> selectAllFoodKind() {

        return commonTrans.loadAll(TDataFoodKind.class);
    }

    @Override
    public List<TDataFood> selectAllFood() {
        return commonTrans.loadAll(TDataFood.class);
    }

    @Override
    public List<Map<String, Object>> selectFoodByKind(String kindName) {
        Map<String, Object> map = new HashMap<>();
        map.put("kindName", kindName);
        return recordDao.selectFoodByKind(map);
    }

    @Override
    public <T> Integer deleteDiet(Integer id) throws Exception {
        int result = commonTrans.deleteEntityById(TRecordDiet.class, id);
        return result;
    }

    @Override
    public <T> List<Map<String, Object>> selectDietFoodByDietId(Integer dietId) {
        List<Map<String, Object>> list = recordDao.selectDietFoodByDietId(dietId);
        return list;
    }

    @Override
    public <T> Integer deleteDietFood(Integer id) throws Exception {
        int result = commonTrans.deleteEntityById(TRecordDietFood.class, id);
        return result;
    }
}
