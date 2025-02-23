package web.ssm.aspect;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import web.ssm.dto.LoginDTO;
import web.ssm.entity.LoginLogEntity;
import web.ssm.mapper.LoginLogMapper;
import web.ssm.utils.LoginLogHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 登录日志切面
 * 用于记录用户登录相关的日志信息
 */
@Aspect
@Component
public class LoginLogAspect {

  @Autowired
  private LoginLogMapper loginLogMapper;

  /**
   * 登录前的处理
   * 记录登录的基本信息，如登录账号、IP地址、时间等
   * 
   * @param joinPoint 切点
   */
  @Before("execution(* web.ssm.controller.LoginController.loginCheck(..))")
  public void beforeLogin(JoinPoint joinPoint) {
    // 获取当前请求的上下文信息
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    // 从请求参数中获取登录信息
    Object[] args = joinPoint.getArgs();
    LoginDTO loginDTO = (LoginDTO) args[0];

    // 创建并填充登录日志实体
    LoginLogEntity log = new LoginLogEntity();
    log.setLoginCode(loginDTO.getLoginCode()); // 设置登录账号
    log.setIpAddress(getIpAddress(request)); // 设置IP地址
    log.setLoginTime(new Date()); // 设置登录时间
    log.setUserAgent(request.getHeader("User-Agent")); // 设置用户代理信息

    // 将日志信息存储在ThreadLocal中，供后续使用
    LoginLogHolder.setCurrentLog(log);
  }

  /**
   * 登录完成后的处理
   * 根据登录结果记录登录状态和消息
   * 
   * @param joinPoint 切点
   * @param result    登录结果
   */
  @AfterReturning(pointcut = "execution(* web.ssm.controller.LoginController.loginCheck(..))", returning = "result")
  public void afterLogin(JoinPoint joinPoint, Object result) {
    try {
      LoginLogEntity log = LoginLogHolder.getCurrentLog();
      if (log != null) {
        JSONObject jsonResult = (JSONObject) result;
        // 根据返回结果判断登录状态
        if ("success".equals(jsonResult.getString("message"))) {
          // 登录成功的日志记录
          log.setLoginStatus("SUCCESS");
          log.setLoginMessage("登录成功");
        } else {
          // 登录失败的日志记录
          log.setLoginStatus("FAILED");
          log.setLoginMessage(jsonResult.getString("info"));
        }
        // 保存日志到数据库
        loginLogMapper.insert(log);
      }
    } finally {
      // 清理ThreadLocal中的数据，防止内存泄漏
      LoginLogHolder.clear();
    }
  }

  /**
   * 获取请求的真实IP地址
   * 考虑了各种代理服务器的情况
   * 
   * @param request HTTP请求
   * @return IP地址
   */
  private String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}