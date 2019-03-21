package com.lifeshs.product.service.healthData;

import com.lifeshs.product.common.constants.healthData.HealthType;
import com.lifeshs.product.domain.po.device.TSportLocation;
import com.lifeshs.product.domain.po.device.TUserTerminal;
import com.lifeshs.product.domain.po.member.TUserMonitorTrack;

import java.util.List;
import java.util.Map;

public interface IDeviceService {

    /**按照日期获取设备数据总条数（日期相同的多条数据算一条）
     * @author wenxian.cai
     * @DateTime 2016年9月20日下午7:12:18
     * @serverComment
     * @param
     */
    public <T> Integer selectDeviceDataCountByMeasureDate(Class<T> entityClass,Map<String, Object> params);


    /**
     *
     *  @author zhiguo.lin
     *  @DateTime 2016年7月27日 上午9:38:04
     *  @serverComment 服务注解
     *
     *  @param
     *  @return
     */
    public <T> T selectDeviceDataLastestDate(Class<T> entityName, int userId, String terminalType, String measure);


    /**
     *  @author yuhang.weng
     *  @DateTime 2016年7月12日 上午11:20:04
     *  @serverComment 按照测量时间倒序获取定位数据
     *
     *  @param userId
     *  @param terminalType LCHB HL031 HL03 C3 ... (详细查看com.lifeshs.service.impl.terminal.TerminalType)
     *  @param limit 限制查询记录数量 (1..2..3..4...按需求填写)
     *  @return
     */
    public List<TSportLocation> findLatestGpsMessage(int userId, String terminalType, int limit);



    /**
     *  @author yuhang.weng
     *  @DateTime 2016年7月22日 上午10:34:35
     *  @serverComment 按照指定日期查询定位信息
     *
     *  @param userId
     *  @param terminalType LCHB HL031 HL03 C3 ... (详细查看com.lifeshs.service.impl.terminal.TerminalType)
     *  @param
     *  @return
     *  @throws Exception
     */
    public List<TSportLocation> findLocationByDateTime(int userId, String terminalType, String startTime, String endTime);

    /**
     *  @author yuhang.weng
     *  @DateTime 2016年7月22日 上午11:20:21
     *  @serverComment 查询运动轨迹的信息（通过orderParam对结果进行排序）
     *
     *  @param deviceId 设备ID
     *  @param orderParam 排序条件
     *  @param type 0_ASC、1_DESC
     *  @return
     */
    public List<TUserMonitorTrack> findTracksOrderByParam(int deviceId, String orderParam, int type);


    /**
     *  @author yuhang.weng
     *  @DateTime 2016年6月16日 下午4:03:01
     *  @serverComment 查询已绑定的设备记录
     *
     *  @param imei 串号
     *  @param terminalType HL03、C3
     *  @return
     */
    public TUserTerminal selectDeviceIsBinding(String imei, String terminalType);


    /**
     *  获取特定的健康标准值
     *  @author zhiguo.lin
     *  @DateTime 2016年9月13日 上午9:56:26
     *
     *  @param userId
     *  @param healthTypes
     *  @return
     */
    public Map<String, Object> getHealthStandardValueByHealthType(Integer userId, List<HealthType> healthTypes);

    /**
     *  获取特定的健康标准值
     *  @author yuhang.weng
     *  @DateTime 2016年12月24日 下午4:03:00
     *
     *  @param userId
     *  @param healthTypes
     *  @return
     */
    public Map<String, Object> getHealthStandardValueByHealthType2(Integer userId, List<HealthType> healthTypes);

    /**
     *  @author yuhang.weng
     *  @DateTime 2016年7月26日 下午4:35:10
     *  @serverComment 获取设备最新的数据(设备类型限定)
     *
     *  @param entityName
     *  @param userId
     *  @param terminalType 设备类型
     *  @return
     *  @throws Exception
     */
    public <T> T selectDeviceDataLastest(Class<T> entityName, int userId, String terminalType, String measureDate);

}

