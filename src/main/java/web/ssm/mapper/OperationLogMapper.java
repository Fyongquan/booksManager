package web.ssm.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import web.ssm.entity.OperationLogEntity;

@Mapper
public interface OperationLogMapper {
  @Insert("INSERT INTO tb_operation_log (user_id, user_name, operation, method, params, ip, gmt_create) " +
          "VALUES (#{userId}, #{userName}, #{operation}, #{method}, #{params}, #{ip}, #{gmtCreate})")
  int insert(OperationLogEntity operationLogEntity);
}