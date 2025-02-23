package web.ssm.utils;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import web.ssm.dto.TokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.Calendar;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * JWT工具类
 * 用于处理JWT(JSON Web Token)的生成和验证
 * 包含：
 * - token生成
 * - token验证
 * - token解析
 */
@Component
public class JWTUtil {

  private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
  private static final String SECRET_KEY = "your-secret-key";

  /**
   * 生成JWT token
   */
  public static String createSign(String payload, Integer expire) {
    try {
      Date iatDate = new Date();
      // 过期时间
      Calendar nowTime = Calendar.getInstance();
      nowTime.add(Calendar.MINUTE, expire);
      Date expiresDate = nowTime.getTime();

      return JWT.create()
          .withClaim("payload", payload)
          .withIssuedAt(iatDate) // 签发时间
          .withExpiresAt(expiresDate) // 过期时间
          .withClaim("nonce", UUID.randomUUID().toString()) // 随机字符串
          .sign(Algorithm.HMAC256(SECRET_KEY));
    } catch (Exception e) {
      logger.error("Token生成失败", e);
      return null;
    }
  }

  /**
   * 验证Token
   */
  public static TokenDTO verifyToken(String token) {
    try {
      JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
      DecodedJWT jwt = verifier.verify(token);

      String payload = jwt.getClaim("payload").asString();
      JSONObject jsonObject = JSONObject.parseObject(payload);

      TokenDTO tokenDTO = new TokenDTO();
      tokenDTO.setLoginId(jsonObject.getInteger("loginId"));
      tokenDTO.setLoginCode(jsonObject.getString("loginCode"));
      tokenDTO.setLoginName(jsonObject.getString("loginName"));
      tokenDTO.setUserType(jsonObject.getString("userType"));
      tokenDTO.setRoleType(jsonObject.getString("roleType"));

      return tokenDTO;
    } catch (Exception e) {
      logger.error("Token验证失败: {}", token, e);
      return null;
    }
  }

  /**
   * 获取Token中的用户信息
   */
  public static String getSeed(String token) {
    try {
      JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
      Map<String, Claim> claims = verifier.verify(token).getClaims();
      return claims.get("seed").asString();
    } catch (Exception e) {
      logger.error("获取Token信息失败", e);
      return null;
    }
  }
}
