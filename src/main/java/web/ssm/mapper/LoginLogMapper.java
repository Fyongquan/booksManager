package web.ssm.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import web.ssm.entity.LoginLogEntity;

@Mapper
public interface LoginLogMapper {
  @Insert("INSERT INTO tb_login_log (login_code, ip_address, login_status, login_message, login_time, user_agent) " +
      "VALUES (#{loginCode}, #{ipAddress}, #{loginStatus}, #{loginMessage}, #{loginTime}, #{userAgent})")
  int insert(LoginLogEntity loginLog);
}