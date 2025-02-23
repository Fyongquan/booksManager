package web.ssm.service;

import com.alibaba.fastjson.JSONObject;
import web.ssm.dto.UserDTO;
import web.ssm.dto.KaptchaDTO;

public interface LoginService {

  /**
   * @Description: 获取验证码信息
   * @Author: SN
   * @Date: 2023/02/31 11:02
   */
  KaptchaDTO getCode() throws Exception;

  /**
   * @Description: 登录认证
   * @Author: SN
   * @Date: 2020/02/31 11:02
   */
  JSONObject loginCheck(String loginCode, String loginPwd, String uuid, String imageCode);

  /**
   * @Description: 根据登录账号获取用户信息
   * @Author: SN
   * @Date: 2023/02/31 11:02
   */
  UserDTO getByLoginCode(String loginCode);

  /**
   * 根据手机号获取用户信息
   */
  UserDTO getByUserPhone(String phone);

  JSONObject logout(String token);
}
