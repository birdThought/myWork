package com.lifeshs.product.service.user.family.impl;

import com.lifeshs.product.dao.user.IMemberDao;
import com.lifeshs.product.domain.dto.common.ServiceMessage;
import com.lifeshs.product.domain.dto.user.MemberUserDTO;
import com.lifeshs.product.domain.dto.user.UserRecordDTO;
import com.lifeshs.product.domain.dto.user.GroupMemberVO;
import com.lifeshs.product.domain.po.member.TUser;
import com.lifeshs.product.service.common.transform.ICommonTrans;
import com.lifeshs.product.service.user.family.IFamilyService;
import com.lifeshs.product.utils.DateTimeUtilT;
import com.lifeshs.product.utils.MD5Utils;
import com.lifeshs.product.utils.PasswordUtil;
import com.lifeshs.product.utils.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class FamilyServiceImpl implements IFamilyService {

    @Autowired
    private ICommonTrans commonTrans;
    
    @Autowired
    private IMemberDao memberDao;

    @Override
    public TUser findUserByUserName(String userName) throws Exception {
        return commonTrans.findUniqueByProperty(TUser.class, "userName", userName);
    }

    @Override
    public Map<String, Object> findUserListByUserName(String userName, Integer userId) {
        Map<String, Object> data = new HashMap<>();

//        TUser userInvitee = commonTrans.findUniqueByProperty(TUser.class, "userName", userName);
        MemberUserDTO userInvitee = memberDao.getUserByUserName(userName);
        if (userInvitee == null) {
            return data;
        }
        UserRecordDTO recordDTO = userInvitee.getRecordDTO();

        TUser userNowLogin = commonTrans.getEntity(TUser.class, userId);
        String groupKey = userNowLogin.getGroupKey();

        boolean ownerMember = false;

        // group key相同，并且不为空， 即是用户已经在家庭组中
        if (StringUtils.equals(groupKey, userInvitee.getGroupKey()) && StringUtils.isNotBlank(groupKey)) {
            ownerMember = true;
        }

        String realName = userInvitee.getRealName() == null ? userInvitee.getUserName() : userInvitee.getRealName();
        
        if (realName.length() == 2) {
            realName = realName.substring(0, 1) + "****";
        } else if (realName.length() > 3) {
            realName = realName.substring(0, 1) + "****" + realName.substring(realName.length() - 1, realName.length());
        }
        
        Integer age = 0;
        if (recordDTO.getBirthday() != null) {
            age = DateTimeUtilT.calculateAge(recordDTO.getBirthday());
        }
        String mobile = userInvitee.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            mobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
        }

        data.put("id", userInvitee.getId());
        data.put("realName", realName);
        data.put("userName", userInvitee.getUserName());
        data.put("age", age);
        data.put("mobile", mobile);
        data.put("ownerMember", ownerMember);
        data.put("photo", userInvitee.getPhoto());

        return data;
    }

    @Override
    public ServiceMessage updateUserGroupKey(String userName, String password, int currentUserId) {
        String groupKey = getCurrentUserGroupKey(currentUserId);

        MemberUserDTO user = findUserByNameAndPassword(userName, password);
        if (user == null) {
            // 用户名或密码不正确
            return new ServiceMessage(false, "用户名或密码不正确");
        }

        if (groupKey.equals(user.getGroupKey())) {
            return new ServiceMessage(false, "该用户已经加入家庭组");
        }

        if (user.getGroupKey() != null && !"".equals(user.getGroupKey())) {
            // 该用户已经加入别的家庭组
            return new ServiceMessage(false, "该用户已经加入别的家庭组");
        }
        MemberUserDTO memberUserDTO = new MemberUserDTO();
        memberUserDTO.setId(user.getId());
        memberUserDTO.setGroupKey(groupKey);
        memberDao.updateUser(memberUserDTO);

        return new ServiceMessage(true, "成功加入家庭组");
    }

    private MemberUserDTO findUserByNameAndPassword(String userName, String password) {
        MemberUserDTO familyMember = memberDao.getUserByUserName(userName);
        if (familyMember == null) {
            return null;
        }
        String original_password = familyMember.getPassword();
        String md5Password = MD5Utils.encryptPassword(password);
        if (StringUtils.equals(md5Password, original_password)) {
            return familyMember;
        }
        // Start 临时使用，替换旧平台密码完成后清除 dengfeng
        String enpwdOld = PasswordUtil.encrypt("", password, PasswordUtil.getStaticSalt());
        if (StringUtils.equals(enpwdOld, original_password)) {
            // 密码正确，就将原密码修改为新的加密方式
            MemberUserDTO memberUserDTO = new MemberUserDTO();
            memberUserDTO.setId(familyMember.getId());
            memberUserDTO.setPassword(password);
            memberDao.updateUser(memberUserDTO);
        }
        return null;
    }

    @Override
    public List<GroupMemberVO> findGroupMember(Integer userId) {
        List<GroupMemberVO> users = new ArrayList<>();

        MemberUserDTO user = memberDao.getUser(userId);
        String groupKey = user.getGroupKey();

        List<MemberUserDTO> memberUserDTOS = memberDao.listUserByGroupKey(groupKey);
        if (memberUserDTOS != null) {
            for (MemberUserDTO member : memberUserDTOS) {
                GroupMemberVO tmp = new GroupMemberVO();

                UserRecordDTO recordDTO = member.getRecordDTO();
                String realName = member.getUserName();
                if (StringUtils.isNotBlank(member.getRealName())) {
                    realName = member.getRealName();
                }
                int age = 0;
                if (recordDTO.getBirthday() != null) {
                    age = DateTimeUtilT.calculateAge(recordDTO.getBirthday());
                }
                boolean isCurrentUser = false;
                if (userId.equals(member.getId())) {
                    isCurrentUser = true;
                }

                tmp.setId(member.getId());
                tmp.setRealName(realName);
                tmp.setAge(age);
                tmp.setMobile(member.getMobile());
                tmp.setIsCurrentUser(isCurrentUser);
                tmp.setBirthday(recordDTO.getBirthday());
                tmp.setHeight(recordDTO.getHeight());
                tmp.setWeight(recordDTO.getWeight());
                tmp.setHip(recordDTO.getHip());
                tmp.setWaist(recordDTO.getWaist());
                tmp.setBust(recordDTO.getBust());
                tmp.setSex(recordDTO.getGender());
                tmp.setPhoto(member.getPhoto());

                users.add(tmp);
            }
        }

        return users;
    }

    private String getCurrentUserGroupKey(int userId) {
        MemberUserDTO user = memberDao.getUser(userId);

        String groupKey = user.getGroupKey();
        if (groupKey == null || "".equals(groupKey)) {
            // 创建用户组
            groupKey = UUID.generate();
            MemberUserDTO memberUserDTO = new MemberUserDTO();
            memberUserDTO.setId(userId);
            memberUserDTO.setGroupKey(groupKey);
            memberDao.updateUser(memberUserDTO);
        }

        return groupKey;
    }

    @Override
    public void updateMemberInfo(MemberUserDTO user, Integer currentUserId) {
        if (currentUserId != null) {
            String groupKey = getCurrentUserGroupKey(currentUserId);
            user.setGroupKey(groupKey);
        }
        memberDao.updateUser(user);
        memberDao.updateUserRecord(user.getRecordDTO());
    }
}
