package com.lifeshs.product.service.common.impl;

import com.lifeshs.product.dao.common.IAppVersionDao;
import com.lifeshs.product.service.common.IAppVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AppVersionServiceImpl implements IAppVersion {

    @Resource(name = "IAppVersionDao")
    private IAppVersionDao appVersionDao;

    @Override
    public Integer queryAppVersion(String appName, Integer version) {
        Integer version_new = null;

        boolean isAppNameNull = StringUtils.isBlank(appName);
        boolean isVersionNull = version == null;

        if (isAppNameNull || isVersionNull) {
            return version_new;
        }

        Integer v = appVersionDao.queryAppMaxVersion(appName);
        if (v == null || v <= version) {
            return version_new;
        }
        return v;
    }

    @Override
    public String queryIOSPublicVersion() {
        String version_new = appVersionDao.queryAppMaxPubliceVersion("ios");
        return version_new;
    }

    @Override
    public String queryManagerIOSPublicVersion() {
        String version_new = appVersionDao.queryAppMaxPubliceVersion("ios_manager");
        return version_new;
    }

}

