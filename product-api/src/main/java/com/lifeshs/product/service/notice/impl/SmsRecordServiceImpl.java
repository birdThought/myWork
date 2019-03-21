package com.lifeshs.product.service.notice.impl;

import com.lifeshs.product.common.constants.base.ErrorCodeEnum;
import com.lifeshs.product.common.exception.base.OperationException;
import com.lifeshs.product.common.exception.sms.SMSException;
import com.lifeshs.product.component.sms.SmsPortType;
import com.lifeshs.product.component.sms.SmsService;
import com.lifeshs.product.dao.notice.ISmsRecordDao;
import com.lifeshs.product.domain.vo.notice.SmsRecordPO;
import com.lifeshs.product.domain.dto.notice.SmsResult;
import com.lifeshs.product.domain.vo.notice.SmsRecordVO;
import com.lifeshs.product.service.common.IPagingQueryProc;
import com.lifeshs.product.service.common.impl.Paging;
import com.lifeshs.product.service.notice.ISmsRecordService;
import com.lifeshs.product.utils.DateTimeUtilT;
import com.lifeshs.product.utils.PropertiesUtil;
import com.lifeshs.product.utils.SMSCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service(value = "smsRecordService")
public class SmsRecordServiceImpl implements ISmsRecordService {

    private static String orgNumber;
    private static String smsUser;
    private static String smsPassword;

    static {
        PropertiesUtil pro = new PropertiesUtil("sysConfig.properties");
        orgNumber = pro.readProperty("sms.org.unmber");
        smsUser = pro.readProperty("sms.user");
        smsPassword = pro.readProperty("sms.password");
    }

    private static final Logger logger = Logger.getLogger(SmsRecordServiceImpl.class);

    @Resource(name = "smsRecordDao")
    private ISmsRecordDao smsRecordDao;

    @Override
    public void addSmsRecord(SmsRecordPO record) throws OperationException {
        int res = smsRecordDao.addSmsRecord(record);
        if (res == 0) {
            throw new OperationException("保存短信记录失败", ErrorCodeEnum.FAILED);
        }
    }

    @Override
    public void addSmsRecord(List<SmsRecordPO> recordList) throws OperationException {
        if (recordList.size() == 0) {
            throw new OperationException("请至少保存一条记录", ErrorCodeEnum.FAILED);
        }

        int res = smsRecordDao.addSmsRecordList(recordList);
        if (res != recordList.size()) {
            throw new OperationException("保存短信记录失败", ErrorCodeEnum.FAILED);
        }
    }

    @Override
    public Paging<SmsRecordVO> findSmsRecordList(String userName, String receiveMobile, int curPage, int pageSize) {
        Paging<SmsRecordVO> paging = new Paging<>(curPage, pageSize);
        paging.setQueryProc(new IPagingQueryProc<SmsRecordVO>() {

            @Override
            public int queryTotal() {
                return smsRecordDao.getSmsRecordTotalRecord(userName, receiveMobile);
            }

            @Override
            public List<SmsRecordVO> queryData(int startRow, int pageSize) {
                return smsRecordDao.findSmsRecordList(userName, receiveMobile, startRow, pageSize);
            }
        });
        return paging;
    }

    @Override
    public boolean send(String userNumber, String message){
        boolean res = false;
        if (!StringUtils.isNotBlank(userNumber)) {
            return res;
        }
        SmsService sms = new SmsService();
        SmsPortType smsPortType = sms.getSmsHttpPort();
        //发送的内容
//        String message = messageHandle(smsCommand, str);
//          if(!"JKTX".equals(smsCommand.toString())){
        message = message + "  " + DateTimeUtilT.dateTime(new Date());
//          }

        //发送
        String result = smsPortType.sms(orgNumber, smsUser, smsPassword, message, userNumber, "", "", "1", "", "1", "");
        SmsResult smsResult = null;
        try {
            smsResult = handleResult(result);
        } catch (SMSException e) {
            logger.error("短信发送失败:" + e);
        }
        if (smsResult != null) {
            res = smsResult.isSuccess();
        } else {
            res = false;
        }
        logger.info("接收人:" + userNumber + ",发送的内容:" + message + ",结果:" + result);
        //记录发送的指令
        int status = res ? 0 : 1;
        int sendType = 0;
        System.out.println("sms:" + "接收人:" + userNumber + ",发送的内容:" + message + ",结果:" + result);
        /*memberService.saveSmsRecord(sendId, sendType, message, userNumber, status);*/

        return res;
    }

    /**
     * 指令处理
     * @param smsCommand
     * @return
     */
    private String messageHandle(SMSCommand smsCommand, String... str) {
        String message = String.format(smsCommand.GetCommand().replace("?", "%s"), str);
        return message;
    }

    /**
     * 结果处理
     *
     * @param result
     * @return
     * @throws SMSException
     */
    private static SmsResult handleResult(String result) throws SMSException {
        SmsResult sr = new SmsResult();
        String[] results = result.split("&");
        for (String string : results) {
            String[] params = string.split("=");
            if ("result".equals(params[0])) {
                sr.setResult(params[1]);
                if (!"0".equals(params[1])) {
                    sr.setSuccess(false);
                }
            } else if ("description".equals(params[0])) {
                sr.setDescription(params[1]);
            } else if ("taskid".equals(params[0]) && params.length == 2) {
                sr.setTaskid(params[1]);
            } else if ("faillist".equals(params[0]) && params.length == 2) {
                sr.setFaillist(params[1]);
            } else if ("task_id".equals(params[0]) && params.length == 2) {
                sr.setTask_id(params[1]);
            }
        }
        // 如果短信发送失败抛出发送失败异常
        if (!sr.isSuccess()) {
            throw new SMSException(sr.getDescription());
        }
        return sr;
    }
}

