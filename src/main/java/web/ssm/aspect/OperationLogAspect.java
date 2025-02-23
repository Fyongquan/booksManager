package web.ssm.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.ssm.annotation.OperationLog;
import web.ssm.dto.OpResultDTO;
import web.ssm.dto.TokenDTO;
import web.ssm.entity.OperationLogEntity;
import web.ssm.mapper.OperationLogMapper;
import web.ssm.utils.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

  private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

  @Autowired
  private OperationLogMapper operationLogMapper;

  @Around("@annotation(operationLog)")
  public Object aroundOperation(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
    // 如果是导出方法，直接执行，不记录日志
    if (joinPoint.getSignature().getName().equals("exportBooks")) {
      return joinPoint.proceed();
    }

    // 获取当前请求对象
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    // 从token中获取用户信息
    String token = request.getHeader("token");
    TokenDTO tokenDTO = JWTUtil.verifyToken(token);

    // 创建操作日志实体
    OperationLogEntity logEntity = new OperationLogEntity();
    logEntity.setUserId(Integer.parseInt(tokenDTO.getLoginId().toString()));
    logEntity.setUserName(tokenDTO.getLoginName());
    logEntity.setOperation(operationLog.value());
    logEntity.setMethod(joinPoint.getSignature().getName());

    // 处理方法参数
    Object[] args = joinPoint.getArgs();
    String params = handleMethodArgs(args, joinPoint);
    logEntity.setParams(params);

    logEntity.setIp(getIpAddress(request));
    logEntity.setGmtCreate(new Date());

    try {
      // 执行目标方法
      Object result = joinPoint.proceed();

      // 保存日志
      operationLogMapper.insert(logEntity);
      return result;

    } catch (Exception e) {
      // 记录异常日志
      logger.error("操作执行失败", e);
      operationLogMapper.insert(logEntity);
      throw e;
    }
  }

  /**
   * 处理方法参数，特殊处理某些类型
   */
  private String handleMethodArgs(Object[] args, ProceedingJoinPoint joinPoint) {
    if (args == null || args.length == 0) {
      return "";
    }

    Object[] logArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof MultipartFile) {
        // 处理文件上传
        MultipartFile file = (MultipartFile) args[i];
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("fileName", file.getOriginalFilename());
        fileInfo.put("fileSize", file.getSize());
        fileInfo.put("contentType", file.getContentType());
        logArgs[i] = fileInfo;
      } else if (args[i] instanceof HttpServletResponse) {
        // 跳过response对象
        continue;
      } else if (args[i] instanceof String
          && "token".equals(((MethodSignature) joinPoint.getSignature()).getParameterNames()[i])) {
        // 跳过token参数
        continue;
      } else {
        logArgs[i] = args[i];
      }
    }
    return JSON.toJSONString(logArgs);
  }

  /**
   * 获取IP地址
   */
  private String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}