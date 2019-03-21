package com.lifeshs.product.domain.po.log;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

    public @Data class TLogLogin implements Serializable {
        /**
         * 日志_登录记录
         */
        private Integer id;

        /**
         * 登录时间
         */
        private Date loginTime;

        /**
         * 用户类型：1_会员，2_机构员工
         */
        private Integer userType;

        /**
         * 登录用户ID
         */
        private Integer userId;

        /**
         * 登录用户名
         */
        private String userName;

        /**
         * 用户的机构ID(只记录机构员工)
         */
        private Integer orgId;

        /**
         * 终端类型：ios,android,browse
         */
        private String terminalType;

        /**
         * 登录IP
         */
        private String ip;

        public TLogLogin() {
            super();
        }

        public TLogLogin(Integer id, Date loginTime, Integer userType, Integer userId, String userName, Integer orgId, String terminalType, String ip) {
            super();
            this.id = id;
            this.loginTime = loginTime;
            this.userType = userType;
            this.userId = userId;
            this.userName = userName;
            this.orgId = orgId;
            this.terminalType = terminalType;
            this.ip = ip;
        }


    }
