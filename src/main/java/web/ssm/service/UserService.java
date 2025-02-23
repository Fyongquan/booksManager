package web.ssm.service;

import web.ssm.dto.UserDTO;
import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;
import java.util.List;

public interface UserService {
  /**
   * 根据用户编码获取用户信息
   */
  UserDTO getByUserCode(String userCode);

  /**
   * 获取所有用户
   */
  List<UserDTO> getAllUsers();

  /**
   * 批量保存用户
   */
  boolean batchSave(List<UserDTO> userDTOList);

  /**
   * 分页查询用户列表
   */
  TableRspDTO list4Table(TableReqDTO tableReqDTO);

  /**
   * 添加用户
   */
  Integer add(UserDTO userDTO);

  /**
   * 编辑用户
   */
  Integer edit(UserDTO userDTO);

  /**
   * 删除用户
   * 
   * @param userId 用户ID
   * @return 是否删除成功
   */
  boolean remove(Integer userId);

  /**
   * 重置密码
   */
  Integer resetPassword(Integer userId);

  /**
   * 批量删除用户
   */
  Integer batchRemove(List<Integer> userIds);

  /**
   * 修改密码
   */
  Integer updatePassword(Integer userId, String oldPassword, String newPassword);

  /**
   * 获取用户总数
   */
  Integer getUserCount();

  UserDTO getByEmail(String email);

  UserDTO getByPhone(String phone);

  void updateProfile(UserDTO userDTO);

  /**
   * 更新用户头像
   */
  void updateAvatar(UserDTO userDTO);
}
