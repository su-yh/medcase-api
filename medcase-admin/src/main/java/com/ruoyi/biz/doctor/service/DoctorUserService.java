package com.ruoyi.biz.doctor.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.biz.doctor.request.DoctorUserQuery;
import com.ruoyi.biz.doctor.response.DoctorUserVO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 医生管理业务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class DoctorUserService {
    private final ISysUserService userService;

    public List<DoctorUserVO> list(DoctorUserQuery query) {
        SysUser condition = new SysUser();
        condition.setUserType(UserTypeEnums.DOCTOR);
        condition.setNickName(query == null ? null : query.getName());
        condition.setUserName(query == null ? null : query.getUsername());
        condition.setPhonenumber(query == null ? null : query.getPhone());
        condition.setStatus(query == null ? null : query.getStatus());
        return userService.selectUserList(condition).stream()
                .map(DoctorUserVO::fromUser)
                .collect(Collectors.toList());
    }

    public DoctorUserVO detail(Long userId) {
        if (userId == null) {
            return null;
        }

        SysUser user = userService.selectUserById(userId);
        if (user == null || user.getUserType() != UserTypeEnums.DOCTOR) {
            return null;
        }
        userService.checkUserDataScope(userId);
        return DoctorUserVO.fromUser(user);
    }
}
