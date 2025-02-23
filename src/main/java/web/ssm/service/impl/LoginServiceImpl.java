package web.ssm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.Producer;
import web.ssm.dto.KaptchaDTO;
import web.ssm.dto.TokenDTO;
import web.ssm.dto.UserDTO;
import web.ssm.mapper.UserMapper;
import web.ssm.service.LoginService;
import web.ssm.utils.JWTUtil;
import web.ssm.utils.RedisUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class LoginServiceImpl implements LoginService {

  private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
  private final Producer kaptchaProducer;
  private final RedisUtil redisUtil;
  private final UserMapper userMapper;

  @Autowired
  public LoginServiceImpl(UserMapper userMapper, Producer kaptchaProducer, RedisUtil redisUtil) {
    this.userMapper = userMapper;
    this.kaptchaProducer = kaptchaProducer;
    this.redisUtil = redisUtil;
  }

  // token有效时长
  private Integer expire = 5;

  private static final String LOGIN_FAIL_PREFIX = "login:fail:";
  private static final int MAX_FAIL_COUNT = 5;
  private static final int LOCK_TIME = 30; // 锁定30分钟

  private boolean isAccountLocked(String loginCode) {
    Integer failCount = redisUtil.getLoginFailCount(loginCode);
    return failCount != null && failCount >= MAX_FAIL_COUNT;
  }

  private void incrementFailCount(String loginCode) {
    Integer failCount = redisUtil.getLoginFailCount(loginCode);
    if (failCount == null) {
      redisUtil.setLoginFailCount(loginCode, 1, LOCK_TIME);
    } else {
      redisUtil.setLoginFailCount(loginCode, failCount + 1, LOCK_TIME);
    }
  }

  @Override
  public KaptchaDTO getCode() throws Exception {
    // 生成验证码
    String text = kaptchaProducer.createText();
    BufferedImage bufferedImage = kaptchaProducer.createImage(text);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "png", outputStream);
    BASE64Encoder base64Encoder = new BASE64Encoder();
    String base64 = base64Encoder.encode(outputStream.toByteArray());
    String captchaBase64 = base64.replaceAll("\r\n", "");
    // 存入Redis数据库
    String uuid = RandomStringUtils.random(15, true, true);
    redisUtil.storeVerifyCode(uuid, text);
    // 生成验证码返回数
    KaptchaDTO kaptchaDTO = new KaptchaDTO();
    kaptchaDTO.setUuid(uuid);
    kaptchaDTO.setBase64(captchaBase64);
    return kaptchaDTO;
  }

  @Override
  public JSONObject loginCheck(String loginCode, String loginPwd, String uuid, String imageCode) {
    JSONObject result = new JSONObject();

    try {
      // 验证码校验
      String code = redisUtil.getVerifyCode(uuid);
      if (code == null) {
        result.put("message", "error");
        result.put("info", "验证码已过期");
        return result;
      }

      // 检查账号是否被锁定
      if (isAccountLocked(loginCode)) {
        result.put("message", "error");
        result.put("info", "账号已被锁定，请" + LOCK_TIME + "分钟后再试");
        return result;
      }

      if (!code.equalsIgnoreCase(imageCode)) {
        result.put("message", "error");
        result.put("info", "验证码错误");
        return result;
      }

      // 验证完成后删除验证码
      redisUtil.del("verify:code:" + uuid);

      // 验证用户名和密码
      UserDTO userDTO = this.getByLoginCode(loginCode);
      if (userDTO == null) {
        incrementFailCount(loginCode);
        result.put("message", "error");
        result.put("info", "用户名或密码错误");
        return result;
      }

      String encryptedPwd = DigestUtils.md5DigestAsHex(loginPwd.getBytes());
      if (!userDTO.getLoginPwd().equals(encryptedPwd)) {
        incrementFailCount(loginCode);
        result.put("message", "error");
        result.put("info", "用户名或密码错误");
        return result;
      }

      // 登录成功，清除失败记录
      redisUtil.del(LOGIN_FAIL_PREFIX + loginCode);

      // 生成token
      JSONObject tokenData = new JSONObject();
      tokenData.put("loginId", userDTO.getUserId());
      tokenData.put("loginCode", userDTO.getLoginCode());
      tokenData.put("loginName", userDTO.getUserName());
      tokenData.put("userType", userDTO.getUserType());
      tokenData.put("roleType", userDTO.getRoleType());

      String token = JWTUtil.createSign(tokenData.toJSONString(), 1440);

      // 存储token和用户信息
      redisUtil.storeLoginToken(userDTO.getUserId(), token);
      redisUtil.storeLoginInfo(token, userDTO);

      // 返回结果
      result.put("message", "success");
      result.put("token", token);
      result.put("info", userDTO);

    } catch (Exception e) {
      logger.error("登录失败", e);
      result.put("message", "error");
      result.put("info", "系统错误");
    }

    return result;
  }

  @Override
  public UserDTO getByLoginCode(String loginCode) {
    try {
      return userMapper.getByLoginCode(loginCode);
    } catch (Exception e) {
      logger.error("获取用户信息失败", e);
      return null;
    }
  }

  @Override
  public UserDTO getByUserPhone(String phone) {
    try {
      if (phone == null || phone.trim().isEmpty()) {
        return null;
      }
      return userMapper.getByUserPhone(phone);
    } catch (Exception e) {
      logger.error("获取用户信息失败", e);
      return null;
    }
  }

  @Override
  public JSONObject logout(String token) {
    JSONObject result = new JSONObject();
    try {
      // 获取用户信息
      UserDTO userDTO = redisUtil.getLoginInfo(token);
      if (userDTO != null) {
        // 删除Redis中的token和用户信息
        redisUtil.del("login:token:" + userDTO.getUserId());
        redisUtil.del("login:info:" + token);
      }

      result.put("message", "success");
    } catch (Exception e) {
      logger.error("注销失败", e);
      result.put("message", "error");
      result.put("info", "系统错误");
    }
    return result;
  }
}
