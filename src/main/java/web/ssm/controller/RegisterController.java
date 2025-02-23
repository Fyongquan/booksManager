package web.ssm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import web.ssm.dto.OpResultDTO;
import web.ssm.dto.UserDTO;
import web.ssm.service.UserService;

/**
 * 用户注册控制器
 * 处理新用户注册功能
 */
@RestController
@RequestMapping("/api/register")
public class RegisterController {

  private final Logger logger = LoggerFactory.getLogger(RegisterController.class);
  private final UserService userService;

  @Autowired
  public RegisterController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 用户注册
   * 处理新用户注册请求，设置默认角色并保存用户信息
   * 
   * @param userDTO 用户信息，包含：
   *                - loginCode: 登录账号（必填）
   *                - loginPwd: 登录密码（必填）
   *                - userName: 用户名称
   *                - userEmail: 邮箱
   *                - userPhone: 手机号
   * @return 注册结果，包含：
   *         - success: 用户ID和用户信息
   *         - error: 错误信息
   */
  @PostMapping("/add")
  @ResponseBody
  public OpResultDTO register(@RequestBody UserDTO userDTO) {
    OpResultDTO opResult = new OpResultDTO();
    try {
      // 设置默认角色为普通用户
      userDTO.setRoleType("USER");

      // 添加用户
      Integer userId = userService.add(userDTO);
      logger.info("用户添加结果: userId={}", userId);

      if (userId != null && userId > 0) {
        opResult.setMsgResult("success");
        opResult.setObjResult(userId);
        opResult.setData(userDTO);
        opResult.setMsgInfo("注册成功");
      } else {
        opResult.setMsgResult("error");
        opResult.setMsgInfo("注册失败");
      }
    } catch (Exception e) {
      logger.error("用户注册失败: {}", e.getMessage(), e);
      opResult.setMsgResult("error");
      opResult.setMsgInfo(e.getMessage());
    }

    logger.info("返回结果: {}", opResult);
    return opResult;
  }
}