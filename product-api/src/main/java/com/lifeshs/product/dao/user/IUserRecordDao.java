package com.lifeshs.product.dao.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lifeshs.product.domain.vo.healthData.UserRecord;
import com.lifeshs.product.domain.vo.notice.UserDeviceTokenPO;
import com.lifeshs.product.domain.vo.statistice.StatisticsVO;
import com.lifeshs.product.domain.vo.user.UserVo;
import com.lifeshs.product.domain.vo.user.memberCountVo;

import java.util.List;

@Repository
@Mapper
public interface IUserRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserRecord record);

    int insertSelective(UserRecord record);

    UserRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserRecord record);

    int updateByPrimaryKey(UserRecord record);

    /**
     * 根据userId获取用户记录对象
     *
     * @param userId
     * @return
     */
    UserRecord selectByUserId(@Param("userId") Integer userId);

    /**
     * 分页获取用户数据
     * @param curPage
     * @param pageSize
     * @return
     */
    List<UserVo> findByUserInfoList(@Param("userName")String userName,@Param("realName")String realName,@Param("orgName") String orgName,
                                    @Param("mobile")String mobile,@Param("curPage") int curPage , @Param("pageSize") int pageSize);
    /**
     * 分页获取用户数据(代理商过滤)
     * @param curPage
     * @param pageSize
     * @return
     */
    List<UserVo> findByUserInfoListAgent(@Param("userNo") String userNo,@Param("userName")String userName,@Param("realName")String realName,@Param("orgName") String orgName,
                                    @Param("mobile")String mobile,@Param("curPage") int curPage , @Param("pageSize") int pageSize);

    /**
     * 总记录
     * @return
     */
    int findByCountSum(@Param("userName") String userName,@Param("realName") String realName,@Param("orgName") String orgName,@Param("mobile") String mobile);
    /**
     * 总记录(代理商过滤)
     * @return
     */
    int findByCountSumAgent(@Param("userNo") String userNo,@Param("userName") String userName,@Param("realName") String realName,@Param("orgName") String orgName,@Param("mobile") String mobile);

    /**
     * 条件查询
     * @param province
     * @param gender
     * @param startAge
     * @param endAge
     * @param weight
     * @param label
     * @param mobile
     * @return
     */
    List<StatisticsVO> findStatisticsDetaList(@Param("province")String province,@Param("users") Integer users,@Param("gender")Integer gender,@Param("startAge")String startAge
                                            ,@Param("endAge")String endAge,@Param("weight")Integer weight,@Param("label")Integer label,@Param("mobile")
                                              String mobile,@Param("curPage") int curPage,@Param("pageSize") int pageSize);
    
    List<UserDeviceTokenPO> findUserDeviceTokenList(@Param("province")String province,@Param("users") Integer users,@Param("gender")Integer gender,@Param("startAge")String startAge
            ,@Param("endAge")String endAge,@Param("weight")Integer weight,@Param("label")Integer label,@Param("mobile") String mobile);
    

    /**
     * 条件查询
     * @param users
     * @param gender
     * @param startAge
     * @param endAge
     * @param weight
     * @param label
     * @param mobile
     * @return
     */
    int findByDataListSum(@Param("province")String province,@Param("users")Integer users, @Param("gender")Integer gender, @Param("startAge")String startAge, @Param("endAge")String endAge,
                          @Param("weight")Integer weight, @Param("label")Integer label,@Param("mobile") String mobile);



    List<memberCountVo> findByCountDataList(@Param("userNo") String userNo, @Param("orgName")String orgName,@Param("province")String province, @Param("avgAge")Integer avgAge, @Param("radioValue")Integer radioValue, @Param("curPage")int curPage, @Param("pageSize")int pageSize);

    int findByCountData(@Param("userNo") String userNo, @Param("orgName")String orgName, @Param("province")String province,@Param("avgAge")Integer avgAge, @Param("radioValue")Integer radioValue);

    memberCountVo countGenderSum(@Param("userNo") String userNo);
}