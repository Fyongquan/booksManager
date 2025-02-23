package web.ssm.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import web.ssm.dto.UserDTO;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

  private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

  @Autowired
  private StringRedisTemplate redisTemplate;

  private static final int DEFAULT_EXPIRE = 1440; // 默认过期时间24小时（分钟）
  private static final String LOGIN_FAIL_PREFIX = "login:fail:"; // 登录失败次数前缀
  private static final String VERIFY_CODE_PREFIX = "verify:code:"; // 添加前缀常量
  private static final int VERIFY_CODE_EXPIRE = 1; // 验证码过期时间（分钟）

  /**
   * 存储字符串
   */
  public void set(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  /**
   * 存储带过期时间的字符串（分钟）
   */
  public void set(String key, String value, int minutes) {
    redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
  }

  /**
   * 获取字符串
   */
  public String get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  /**
   * 删除键
   */
  public void del(String key) {
    redisTemplate.delete(key);
  }

  /**
   * 存储登录token
   */
  public void storeLoginToken(Integer loginId, String token) {
    String key = "login:token:" + loginId;
    set(key, token, DEFAULT_EXPIRE);
  }

  /**
   * 获取登录token
   */
  public String getLoginToken(Integer loginId) {
    return get("login:token:" + loginId);
  }

  /**
   * 存储登录信息
   */
  public void storeLoginInfo(String token, UserDTO userDTO) {
    String userJson = JSONObject.toJSONString(userDTO);
    set("login:info:" + token, userJson, DEFAULT_EXPIRE);
  }

  /**
   * 获取登录信息
   */
  public UserDTO getLoginInfo(String token) {
    String userJson = get("login:info:" + token);
    return userJson != null ? JSONObject.parseObject(userJson, UserDTO.class) : null;
  }

  /**
   * 存储验证码
   */
  public void storeVerifyCode(String uuid, String code) {
    String key = VERIFY_CODE_PREFIX + uuid;
    set(key, code, VERIFY_CODE_EXPIRE);
    logger.debug("存储验证码: key={}, code={}", key, code);
  }

  /**
   * 获取验证码
   */
  public String getVerifyCode(String uuid) {
    String key = VERIFY_CODE_PREFIX + uuid;
    String code = get(key);
    logger.debug("获取验证码: key={}, code={}", key, code);
    return code;
  }

  /**
   * 存储整数值
   */
  public void setInt(String key, int value, int minutes) {
    set(key, String.valueOf(value), minutes);
  }

  /**
   * 获取整数值
   */
  public Integer getInt(String key) {
    String value = get(key);
    return value != null ? Integer.parseInt(value) : null;
  }

  /**
   * 获取登录失败次数
   */
  public Integer getLoginFailCount(String loginCode) {
    String value = get(LOGIN_FAIL_PREFIX + loginCode);
    return value != null ? Integer.parseInt(value) : null;
  }

  /**
   * 设置登录失败次数
   */
  public void setLoginFailCount(String loginCode, int count, int minutes) {
    set(LOGIN_FAIL_PREFIX + loginCode, String.valueOf(count), minutes);
  }
}
