package com.lifeshs.product.controller.aop;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminAOP {

    private Logger logger = LoggerFactory.getLogger(AdminAOP.class);

//    @Autowired
//    private ITokenService tokenService;

    /** 第一期的app切面 */
    @Pointcut(value = "execution(public com.alibaba.fastjson.JSONObject com.lifeshs.product.api.controller.admin.*.*(..))")
    public void user() {
    }

    @Around(value = "user()")
    public JSONObject userAround(ProceedingJoinPoint point) throws Throwable {
        long s = System.currentTimeMillis();
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();

        logger.info("方法:" + methodName + "["+System.currentTimeMillis()+"]");
        logger.info("params:" + args.toString());

        /** 执行原来的任务 */
        JSONObject returnJson = (JSONObject) point.proceed(args);
        logger.info("内容体:" + returnJson.toString());
        logger.info("总耗时:" + (System.currentTimeMillis() - s) + "毫秒\n");

        return returnJson;
    }


    /**
     * token有效性校验
     *
     * @author yuhang.weng
     * @DateTime 2017年2月22日 上午10:33:40
     *
     * @param userId
     *            用户ID
     * @param userToken
     *            用户正确token
     * @param jsonToken
     *            待校验token
     * @return
     */
    private boolean isTokenValid(Integer userId, String userToken, String jsonToken) {
        /** token是否相同 */

        return true;
    }
}
