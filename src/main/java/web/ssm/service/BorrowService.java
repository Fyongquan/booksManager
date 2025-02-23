package web.ssm.service;

import web.ssm.dto.BorrowDTO;
import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;

public interface BorrowService {
  TableRspDTO list4Table(TableReqDTO tableReqDTO);

  Integer borrow(BorrowDTO borrowDTO);

  Integer return_(Integer borrowId);

  Integer renew(Integer borrowId);

  /**
   * 更新逾期状态
   * 
   * @return 更新的记录数
   */
  int updateOverdueStatus();
}