package web.ssm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.ssm.annotation.OperationLog;
import web.ssm.dto.*;
import web.ssm.service.BookService;
import web.ssm.utils.JWTUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import java.math.BigDecimal;

import java.util.List;
import java.util.HashMap;
import com.alibaba.excel.EasyExcel;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import web.ssm.utils.ExcelUtils;
import java.net.URLEncoder;
import com.alibaba.fastjson.JSONObject;

/**
 * 图书管理控制器
 * 处理图书的增删改查、导入导出等功能
 * 包括：
 * - 图书基本信息管理
 * - Excel导入导出
 * - 分页查询
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

  private final Logger logger = LoggerFactory.getLogger(BookController.class);
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  /**
   * 获取单个图书信息
   * 
   * @param token  用户token，用于身份验证
   * @param bookId 图书ID
   * @return 图书信息，包含：
   *         - success: 图书详细信息
   *         - error: 错误信息（token无效、图书不存在等）
   */
  @GetMapping("/{id}")
  public OpResultDTO getById(@RequestHeader String token, @PathVariable("id") Integer bookId) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      BookDTO book = bookService.getById(bookId);
      if (book != null) {
        result.setMsgResult("success");
        result.setData(book);
      } else {
        result.setMsgResult("error");
        result.setMsgInfo("图书不存在");
      }
    } catch (Exception e) {
      logger.error("获取图书失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取图书失败：" + e.getMessage());
    }
    return result;
  }

  /**
   * 获取图书列表
   * 支持分页、排序和搜索
   * 
   * @param tableReqDTO 分页和查询参数，包含：
   *                    - currentPage: 当前页码
   *                    - pageSize: 每页条数
   *                    - queryText: 搜索关键词
   *                    - orderBy: 排序字段和方向
   * @param token       JWT token，用于身份验证
   * @return 分页数据，包含：
   *         - total: 总记录数
   *         - list: 当前页数据
   */
  @GetMapping("/list")
  public OpResultDTO list(TableReqDTO tableReqDTO, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      if (tableReqDTO == null) {
        tableReqDTO = new TableReqDTO();
        tableReqDTO.setPageNum(1);
        tableReqDTO.setPageSize(10);
        tableReqDTO.setQueryText("");
      }

      TableRspDTO data = bookService.list4Table(tableReqDTO);
      result.setMsgResult("success");
      result.setData(data);
    } catch (Exception e) {
      logger.error("获取图书列表失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取图书列表失败");
    }
    return result;
  }

  /**
   * 添加图书
   * 创建新的图书记录
   * 
   * @param bookDTO 图书信息，包含：
   *                - bookName: 书名
   *                - author: 作者
   *                - price: 价格
   *                - description: 描述
   * @param token   JWT token，用于身份验证
   * @return 新图书ID和信息
   */
  @PostMapping("/add")
  public OpResultDTO add(@RequestBody BookDTO bookDTO, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      if (bookDTO.getBookName() == null || bookDTO.getBookName().trim().isEmpty()) {
        result.setMsgResult("error");
        result.setMsgInfo("书名不能为空");
        return result;
      }

      if (bookDTO.getPrice() == null || bookDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
        result.setMsgResult("error");
        result.setMsgInfo("价格不能为负数");
        return result;
      }

      Integer bookId = bookService.add(bookDTO);
      result.setMsgResult("success");
      result.setData(bookId);
    } catch (Exception e) {
      logger.error("添加图书失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("添加图书失败：" + e.getMessage());
    }
    return result;
  }

  /**
   * 更新图书信息
   * 修改现有图书记录
   * 
   * @param bookDTO 更新的图书信息
   * @param token   JWT token，用于身份验证
   * @return 更新结果
   */
  @PutMapping("/update")
  public OpResultDTO update(@RequestBody BookDTO bookDTO, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      boolean success = bookService.update(bookDTO);
      if (success) {
        result.setMsgResult("success");
      } else {
        result.setMsgResult("error");
        result.setMsgInfo("更新图书失败：图书不存在");
      }
    } catch (Exception e) {
      logger.error("更新图书失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("更新图书失败：" + e.getMessage());
    }
    return result;
  }

  /**
   * 删除图书
   * 
   * @param bookId 图书ID
   * @param token  JWT token，用于身份验证
   * @return 删除结果
   */
  @DeleteMapping("/{id}")
  public OpResultDTO remove(@PathVariable("id") Integer bookId, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      bookService.delete(bookId);
      result.setMsgResult("success");
      result.setMsgInfo("删除成功");
    } catch (Exception e) {
      logger.error("删除图书失败", e);
      result.setMsgResult("error");
      if (e.getMessage().contains("借阅记录")) {
        result.setMsgInfo(e.getMessage());
      } else {
        result.setMsgInfo("删除图书失败：" + e.getMessage());
      }
    }
    return result;
  }

  /**
   * 导出图书数据到Excel
   * 
   * @param response HTTP响应对象
   */
  @GetMapping("/excel/export")
  public void exportBooks(HttpServletResponse response) {
    try {
      // 获取数据
      List<BookDTO> books = bookService.list();
      if (books == null || books.isEmpty()) {
        throw new RuntimeException("没有数据可导出");
      }

      // 设置响应头
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setCharacterEncoding("utf-8");
      String fileName = URLEncoder.encode("图书列表", "UTF-8").replaceAll("\\+", "%20");
      response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

      // 导出Excel
      EasyExcel.write(response.getOutputStream(), BookDTO.class)
          .sheet("图书列表")
          .doWrite(books);

    } catch (Exception e) {
      logger.error("导出失败", e);
      try {
        // 如果还没有发送响应
        if (!response.isCommitted()) {
          response.reset();
          response.setContentType("application/json;charset=utf-8");
          response.getWriter().write("{\"status\":\"error\",\"message\":\"导出失败：" + e.getMessage() + "\"}");
        }
      } catch (IOException ex) {
        logger.error("写入错误响应失败", ex);
      }
    }
  }

  /**
   * 从Excel导入图书数据
   * 
   * @param file  Excel文件
   * @param token JWT token，用于身份验证
   * @return 导入结果
   */
  @PostMapping("/excel/import")
  @OperationLog("导入图书数据")
  public OpResultDTO importBooks(@RequestParam("file") MultipartFile file, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("token无效");
        return result;
      }

      List<BookDTO> books = ExcelUtils.importBooks(file);
      bookService.batchSave(books);

      result.setMsgResult("success");
      result.setMsgInfo("导入成功");
    } catch (Exception e) {
      logger.error("导入失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("导入失败:" + e.getMessage());
    }
    return result;
  }
}