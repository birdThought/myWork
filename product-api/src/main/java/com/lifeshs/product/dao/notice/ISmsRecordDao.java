package com.lifeshs.product.dao.notice;

import com.lifeshs.product.domain.vo.notice.SmsRecordPO;
import com.lifeshs.product.domain.vo.notice.SmsRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "smsRecordDao")
@Mapper
public interface ISmsRecordDao {

    /**
     *  添加一条记录
     *  @author yuhang.weng
     *  @DateTime 2017年9月28日 上午11:38:30
     *
     *  @param record
     *  @return
     */
    int addSmsRecord(SmsRecordPO record);

    /**
     *  添加多条记录
     *  @author yuhang.weng
     *  @DateTime 2017年9月28日 上午11:38:40
     *
     *  @param recordList 短信记录列表
     *  @return
     */
    int addSmsRecordList(@Param("recordList") List<SmsRecordPO> recordList);

    /**
     * 获取短信总记录数
     * @author zizhen.huang
     * @DateTime 2018年1月23日19:56:47
     *
     * @param userName 用户名
     * @param receiveMobile 接收号码
     * @return
     */
    int getSmsRecordTotalRecord(@Param("userName") String userName, @Param("receiveMobile") String receiveMobile);

    /**
     * 获取短信记录列表
     * @author zizhen.huang
     * @DateTime 2018年1月23日20:00:57
     *
     * @param userName 用户名
     * @param receiveMobile 接收号码
     * @param startRow
     * @param pageSize
     * @return
     */
    List<SmsRecordVO> findSmsRecordList(@Param("userName") String userName, @Param("receiveMobile") String receiveMobile, @Param("startRow") int startRow, @Param("pageSize") int pageSize);
}

