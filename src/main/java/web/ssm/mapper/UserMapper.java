package web.ssm.mapper;

import web.ssm.annotation.OperationLog;
import web.ssm.dto.UserDTO;
import web.ssm.entity.UserEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问接口
 * 提供：
 * - 用户CRUD操作
 * - 用户查询方法
 * - 密码更新
 * - 头像更新
 */
@Mapper
@Repository
public interface UserMapper {

  /**
   * 根据登录账号获取用户信息
   */
  @Select({ "SELECT " +
      "user_id, login_code, login_pwd, user_name, " +
      "user_email, user_phone, user_type, role_type, gmt_create, avatar " +
      "FROM tb_user " +
      "WHERE login_code = #{loginCode} OR " +
      "user_email = #{loginCode} OR " +
      "user_phone = #{loginCode} LIMIT 1"
  })
  UserDTO getByLoginCode(@Param("loginCode") String loginCode) throws Exception;

  /**
   * 根据用户ID获取用户信息
   */
  @Select("SELECT " +
      "user_id, login_code, login_pwd, user_name, " +
      "user_email, user_phone, user_type, role_type, gmt_create, avatar " +
      "FROM tb_user WHERE user_id = #{userId}")
  UserDTO getByUserId(@Param("userId") Integer userId) throws Exception;

  /**
   * 获取所有用户
   */
  @Select("SELECT * FROM tb_user ORDER BY user_id DESC")
  List<UserDTO> getAllUsers() throws Exception;

  /**
   * 分页查询用户列表
   */
  @Select({ "SELECT COUNT(*) FROM tb_user " +
      "WHERE login_code LIKE CONCAT('%',#{queryText},'%') " +
      "OR user_name LIKE CONCAT('%',#{queryText},'%')"
  })
  Integer count4Table(@Param("queryText") String queryText) throws Exception;

  @Select({ "SELECT * FROM tb_user " +
      "WHERE login_code LIKE CONCAT('%',#{queryText},'%') " +
      "OR user_name LIKE CONCAT('%',#{queryText},'%') " +
      "ORDER BY gmt_create DESC " +
      "LIMIT #{start}, #{pageSize}"
  })
  List<UserDTO> list4Table(@Param("start") Integer start,
      @Param("pageSize") Integer pageSize,
      @Param("queryText") String queryText) throws Exception;

  /**
   * 添加用户
   */
  @Insert("INSERT INTO tb_user (" +
      "login_code, login_pwd, user_name, user_email, user_phone, " +
      "user_type, role_type, gmt_create) " +
      "VALUES (#{userEntity.loginCode}, #{userEntity.loginPwd}, " +
      "#{userEntity.userName}, #{userEntity.userEmail}, #{userEntity.userPhone}, " +
      "#{userEntity.userType}, #{userEntity.roleType}, #{userEntity.gmtCreate})")
  @Options(useGeneratedKeys = true, keyProperty = "userEntity.userId", keyColumn = "user_id")
  Integer add(@Param("userEntity") UserEntity userEntity) throws Exception;

  /**
   * 编辑用户
   */
  @Update("UPDATE tb_user SET " +
      "user_name = #{userEntity.userName}, " +
      "user_email = #{userEntity.userEmail}, " +
      "user_phone = #{userEntity.userPhone}, " +
      "user_type = #{userEntity.userType}, " +
      "role_type = #{userEntity.roleType} " +
      "WHERE user_id = #{userEntity.userId}")
  Integer edit(@Param("userEntity") UserEntity userEntity) throws Exception;

  /**
   * 删除用户
   */
  @Delete("DELETE FROM tb_user WHERE user_id = #{userId}")
  @OperationLog("删除用户")
  int delete(@Param("userId") Integer userId);

  /**
   * 批量删除用户
   */
  @Delete("<script>" +
      "DELETE FROM tb_user WHERE user_id IN " +
      "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
      "#{id}" +
      "</foreach>" +
      "</script>")
  Integer batchRemove(@Param("userIds") List<Integer> userIds) throws Exception;

  /**
   * 批量保存用户
   */
  @Insert("<script>" +
      "INSERT INTO tb_user (login_code, login_pwd, user_name, " +
      "user_email, user_phone, user_type, role_type, gmt_create) VALUES " +
      "<foreach collection='list' item='user' separator=','>" +
      "(#{user.loginCode}, #{user.loginPwd}, #{user.userName}, " +
      "#{user.userEmail}, #{user.userPhone}, #{user.userType}, " +
      "#{user.roleType}, #{user.gmtCreate})" +
      "</foreach>" +
      "</script>")
  Integer batchSave(List<UserDTO> userDTOList) throws Exception;

  /**
   * 更新密码
   */
  @Update("UPDATE tb_user SET login_pwd = #{userEntity.loginPwd} " +
      "WHERE user_id = #{userEntity.userId}")
  Integer updatePassword(@Param("userEntity") UserEntity userEntity) throws Exception;

  /**
   * 根据邮箱查询用户
   */
  @Select("SELECT * FROM tb_user WHERE user_email = #{userEmail}")
  UserDTO getByUserEmail(String userEmail);

  /**
   * 根据手机号查询用户
   */
  @Select("SELECT * FROM tb_user WHERE user_phone = #{phone}")
  UserDTO getByUserPhone(@Param("phone") String phone);

  @Update("UPDATE tb_user SET " +
      "user_name = #{userName}, " +
      "user_email = #{userEmail}, " +
      "user_phone = #{userPhone}, " +
      "user_type = #{userType}, " +
      "role_type = #{roleType}, " +
      "avatar = #{avatar} " +
      "WHERE user_id = #{userId}")
  int update(UserDTO userDTO);

  @Select("SELECT " +
      "user_id, login_code, login_pwd, user_name, " +
      "user_email, user_phone, user_type, role_type, gmt_create, avatar " +
      "FROM tb_user WHERE user_email = #{email} LIMIT 1")
  UserDTO getByEmail(@Param("email") String email);

  @Select("SELECT " +
      "user_id, login_code, login_pwd, user_name, " +
      "user_email, user_phone, user_type, role_type, gmt_create, avatar " +
      "FROM tb_user WHERE user_phone = #{phone} LIMIT 1")
  UserDTO getByPhone(@Param("phone") String phone);

  /**
   * 更新用户头像
   */
  @Update("UPDATE tb_user SET avatar = #{avatar} WHERE user_id = #{userId}")
  int updateAvatar(UserDTO userDTO) throws Exception;
}
