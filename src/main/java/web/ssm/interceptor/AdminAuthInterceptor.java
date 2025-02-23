package web.ssm.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import web.ssm.dto.TokenDTO;
import web.ssm.dto.UserDTO;
import web.ssm.service.LoginService;
import web.ssm.utils.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员权限拦截器
 * 用于拦截需要管理员权限的请求
 * 验证用户是否具有管理员角色
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

  private final LoginService loginService;

  /**
   * 构造函数
   * 注入登录服务用于验证用户信息
   * 
   * @param loginService 登录服务
   */
  public AdminAuthInterceptor(LoginService loginService) {
    this.loginService = loginService;
  }

  /**
   * 预处理方法
   * 在请求处理之前进行调用
   * 验证流程：
   * 1. 获取token
   * 2. 验证token有效性
   * 3. 获取用户信息
   * 4. 验证用户角色是否为管理员
   * 
   * @param request  HTTP请求对象
   * @param response HTTP响应对象
   * @param handler  处理器对象
   * @return true:继续流程 false:中断流程
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 获取token
    String token = request.getHeader("token");
    if (token != null) {
      // 验证token
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO != null) {
        // 获取用户信息
        UserDTO userDTO = loginService.getByLoginCode(tokenDTO.getLoginCode());
        // 验证是否是管理员
        if (userDTO != null && "ADMIN".equals(userDTO.getRoleType())) {
          return true;
        }
      }
    }
    // 验证失败，设置响应头
    response.setHeader("authstatus", "no");
    return false;
  }
}