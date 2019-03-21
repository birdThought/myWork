package com.lifeshs.product.thirdservice;

import com.lifeshs.product.common.constants.base.UserType;
import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.common.constants.notice.ActivityMemberEnum;
import com.lifeshs.product.common.constants.notice.MessageType;
import com.lifeshs.product.common.exception.base.ParamException;
import com.lifeshs.product.component.umeng.UMengPushUtil;
import com.lifeshs.product.component.umeng.util.CallBackDTO;
import com.lifeshs.product.component.umeng.util.Key;
import com.lifeshs.product.component.umeng.util.UMengOpenTypeEnum;
import com.lifeshs.product.dao.notice.IPushDao;
import com.lifeshs.product.domain.dto.common.PushTaskDTO;
import com.lifeshs.product.domain.dto.notice.MessageDTO;
import com.lifeshs.product.domain.dto.task.MeasureReminderTaskDTO;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.vo.notice.MeasureReminderTaskPo;
import com.lifeshs.product.domain.vo.notice.UserDeviceTokenPO;
import com.lifeshs.product.service.notice.IMessageService;
import com.lifeshs.product.service.notice.IPushDataService;
import com.lifeshs.product.service.user.IMemberService;
import com.lifeshs.product.utils.DateTimeUtilT;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 友盟消息推送
 * 
 * @author yuhang.weng
 * @version 1.0
 * @DateTime 2017年9月8日 上午11:06:54
 */
public class UMengPushService {

    @Autowired
    protected IMessageService messageService;

    @Resource(name = "memberPushDao")
    private IPushDao memberPushDao;

    @Resource(name = "pushDataService")
    private IPushDataService pushDataService;
    
    @Resource(name = "pushDataService")
    private IPushDataService pDataService;

    @Autowired
    protected IMemberService memberService;


    private Key appAndroidKey;
    private Key appIOSKey;
    private Key mappAndroidKey;
    private Key mappIOSKey;
    private String productionMode;
    private UMengPushUtil uMengPushUtil;
    private final Logger logger = Logger.getLogger(UMengPushService.class);
    private final int UM_SMS_MAX_NUM = 500;  //友盟单次发送短信最大为500条

    public UMengPushService(Key appAndroidKey, Key appIOSKey, Key mappAndroidKey, Key mappIOSKey,
            String productionMode) {
        this.appAndroidKey = appAndroidKey;
        this.appIOSKey = appIOSKey;
        this.mappAndroidKey = mappAndroidKey;
        this.mappIOSKey = mappIOSKey;
        this.productionMode = productionMode;
        this.uMengPushUtil = new UMengPushUtil(appAndroidKey, appIOSKey, mappAndroidKey, mappIOSKey, productionMode);
    }
    
    //平台手动批量推送消息
    public void manualPushBatch(List<UserDeviceTokenPO> list, String title, String content, UMengOpenTypeEnum openType, String openTargetOrUrl, String[] params){
        MessageType msgType = MessageType.SYSTEM;
        UserType userType =  UserType.member;  //此参数应由外面给入，暂时只实现member
        String openAttach = null,openTarget = null, openAttachIOS = null, openTargetIOS = null;
        
        //如果是URL和打开APP窗口，处理数据，TEXT无需处理
        if(openType == UMengOpenTypeEnum.URL){
            openTarget = openTargetOrUrl;
            openTargetIOS = openTargetOrUrl;
        }
        if(openType == UMengOpenTypeEnum.Activity){
            ActivityMemberEnum activityEnum = ActivityMemberEnum.valueOf(openTargetOrUrl);
            userType = UserType.parseOf(activityEnum.userType());
            msgType = MessageType.parseOf(activityEnum.messageType());
            openTarget = activityEnum.openTarget();
            openTargetIOS = activityEnum.openTargetIOS();
            
            //装配参数
            openAttach = activityEnum.openAttach();
            openAttachIOS = activityEnum.openAttachIOS();
            if(params != null && params.length > 0) {
                for (int i=0; i<params.length; i++) {
                    openAttach = openAttach.replace("$"+(i+1), params[i]);
                    openAttachIOS = openAttachIOS.replace("$"+(i+1), params[i]);
                }
            }
        }
        
        //对用户进行分类处理
        List<List<UserDeviceTokenPO>> listAll = new ArrayList<List<UserDeviceTokenPO>>();
        boolean isManager = UserType.orgUser == userType ? true : false;
        List<UserDeviceTokenPO> iosIsManagerlist = null;
        List<UserDeviceTokenPO> iosNotIsManagerlist = null;
        List<UserDeviceTokenPO> androidIsManagerlist = null;
        List<UserDeviceTokenPO> androidNotIsManagerlist = null;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getOS() ==1 && isManager){
                androidIsManagerlist = paramAssemble(listAll, androidIsManagerlist, list.get(i));  //安卓、机构用户
            }else if (list.get(i).getOS() ==1 && !isManager){
                androidNotIsManagerlist = paramAssemble(listAll, androidNotIsManagerlist, list.get(i));  //安卓、非机构用户
            }else if(list.get(i).getOS() ==2 && isManager){
                iosIsManagerlist = paramAssemble(listAll, iosIsManagerlist, list.get(i));  //苹果、机构用户
            }else if(list.get(i).getOS() ==2 && !isManager){
                iosNotIsManagerlist = paramAssemble(listAll, iosNotIsManagerlist, list.get(i));  //苹果、非机构用户
            }else{
                logger.error("友盟推送数据类型超出范围("+title+"): system:" +list.get(i).getOS()+",isManager:"+isManager);
            }
        }
        
        //分批推送信息
        for(int i = 0; i<listAll.size(); i++){
            if(listAll.get(i).size() == 0)
                continue;
            List<Integer> userList = new ArrayList<>();
            List<String> tokenList = new ArrayList<String>();
            for(UserDeviceTokenPO deviceTokenPO : listAll.get(i)){
                userList.add(deviceTokenPO.getUserId());
                tokenList.add(deviceTokenPO.getDeviceToken());
            }
            //保存到表，放在此处分批保存(一次全部保存的话数据量过大时会崩溃)
            messageService.saveMessage(userList, openType, userType, title, content, openTarget, openAttach, openTargetIOS, openAttachIOS, msgType);
            
            try {
                //推送消息
                boolean isIos = listAll.get(i).get(0).getOS() == 2 ? true : false;
                afterSend(uMengPushUtil.pushMessage(isIos, isManager, tokenList, title, content, openType, openTarget, openAttach, openTargetIOS, openAttachIOS),0);
            } catch (ParamException e) {
                logger.error("UMengPushService数据推送:",e);
            }
        }
    }
    
    /**
     * 
     *  推送信息分类处理
     *  @author liaoguo
     *  @DateTime 2018年5月22日 上午11:50:12
     *
     *  @param listAll 分类后的集合
     *  @param typelist 需要分类的数据
     *  @param tokenPO 要加入的数据
     */
    private List<UserDeviceTokenPO> paramAssemble(List<List<UserDeviceTokenPO>> listAll, List<UserDeviceTokenPO> typelist, UserDeviceTokenPO tokenPO){
        if(typelist == null || typelist.size() == UM_SMS_MAX_NUM){
            typelist = new ArrayList<UserDeviceTokenPO>();
            listAll.add(typelist);
        }
        typelist.add(tokenPO);
        return typelist;
    }
    
    //平台手动推送消息,由于未给入用户devicetoken，因此做循环单条发
    public void manualPush(Integer[] ids, String title, String content, UMengOpenTypeEnum openType, String openTarget, String[] params){

        for(int i=0;i<ids.length;i++){
            if(openType == UMengOpenTypeEnum.TEXT)
                pushText(UserType.member, ids[i], title, content, MessageType.SYSTEM);
            else if(openType == UMengOpenTypeEnum.URL)
                pushURL(UserType.member, ids[i], title, content, openTarget, MessageType.SYSTEM);
            else
                pushActivity(ids[i], content, ActivityMemberEnum.valueOf(openTarget), params);
        }
    }

     //门店手动推送消息
     public void pushStoreMessage(Integer[] userIdList,String title, int sendId, String content, UMengOpenTypeEnum openType, String openTarget, String[] params){
         String text = content;
         //暂时按条推, 后期改为批量推送保存
         //pushAndSaveManage(UserType.member, StringUtils.join(userIdList, ","), title, content, openType, openTarget, params, MessageType.SYSTEM);
         for (int i = 0; i < userIdList.length; i++) {
             if(openType == UMengOpenTypeEnum.TEXT)
                 pushText(UserType.member, userIdList[i], title, content, MessageType.SYSTEM);
             else if(openType == UMengOpenTypeEnum.URL)
                 pushURL(UserType.member, userIdList[i], title, content, openTarget, MessageType.SYSTEM);
             else
                 pushActivity(userIdList[i], content, ActivityMemberEnum.valueOf(openTarget), params);
         }
     }

     //门店管理员提醒服务师赶紧完成用户订单
     public void notifyEmployeeFinishOrder(int orderId, int employeeId, String realName, String subject){
         String[] attachParam = new String[] {String.valueOf(orderId)};
         String content = "用户["+realName+"]的订单["+subject+"]未完成，请及时服务用户。";
         pushActivity(employeeId, content, ActivityMemberEnum.EMPLOYEE_FINISH_ORDER, attachParam);
     }

     //分析报告完成后给用户的推送
    public void replyReportEnd(int userId, int reportAnalysisId, Date date){
        String[] attachParam = new String[] {String.valueOf(reportAnalysisId)};
        String content = "您于" + DateTimeUtilT.date(date) + "提交的分析报告结果已给出";
        pushActivity(userId, content, ActivityMemberEnum.REPLY_END, attachParam);
    }

    //渠道商提交用户分析报告申请时给用户的推送
    public void replyReportStart(int userId, int reportAnalysisId){
        String[] attachParam = new String[] {String.valueOf(reportAnalysisId)};
        String content = "您于" + DateTimeUtilT.date(new Date()) + "提交的分析报告正在处理中。";
        pushActivity(userId, content, ActivityMemberEnum.REPLY_START, attachParam);
    }

    //用户设定的测量提醒推送
    public void measureRemindPush(int weekDay) {
        String title = "测量提醒";
        String text = "";
        List<MeasureReminderTaskDTO> datas = memberPushDao.listMeasureReminderTask(weekDay);
        for (MeasureReminderTaskDTO data : datas) {
            String deviceToken = data.getDeviceToken();
            if (StringUtils.isEmpty(deviceToken))
                continue;    // deviceToken不存在的数据不进行推送处理
            text = "生命守护提醒您，您需要测量"+data.getDevices() + "了";
            int receiverId = data.getReceiverId();
            boolean ios = data.getOS() == 2 ? true : false;
            pushText(UserType.member, receiverId, title, text, MessageType.SYSTEM);
        }
    }

    /**
     *
     *  向服务师推送新用户购买信息
     *  @author NaN
     *  @DateTime 2018年5月11日 上午10:19:00
     *
     *  @param employeeId
     *  @param userId
     *  @param orderNumber
     *  @param orderId
     */
    public void pushOrderMessage(int employeeId, int userId, String subject, String orderNumber, int orderId){
        String[] attachParam = new String[] { String.valueOf(orderId) };
        MemberUserDTO memberUserDTO = memberService.getUser(userId);
        String content = "新用户[" + memberUserDTO.getRealName() + "]购买了[" + subject + "]服务，订单号:" + orderNumber + "。";
        pushActivity(employeeId, content, ActivityMemberEnum.ORDER_BUY_EMPLOYEE, attachParam);
    }

    //用户测量时有异常数据向门店服务师推送
    public void saveWarningMessage(int userId, DeviceType deviceType, Date date) {
//        MemberUserDTO memberUserDTO = memberService.getUser(userId);
//        List<Integer> list = employeeService.findEmployeeListByUserId(userId);
//        for (Integer orgUserId : list) {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            String[] params = new String[]{String.valueOf(userId), formatter.format(date)};
//            String content = "用户『" + (memberUserDTO.getRealName() == null ? memberUserDTO.getUserName() : memberUserDTO.getRealName()) + "』 " + deviceType.getName() + "设备数据异常。";
//            pushActivity(orgUserId, content, ActivityMemberEnum.MEASURE_WARNING, params);
//        }
    }

    //推送服务将到期通知给用户
    public void pushWillFailToUser(List<MessageDTO> list){
        if(list == null || list.size() == 0)
            return;
        for(MessageDTO messageDTO : list){
            messageDTO.setTitle("用户服务即将到期通知");
            messageDTO.setUserType(UserType.member.getValue());
            messageDTO.setOpenType(UMengOpenTypeEnum.Activity.value());
            messageDTO.setOpenTarget(ActivityMemberEnum.SERVE_WILL_END_USER.openTarget());
            messageDTO.setOpenTargetIOS(ActivityMemberEnum.SERVE_WILL_END_USER.openTargetIOS());
        }
        pushList(list);
    }

    //推送服务到期通知给用户
    public void pushFailToUser(List<MessageDTO> list){
        if(list == null || list.size() == 0)
            return;
        for(MessageDTO messageDTO : list){
            messageDTO.setTitle("用户服务到期通知");
            messageDTO.setUserType(UserType.member.getValue());
            messageDTO.setOpenType(UMengOpenTypeEnum.Activity.value());
            messageDTO.setOpenTarget(ActivityMemberEnum.SERVE_END_USER.openTarget());
            messageDTO.setOpenTargetIOS(ActivityMemberEnum.SERVE_END_USER.openTargetIOS());
        }
        pushList(list);
    }
    
    public void pushWillFailComboToUser(List<MessageDTO> list) {
    	if(list == null || list.size() == 0)
            return;
    	for(MessageDTO messageDTO : list){
            messageDTO.setTitle("套餐提前通知");
            messageDTO.setUserType(UserType.member.getValue());
            messageDTO.setOpenType(UMengOpenTypeEnum.TEXT.value());
            messageDTO.setOpenTarget(ActivityMemberEnum.SERVE_END_USER.openTarget());
            messageDTO.setOpenTargetIOS(ActivityMemberEnum.SERVE_END_USER.openTargetIOS());
        }
    	pushList(list);
    }
    
    public void pushFailedComboToUser(List<MessageDTO> list) {
    	if(list == null || list.size() == 0)
            return;
    	for(MessageDTO messageDTO : list){
            messageDTO.setTitle("套餐到期通知");
            messageDTO.setUserType(UserType.member.getValue());
            messageDTO.setOpenType(UMengOpenTypeEnum.TEXT.value());
            messageDTO.setOpenTarget(ActivityMemberEnum.SERVE_END_USER.openTarget());
            messageDTO.setOpenTargetIOS(ActivityMemberEnum.SERVE_END_USER.openTargetIOS());
        }
    	pushList(list);
    }

    //推送用户服务将到期通知给服务师
    public void pushWillFailToEmployee(List<MessageDTO> list){
        if(list == null || list.size() == 0)
            return;
        for(MessageDTO messageDTO : list){
            messageDTO.setTitle("用户服务即将到期通知");
            messageDTO.setUserType(UserType.orgUser.getValue());
            messageDTO.setOpenType(UMengOpenTypeEnum.Activity.value());
            messageDTO.setOpenTarget(ActivityMemberEnum.WILL_FAIL_EMPLOYEE.openTarget());
            messageDTO.setOpenTargetIOS(ActivityMemberEnum.WILL_FAIL_EMPLOYEE.openTargetIOS());
        }
        pushList(list);
    }

    //批量推送和保存消息，推送为单条发送方式，给入条件不含token
    private void pushList(List<MessageDTO> list){
        messageService.saveMessage(list, MessageType.SYSTEM);
        for(MessageDTO messageDTO : list) {
            pushManage(UserType.parseOf(messageDTO.getUserType()), messageDTO.getUserId(), messageDTO.getTitle(), messageDTO.getContent(), UMengOpenTypeEnum.parseOf(messageDTO.getOpenType()),
                    messageDTO.getOpenTarget(), messageDTO.getOpenAttach(), messageDTO.getOpenTargetIOS(), messageDTO.getOpenAttachIOS());
        }
    }
    //推送保存消息—APP窗体，不再保存到表t_message
    private void pushActivity(int userId, String content, ActivityMemberEnum activityEnum, String[] params){
        //装配参数
        String openAttach = activityEnum.openAttach();
        String openAttachIOS = activityEnum.openAttachIOS();
        if(params != null && params.length > 0) {
            for (int i=0; i<params.length; i++) {
                openAttach = openAttach.replace("$"+(i+1), params[i]);
                openAttachIOS = openAttachIOS.replace("$"+(i+1), params[i]);
            }
        }
        pushAndSaveManage(UserType.parseOf(activityEnum.userType()), userId, activityEnum.title(), content, UMengOpenTypeEnum.Activity,
                activityEnum.openTarget(), openAttach, activityEnum.openTargetIOS(), openAttachIOS, MessageType.parseOf(activityEnum.messageType()));
    }

    //推送保存消息—URL
    private void pushURL(UserType userType, int userId, String title, String content, String openTarget, MessageType msgType){
        pushAndSaveManage(userType, userId, title, content, UMengOpenTypeEnum.URL, openTarget, null, openTarget, null, MessageType.SYSTEM);
    }

    //推送保存消息—文本
    private void pushText(UserType userType, int userId, String title, String content, MessageType msgType){
        pushAndSaveManage(userType, userId, title, content, UMengOpenTypeEnum.TEXT, null, null, null, null, MessageType.SYSTEM);
    }

    /**
     * 通用推送消息和保存信息
     *  @author NaN
     *  @DateTime 2018年5月9日 下午4:32:01
     *  @param userId
     *  @param userType 门店服务师，用户
     *  @param openType
     *  @param title
     *  @param content
     */
    private void pushAndSaveManage(UserType userType, int userId, String title, String content, UMengOpenTypeEnum openType, String openTarget, String openAttach, String openTargetIOS, String openAttachIOS, MessageType msgType){
        int iosNum = 0;
        String deviceToken = null;
        boolean isManager = false;

        //保存到消息表
        int id = messageService.saveMessage(userId, userType, title, content, openType, openTarget, openAttach, openTargetIOS, openAttachIOS, msgType);
        //获取token码
        if(UserType.member == userType){    //用户
            UserDeviceTokenPO tokenPO = pDataService.getUserPushToken(userId);
            if (tokenPO != null) {
                iosNum = tokenPO.getOS();
                deviceToken = tokenPO.getDeviceToken();
            }
        }
        if(org.springframework.util.StringUtils.isEmpty(deviceToken))   //取不到token则不做推送
            return ;
        boolean ios = (iosNum == 2) ? true : false;
        try {
            afterSend(uMengPushUtil.pushMessage(ios, isManager, deviceToken, title, content, openType, openTarget, openAttach, openTargetIOS, openAttachIOS), userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 通用推送消息，不保存信息
     *  @author NaN
     *  @DateTime 2018年5月9日 下午4:32:01
     *  @param userId
     *  @param userType 门店服务师，用户
     *  @param openType
     *  @param title
     *  @param content
     */
    private void pushManage(UserType userType, int userId, String title, String content, UMengOpenTypeEnum openType, String openTarget, String openAttach, String openTargetIOS, String openAttachIOS){
        int iosNum = 0;
        String deviceToken = null;
        boolean isManager = false;

        //获取token码
        if(UserType.member == userType){    //用户
            UserDeviceTokenPO tokenPO = pDataService.getUserPushToken(userId);
            if (tokenPO != null) {
                iosNum = tokenPO.getOS();
                deviceToken = tokenPO.getDeviceToken();
            }
        }
        if(org.springframework.util.StringUtils.isEmpty(deviceToken))   //取不到token则不做推送
            return ;
        boolean ios = (iosNum == 2) ? true : false;
        try {
            afterSend(uMengPushUtil.pushMessage(ios, isManager, deviceToken, title, content, openType, openTarget, openAttach, openTargetIOS, openAttachIOS), userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //推送后处理，如果失败保存到表里
    private void afterSend(CallBackDTO callBack, int messageId){
        String errorCode = callBack.getData().getErrorCode();
        // 失败
        if (errorCode != null) {
            String errorMsg = String.format("消息ID为%d的推送任务发送失败，错误码为%s，返回内容:%s", messageId, errorCode, callBack.getData().getErrorMsg());
            logger.error(errorMsg);
            PushTaskDTO pushTask = new PushTaskDTO();
            pushTask.setReminderDetailId(messageId);
            pushTask.setMsgId(callBack.getData().getMsgId());
            pushTask.setTaskId(callBack.getData().getTaskId());
            pushTask.setStatus(3);
            pushTask.setErrorMsg(errorMsg);
            memberPushDao.addPushTask(pushTask);
        }
    }
    
    
    /**
     * 服务师设定的用户提示闹钟
     * @param
     */
	public void MemberserviceRemind(Integer weekDay) {
		 String title = "服务提醒";
	        String text = "";
	        List<MeasureReminderTaskPo> datas = memberPushDao.MemberserviceRemind(weekDay);
	        for (MeasureReminderTaskPo data : datas) {	        	
	            String deviceToken = data.getDeviceToken();
	            if (StringUtils.isEmpty(deviceToken))
	                continue;    // deviceToken不存在的数据不进行推送处理
	            text = data.getContent();
	            int receiverId = data.getReceiverId();
	            boolean ios = data.getOS() == 2 ? true : false;
	            pushText(UserType.member, receiverId, title, text, MessageType.SERVICES);
	        }
		
	}
}
