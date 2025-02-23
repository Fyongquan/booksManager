package web.ssm.dto;

import java.io.Serializable;

/**
 * 操作结果数据传输对象
 * 用于统一封装API接口的返回结果
 */
public class OpResultDTO implements Serializable {

  private static final long serialVersionUID = 1L;

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
   * 操作结果对象
   * 通常用于返回ID等简单数据
   */
  private Object objResult;

  /**
   * 返回的数据对象
   * 用于返回复杂的数据结构
   */
  private Object data;

  /**
   * 无参构造函数
   */
  public OpResultDTO() {
  }

  /**
   * 构造函数
   * 
   * @param msgResult 操作结果
   * @param objResult 结果对象
   */
  public OpResultDTO(String msgResult, Object objResult) {
    this.msgResult = msgResult;
    this.objResult = objResult;
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
   * 获取结果对象
   * 
   * @return 结果对象
   */
  public Object getObjResult() {
    return objResult;
  }

  /**
   * 设置结果对象
   * 
   * @param objResult 结果对象
   */
  public void setObjResult(Object objResult) {
    this.objResult = objResult;
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

  /**
   * 创建成功结果
   * 
   * @param data 返回的数据
   * @return 成功的操作结果对象
   */
  public static OpResultDTO success(Object data) {
    OpResultDTO result = new OpResultDTO();
    result.setMsgResult("success");
    result.setData(data);
    return result;
  }

  /**
   * 创建错误结果
   * 
   * @param message 错误信息
   * @return 错误的操作结果对象
   */
  public static OpResultDTO error(String message) {
    OpResultDTO result = new OpResultDTO();
    result.setMsgResult("error");
    result.setMsgInfo(message);
    return result;
  }

  @Override
  public String toString() {
    return "OpResultDTO{" +
        "msgResult='" + msgResult + '\'' +
        ", msgInfo='" + msgInfo + '\'' +
        ", objResult=" + objResult +
        ", data=" + data +
        '}';
  }
}
