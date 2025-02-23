package web.ssm.utils;

import web.ssm.entity.LoginLogEntity;

/**
 * 登录日志线程本地存储工具类
 * 用于在同一线程中传递登录日志信息
 * 基于ThreadLocal实现线程隔离
 */
public class LoginLogHolder {

  /**
   * 线程本地存储对象
   * 用于存储当前线程的登录日志实体
   * 每个线程都有自己独立的存储空间
   */
  private static final ThreadLocal<LoginLogEntity> logHolder = new ThreadLocal<>();

  /**
   * 设置当前线程的登录日志
   * 
   * @param log 登录日志实体
   */
  public static void setCurrentLog(LoginLogEntity log) {
    logHolder.set(log);
  }

  /**
   * 获取当前线程的登录日志
   * 
   * @return 登录日志实体，如果未设置则返回null
   */
  public static LoginLogEntity getCurrentLog() {
    return logHolder.get();
  }

  /**
   * 清除当前线程的登录日志
   * 防止内存泄漏，在使用完毕后必须调用此方法
   */
  public static void clear() {
    logHolder.remove();
  }
}