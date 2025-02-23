package web.ssm.dto;

/**
 * 通用结果返回类
 * 用于统一封装API接口的返回结果
 * 简化版的操作结果对象，主要用于简单的成功/失败场景
 */
public class Result {

  /**
   * 操作结果
   * - success: 操作成功
   * - error: 操作失败
   */
  private String msgResult;

  /**
   * 提示信息
   * 用于描述操作结果的详细信息，如错误原因等
   */
  private String msgInfo;

  /**
   * 返回的数据对象
   * 用于返回业务数据
   */
  private Object data;

  /**
   * 无参构造函数
   */
  public Result() {
  }

  /**
   * 全参构造函数
   * 
   * @param msgResult 操作结果
   * @param msgInfo   提示信息
   * @param data      返回的数据
   */
  public Result(String msgResult, String msgInfo, Object data) {
    this.msgResult = msgResult;
    this.msgInfo = msgInfo;
    this.data = data;
  }

  /**
   * 创建成功结果
   * 
   * @param data 返回的数据
   * @return 成功的结果对象
   */
  public static Result success(Object data) {
    return new Result("success", null, data);
  }

  /**
   * 创建错误结果
   * 
   * @param message 错误信息
   * @return 错误的结果对象
   */
  public static Result error(String message) {
    return new Result("error", message, null);
  }

  /**
   * 获取操作结果
   * 
   * @return 操作结果
   */
  public String getMsgResult() {
    return msgResult;
  }

  /**
   * 设置操作结果
   * 
   * @param msgResult 操作结果
   */
  public void setMsgResult(String msgResult) {
    this.msgResult = msgResult;
  }

  /**
   * 获取提示信息
   * 
   * @return 提示信息
   */
  public String getMsgInfo() {
    return msgInfo;
  }

  /**
   * 设置提示信息
   * 
   * @param msgInfo 提示信息
   */
  public void setMsgInfo(String msgInfo) {
    this.msgInfo = msgInfo;
  }

  /**
   * 获取数据对象
   * 
   * @return 数据对象
   */
  public Object getData() {
    return data;
  }

  /**
   * 设置数据对象
   * 
   * @param data 数据对象
   */
  public void setData(Object data) {
    this.data = data;
  }
}