package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.service.imp.UserServiceImp;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取电话
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用阿里云服务向手机发送验证码
            //SMSUtils.sendMessage("瑞吉外卖","F4xvUh0AzJzbmAocmfztmsTksaHoLw",phone,code);
            log.info("code = " + code);
            //将生成验证码保存到Session
            session.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
        return R.error("短信发送失败");

    }

    /**
     * 登录检查，拿到手机号以及Phone
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取电话
        String phone = map.get("phone").toString();
        //获取提交过来的code
        String code = map.get("code").toString();
        //获取存在Session的验证码
        String codeInSession = session.getAttribute(phone).toString();
        if (code != null && code.equals(codeInSession)){
            //判断是否为新用户，是就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            //是新用户
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setName(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");

    }

}
