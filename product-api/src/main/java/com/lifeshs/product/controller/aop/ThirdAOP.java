package com.lifeshs.product.controller.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 第三方接口全局处理
 * Created by dengfeng on 2018/1/26 0026.
 */
@Aspect
@Component
public class ThirdAOP {

    private Logger logger = LoggerFactory.getLogger(ThirdAOP.class);

//    @Autowired
//    private ITokenService tokenService;

    @Pointcut(value = "execution(public * com.lifeshs.product.api.controller.third..*.*(..))")
    public void third() {}

    @Around(value = "third()")
    public Object thirdAround(ProceedingJoinPoint point) throws Throwable {
        long s = System.currentTimeMillis();
        Object[] args = point.getArgs();

        // URL转义
        //param = URLDecoder.decode(param, "UTF-8");

        String methodName = point.getSignature().getName();
        logger.info("方法:" + methodName + "["+System.currentTimeMillis()+"]");
        logger.info("params:" + args.toString());



         //身份验证
//        if (!validCheck(appid, token, timestamp)) {
//            return MAppNormalService.error("身份验证出错", 2);
//
//        }
        Object returnObj = point.proceed(args);

        logger.info("内容体:" + returnObj.toString());
        logger.info("总耗时:" + (System.currentTimeMillis() - s) + "毫秒\n");

        return returnObj;
    }

    /**
     * 身份有效性校验，验证appid和token是否有效，验证timestamp在三分钟内
     * 
     * @param appid
     * @return
     */
    private boolean validCheck(String appid, String token, String timestamp) {

        return true;
    }

}
