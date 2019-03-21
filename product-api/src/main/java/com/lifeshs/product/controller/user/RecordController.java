package com.lifeshs.product.controller.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lifeshs.product.common.constants.jsonAttribute.base.Normal;
import com.lifeshs.product.common.constants.jsonAttribute.base.Page;
import com.lifeshs.product.common.constants.jsonAttribute.record.*;
import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.constants.jsonAttribute.user.User;
import com.lifeshs.product.common.exception.base.BaseException;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.common.exception.base.ParamException;
import com.lifeshs.product.domain.dto.record.MedicalDTO;
import com.lifeshs.product.domain.dto.common.ImageDTO;
import com.lifeshs.product.domain.dto.common.aop.AppJSON;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.po.record.TRecordSport;
import com.lifeshs.product.domain.po.record.TRecordSportDetail;
import com.lifeshs.product.domain.vo.record.MedicalCourseImgPO;
import com.lifeshs.product.domain.vo.record.PhysicalAnalysisPO;
import com.lifeshs.product.domain.vo.record.PhysicalImgPO;
import com.lifeshs.product.domain.vo.record.*;
import com.lifeshs.product.service.common.IAppRecordService;
import com.lifeshs.product.service.common.impl.AppNormalServiceImpl;
import com.lifeshs.product.service.healthData.IMedicalService;
import com.lifeshs.product.service.healthData.IPhysicalAnalysisService;
import com.lifeshs.product.service.healthData.IPhysicalService;
import com.lifeshs.product.utils.DateTimeUtilT;
import com.lifeshs.product.utils.ImageUtilV2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

/**
 * 应用app健康档案
 *
 * @author yuhang.weng
 * @DateTime 2017年2月21日 下午4:43:59
 */
@RestController(value = "appRecordController")
@RequestMapping(value = { "/app", "/app/record" })
@CrossOrigin(origins = "*")
public class RecordController {

    @Resource(name = "appRecordService")
    private IAppRecordService recordService;

    @Resource(name = "medicalService")
    private IMedicalService medicalService;

    @Resource(name = "physicalAnalysisServiceImpl")
    private IPhysicalAnalysisService physicalAnalysisService;

    @Resource(name = "physicalService")
    private IPhysicalService physicalService;



    /** 健康档案上传文件统一目录 */
    private static final String RECORD_UPLOAD_CATEGORY_PATH = "record";

    /**
     * 获取病历列表
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日上午11:33:47
     * @serverComment
     * @param
     */
    @RequestMapping(value = "getMedicalRecordsList", method = RequestMethod.POST)
    public JSONObject getMedicalRecordsList(@RequestBody String json) throws Exception {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();

        int pageIndex = mm_0.getIntValue(Page.INDEX);// 页码
        int pageSize = mm_0.getIntValue(Page.SIZE);// 每页行数
        if(mm_0.containsKey(User.ID)) {
            userId = mm_0.getIntValue(User.ID);
        }

        List<MedicalBasicVO> medicalList = medicalService.listMedicalBasic(userId, pageIndex, pageSize).getData();
        List<Map<String, Object>> returnDataList = new ArrayList<>();
        for (MedicalBasicVO m : medicalList) {
            Map<String, Object> returnData = new HashMap<>();
            returnData.put(Medical.ID, m.getId());
            returnData.put(Medical.DEPARTMENTS, m.getDepartments());
            returnData.put(Medical.DOCTOR_DIAGNOSIS, m.getDoctorDiagnosis());
            returnData.put(Medical.VISITING_DATE, m.getVisitingDate());
            returnData.put(Medical.BASIC_CONDITION, m.getBasicCondition());
            returnData.put(Medical.TITLE, m.getTitle());
            returnData.put(Medical.HOSPITAL, m.getHospital());
            returnDataList.add(returnData);
        }

        return AppNormalServiceImpl.success(returnDataList, true);
    }


    /**
     * 获取病历详细
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午2:44:02
     * @serverComment
     * @param
     */
    @RequestMapping(value = "getMedicalRecords", method = RequestMethod.POST)
    public JSONObject getMedicalRecords(@RequestBody String json) {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();

        int id = mm_0.getIntValue(Medical.ID);// 病历的ID
        if(mm_0.containsKey(User.ID)) {
            userId = mm_0.getIntValue(User.ID);
        }

        MedicalVO medical = medicalService.getMedical(id, userId);
        Map<String, Object> returnData = enclosureMedical(medical);
        return AppNormalServiceImpl.success(returnData, true);
    }

    /**
     *  封装病历数据
     *  @author yuhang.weng
     *  @DateTime 2017年9月14日 下午2:55:10
     *
     *  @param data
     *  @return
     */
    private Map<String, Object> enclosureMedical(MedicalVO data) {
        Map<String, Object> returnData = new HashMap<>();
        if (data == null) {
            return returnData;
        }
        String doctorDiagnosis = data.getDoctorDiagnosis();
        // 若长度大于30,取前三十个字符
        if (doctorDiagnosis.length() > 30) {
            doctorDiagnosis = doctorDiagnosis.substring(0, 30);
        }
        returnData.put(Medical.ID, data.getId());
        returnData.put(Medical.TITLE, data.getTitle());
        returnData.put(Medical.VISITING_DATE, data.getVisitingDate());
        returnData.put(Medical.HOSPITAL, data.getHospital());
        returnData.put(Medical.DEPARTMENTS, data.getDepartments());
        returnData.put(Medical.DOCTOR_DIAGNOSIS, doctorDiagnosis);
        returnData.put(Medical.BASIC_CONDITION, data.getBasicCondition());

        List<Map<String, Object>> returnDataCourseList = enclosureMedicalCourseList(data.getCourseList());
        returnData.put(MedicalCourse.COURSE, returnDataCourseList);
        return returnData;
    }

    /**
     *  封装病程数据
     *  @author yuhang.weng
     *  @DateTime 2017年9月14日 下午2:55:21
     *
     *  @param dataList 病程集合
     *  @return
     */
    private List<Map<String, Object>> enclosureMedicalCourseList(List<MedicalCourseVO> dataList) {
        List<Map<String, Object>> returnDataList = new ArrayList<>();
        // 数据封装病程
        for (MedicalCourseVO m : dataList) {
            Map<String, Object> returnDataCourseMap = enclosureMedicalCourse(m);
            returnDataList.add(returnDataCourseMap);
        }
        return returnDataList;
    }

    /**
     *  封装病程数据
     *  @author yuhang.weng
     *  @DateTime 2017年9月15日 上午11:57:04
     *
     *  @param data 一个病程
     *  @return
     */
    private Map<String, Object> enclosureMedicalCourse(MedicalCourseVO data) {
        Map<String, Object> returnDataCourseMap = new HashMap<>();
        returnDataCourseMap.put(MedicalCourse.ID, data.getId());
        returnDataCourseMap.put(MedicalCourse.TYPE, data.getCourseType());
        returnDataCourseMap.put(MedicalCourse.REMARK, data.getRemark());
        returnDataCourseMap.put(MedicalCourse.VISITING_DATE, data.getVisitingDate());

        // 数据封装图片
        List<Map<String, Object>> returnDataImgList = enclosureMedicalCourseImgList(data.getImgList());
        returnDataCourseMap.put(MedicalCourseImg.IMG, returnDataImgList);
        String img1 = null;
        String img2 = null;
        String img3 = null;
        for (int i = 0; i < data.getImgList().size(); i++) {
            if (i == 0) {
                img1 = data.getImgList().get(i).getImg();
            }
            if (i == 1) {
                img2 = data.getImgList().get(i).getImg();
            }
            if (i == 2) {
                img3 = data.getImgList().get(i).getImg();
            }
        }
        returnDataCourseMap.put(MedicalCourse.IMG1, img1);
        returnDataCourseMap.put(MedicalCourse.IMG2, img2);
        returnDataCourseMap.put(MedicalCourse.IMG3, img3);
        return returnDataCourseMap;
    }

    /**
     *  封装病程图片数据
     *  @author yuhang.weng
     *  @DateTime 2017年9月14日 下午2:55:48
     *
     *  @param dataList
     *  @return
     */
    private List<Map<String, Object>> enclosureMedicalCourseImgList(List<MedicalCourseImgPO> dataList) {
        List<Map<String, Object>> returnDataImgList = new ArrayList<>();
        for (MedicalCourseImgPO i : dataList) {
            Map<String, Object> returnDataImg = new HashMap<>();
            returnDataImg.put(MedicalCourseImg.ID, i.getId());
            returnDataImg.put(MedicalCourseImg.IMG_PATH, i.getImg());
            returnDataImg.put(MedicalCourseImg.CREATE_DATE, i.getCreateDate());
            returnDataImg.put(MedicalCourseImg.MODIFY_DATE, i.getModifyDate());
            returnDataImgList.add(returnDataImg);
        }
        return returnDataImgList;
    }

    /**
     * 添加个人病历
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午4:18:11
     * @serverComment
     * @param
     */
    @RequestMapping(value = "addMedicalRecords", method = RequestMethod.POST)
    public JSONObject addMedicalRecords(@RequestBody String json) throws Exception {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
/*
        String title = mm_0.getString(Medical.TITLE); // 标题
        String visitingDate = mm_0.getString(Medical.VISITING_DATE); // 每页行数
        Integer departmentId = mm_0.getInteger(Medical.DEPARTMENT_ID); // 科室id
        String doctorDiagnosis = mm_0.getString(Medical.DOCTOR_DIAGNOSIS); // 医生诊断
        String basicCondition = mm_0.getString(Medical.BASIC_CONDITION); // 基本病情
        String hospital = (String) mm_0.get(Medical.HOSPITAL);*/
        MedicalDTO medicalDTO =  new MedicalDTO();
        medicalDTO.setUserId(userId);
        medicalDTO.setDepartmentId(mm_0.getIntValue(Medical.DEPARTMENT_ID));
        medicalDTO.setDoctorDiagnosis(mm_0.getString(Medical.DOCTOR_DIAGNOSIS));
        medicalDTO.setTitle(mm_0.getString(Medical.TITLE));
        medicalDTO.setHospital(mm_0.getString(Medical.HOSPITAL));
        medicalDTO.setVisitingDate(mm_0.getString(Medical.VISITING_DATE));
        medicalDTO.setBasicCondition(mm_0.getString(Medical.BASIC_CONDITION));
        medicalDTO.setId(mm_0.getIntValue(Medical.ID));
        return recordService.addMedicalRecords(medicalDTO);
    }

    /**
     * 修改个人病历
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午5:26:54
     * @serverComment
     * @param json
     */
    @RequestMapping(value = "modifyMedicalRecords", method = RequestMethod.POST)
    public JSONObject modifyMedicalRecords(String json) throws Exception {
        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);

        MedicalDTO medicalDTO =  new MedicalDTO();
        medicalDTO.setUserId(userId);
        medicalDTO.setDepartmentId(mm_0.getIntValue(Medical.DEPARTMENT_ID));
        medicalDTO.setDoctorDiagnosis(mm_0.getString(Medical.DOCTOR_DIAGNOSIS));
        medicalDTO.setTitle(mm_0.getString(Medical.TITLE));
        medicalDTO.setHospital(mm_0.getString(Medical.HOSPITAL));
        medicalDTO.setVisitingDate(mm_0.getString(Medical.VISITING_DATE));
        medicalDTO.setBasicCondition(mm_0.getString(Medical.BASIC_CONDITION));
        medicalDTO.setId(mm_0.getIntValue(Medical.ID));
        return recordService.modifyMedicalRecords(medicalDTO);
    }

    /**
     * 删除个人病历
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午5:57:25
     * @serverComment
     * @param json
     */
    @RequestMapping(value = "delMedicalRecords", method = RequestMethod.POST)
    public JSONObject delMedicalRecords(@RequestBody String json) throws Exception {
        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);

        int id = mm_0.getIntValue(Medical.ID); // 病历ID
        return recordService.delMedicalRecords(userId,id);
    }

    /**
     * 获取所有科室
     *
     * @author yuhang.weng
     * @DateTime 2016年12月12日 上午10:26:07
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getMedicalDepartments", method = RequestMethod.POST)
    public JSONObject getMedicalDepartments(@RequestBody String json) {
        return recordService.getMedicalDepartments(json);
    }

    /**
     * 添加个人病程
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午7:53:18
     * @serverComment
     * @param
     */
    @RequestMapping(value = "addMedicalCourse", method = RequestMethod.POST)
    public JSONObject addMedicalCourse(@RequestBody String json) throws BaseException {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        Integer userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();

        int medicalId = mm_0.getIntValue("medicalId"); // 病历ID // TODO 特例
        String visitingDate = mm_0.getString(Medical.VISITING_DATE); // 就诊日期
        String courseType = mm_0.getString(Medical.COURSE_TYPE); // 病程类型
        String remark = mm_0.getString(Medical.REMARK); // 备注

        /**
         * 如果是版本 2.4.5，包括2.4.5之前的接口，使用的是3张BASE64的图片
         * 版本2.4.5之后的接口，使用9张图模式
         */
        List<MedicalCourseImgPO> imgList = new ArrayList<>();
        /** 这一段是兼容性代码 */
        String img1 = mm_0.getString(Medical.IMG1);
        String img2 = mm_0.getString(Medical.IMG2);
        String img3 = mm_0.getString(Medical.IMG3);
        boolean img1NotNull = (img1 != null);
        boolean img2NotNull = (img2 != null);
        boolean img3NotNull = (img3 != null);
        if (img1NotNull || img2NotNull || img3NotNull) {
            ImageDTO imageVO = null;
            if (img1NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img1, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            if (img2NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img2, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            if (img3NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img3, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            /** 兼容代码结尾 */
        } else {
            /** 2.4.5之后的9张图代码 */
            String imgStr = mm_0.getString(MedicalCourseImg.IMG);
            if (StringUtils.isNotBlank(imgStr)) {
                List<MedicalCourseImgVO> imgVOList = JSONArray.parseArray(imgStr, MedicalCourseImgVO.class);
                for (MedicalCourseImgVO i : imgVOList) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    // 图片需要移动到正式目录下
                    String nwNetPath = null;
                    try {
                        nwNetPath = ImageUtilV2.copyImgFileToUploadFolder(i.getImg(), RECORD_UPLOAD_CATEGORY_PATH, false);
                    } catch (IOException e) {
                        throw new ParamException("您提交的图片格式不正确", ErrorCodeEnum.FAILED);
                    }

                    imgPO.setImg(nwNetPath);
                    imgList.add(imgPO);
                }
            }
            /** 9张图代码结尾 */
        }

        Date mDate = null;
        if (StringUtils.isNotBlank(visitingDate)) {
            mDate = DateTimeUtilT.date(visitingDate);
        }

        MedicalCourseVO course = new MedicalCourseVO();
        course.setMedicalId(medicalId);
        course.setCourseType(courseType);
        course.setRemark(remark);
        course.setVisitingDate(mDate);
        course.setImgList(imgList);

        medicalService.addMedicalCourse(course, userId);

        // 返回对象
        MedicalCourseVO courseVO = medicalService.getMedicalCourse(course.getId(), userId);
        Map<String, Object> returnData = enclosureMedicalCourse(courseVO);
        return AppNormalServiceImpl.success(returnData);
    }

    /**
     * 修改个人病程
     *
     * @author wenxian.cai
     * @DateTime 2016年8月9日下午8:27:27
     * @serverComment
     * @param
     */
    @RequestMapping(value = "modifyMedicalCourse", method = RequestMethod.POST)
    public JSONObject modifyMedicalCourse(@RequestBody String json) throws BaseException {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();

        int id = mm_0.getIntValue(MedicalCourse.ID); // 病程ID
        String visitingDate = mm_0.getString(MedicalCourse.VISITING_DATE); // 就诊日期
        String courseType = mm_0.getString(MedicalCourse.TYPE); // 病程类型
        String remark = mm_0.getString(MedicalCourse.REMARK); // 备注

        /**
         * 如果是版本 2.4.5，包括2.4.5之前的接口，使用的是3张BASE64的图片
         * 版本2.4.5之后的接口，使用9张图模式
         */
        List<MedicalCourseImgPO> imgList = new ArrayList<>();
        /** 这一段是兼容性代码 */
        String img1 = mm_0.getString(Medical.IMG1);
        String img2 = mm_0.getString(Medical.IMG2);
        String img3 = mm_0.getString(Medical.IMG3);
        boolean img1NotNull = (img1 != null);
        boolean img2NotNull = (img2 != null);
        boolean img3NotNull = (img3 != null);
        if (img1NotNull || img2NotNull || img3NotNull) {
            ImageDTO imageVO = null;
            if (img1NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img1, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            if (img2NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img2, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            if (img3NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img3, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            /** 兼容代码结尾 */
        } else {
            /** 2.4.5之后的9张图代码 */
            String imgStr = mm_0.getString(MedicalCourseImg.IMG);
            if (StringUtils.isNotBlank(imgStr)) {
                List<MedicalCourseImgVO> imgVOList = JSONArray.parseArray(imgStr, MedicalCourseImgVO.class);
                for (MedicalCourseImgVO i : imgVOList) {
                    MedicalCourseImgPO imgPO = new MedicalCourseImgPO();
                    // 图片需要移动到正式目录下
                    String netPath = i.getImg();
                    if (StringUtils.isNotBlank(netPath)) {
                        try {
                            netPath = ImageUtilV2.copyImgFileToUploadFolder(netPath, RECORD_UPLOAD_CATEGORY_PATH, false);
                        } catch (IOException e) {
                            throw new ParamException("您提交的图片格式不正确", ErrorCodeEnum.FAILED);
                        }
                    }

                    imgPO.setId(i.getId());
                    imgPO.setImg(netPath);
                    imgList.add(imgPO);
                }
            }
            /** 9张图代码结尾 */
        }

        Date mDate = null;
        if (StringUtils.isNotBlank(visitingDate)) {
            mDate = DateTimeUtilT.date(visitingDate);
        }

        MedicalCourseVO course = new MedicalCourseVO();
        course.setId(id);
        course.setCourseType(courseType);
        course.setRemark(remark);
        course.setVisitingDate(mDate);
        course.setImgList(imgList);

        medicalService.updateMedicalCourse(course, userId);

        // 返回对象
        MedicalCourseVO courseVO = medicalService.getMedicalCourse(id, userId);
        Map<String, Object> returnData = enclosureMedicalCourse(courseVO);
        return AppNormalServiceImpl.success(returnData);
    }

    /**
     * 删除个人病程
     *
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午9:27:28
     * @serverComment
     * @param
     */
    @RequestMapping(value = "delMedicalCourse", method = RequestMethod.POST)
    public JSONObject delMedicalCourse(@RequestBody String json) throws OperationException {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        int id = appJSON.getData().getFirstJSONObject().getIntValue("id"); // 病程主键ID

        medicalService.deleteMedicalCourse(id, userId);
        return AppNormalServiceImpl.success();
    }


    /**
     * 获取个人体检报告列表
     *
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午9:47:43
     * @serverComment
     * @param json
     */
    @RequestMapping(value = "getPhysicalsList", method = RequestMethod.POST)
    public JSONObject getPhysicalsList(@RequestBody String json) {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();

        Integer pageIndex = mm_0.getInteger(Page.INDEX);// 页码
        Integer pageSize = mm_0.getInteger(Page.SIZE);// 每页行数
        if(mm_0.containsKey(User.ID)) {
            userId = mm_0.getIntValue(User.ID);
        }

        List<PhysicalVO> physicalList = physicalService.listPhysical(userId, pageIndex, pageSize).getData();
        // 封装数据
        List<Map<String, Object>> returnDataList = enclosurePhysicalList(physicalList);
        return AppNormalServiceImpl.success(returnDataList, true);
    }

    /**
     *  封装体检报告对象数据
     *  @author yuhang.weng
     *  @DateTime 2017年9月12日 下午2:40:27
     *
     *  @param dataList
     *  @return
     */
    private List<Map<String, Object>> enclosurePhysicalList(List<PhysicalVO> dataList) {
        List<Map<String, Object>> returnDataList = new ArrayList<>();
        for (PhysicalVO physical : dataList) {
            Map<String, Object> returnData = enclosurePhysical(physical);
            returnDataList.add(returnData);
        }
        return returnDataList;
    }

    /**
     *  封装体检报告对象数据
     *  @author yuhang.weng
     *  @DateTime 2017年9月12日 下午2:40:44
     *
     *  @param data
     *  @return
     */
    private Map<String, Object> enclosurePhysical(PhysicalVO data) {
        if (data == null) {
            return null;
        }

        String img1 = null;
        String img2 = null;
        String img3 = null;

        Map<String, Object> returnData = new HashMap<>();
        returnData.put(PhysicalRecord.ID, data.getId());
        returnData.put(PhysicalRecord.TITLE, data.getTitle());
        returnData.put(PhysicalRecord.ORG, data.getPhysicalsOrg());
        returnData.put(PhysicalRecord.DATE, data.getPhysicalsDate());
        returnData.put(PhysicalRecord.DESCRIPTION, data.getDescription());

        List<Map<String, Object>> returnImgList = new ArrayList<>();
        for (int i = 0; i < data.getImgList().size(); i++) {
            PhysicalImgPO img = data.getImgList().get(i);
            Map<String, Object> returnImg = new HashMap<>();
            returnImg.put(PhysicalImg.ID, img.getId());
            returnImg.put(PhysicalImg.IMG_PATH, img.getImg());
            returnImg.put(PhysicalImg.CREATE_DATE, img.getCreateDate());
            returnImg.put(PhysicalImg.MODIFY_DATE, img.getModifyDate());
            returnImgList.add(returnImg);

            /** 兼容代码开始(版本<=2.4.5) */
            if (i == 0) {
                img1 = img.getImg();
            }
            if (i == 1) {
                img2 = img.getImg();
            }
            if (i == 2) {
                img3 = img.getImg();
            }
            /** 兼容代码结束 */
        }

        returnData.put(PhysicalRecord.IMG, returnImgList);

        /** 兼容代码开始(版本<=2.4.5) */
        returnData.put(PhysicalRecord.IMG1, img1);
        returnData.put(PhysicalRecord.IMG2, img2);
        returnData.put(PhysicalRecord.IMG3, img3);
        /** 兼容代码结束 */

        List<Map<String, Object>> returnAnalysisList = new ArrayList<>();
        for (PhysicalAnalysisPO a : data.getAnalysisList()) {
            Map<String, Object> returnAnalysis = new HashMap<>();
            returnAnalysis.put("doctorSign", a.getDoctorSign());
            returnAnalysis.put("content", a.getReply());
            returnAnalysisList.add(returnAnalysis);
        }
        returnData.put(PhysicalRecord.ANALYSIS, returnAnalysisList);

        return returnData;
    }

    /**
     * 获取体检报告详细
     *
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午10:23:44
     * @serverComment
     * @param
     */
    @RequestMapping(value = "getPhysicals", method = RequestMethod.POST)
    public JSONObject getPhysicals(@RequestBody String json) {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        int id = mm_0.getIntValue(PhysicalRecord.ID);// 体检报告主键ID

        PhysicalVO physical = physicalService.getPhysical(id, userId);
        // 数据封装
        Map<String, Object> returnData = enclosurePhysical(physical);
        return AppNormalServiceImpl.success(returnData, true);
    }

    /**
     * 添加个人体检报告
     *
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午10:40:10
     * @serverComment
     * @param json
     */
    @RequestMapping(value = "addPhysicals", method = RequestMethod.POST)
    public JSONObject addPhysicals(@RequestBody  String json) throws BaseException {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String physicalsOrg = mm_0.getString(PhysicalRecord.ORG); // 就诊日期
        String physicalsDate = mm_0.getString(PhysicalRecord.DATE); // 病程类型
        String descript = mm_0.getString("remark");
        if (descript == null) {
            // remark 与 PhysicalRecord.DESCRIPTION 都属于描述字段
            descript = mm_0.getString(PhysicalRecord.DESCRIPTION);
        }

        /**
         * 如果是版本 2.4.5，包括2.4.5之前的接口，使用的是3张BASE64的图片
         * 版本2.4.5之后的接口，使用9张图模式
         */
        List<PhysicalImgPO> imgList = new ArrayList<>();
        /** 这一段是兼容性代码 */
        String img1 = mm_0.getString(PhysicalRecord.IMG1);
        String img2 = mm_0.getString(PhysicalRecord.IMG2);
        String img3 = mm_0.getString(PhysicalRecord.IMG3);
        boolean img1NotNull = (img1 != null);
        boolean img2NotNull = (img2 != null);
        boolean img3NotNull = (img3 != null);
        if (img1NotNull || img2NotNull || img3NotNull) {
            ImageDTO imageVO = null;
            if (img1NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img1, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    PhysicalImgPO imgPO = new PhysicalImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            if (img2NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img2, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    PhysicalImgPO imgPO = new PhysicalImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            if (img3NotNull) {
                imageVO = AppNormalServiceImpl.uploadPhoto(img3, null, RECORD_UPLOAD_CATEGORY_PATH, false);
                if (imageVO.getUploadSuccess()) {
                    PhysicalImgPO imgPO = new PhysicalImgPO();
                    imgPO.setImg(imageVO.getNetPath());
                    imgList.add(imgPO); // 添加一张图片
                }
            }
            /** 兼容代码结尾 */
        } else {
            /** 2.4.5之后的9张图代码 */
            String imgStr = mm_0.getString(PhysicalRecord.IMG);
            if (StringUtils.isNotBlank(imgStr)) {
                List<PhysicalImgVO> imgVOList = JSONArray.parseArray(imgStr, PhysicalImgVO.class);
                for (PhysicalImgVO i : imgVOList) {
                    PhysicalImgPO imgPO = new PhysicalImgPO();
                    // 图片需要移动到正式目录下
                    String nwNetPath = null;
                    try {
                        nwNetPath = ImageUtilV2.copyImgFileToUploadFolder(i.getImg(), RECORD_UPLOAD_CATEGORY_PATH, false);
                    } catch (IOException e) {
                        throw new ParamException("您提交的图片格式不正确", ErrorCodeEnum.FAILED);
                    }
                    imgPO.setImg(nwNetPath);
                    imgList.add(imgPO);
                }
            }
            /** 9张图代码结尾 */
        }

        Date pDate = null;
        if (physicalsDate != null) {
            pDate = DateTimeUtilT.date(physicalsDate);
        }

        PhysicalVO physical = new PhysicalVO();
        physical.setUserId(userId);
        physical.setPhysicalsOrg(physicalsOrg);
        physical.setPhysicalsDate(pDate);
        physical.setDescription(descript);
        physical.setImgList(imgList);

        physicalService.addPhysical(physical);

        PhysicalVO nwPhysical = physicalService.getPhysical(physical.getId(), userId);
        // 封装数据
        Map<String, Object> returnData = enclosurePhysical(nwPhysical);
        return AppNormalServiceImpl.success(returnData);
    }

    /**
     *  修改体检报告阅读状态为已读
     *  @author yuhang.weng
     *  @DateTime 2017年10月27日 上午11:26:56
     *
     *  @param json
     *  @return
     *  @throws OperationException
     */
    @RequestMapping(value = "readPhysicalAnalysis", method = RequestMethod.POST)
    public JSONObject readPhysicalAnalysis(@RequestBody AppJSON json) throws OperationException {
        /*AppJSON appJSON = AppNormalService.parseAppJSON(json);*/
        JSONObject mm_0 = json.getData().getFirstJSONObject();
        int id = mm_0.getIntValue(PhysicalRecord.ID);

        physicalAnalysisService.readAnalysisList(id);
        return AppNormalServiceImpl.success();
    }



    /**
     * 修改个人体检报告
     *
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午11:00:09
     * @serverComment
     * @param
     */
    @RequestMapping(value = "modifyPhysicals", method = RequestMethod.POST)
    public JSONObject modifyPhysicals(@RequestBody String json) throws BaseException {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();

        int id = mm_0.getIntValue(PhysicalRecord.ID); // 体检报告主键ID
        String physicalsOrg = mm_0.getString(PhysicalRecord.ORG); // 就诊日期
        String physicalsDate = mm_0.getString(PhysicalRecord.DATE); // 病程类型
        String descript = mm_0.getString("remark");
        if (descript == null) {
            // remark 与 PhysicalRecord.DESCRIPTION 都属于描述字段
            descript = mm_0.getString(PhysicalRecord.DESCRIPTION);
        }
        String imgStr = mm_0.getString(PhysicalRecord.IMG);

        List<PhysicalImgPO> imgList = new ArrayList<>();
        if (StringUtils.isNotBlank(imgStr)) {
            List<PhysicalImgVO> imgVOList = JSONArray.parseArray(imgStr, PhysicalImgVO.class);
            for (PhysicalImgVO i : imgVOList) {
                PhysicalImgPO imgPO = new PhysicalImgPO();
                String netPath = i.getImg();
                if (StringUtils.isNotBlank(netPath)) {
                    // 图片需要移动到正式目录下
                    try {
                        netPath = ImageUtilV2.copyImgFileToUploadFolder(netPath, RECORD_UPLOAD_CATEGORY_PATH, false);
                    } catch (IOException e) {
                        throw new ParamException("您提交的图片格式不正确", ErrorCodeEnum.FAILED);
                    }
                }
                imgPO.setImg(netPath);
                imgPO.setId(i.getId());
                imgList.add(imgPO);
            }
        }

        Date pDate = null;
        if (StringUtils.isNotBlank(physicalsDate)) {
            pDate = DateTimeUtilT.date(physicalsDate);
        }
        PhysicalVO physical = new PhysicalVO();
        physical.setId(id);
        physical.setUserId(userId);
        physical.setPhysicalsOrg(physicalsOrg);
        physical.setPhysicalsDate(pDate);
        physical.setDescription(descript);
        physical.setImgList(imgList);

        physicalService.updatePhysical(physical);

        PhysicalVO nwPhysical = physicalService.getPhysical(id, userId);
        // 数据封装
        Map<String, Object> returnData = enclosurePhysical(nwPhysical);
        return AppNormalServiceImpl.success(returnData);
    }

    /**
     * 删除个人体检报告
     *
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午11:06:19
     * @serverComment
     * @param json
     */
    @RequestMapping(value = "delPhysicals", method = RequestMethod.POST)
    public JSONObject delPhysicals(@RequestBody String json) throws OperationException {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        int userId = appJSON.getData().getUserId();
        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        int id = mm_0.getIntValue(PhysicalRecord.ID); // 体检报告主键ID
        physicalService.deletePhysical(id, userId);
        return AppNormalServiceImpl.success();
    }


    /**
     * 获取健康日记能量列表
     *
     * @author yuhang.weng
     * @DateTime 2016年10月22日 上午9:12:58
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getDietKcal", method = RequestMethod.POST)
    public JSONObject getDietKcal(@RequestBody  String json) {

        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);
        String startDate = mm_0.getString(Diet.START_DATE);
        String endDate = mm_0.getString(Diet.END_DATE);
        return recordService.getDietKcal( userId, startDate, endDate);
    }

    /**
     * 获取健康日记能量列表
     *
     * @author yuhang.weng
     * @DateTime 2017年2月21日 下午5:05:17
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getDiaryEnergy", method = RequestMethod.POST)
    public JSONObject getDiaryEnergy(@RequestBody  String json) {

        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);
        String startDate = mm_0.getString(Diet.START_DATE);
        String endDate = mm_0.getString(Diet.END_DATE);
        if(mm_0.containsKey(User.ID))
            userId = mm_0.getIntValue(User.ID);

        return recordService.getDiaryEnergy(userId,startDate,endDate);
    }

    /**
     * 获取健康日记(分页)
     *
     * @author yuhang.weng
     * @DateTime 2016年10月28日 上午11:41:01
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getDiets", method = RequestMethod.POST)
    public JSONObject getDiets(@RequestBody  String json) {

        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);

        int pageIndex = mm_0.getIntValue(Page.INDEX);
        int pageSize = mm_0.getIntValue(Page.SIZE);
        if(mm_0.containsKey(User.ID))
            userId = mm_0.getIntValue(User.ID);


        return recordService.getDiets(userId,pageIndex,pageSize);
    }

    /**
     * 获取健康日记详细
     *
     * @author wenxian.cai
     * @DateTime 2016年8月19日上午9:55:27
     * @serverComment
     * @param
     */
    @RequestMapping(value = "getDiet", method = RequestMethod.POST)
    public JSONObject getDiet(@RequestBody  String json) throws Exception {
        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);

        String recordDate = mm_0.getString(Diet.RECORD_DATE); // 档案日期

        return recordService.getDiet(userId,recordDate);
    }

    /**
     * 获取健康日记详细
     *
     * @author yuhang.weng
     * @DateTime 2017年2月21日 下午5:06:03
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getHealthDiary", method = RequestMethod.POST)
    public JSONObject getHealthDiary(@RequestBody String json) {

        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);

        MemberUserDTO user = appJSON.getAopData().getUser();
        int userId = user.getId();

        JSONObject mm_0 = appJSON.getData().getFirstJSONObject();
        String recordDate = mm_0.getString(Diet.RECORD_DATE);
        if(mm_0.containsKey(User.ID))
            userId = mm_0.getIntValue(User.ID);


        return recordService.getHealthDiary(userId,recordDate);
    }

    /**
     * 添加健康日记
     *
     * @author wenxian.cai
     * @DateTime 2016年8月19日上午9:56:51
     * @serverComment
     * @param
     */
    @RequestMapping(value = "addDiet", method = RequestMethod.POST)
    public JSONObject addDiet(@RequestBody  String json) throws Exception {

        String msg = "添加健康日记失败";

        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        int userId = data.getIntValue(User.ID);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);

        JSONObject mm_0 = mm.getJSONObject(0);

        String dietType = mm_0.getString(Diet.TYPE); // 饮食类型
        Time dietTime = new Time(DateTimeUtilT.time(mm_0.getString(Diet.TIME)).getTime()); //
        int energy = mm_0.getIntValue(Diet.ENERGY);
        JSONArray foods = mm_0.getJSONArray(Diet.FOODS);

        Date date_new = new Date();
        // dateFormat.format(date);
        String date = DateTimeUtilT.date(date_new);

        return recordService.addDiet(userId,dietType,dietTime,energy,foods,date,msg);
    }

    /**
     * 添加运动记录
     *
     * @author yuhang.weng
     * @DateTime 2017年2月21日 下午5:06:42
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "addSport", method = RequestMethod.POST)
    public JSONObject addSport(@RequestBody String json) {
        AppJSON appJSON = AppNormalServiceImpl.parseAppJSON(json);
        MemberUserDTO user = appJSON.getAopData().getUser();

        JSONArray mm = appJSON.getData().getMsg();
        for (int i = 0; i < mm.size(); i++) {
            JSONObject mm_i = mm.getJSONObject(i);

            String st = mm_i.getString(RecordSport.START_TIME);
            JSONArray details = mm_i.getJSONArray(RecordSportDetail.DETAILS);

            Time startTime = new Time(DateTimeUtilT.time(st).getTime());

            List<TRecordSportDetail> sportDetails = new ArrayList<>();
            for (int j = 0; j < details.size(); j++) {
                TRecordSportDetail sportDetail = new TRecordSportDetail();
                JSONObject detail = details.getJSONObject(j);
                sportDetail.setDuration(detail.getInteger(RecordSportDetail.DURATION));
                sportDetail.setSportId(detail.getInteger(Sport.ID));

                sportDetails.add(sportDetail);
            }

            TRecordSport recordSport = new TRecordSport();
            recordSport.setUserId(user.getId());
            recordSport.setStartTime(startTime);
            recordSport.setDetails(sportDetails);
            recordSport.setRecordDate(new Date());

            recordService.addSport(recordSport);
        }

        return null;
    }

    /**
     * 获取运动记录的所有运动
     *
     * @author yuhang.weng
     * @DateTime 2017年2月21日 下午5:07:01
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getAllSport", method = RequestMethod.POST)
    public JSONObject getAllSport(@RequestBody String json) {
        return recordService.getAllSport(json);
    }

    /**
     * 获取全部食物类型
     *
     * @author wenxian.cai
     * @DateTime 2016年8月20日上午10:23:37
     * @serverComment
     * @param
     */
    @RequestMapping(value = "getFoodType", method = RequestMethod.POST)
    public JSONObject getFoodType(@RequestBody  String json) throws Exception {
        return recordService.getFoodType(json);
    }

    /**
     * 获取食物详细
     *
     * @author wenxian.cai
     * @DateTime 2016年8月20日上午11:00:34
     * @serverComment
     * @param
     */
    @RequestMapping(value = "getFood", method = RequestMethod.POST)
    public JSONObject getFood(@RequestBody  String json) throws Exception {
        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);
        int foodType = mm.getJSONObject(0).getIntValue("foodType"); // 类型

        return recordService.getFood(foodType);
    }

    /**
     * 获取食物类型以及食物列表
     *
     * @author yuhang.weng
     * @DateTime 2016年10月25日 下午7:08:21
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "getFoodTypeWithFoods", method = RequestMethod.POST)
    public JSONObject getFoodTypeWithFoods(@RequestBody  String json) {
        return recordService.getFoodTypeWithFoods();
    }

    /**
     * 修改健康日记
     *
     * @author wenxian.cai
     * @DateTime 2016年8月19日上午9:57:32
     * @serverComment
     * @param
     */
    @RequestMapping(value = "modifyDiet", method = RequestMethod.POST)
    public JSONObject modifyDiet(@RequestBody  String json) throws Exception {
        return recordService.modifyDiet(json);
    }

    /**
     * 删除健康日记
     *
     * @author wenxian.cai
     * @DateTime 2016年8月19日上午9:58:04
     * @serverComment
     * @param
     */
    @RequestMapping(value = "delDiet", method = RequestMethod.POST)
    public JSONObject delDiet(@RequestBody  String json) throws Exception {
        JSONObject root = JSONObject.parseObject(json);
        JSONObject data = root.getJSONObject(Normal.DATA);
        JSONArray mm = data.getJSONArray(Normal.MESSAGE);
        int id = (int) mm.getJSONObject(0).getIntValue("id"); // 饮食id
        return recordService.delDiet(id);
    }
}
