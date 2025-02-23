package web.ssm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.ssm.dto.*;
import web.ssm.service.BorrowService;
import web.ssm.utils.JWTUtil;

/**
 * 借阅管理控制器
 * 处理图书借阅、归还、续借等操作
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {
  private static final Logger logger = LoggerFactory.getLogger(BorrowController.class);
  private final BorrowService borrowService;

  @Autowired
  public BorrowController(BorrowService borrowService) {
    this.borrowService = borrowService;
  }

  /**
   * 获取所有借阅记录（管理员用）
   * 支持分页和搜索
   * 
   * @param tableReqDTO 分页和查询参数，包含：
   *                    - currentPage: 当前页码
   *                    - pageSize: 每页条数
   *                    - queryText: 搜索关键词
   * @param token       用户token，用于身份验证
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
        result.setMsgInfo("无效的token");
        return result;
      }

      if (tableReqDTO == null) {
        tableReqDTO = new TableReqDTO();
        tableReqDTO.setPageNum(1);
        tableReqDTO.setPageSize(10);
        tableReqDTO.setQueryText("");
      }

      TableRspDTO data = borrowService.list4Table(tableReqDTO);
      result.setMsgResult("success");
      result.setData(data);
    } catch (Exception e) {
      logger.error("获取借阅列表失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取借阅列表失败");
    }
    return result;
  }

  /**
   * 借阅图书
   * 记录借阅信息并更新图书状态
   * 
   * @param borrowDTO 借阅信息，包含：
   *                  - bookId: 图书ID
   *                  - borrowDate: 借阅日期
   *                  - returnDate: 应还日期
   * @param token     用户token，用于身份验证和获取用户ID
   * @return 借阅结果
   */
  @PostMapping
  public OpResultDTO borrow(@RequestBody BorrowDTO borrowDTO, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("无效的token");
        return result;
      }

      borrowDTO.setUserId(tokenDTO.getLoginId()); // 设置用户ID
      borrowService.borrow(borrowDTO);
      result.setMsgResult("success");
    } catch (Exception e) {
      result.setMsgResult("error");
      result.setMsgInfo(e.getMessage());
    }
    return result;
  }

  /**
   * 归还图书
   * 更新借阅状态和图书状态
   * 
   * @param borrowId 借阅记录ID
   * @param token    用户token，用于身份验证
   * @return 归还结果
   */
  @PostMapping("/{id}/return")
  public OpResultDTO return_(@PathVariable("id") Integer borrowId, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("无效的token");
        return result;
      }

      borrowService.return_(borrowId);
      result.setMsgResult("success");
    } catch (Exception e) {
      result.setMsgResult("error");
      result.setMsgInfo(e.getMessage());
    }
    return result;
  }

  /**
   * 续借图书
   * 延长借阅期限
   * 
   * @param borrowId 借阅记录ID
   * @param token    用户token，用于身份验证
   * @return 续借结果
   */
  @PostMapping("/{id}/renew")
  public OpResultDTO renew(@PathVariable("id") Integer borrowId, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("无效的token");
        return result;
      }

      borrowService.renew(borrowId);
      result.setMsgResult("success");
    } catch (Exception e) {
      result.setMsgResult("error");
      result.setMsgInfo(e.getMessage());
    }
    return result;
  }

  /**
   * 获取当前用户的借阅记录
   * 支持分页和搜索
   * 
   * @param tableReqDTO 分页和查询参数
   * @param token       用户token，用于身份验证和获取用户ID
   * @return 用户的借阅记录列表
   */
  @GetMapping("/myBorrows")
  public OpResultDTO getMyBorrows(TableReqDTO tableReqDTO, @RequestHeader String token) {
    OpResultDTO result = new OpResultDTO();
    try {
      TokenDTO tokenDTO = JWTUtil.verifyToken(token);
      if (tokenDTO == null) {
        result.setMsgResult("error");
        result.setMsgInfo("无效的token");
        return result;
      }

      if (tableReqDTO == null) {
        tableReqDTO = new TableReqDTO();
        tableReqDTO.setPageNum(1);
        tableReqDTO.setPageSize(10);
        tableReqDTO.setQueryText("");
      }

      tableReqDTO.setUserId(tokenDTO.getLoginId());
      logger.debug("接收到查询请求: {}, {}", tableReqDTO, tokenDTO);
      TableRspDTO data = borrowService.list4Table(tableReqDTO);
      result.setMsgResult("success");
      result.setData(data);
    } catch (Exception e) {
      logger.error("获取我的借阅列表失败", e);
      result.setMsgResult("error");
      result.setMsgInfo("获取借阅列表失败");
    }
    return result;
  }
}