package com.lifeshs.product.common.constants.promptInfo;

/**
 *  错误信息
 *  @author yuhang.weng
 *  @version 1.0
 *  @DateTime 2017年6月15日 上午11:04:16
 */
public class ErrorInfo {
    // 通用的错误信息
    /** (String)长度不能超过(int)个字符 */
    public final static String LENGTH = "%s长度不能超过%d个字符";
    /** (String)格式不合法 */
    public final static String UNVERIFIY = "%s格式不合法";
    /** (String)不允许为空 */
    public final static String MISSING = "%s不允许为空";
    /** 找不到该(String)的信息 */
    public final static String NOT_FOUND = "找不到该%s的信息";
    /** (String)错误 */
    public final static String MISTAKE = "%s错误";
    // CRUD错误信息
    public final static String UPDATE_FAILED = "更新失败";
    public final static String ADD_FAILED = "新增失败";
    public final static String FETCH_FAILED = "查询失败";
    public final static String DELETE_FAILED = "删除失败";
    
    //引荐人ID
    public final static String NOT_PARENTID = "系统中没有此引荐人ID";

    public final static String FAIL_ACTION = "操作失败";

    public final static String ABOLISHED_METHOD = "停止该接口服务的使用";

    public final static String PARAMETER_MISSING = "参数丢失";

    public final static String NO_MOTHED_AUTH = "用户没有此方法的权限";

    // 注册

    public final static String USERNAME_UNVERIFY = "用户名不合法";

    public final static String PASSWORD_UNVERIFY = "密码不合法";

    public final static String PASSWORD_ATYPISM = "输入的两次密码不一致";

    //引荐人ID
    public final static String MOBILE_ParentId = "没有此引荐人ID";

    public final static String ORGNAME_UNVERIFY = "机构名称不合法";

    public final static String CONTACT_INFORMATION_UNVERIFIY = "联系方式不合法";

    public final static String TEL_UNVERIFIY = "工作电话号码格式不合法";

    public final static String MOBILE_UNVERIFIY = "手机号码格式不合法";

    public final static String EMAIL_UNVERIFIY = "邮箱格式不合法";

    // 登录
    public final static String LOGIN_PARAMETER_MISSING = "登录失败：用户名 或者登录密码为空";

    // 验证码

    public final static String VALID_CODE_CACHE_TYPE_NULL = "验证码发送失败：验证码类型为空";

    public final static String VALID_CODE_MOBILE_MISSING = "验证码发送失败：手机号码为空";

    public final static String VALID_CODE_ILLEGAL_ACTION = "验证码发送失败：非法操作";

    public final static String VALID_CODE_SEND_FAILED = "发送失败";

    public final static String VALID_CODE_UNKNOW = "验证码发送失败：未知错误";

    // 修改手机

    // 修改密码

    public final static String MODIFY_PASSWORD_OLD_MISSING = "原密码不能为空";

    public final static String MODIFY_PASSWORD_NEW_MISSING = "新密码不能为空";

    // 健康数据相关

    public final static String HEALTH_STANDAR_NECESSARY_PARAM_MISSIING_BASE_INFO = "请完善用户个人信息";

    public final static String HEALTH_STANDAR_NECESSARY_PARAM_MISSIING_RECORD = "请完善用户个人档案";

    // 服务相关

    public final static String SERVE_OUT_VALID_DATE = "服务已过期";

    public final static String REQUEST_OVER_TIME = "请求已失效";

    public final static String FORBIDDEN = "该请求已拒绝";

    public final static String UPDATEFAILED = "更新失败";
    public final static String ADDFAILED = "新增失败";
    public final static String FETCHFAILED = "查询失败";
    public final static String DELETEFAILED = "删除失败";
}
