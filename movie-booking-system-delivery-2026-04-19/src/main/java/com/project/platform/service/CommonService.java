package com.project.platform.service;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;

public interface CommonService<T> {
    CurrentUserDTO login(String username, String password);

    void register(JSONObject data);

    void updateCurrentUserInfo(CurrentUserDTO currentUserDTO);

    void updateCurrentUserPassword(UpdatePasswordDTO updatePassword);

    void resetPassword(Integer id);

    T selectById(Integer id);

    void retrievePassword(RetrievePasswordDTO retrievePasswordDTO);

    void assertTelExists(String tel);
}
