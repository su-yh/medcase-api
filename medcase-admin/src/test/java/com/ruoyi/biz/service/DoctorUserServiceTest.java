package com.ruoyi.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.biz.request.DoctorUserQuery;
import com.ruoyi.biz.response.DoctorUserVO;
import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mp.mybatis.PageParam;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DoctorUserServiceTest {
    private DoctorUserService doctorUserService;

    @Mock
    private DoctorUserMapper doctorUserMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorUserService = new DoctorUserService(doctorUserMapper);
    }

    @Test
    void pagePassesQueryToMapperAndMapsDoctorFields() {
        DoctorUserQuery query = new DoctorUserQuery();
        query.setName("张医生");
        query.setPhone("13800000000");
        query.setStatus("0");

        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setNickName("张医生");
        user.setUserName("doctor01");
        user.setPhonenumber("13800000000");
        user.setStatus("0");
        when(doctorUserMapper.selectDoctorPage(any(PageParam.class), org.mockito.ArgumentMatchers.same(query)))
                .thenReturn(new PageResult<>(List.of(user), 1L));

        PageResult<DoctorUserVO> result = doctorUserService.page(new PageParam(), query);

        verify(doctorUserMapper).selectDoctorPage(
                any(PageParam.class), org.mockito.ArgumentMatchers.same(query));
        assertEquals(1L, result.getTotal());
        assertEquals(1L, result.getList().get(0).getId());
        assertEquals("张医生", result.getList().get(0).getName());
    }

    @Test
    void detailRejectsNonDoctorUser() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.ADMIN);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);

        assertNull(doctorUserService.detail(1L));
    }
}
