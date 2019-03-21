package com.lifeshs.product.service.user;

import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.domain.dto.user.GetMemberByHXData;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.vo.notice.PushMessagePO;
import com.lifeshs.product.domain.vo.notice.PushTaskMessagePo;
import com.lifeshs.product.domain.vo.user.User1PO;
import com.lifeshs.product.domain.vo.user.WarningUserVO;

import java.util.Date;
import java.util.List;


/**
 * 会员业务接口
 * Created by dengfeng on 2017/6/19 0019.
 */
public interface IMemberService1 {

    /**
     * @Description: 获取服务师所属的会员数量
     * @Author: wenxian.cai
     * @Date: 2017/6/13 15:42
     */
    int getMemberCountByEmployee(int orgUserId);

    /**
     * 统计门店的会员数量
     *
     * @param orgId
     * @return
     * @author yuhang.weng
     * @DateTime 2017年6月6日 下午2:07:46
     */
    int getMemberCountByStore(int orgId);

    /**
     * 获取门店的异常会员
     * dengfeng
     *
     * @param orgId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<WarningUserVO> findWainingMemberListByStore(int orgId, int pageIndex, int pageSize);

    /**
     * 获取服务师所属的异常会员
     * dengfeng
     *
     * @param employeeId 服务师
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<WarningUserVO> findWainingMemberListByEmployee(int employeeId, int pageIndex, int pageSize);

    /**
     * 根据环信ID获取用户信息
     *
     * @param huanxinUserNames
     * @return
     */
    List<GetMemberByHXData> getUsersByHuanxinId(List<String> huanxinUserNames);

    boolean readUserInfo(Integer userId, Integer orgUserId, String measureDate);

    /**
     * 修改用户备注和病种名
     *
     * @param userDiseasesName 病种名
     * @param userRemark 备注
     * @param orderId 订单ID
     * @return
     */
    boolean modifyMemberInfo(String userDiseasesName, String userRemark, Integer orderId);

    /**
     * 根据账号、姓名、电话号码获取用户列表
     * @param keyword 关键词
     * @return
     */
    List<MemberUserDTO> findUserList(String keyword);

    /**
     * 存储用户测量设备异常信息
     * @param userId 用户id
     * @param healthPackageType 设备类型
     * @param measureDate 测量时间
     * @exception OperationException
     * @return
     */
    void saveHealthpackageWarning(int userId, DeviceType healthPackageType, Date measureDate) throws OperationException;

    /**
     * 根据数组id获取用户列表
     * author: wenxian.cai
     * date: 2017/11/21 17:12
     */
    List<User1PO> listUserByIds(int[] ids);
    
    /**
     * 添加推送消息
     * @param pushMessage
     */
    PushMessagePO addMessage(PushMessagePO pushMessage);
    
    /**
     * 添加定时提醒推送
     * @param pushTaskMessage
     */
	void addPushTask(PushTaskMessagePo pushTaskMessage);
	
}
