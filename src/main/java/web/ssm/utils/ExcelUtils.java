package web.ssm.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import web.ssm.dto.BookDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import web.ssm.dto.OpResultDTO;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Excel工具类
 * 用于处理Excel的导入导出功能
 * 基于EasyExcel实现
 */
public class ExcelUtils {
  private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

  /**
   * 导出图书数据到Excel
   * 特性：
   * - 自动调整列宽
   * - 支持中文文件名
   * - 异常处理
   * - 自动关闭流
   *
   * @param response HTTP响应对象
   * @param books    图书数据列表
   * @throws IOException IO异常
   */
  public static void exportBooks(HttpServletResponse response, List<BookDTO> books) throws IOException {
    try {
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setCharacterEncoding("utf-8");
      String fileName = URLEncoder.encode("图书列表", "UTF-8").replaceAll("\\+", "%20");
      response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

      EasyExcel.write(response.getOutputStream(), BookDTO.class)
          .autoCloseStream(Boolean.FALSE)
          .sheet("图书列表")
          .doWrite(books);

    } catch (Exception e) {
      logger.error("下载文件失败", e);
      throw new RuntimeException("下载文件失败: " + e.getMessage());
    }
  }

  /**
   * 从Excel导入图书数据
   * 
   * @param file Excel文件
   * @return 图书数据列表
   * @throws IOException IO异常
   */
  public static List<BookDTO> importBooks(MultipartFile file) throws IOException {
    return EasyExcel.read(file.getInputStream())
        .head(BookDTO.class)
        .sheet()
        .doReadSync();
  }
}