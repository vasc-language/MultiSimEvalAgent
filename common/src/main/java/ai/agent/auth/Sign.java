package ai.agent.auth;

import ai.agent.exception.RestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌签名和验证类
 */
public class Sign {

    private static Map<String, JWTVerifier> verifierMap = new HashMap<>();
    private static Map<String, Algorithm> algorithmMap = new HashMap<>();

    /**
     * 获取 HMAC256 加密算法实例，带缓存机制
     */
    private static Algorithm getAlgorithm(String secret) {
        Algorithm algorithm = algorithmMap.get(secret);
        if (algorithm == null) {
            synchronized (algorithmMap) {
                algorithm = algorithmMap.get(secret);
                if (algorithm == null) {
                    algorithm = Algorithm.HMAC256(secret);
                    algorithmMap.put(secret, algorithm);
                }
            }
        }
        return algorithm;
    }

    /**
     * 生成 JWT 令牌
     * 包含用户声明
     * 设置 10 分钟过期时间
     */
    public static String getToken(String userName, String secret) {
        if (StringUtils.isEmpty(secret)) {
            throw new RestException("00X", "No signing token!");
        }
        Algorithm algorithm = getAlgorithm(secret);
        String token = JWT.create()
                .withClaim("userName", userName)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000)) // 10分钟
                .sign(algorithm);
        return token;
    }

    /**
     * 验证 JWT 令牌
     * 验证令牌的有效期和完整性
     * @return 返回解码后的 JWT 对象
     */
    public static DecodedJWT verifyToken(String token,String secret) {
        JWTVerifier verifier = verifierMap.get(token);
        if (verifier == null) {
            synchronized (verifierMap) {
                verifier = verifierMap.get(token);
                if (verifier == null) {
                    Algorithm algorithm = getAlgorithm(secret);
                    verifier = JWT.require(algorithm).build();
                    verifierMap.put(token, verifier);
                }
            }
        }
        DecodedJWT decodedJWT = verifier.verify(token);
        if (decodedJWT == null) {
            throw new RestException("00Z", "Token is error!");
        }
        return decodedJWT;
    }
}
