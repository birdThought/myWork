package com.lifeshs.product.service.common.impl;

import com.lifeshs.product.common.IBaseDao;
import com.lifeshs.product.domain.po.code.TSysCode;
import com.lifeshs.product.service.common.ICommonService;
import com.lifeshs.product.service.common.transform.impl.CommonTransImpl;
import com.lifeshs.product.service.common.ICacheService;
import com.lifeshs.product.service.common.ITokenService;
import com.lifeshs.product.thirdservice.HuanXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 版权归 TODO 公共类
 *
 * @author duosheng.mo
 * @DateTime 2016年4月20日 上午9:41:03
 */
@Service("commonService")
public class CommonServiceImpl implements ICommonService {

    @Autowired
    protected HuanXinService huanXinService;
    /**
     * 公共增、删、改、查
     */
    @Autowired
    public CommonTransImpl commonTrans;
    @Autowired
    protected IBaseDao baseDao;
    @Resource(name = "tokenService")
    protected ITokenService tokenService;
    @Autowired
    protected ICacheService cacheService;

    /**
     * 得到最新的用户编号
     *
     * @return 用户编号
     * @author dengfeng
     * @DateTime 2016-6-2 上午11:42:20
     */
    protected TSysCode getUserCode() {
        TSysCode sysCode = commonTrans.get(TSysCode.class, 1);
        String userCode = sysCode.getMemberCode();
        int newUserCode = Integer.valueOf(userCode) + 1;
        sysCode.setMemberCode(String.valueOf(newUserCode));

        commonTrans.updateEntitie(sysCode);
        return sysCode;
    }

    /**
     * @Description: 获取机构用户最新code
     * @Author: wenxian.cai
     * @Date: 2017/6/7 16:35
     */
    protected TSysCode getOrgUserCode() {
        TSysCode sysCode = commonTrans.get(TSysCode.class, 1);
        String resCode = sysCode.getOrgUserCode();
        int code = Integer.valueOf(resCode)+1;
        sysCode.setOrgUserCode(String.valueOf(code));

        commonTrans.saveOrUpdate(sysCode);
        return sysCode;
    }

    /**
     * 注册环信
     *
     * @param userCode
     * @return
     */
    protected boolean registHxUser(String userCode) {
        huanXinService.registryUser(userCode);
        return true;
    }
}