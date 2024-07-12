package com.ruoyi.colorfulfog.controller;

import cn.dev33.satoken.secure.BCrypt;
import com.ruoyi.colorfulfog.model.dto.RegisterBodyDto;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限相关控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/colorful-fog/auth")
public class AuthController {
    @Autowired
    private SysUserServiceImpl sysUserService;

    /**
     * 外来账号注册，有效期7天，账号昵称同名。
     * 手机号收验证码注册
     * 新增一个角色，该角色可以看到超管设置数据和自己创建的数据
     * @param registerBody
     * @return
     */
    @PostMapping("register")
    public R<Void> register(@RequestBody RegisterBodyDto registerBody) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(registerBody.getPhoneNumber());
        sysUser.setNickName(registerBody.getNickName());
        sysUser.setPassword(BCrypt.hashpw(registerBody.getPassWord()));
        sysUser.setPhonenumber(registerBody.getPhoneNumber());
        sysUser.setRoleId(1783068772843823106L);
        sysUser.setDeptId(100L);
        sysUser.setUserType("游客");
        // 用户注册
        sysUserService.registerUserInfo(sysUser);
        return R.ok();
    }

}
