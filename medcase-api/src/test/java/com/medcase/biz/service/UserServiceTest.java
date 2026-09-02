package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserReviewRequest;
import com.medcase.biz.request.UserQuery;
import com.medcase.biz.response.UserVO;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.storage.pojo.FileAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userMapper);
    }

    @Test
    void userModelExposesReviewReason() {
        assertDoesNotThrow(() -> UserEntity.class.getDeclaredField("reviewReason"));
        assertDoesNotThrow(() -> UserVO.class.getDeclaredField("reviewReason"));
    }

    @Test
    void pagePassesQueryToMapperAndMapsDoctorFields() {
        UserQuery query = new UserQuery();
        query.setNickName("张医生");
        query.setPhone("13800000000");
        query.setStatus("0");

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setNickName("张医生");
        user.setUserName("doctor01");
        user.setPhonenumber("13800000000");
        user.setIdCardNumber("110101199001011234");
        user.setTitle("主治医师");
        user.setIdCardFront(attachment("front"));
        user.setIdCardBack(attachment("back"));
        user.setQualificationCertificate(attachment("qualification"));
        user.setStatus(UserStatusEnums.OK);
        when(userMapper.selectUserPage(
                any(PageParam.class), org.mockito.ArgumentMatchers.same(query), eq(UserTypeEnums.DOCTOR)))
                .thenReturn(new PageResult<>(List.of(user), 1L));

        PageResult<UserVO> result = userService.page(new PageParam(), query, UserTypeEnums.DOCTOR);

        verify(userMapper).selectUserPage(
                any(PageParam.class), org.mockito.ArgumentMatchers.same(query), eq(UserTypeEnums.DOCTOR));
        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getList().get(0).getId());
        assertEquals("张医生", result.getList().get(0).getNickName());
        assertEquals("110101199001011234", result.getList().get(0).getIdCardNumber());
        assertEquals("主治医师", result.getList().get(0).getTitle());
    }

    @Test
    void detailRejectsNonUser() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.ADMIN);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);

        assertNull(userService.detail(1L));
    }

    @Test
    void detailMapsDoctorAttachments() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setNickName("张医生");
        user.setIdCardNumber("110101199001011234");
        user.setTitle("主治医师");
        user.setIdCardFront(attachment("front"));
        user.setIdCardBack(attachment("back"));
        user.setQualificationCertificate(attachment("qualification"));
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);

        UserVO result = userService.detail(1L);

        assertEquals("front", result.getIdCardFront().getOriginalFilename());
        assertEquals("qualification", result.getQualificationCertificate().getOriginalFilename());
    }

    @Test
    void reviewApprovesPendingDoctor() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(true);

        userService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        verify(userMapper).updateById(user);
    }

    @Test
    void reviewApprovesRegisteredDoctor() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.REGISTER);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(true);

        userService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        verify(userMapper).updateById(user);
    }

    @Test
    void reviewRejectsRegisteredDoctor() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.REGISTER);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(false);
        request.setReason("身份证照片不清晰");

        userService.review(1L, request);

        assertEquals(UserStatusEnums.REVIEW_FAILED, user.getStatus());
        assertEquals("身份证照片不清晰", user.getReviewReason());
        verify(userMapper).updateById(user);
        verify(userMapper, never()).phoneExists(any());
    }

    @Test
    void reviewRejectsWithoutReason() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(false);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userService.review(1L, request));

        assertEquals(ErrorCodeEnums.USER_REVIEW_REASON_EMPTY, exception.getEc());
        assertEquals(UserStatusEnums.PENDING_REVIEW, user.getStatus());
        verify(userMapper, never()).updateById(any(UserEntity.class));
    }

    @Test
    void reviewDoesNotCheckPhoneDuplicate() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(true);

        userService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        verify(userMapper, never()).phoneExists(any());
        verify(userMapper).updateById(user);
    }

    @Test
    void reviewRejectsPendingDoctorWithReviewFailedStatus() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(false);
        request.setReason("资格证信息不完整");

        userService.review(1L, request);

        assertEquals(UserStatusEnums.REVIEW_FAILED, user.getStatus());
        assertEquals("资格证信息不完整", user.getReviewReason());
        verify(userMapper).updateById(user);
    }

    @Test
    void reviewApprovesDoctorAndClearsPreviousReviewReason() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        user.setReviewReason("历史拒绝原因");
        when(userMapper.selectUserById(1L, UserTypeEnums.DOCTOR)).thenReturn(user);
        when(userMapper.updateById(user)).thenReturn(1);

        UserReviewRequest request = new UserReviewRequest();
        request.setApprove(true);

        userService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        assertNull(user.getReviewReason());
        verify(userMapper).updateById(user);
    }

    private FileAttachment attachment(String filename) {
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("doctor/" + filename);
        attachment.setOriginalFilename(filename);
        return attachment;
    }
}
