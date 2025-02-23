package web.ssm.controller;

import web.ssm.annotation.OperationLog;
import web.ssm.dto.OpResultDTO;
import web.ssm.dto.UserDTO;
import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;
import web.ssm.dto.TokenDTO;
import web.ssm.service.LoginService;
import web.ssm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;

import web.ssm.utils.JWTUtil;
import web.ssm.utils.PathUtils;

/**
 * 用户管理控制器
 * 处理用户的增删改查、密码管理、头像上传等功能
 * 包括：
 * - 用户管理（管理员）
 * - 个人信息管理（普通用户）
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;
  private final PathUtils pathUtils;
  private final Logger logger = LoggerFactory.getLogger(UserController.class);
  private final LoginService loginService;

  @Autowired
  public UserController(UserService userService, PathUtils pathUtils, LoginService loginService) {
    this.userService = userService;
    this.pathUtils = pathUtils;
    this.loginService = loginService;
  }

  /**
   * 获取用户列表
   * 支持分页和搜索
   * 
   * @param tableReqDTO 分页和查询参数，包含：
   *                    - currentPage: 当前页码
   *                    - pageSize: 每页条数
   *                    - queryText: 搜索关键词
   * @param token       JWT token
   * @return 分页数据
   */
  @GetMapping("/query")
  public OpResultDTO list4Table(@RequestHeader String token, TableReqDTO tableReqDTO) {
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

      if (tableReqDTO == null) {
        tableReqDTO = new TableReqDTO();
        tableReqDTO.setPageNum(1);
        tableReqDTO.setPageSize(10);
        tableReqDTO.setQueryText("");
      }

      TableRspDTO data = userService.list4Table(tableReqDTO);
      result.setMsgResult("success");
      result.setData(data);
    } catch (Exception e) {
      logger.error("获取用户列表失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取用户列表失败");
    }
    return result;
  }

  /**
   * 添加用户
   * 创建新用户账号
   * 
   * @param userDTO 用户信息
   * @param token   JWT token
   * @return 新用户ID和信息
   */
  @PostMapping(value = "/add", produces = "application/json;charset=UTF-8")
  @ResponseBody
  @OperationLog("添加用户")
  public OpResultDTO add(@RequestHeader String token, @RequestBody UserDTO userDTO) {
    logger.info("=== 开始处理添加用户请求 ===");
    logger.info("接收到的用户数据: {}", userDTO);
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

      Integer userId = userService.add(userDTO);
      logger.info("用户添加结果: userId={}", userId);
      if (userId != null && userId > 0) {
        result.setMsgResult("success");
        result.setObjResult(userId);
        result.setData(userDTO);
      } else {
        result.setMsgResult("error");
        result.setMsgInfo("添加用户失败");
      }
    } catch (Exception e) {
      logger.error("添加用户失败", e);
      result.setMsgResult("error");
      result.setMsgInfo(e.getMessage());
    }
    logger.info("添加用户处理完成，返回结果: {}", result);
    return result;
  }

  /**
   * 更新用户信息
   * 修改用户基本信息
   * 
   * @param token   JWT token
   * @param userDTO 用户信息
   * @return 更新结果
   */
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @OperationLog("更新用户信息")
  public OpResultDTO update(@RequestHeader String token, @RequestBody UserDTO userDTO) {
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

      result.setMsgResult("success");
      result.setObjResult(userService.edit(userDTO));
    } catch (Exception e) {
      logger.error(e.toString());
      result.setMsgResult("error");
    }
    return result;
  }

  /**
   * 删除用户
   * 
   * @param userId 用户ID
   * @param token  JWT token
   */
  @DeleteMapping("/{id}")
  @OperationLog("删除用户")
  public OpResultDTO remove(@RequestHeader String token, @PathVariable("id") Integer userId) {
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

      boolean success = userService.remove(userId);
      if (success) {
        result.setMsgResult("success");
      } else {
        result.setMsgResult("error");
        result.setMsgInfo("删除用户失败");
      }
    } catch (Exception e) {
      logger.error("删除用户失败: userId={}", userId, e);
      result.setMsgResult("error");
      result.setMsgInfo("删除用户失败: " + e.getMessage());
    }
    return result;
  }

  /**
   * 获取用户总数
   * 用于统计和展示
   * 
   * @param token JWT token
   */
  @RequestMapping(value = "/count", method = RequestMethod.GET)
  @OperationLog("获取用户总数")
  public OpResultDTO getUserCount(@RequestHeader String token) {
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

      Integer count = userService.getUserCount();
      result.setMsgResult("success");
      result.setData(count);
    } catch (Exception e) {
      logger.error("获取用户总数失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取用户总数失败");
    }
    return result;
  }

  /**
   * 修改个人信息
   * 
   * @param token JWT token
   */
  @PutMapping("/profile")
  @OperationLog("修改个人信息")
  public OpResultDTO updateProfile(@RequestHeader String token, @RequestBody UserDTO userDTO) {
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

      // 验证邮箱是否被其他用户使用
      UserDTO existingUserByEmail = userService.getByEmail(userDTO.getUserEmail());
      if (existingUserByEmail != null && !existingUserByEmail.getUserId().equals(userDTO.getUserId())) {
        result.setMsgResult("error");
        result.setMsgInfo("该邮箱已被其他用户使用");
        return result;
      }

      // 验证手机号是否被其他用户使用
      UserDTO existingUserByPhone = userService.getByPhone(userDTO.getUserPhone());
      if (existingUserByPhone != null && !existingUserByPhone.getUserId().equals(userDTO.getUserId())) {
        result.setMsgResult("error");
        result.setMsgInfo("该手机号已被其他用户使用");
        return result;
      }

      userService.updateProfile(userDTO);
      result.setMsgResult("success");
      result.setData(userDTO);
    } catch (Exception e) {
      result.setMsgResult("error");
      result.setMsgInfo(e.getMessage());
    }
    return result;
  }

  /**
   * 修改密码
   * 验证旧密码并更新新密码
   * 
   * @param token  JWT token
   * @param params 包含：userId, oldPassword, newPassword
   */
  @PostMapping("/updatePassword")
  @OperationLog("修改密码")
  public OpResultDTO updatePassword(@RequestHeader String token, @RequestBody Map<String, String> params) {
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

      Integer userId = Integer.parseInt(params.get("userId"));
      String oldPassword = params.get("oldPassword");
      String newPassword = params.get("newPassword");

      Integer updateResult = userService.updatePassword(userId, oldPassword, newPassword);
      if (updateResult > 0) {
        result.setMsgResult("success");
        result.setMsgInfo("密码修改成功");
      } else {
        result.setMsgResult("error");
        result.setMsgInfo("密码修改失败");
      }
    } catch (Exception e) {
      logger.error("修改密码失败", e);
      result.setMsgResult("error");
      result.setMsgInfo(e.getMessage());
    }
    return result;
  }

  /**
   * 重置密码
   * 将用户密码重置为默认值（123456）
   * 
   * @RequestHeader String token
   * @param userId 用户ID
   */
  @PostMapping("/resetPassword/{userId}")
  @OperationLog("重置密码")
  public OpResultDTO resetPassword(@RequestHeader String token, @PathVariable("userId") Integer userId) {
    OpResultDTO opResult = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        opResult.setMsgResult("error");
        opResult.setMsgInfo("token无效");
        return opResult;
      }

      Integer result = userService.resetPassword(userId);
      if (result > 0) {
        opResult.setMsgResult("success");
        opResult.setMsgInfo("密码已重置为: 123456");
      } else {
        opResult.setMsgResult("error");
        opResult.setMsgInfo("重置密码失败");
      }
    } catch (Exception e) {
      logger.error("重置密码失败: userId={}", userId, e);
      opResult.setMsgResult("error");
      opResult.setMsgInfo(e.getMessage());
    }
    return opResult;
  }

  /**
   * 上传头像
   * 保存头像文件并更新用户头像路径
   * 
   * @param token  JWT token
   * @param file   头像图片文件
   * @param userId 用户ID
   * 
   * @return 头像访问路径
   */
  @PostMapping("/avatar")
  @OperationLog("上传头像")
  public OpResultDTO uploadAvatar(@RequestHeader String token, @RequestParam("file") MultipartFile file,
      @RequestParam Integer userId) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      // 获取项目根路径
      String projectPath = System.getProperty("user.dir");
      // 设置上传目录
      String uploadDir = projectPath + "/src/main/resources/static/uploads";
      File dir = new File(uploadDir);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      // 生成文件名
      String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
      String fileName = UUID.randomUUID().toString() + suffix;

      // 保存文件
      File dest = new File(dir, fileName);
      logger.info("Saving file to: {}", dest.getAbsolutePath());
      file.transferTo(dest);

      // 更新用户头像路径
      UserDTO userDTO = new UserDTO();
      userDTO.setUserId(userId);
      userDTO.setAvatar("/uploads/" + fileName);

      userService.updateAvatar(userDTO);

      result.setMsgResult("success");
      result.setData("/uploads/" + fileName);
    } catch (Exception e) {
      logger.error("上传头像失败: {}", e.getMessage(), e);
      result.setMsgResult("error");
      result.setMsgInfo("上传失败");
    }
    return result;
  }

  /**
   * 获取当前登录用户信息
   * 根据token获取用户详细信息，并清除敏感数据
   * 
   * @param token JWT token，从请求头获取
   * @return 用户信息，包含：
   *         - success: 用户基本信息（不含密码）
   *         - error: 获取失败信息
   */
  @GetMapping("/info")
  public OpResultDTO getUserInfo(@RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      // 1. 验证token并获取用户标识
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO != null) {
        // 2. 根据登录账号获取完整用户信息
        UserDTO userDTO = loginService.getByLoginCode(tokenDTO.getLoginCode());
        if (userDTO != null) {
          // 3. 清除敏感信息（密码）
          userDTO.setLoginPwd(null);
          result.setMsgResult("success");
          result.setData(userDTO);
          return result;
        }
      }
      result.setMsgResult("error");
      result.setMsgInfo("获取用户信息失败");
    } catch (Exception e) {
      result.setMsgResult("error");
      result.setMsgInfo("系统错误");
    }
    return result;
  }
}
