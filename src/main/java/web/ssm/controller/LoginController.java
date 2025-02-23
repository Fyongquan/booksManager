package web.ssm.controller;

import com.alibaba.fastjson.JSONObject;
import web.ssm.dto.KaptchaDTO;
import web.ssm.dto.LoginDTO;
import web.ssm.dto.OpResultDTO;
import web.ssm.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.ssm.utils.JWTUtil;
import web.ssm.dto.TokenDTO;

/**
 * 登录控制器
 * 处理用户登录、验证码生成、token验证等功能
 */
@RestController
@RequestMapping("/api/login")
public class LoginController {

  private final Logger logger = LoggerFactory.getLogger(LoginController.class);
  private final LoginService loginService;

  @Autowired
  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  /**
   * 获取验证码
   * 生成图片验证码并返回Base64编码
   * 
   * @return 验证码信息，包含：
   *         - uuid: 验证码标识
   *         - base64: 验证码图片的Base64编码
   */
  @GetMapping("/code")
  public OpResultDTO getCode() {
    OpResultDTO result = new OpResultDTO();
    try {
      KaptchaDTO kaptchaDTO = loginService.getCode();
      result.setMsgResult("success");
      result.setData(kaptchaDTO);
    } catch (Exception e) {
      logger.error("获取验证码失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取验证码失败");
    }
    return result;
  }

  /**
   * 用户登录验证
   * 验证用户名、密码和验证码
   * 
   * @param loginDTO 登录信息，包含：
   *                 - loginCode: 登录账号
   *                 - loginPwd: 登录密码
   *                 - uuid: 验证码标识
   *                 - imageCode: 验证码
   * @return 登录结果，包含：
   *         - success: token信息
   *         - error: 错误信息
   */
  @PostMapping("/check")
  public JSONObject loginCheck(@RequestBody LoginDTO loginDTO) {
    try {
      return loginService.loginCheck(
          loginDTO.getLoginCode(),
          loginDTO.getLoginPwd(),
          loginDTO.getUuid(),
          loginDTO.getImageCode());
    } catch (Exception e) {
      logger.error("登录失败", e);
      JSONObject result = new JSONObject();
      result.put("message", "error");
      result.put("info", "系统错误");
      return result;
    }
  }

  /**
   * Token验证
   * 验证token的有效性并返回用户信息
   * 
   * @param token JWT token
   * @return 验证结果，包含：
   *         - success: 用户信息
   *         - error: token无效或过期
   */
  @GetMapping("/token")
  public OpResultDTO tokenCheck(@RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
      } else {
        result.setMsgResult("success");
        result.setData(tokenDTO);
      }
    } catch (Exception e) {
      logger.error("Token验证失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("token已过期");
    }
    return result;
  }
}
