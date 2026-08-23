package com.ruoyi.biz.doctor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.biz.doctor.request.DoctorUserQuery;
import com.ruoyi.biz.doctor.response.DoctorUserVO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.ruoyi.system.service.ISysUserService;

class DoctorUserServiceTest {
    private DoctorUserService doctorUserService;

    @Mock
    private ISysUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorUserService = new DoctorUserService(userService);
    }

    @Test
    void listForcesDoctorUserTypeAndMapsDoctorFields() {
        DoctorUserQuery query = new DoctorUserQuery();
        query.setName("张医生");
        query.setUsername("doctor01");
        query.setPhone("13800000000");
        query.setStatus("0");

        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setNickName("张医生");
        user.setUserName("doctor01");
        user.setPhonenumber("13800000000");
        user.setStatus("0");
        when(userService.selectUserList(any(SysUser.class))).thenReturn(List.of(user));

        List<DoctorUserVO> result = doctorUserService.list(query);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(userService).selectUserList(captor.capture());
        assertEquals(UserTypeEnums.DOCTOR, captor.getValue().getUserType());
        assertEquals("张医生", captor.getValue().getNickName());
        assertEquals("doctor01", captor.getValue().getUserName());
        assertEquals("13800000000", captor.getValue().getPhonenumber());
        assertEquals("0", captor.getValue().getStatus());
        assertEquals(1L, result.get(0).getId());
        assertEquals("张医生", result.get(0).getName());
    }

    @Test
    void detailRejectsNonDoctorUser() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.ADMIN);
        when(userService.selectUserById(1L)).thenReturn(user);

        assertNull(doctorUserService.detail(1L));
    }
}
