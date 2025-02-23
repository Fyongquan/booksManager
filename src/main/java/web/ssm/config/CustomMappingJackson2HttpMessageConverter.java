package web.ssm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 自定义JSON转换器
 * 继承自 MappingJackson2HttpMessageConverter
 * 用于处理 HTTP 请求和响应中的 JSON 数据转换
 * 添加了日志记录功能，方便调试和问题排查
 */
@Component // 注册为Spring组件
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
  private static final Logger logger = LoggerFactory.getLogger(CustomMappingJackson2HttpMessageConverter.class);

  /**
   * 读取 HTTP 请求中的 JSON 数据并转换为 Java 对象
   * 
   * @param type         目标类型
   * @param contextClass 上下文类
   * @param inputMessage HTTP输入消息
   * @return 转换后的对象
   */
  @Override
  public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {
    logger.info("Converting HTTP message to type: {}", type);
    try {
      Object result = super.read(type, contextClass, inputMessage);
      logger.info("Converted result: {}", result);
      return result;
    } catch (Exception e) {
      logger.error("Error converting message: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * 将 Java 对象转换为 JSON 并写入 HTTP 响应
   * 
   * @param object        要转换的对象
   * @param type          对象类型
   * @param outputMessage HTTP输出消息
   */
  @Override
  protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    logger.info("Writing object to HTTP message: {}", object);
    super.writeInternal(object, type, outputMessage);
  }
}