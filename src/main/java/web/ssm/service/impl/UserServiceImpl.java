package web.ssm.service.impl;

import web.ssm.dto.TableReqDTO;
import web.ssm.dto.TableRspDTO;
import web.ssm.dto.UserDTO;
import web.ssm.entity.UserEntity;
import web.ssm.mapper.UserMapper;
import web.ssm.service.UserService;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserMapper userMapper;
  private final MapperFactory mapperFactory;

  @Autowired
  public UserServiceImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
    this.mapperFactory = new DefaultMapperFactory.Builder().build();
  }

  @Override
  public UserDTO getByUserCode(String userCode) {
    try {
      return userMapper.getByLoginCode(userCode);
    } catch (Exception e) {
      throw new RuntimeException("获取用户信息失败", e);
    }
  }

  @Override
  public List<UserDTO> getAllUsers() {
    try {
      return userMapper.getAllUsers();
    } catch (Exception e) {
      throw new RuntimeException("获取所有用户失败", e);
    }
  }

  @Override
  public boolean batchSave(List<UserDTO> userDTOList) {
    try {
      return userMapper.batchSave(userDTOList) > 0;
    } catch (Exception e) {
      throw new RuntimeException("批量保存用户失败", e);
    }
  }

  @Override
  public TableRspDTO list4Table(TableReqDTO tableReqDTO) {
    try {
      Integer count = userMapper.count4Table(tableReqDTO.getQueryText());
      List<UserDTO> listUserDTOs = userMapper.list4Table(tableReqDTO.getStart(),
          tableReqDTO.getPageSize(), tableReqDTO.getQueryText());
      return new TableRspDTO(count, listUserDTOs);
    } catch (Exception e) {
      throw new RuntimeException("获取用户列表失败", e);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer add(UserDTO userDTO) {
    try {
      logger.info("开始添加用户: {}", userDTO);

      // 检查邮箱唯一性
      if (userDTO.getUserEmail() != null && !userDTO.getUserEmail().trim().isEmpty()) {
        UserDTO existingUserByEmail = userMapper.getByUserEmail(userDTO.getUserEmail());
        if (existingUserByEmail != null) {
          throw new RuntimeException("邮箱已被使用");
        }
      } else {
        // 如果邮箱为空，设置为 null 而不是空字符串
        userDTO.setUserEmail(null);
      }

      // 检查手机号唯一性
      if (userDTO.getUserPhone() != null && !userDTO.getUserPhone().trim().isEmpty()) {
        UserDTO existingUserByPhone = userMapper.getByUserPhone(userDTO.getUserPhone());
        if (existingUserByPhone != null) {
          throw new RuntimeException("手机号已被使用");
        }
      } else {
        // 如果手机号为空，设置为 null 而不是空字符串
        userDTO.setUserPhone(null);
      }

      // 检查账号是否已存在
      UserDTO existingUser = userMapper.getByLoginCode(userDTO.getLoginCode());
      if (existingUser != null) {
        throw new RuntimeException("账号已存在");
      }

      // 设置用户类型和角色类型
      if (userDTO.getUserType() == null || userDTO.getUserType().trim().isEmpty()) {
        userDTO.setUserType("user");
      }
      if (userDTO.getRoleType() == null || userDTO.getRoleType().trim().isEmpty()) {
        userDTO.setRoleType(userDTO.getUserType().toUpperCase());
      }

      // 加密密码
      String encryptedPwd = DigestUtils.md5DigestAsHex(userDTO.getLoginPwd().getBytes());
      userDTO.setLoginPwd(encryptedPwd);

      // 转换并保存
      UserEntity userEntity = mapperFactory.getMapperFacade().map(userDTO, UserEntity.class);
      userEntity.setGmtCreate(new Date());

      // 执行添加操作
      Integer result = userMapper.add(userEntity);
      if (result <= 0) {
        throw new RuntimeException("添加用户失败");
      }

      logger.info("添加用户成功: userId={}", userEntity.getUserId());
      return userEntity.getUserId();
    } catch (Exception e) {
      logger.error("添加用户失败: {}", userDTO, e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public Integer edit(UserDTO userDTO) {
    try {
      MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
      UserEntity userEntity = mapperFactory.getMapperFacade().map(userDTO, UserEntity.class);
      return userMapper.edit(userEntity);
    } catch (Exception e) {
      throw new RuntimeException("编辑用户失败", e);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean remove(Integer userId) {
    try {
      logger.info("开始删除用户: userId={}", userId);

      // 检查用户是否存在
      if (userId == null) {
        throw new IllegalArgumentException("用户ID不能为空");
      }

      // 检查是否有关联的借阅记录
      // TODO: 如果需要检查关联关系，在这里添加检查逻辑

      // 执行删除操作
      int result = userMapper.delete(userId);

      boolean success = result > 0;
      logger.info("删除用户{}: userId={}", success ? "成功" : "失败", userId);

      return success;
    } catch (Exception e) {
      logger.error("删除用户失败: userId={}", userId, e);
      throw new RuntimeException("删除用户失败: " + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public Integer resetPassword(Integer userId) {
    try {
      UserEntity userEntity = new UserEntity();
      userEntity.setUserId(userId);
      String defaultPwd = DigestUtils.md5DigestAsHex("123456".getBytes());
      userEntity.setLoginPwd(defaultPwd);
      Integer result = userMapper.updatePassword(userEntity);

      if (result > 0) {
        logger.info("用户密码已重置为默认密码: userId={}", userId);
      } else {
        logger.error("重置密码失败: userId={}", userId);
      }

      return result;
    } catch (Exception e) {
      logger.error("重置密码失败: userId={}", userId, e);
      throw new RuntimeException("重置密码失败", e);
    }
  }

  @Override
  @Transactional
  public Integer batchRemove(List<Integer> userIds) {
    try {
      return userMapper.batchRemove(userIds);
    } catch (Exception e) {
      throw new RuntimeException("批量删除用户失败", e);
    }
  }

  @Override
  @Transactional
  public Integer updatePassword(Integer userId, String oldPassword, String newPassword) {
    try {
      UserDTO user = userMapper.getByUserId(userId);
      if (user == null) {
        throw new RuntimeException("用户不存在");
      }

      String oldEncryptedPwd = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
      if (!user.getLoginPwd().equals(oldEncryptedPwd)) {
        throw new RuntimeException("原密码错误");
      }

      UserEntity userEntity = new UserEntity();
      userEntity.setUserId(userId);
      userEntity.setLoginPwd(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
      return userMapper.updatePassword(userEntity);
    } catch (Exception e) {
      throw new RuntimeException("修改密码失败", e);
    }
  }

  @Override
  public Integer getUserCount() {
    try {
      return userMapper.count4Table("");
    } catch (Exception e) {
      throw new RuntimeException("获取用户总数失败", e);
    }
  }

  @Override
  public UserDTO getByEmail(String email) {
    return userMapper.getByEmail(email);
  }

  @Override
  public UserDTO getByPhone(String phone) {
    return userMapper.getByPhone(phone);
  }

  @Override
  @Transactional
  public void updateProfile(UserDTO userDTO) {
    try {
      // 验证用户是否存在
      UserDTO existingUser = userMapper.getByUserId(userDTO.getUserId());
      if (existingUser == null) {
        throw new RuntimeException("用户不存在");
      }

      // 如果没有新的头像，保留原来的头像
      if (userDTO.getAvatar() == null) {
        userDTO.setAvatar(existingUser.getAvatar());
      }

      // 更新用户信息
      int rows = userMapper.update(userDTO);
      if (rows != 1) {
        throw new RuntimeException("更新用户信息失败");
      }
    } catch (Exception e) {
      logger.error("更新用户信息失败", e);
      throw new RuntimeException("更新用户信息失败: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateAvatar(UserDTO userDTO) {
    try {
      // 验证用户是否存在
      UserDTO existingUser = userMapper.getByUserId(userDTO.getUserId());
      if (existingUser == null) {
        throw new RuntimeException("用户不存在");
      }

      // 更新用户头像
      int rows = userMapper.updateAvatar(userDTO);
      if (rows != 1) {
        throw new RuntimeException("更新头像失败");
      }
    } catch (Exception e) {
      logger.error("更新用户头像失败", e);
      throw new RuntimeException("更新用户头像失败: " + e.getMessage());
    }
  }
}
