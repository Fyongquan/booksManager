package web.ssm.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import web.ssm.dto.TokenDTO;
import web.ssm.utils.JWTUtil;
import web.ssm.utils.RedisUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Token 拦截器
 * 用于验证请求中的 token
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

  @Autowired
  private RedisUtil redisUtil;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // 获取token
    String token = request.getHeader("token");
    if (token == null || token.isEmpty()) {
      logger.warn("Token为空");
      responseError(response, "未登录");
      return false;
    }

    // 验证token
    TokenDTO tokenDTO = JWTUtil.verifyToken(token);
    if (tokenDTO == null) {
      logger.warn("Token无效");
      responseError(response, "token无效");
      return false;
    }

    // 检查是否是当前用户的最新token
    String currentToken = redisUtil.getLoginToken(tokenDTO.getLoginId());
    logger.debug("当前Token: {}", token);
    logger.debug("Redis中的Token: {}", currentToken);

    if (currentToken == null) {
      logger.warn("Redis中不存在Token");
      responseError(response, "登录已过期，请重新登录");
      return false;
    }

    if (!token.equals(currentToken)) {
      logger.warn("Token不匹配，用户已在其他地方登录");
      responseError(response, "您的账号已在其他地方登录，请重新登录");
      return false;
    }

    return true;
  }

  private void responseError(HttpServletResponse response, String message) throws Exception {
    response.setContentType("application/json;charset=utf-8");
    JSONObject json = new JSONObject();
    json.put("message", "error");
    json.put("info", message);
    PrintWriter out = response.getWriter();
    out.write(json.toString());
    out.flush();
    out.close();
  }
}