package com.lifeshs.product.service.healthData;

import com.lifeshs.product.common.exception.base.ParamException;
import com.lifeshs.product.domain.po.record.TDataFood;
import com.lifeshs.product.domain.po.record.TDataFoodKind;
import com.lifeshs.product.domain.po.record.TRecordPhysicals;
import com.lifeshs.product.domain.dto.common.PaginationDTO;
import com.lifeshs.product.domain.dto.record.DietDetail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRecordService {

    /**
     * 通过主键ID获取体检报告实体
     * @author wenxian.cai
     * @DateTime 2016年8月10日上午10:03:42
     * @serverComment
     * @param
     */
    public Object selectPhysicalsById(int Id);

    /**
     * 通过用户ID分页查询体检报告
     * @author wenxian.cai
     * @DateTime 2016年8月8日下午4:43:25
     * @serverComment
     * @param userId:用户ID
     * @param page:页码
     * @param pageSize:每页显示数量
     */
    public PaginationDTO selectPhysicalsByUserIdPageSplit(Integer userId, int page, int pageSize);

    /**
     * 添加体检报告
     * @author wenxian.cai
     * @DateTime 2016年8月8日下午4:56:32
     * @serverComment
     * @param
     */
    public Boolean addPhysicals(TRecordPhysicals recordPhysical) throws Exception;

    /**
     * 修改体检报告
     * @author wenxian.cai
     * @DateTime 2016年8月8日下午4:57:34
     * @serverComment
     * @param
     */
    public Boolean updatePhysicals(TRecordPhysicals recordPhysicals) throws ParamException, IOException;

    /**
     *  删除体检报告
     *  @author wenxian.cai
     *	@DateTime 2016年8月8日下午4:59:00
     *
     *	@param userId
     *  @param reportId
     *  @return
     */
    public Integer deletePhysicals(Integer userId, Integer reportId);

    /**
     * 查询最近这一周的饮食能量
     * @author wenxian.cai
     * @DateTime 2016年8月13日下午4:12:04
     * @serverComment
     * @param
     */
    public List<Map<String, Object>> selectDietEnergyByUserIdWithDate(Integer userId, boolean customDate, String startDate, String endDate);


    /**
     *  按照日期获取饮食记录
     *  @author yuhang.weng
     *	@DateTime 2016年10月28日 下午2:21:28
     *
     *  @param userId
     *  @param curPage
     *  @param pageSize
     *  @return
     */
    public List<DietDetail> selectDietSplitWithRecordDate(int userId, int curPage, int pageSize);

    /**
     * 通过用户id和日期查询饮食列表
     * @author wenxian.cai
     * @DateTime 2016年8月10日下午2:56:08
     * @serverComment
     * @param
     */
    public List<Map<String, Object>> selectDietByUserIdWithDate(Integer userID,String date);

    /**
     * 添加饮食
     * @author wenxian.cai
     * @DateTime 2016年8月10日下午2:48:10
     * @serverComment
     * @param
     */
    public <T> Integer addDiet(HashMap<String, Object> map, Integer userId) throws Exception;

    /**
     * 添加食物
     * @author wenxian.cai
     * @DateTime 2016年8月10日下午2:48:25
     * @serverComment
     */
    public <T> Integer addDietFood(HashMap<String, Object> map,Integer dietId);

    /**
     * 返回全部食物类型
     * @author wenxian.cai
     * @DateTime 2016年8月16日下午8:30:53
     * @serverComment
     * @param
     */
    public List<TDataFoodKind> selectAllFoodKind();

    /**
     * 获取全部食物信息
     * @author wenxian.cai
     * @DateTime 2016年8月20日上午11:19:46
     * @serverComment
     * @param
     */
    public List<TDataFood> selectAllFood();

    /**
     * 根据食物类型名称获取属于该类型的食物信息
     * @author wenxian.cai
     * @DateTime 2016年8月16日下午7:55:11
     * @serverComment
     * @param
     */
    public List<Map<String, Object>> selectFoodByKind(String kindName);

    /**
     * 删除饮食
     * @author wenxian.cai
     * @DateTime 2016年8月10日下午2:40:56
     * @serverComment
     * @param主键id
     */
    public <T> Integer deleteDiet(Integer id) throws Exception;

    /**
     * 通过饮食id查询食物列表
     * @author wenxian.cai
     * @DateTime 2016年8月10日下午2:57:23
     * @serverComment
     * @param
     */
    public <T> List<Map<String, Object>> selectDietFoodByDietId(Integer DietID);

    /**
     * @author wenxian.cai
     * @DateTime 2016年8月10日下午2:41:31
     * @serverComment
     * @param主键id
     */
    public <T> Integer deleteDietFood(Integer id) throws Exception;

    public <T> Integer selectPhysicalsCountByUserId(Integer userId);

}
