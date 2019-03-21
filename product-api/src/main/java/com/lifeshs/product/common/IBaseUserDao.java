package com.lifeshs.product.common;

import com.lifeshs.product.domain.dto.user.SensitiveOperationLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository(value = "baseUserDao")
@Mapper
public interface IBaseUserDao {

    /**
     *  添加一条敏感记录
     *  @author yuhang.weng
     *	@DateTime 2017年4月7日 上午10:33:25
     *
     *  @param log
     */
    void insertSensitiveLog(SensitiveOperationLogDTO log);
}
