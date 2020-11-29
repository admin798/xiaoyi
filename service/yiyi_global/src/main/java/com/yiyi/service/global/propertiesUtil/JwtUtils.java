package com.yiyi.service.global.propertiesUtil;

import com.yiyi.service.global.entity.respon.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    public static final String APP_SECRET = "asjkaflskkskdfnsjhaiwjriasj";

    private static Key getKeyInstance(){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(APP_SECRET);
        return new SecretKeySpec(bytes,signatureAlgorithm.getJcaName());
    }

    public static String getJwtToken(UserInfo jwtInfo, int expire){

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("yiyi-user")
                .setIssuedAt(new Date())
                .setExpiration(DateTime.now().plusDays(expire).toDate())
                .claim("id", jwtInfo.getId())
                .claim("nickname", jwtInfo.getNickname())
                .claim("avatar", jwtInfo.getAvatar())
                .claim("mobile", jwtInfo.getMobile())
                .claim("sign",jwtInfo.getSign() )
                .signWith(SignatureAlgorithm.HS256, getKeyInstance())
                .compact();

        return JwtToken;
    }

    public static UserInfo getUserInfo(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if(StringUtils.isEmpty(jwtToken)){
            return null;
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        UserInfo jwtInfo = new UserInfo(
                claims.get("id").toString(),
                claims.get("nickname").toString(),
                claims.get("mobile").toString(),
                claims.get("avatar").toString(),
                claims.get("sign").toString());
        return jwtInfo;
    }
}
