package web.ssm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 用于统一处理系统中抛出的异常
 * 将异常信息转换为友好的响应格式
 */
@RestControllerAdvice // 标记为全局异常处理器，并将返回值自动转换为JSON
public class GlobalExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * 处理所有未被特定处理器处理的异常
   * 
   * @param e 捕获到的异常
   * @return 统一的错误响应格式
   */
  @ExceptionHandler(Exception.class) // 处理 Exception 类型的异常
  public Map<String, Object> handleException(Exception e) {
    // 记录错误日志
    logger.error("系统异常", e);

    // 构建错误响应
    Map<String, Object> result = new HashMap<>();
    result.put("status", "error"); // 状态标识
    result.put("message", e.getMessage()); // 错误信息
    result.put("exception", e.getClass().getName()); // 异常类名

    // 获取完整堆栈信息
    StringBuilder stackTrace = new StringBuilder();
    for (StackTraceElement element : e.getStackTrace()) {
      stackTrace.append("\n    at ").append(element.toString());
    }
    result.put("stackTrace", stackTrace.toString()); // 堆栈信息

    return result;
  }
}