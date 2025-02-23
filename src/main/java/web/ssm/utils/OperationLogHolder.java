package web.ssm.utils;

import web.ssm.entity.OperationLogEntity;

/**
 * 操作日志线程本地存储工具类
 * 用于在同一线程中传递操作日志信息
 * 基于ThreadLocal实现线程隔离
 * 
 * 主要用于：
 * - 记录用户操作
 * - 跟踪业务流程
 * - 审计日志记录
 */
public class OperationLogHolder {

  /**
   * 线程本地存储对象
   * 用于存储当前线程的操作日志实体
   * 每个线程都有自己独立的存储空间
   */
  private static final ThreadLocal<OperationLogEntity> logHolder = new ThreadLocal<>();

  /**
   * 设置当前线程的操作日志
   * 通常在操作开始时调用
   * 
   * @param log 操作日志实体
   */
  public static void setCurrentLog(OperationLogEntity log) {
    logHolder.set(log);
  }

  /**
   * 获取当前线程的操作日志
   * 
   * @return 操作日志实体，如果未设置则返回null
   */
  public static OperationLogEntity getCurrentLog() {
    return logHolder.get();
  }

  /**
   * 清除当前线程的操作日志
   * 防止内存泄漏，在使用完毕后必须调用此方法
   */
  public static void clear() {
    logHolder.remove();
  }
}