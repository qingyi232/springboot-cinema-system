package com.project.platform.controller;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.LoginDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.SendRetrieveCodeDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.exception.CustomException;
import com.project.platform.service.AdminService;
import com.project.platform.service.CinemaService;
import com.project.platform.service.CommonService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.JwtUtils;
import com.project.platform.utils.RetrieveCodeStore;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @Resource
    private CinemaService cinemaService;

    @Resource
    private RetrieveCodeStore retrieveCodeStore;

    @PostMapping("login")
    public ResponseVO<String> login(@RequestBody LoginDTO loginDTO) {
        CommonService commonService = getCommonService(loginDTO.getType());
        CurrentUserDTO currentUserDTO = commonService.login(loginDTO.getUsername(), loginDTO.getPassword());
        currentUserDTO.setType(loginDTO.getType());
        String token = JwtUtils.generateToken(currentUserDTO);
        return ResponseVO.ok(token);
    }

    @PutMapping("register")
    public ResponseVO register(@RequestBody JSONObject data) {
        String type = data.getString("type");
        CommonService commonService = getCommonService(type);
        commonService.register(data);
        return ResponseVO.ok();
    }

    @PostMapping("updateCurrentUser")
    public ResponseVO updateCurrentUser(@RequestBody CurrentUserDTO currentUserDTO) {
        CommonService commonService = getCommonService(CurrentUserThreadLocal.getCurrentUser().getType());
        commonService.updateCurrentUserInfo(currentUserDTO);
        return ResponseVO.ok();
    }

    @PostMapping("updatePassword")
    public ResponseVO updatePassword(@RequestBody UpdatePasswordDTO updatePassword) {
        CommonService commonService = getCommonService(CurrentUserThreadLocal.getCurrentUser().getType());
        commonService.updateCurrentUserPassword(updatePassword);
        return ResponseVO.ok();
    }

    @PostMapping("resetPassword")
    public ResponseVO resetPassword(@RequestParam String type, @RequestParam Integer id) {
        CommonService commonService = getCommonService(type);
        commonService.resetPassword(id);
        return ResponseVO.ok();
    }

    @GetMapping("currentUser")
    public ResponseVO<CurrentUserDTO> getCurrentUser() {
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        CommonService commonService = getCommonService(CurrentUserThreadLocal.getCurrentUser().getType());
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        BeanUtils.copyProperties(commonService.selectById(userId), currentUserDTO);
        currentUserDTO.setType(CurrentUserThreadLocal.getCurrentUser().getType());
        return ResponseVO.ok(currentUserDTO);
    }

    @PostMapping("sendRetrieveCode")
    public ResponseVO<String> sendRetrieveCode(@RequestBody SendRetrieveCodeDTO sendRetrieveCodeDTO) {
        validateRetrieveBase(sendRetrieveCodeDTO.getType(), sendRetrieveCodeDTO.getTel());
        CommonService commonService = getCommonService(sendRetrieveCodeDTO.getType());
        commonService.assertTelExists(sendRetrieveCodeDTO.getTel());
        String code = retrieveCodeStore.generateCode(sendRetrieveCodeDTO.getType(), sendRetrieveCodeDTO.getTel());
        return ResponseVO.ok(code);
    }

    @PostMapping("retrievePassword")
    public ResponseVO retrievePassword(@RequestBody RetrievePasswordDTO retrievePasswordDTO) {
        validateRetrieveBase(retrievePasswordDTO.getType(), retrievePasswordDTO.getTel());
        if (retrievePasswordDTO.getCode() == null || retrievePasswordDTO.getCode().isBlank()) {
            throw new CustomException("验证码不能为空");
        }
        retrieveCodeStore.verifyCode(retrievePasswordDTO.getType(), retrievePasswordDTO.getTel(), retrievePasswordDTO.getCode());
        CommonService commonService = getCommonService(retrievePasswordDTO.getType());
        commonService.retrievePassword(retrievePasswordDTO);
        return ResponseVO.ok();
    }

    private void validateRetrieveBase(String type, String tel) {
        if (type == null || type.isBlank()) {
            throw new CustomException("用户类型不能为空");
        }
        if (tel == null || tel.isBlank()) {
            throw new CustomException("手机号不能为空");
        }
    }

    private CommonService getCommonService(String type) {
        switch (type) {
            case "ADMIN":
                return adminService;
            case "USER":
                return userService;
            case "CINEMA":
                return cinemaService;
            default:
                throw new CustomException("用户类型错误");
        }
    }
}
