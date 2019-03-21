package com.lifeshs.product.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Yue.Li
 * @Date 2017/4/5.
 */
@RestController(value = "webH5Controller")
@RequestMapping(value = { "/app/appweb" })
public class WebH5Controller {

    /**
     * 移动端资讯
     * 跳转至新的地址
     * @return 跳转JS
     */
    @RequestMapping(params = "informationIndex")
    public String informationIndex() {
        return "<script type='text/javascript'>window.location = 'http://www.lifekeepers.cn/app/appweb.do?informationIndex';</script>";
    }
}
