//package by.vorivoda.matvey.trash;
//
//import by.vorivoda.matvey.model.GlobalConstants;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.DefaultCsrfToken;
//import org.springframework.stereotype.Repository;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.Objects;
//import java.util.UUID;
//
//import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
//
//@Repository
//public class JwtTokenRepository implements CsrfTokenRepository {
//
//    private static final String tokenHeaderName = "x-csrf-token";
//    private static final Integer durationInMinutes = 60;
//
//    public JwtTokenRepository() {}
//
//    @Override
//    public CsrfToken generateToken(HttpServletRequest httpServletRequest) {
//        String id = UUID.randomUUID().toString().replace("-", "");
//        Date now = new Date();
//        Date exp = Date.from(LocalDateTime.now().plusMinutes(durationInMinutes)
//                .atZone(ZoneId.systemDefault()).toInstant());
//
//        String token = "";
//        try {
//            token = Jwts.builder()
//                    .setId(id)
//                    .setIssuedAt(now)
//                    .setNotBefore(now)
//                    .setExpiration(exp)
//                    .signWith(SignatureAlgorithm.HS256, secretKey)
//                    .compact();
//        } catch (JwtException e) {
//            e.printStackTrace();
//            //ignore
//        }
//        return new DefaultCsrfToken(tokenHeaderName, "_csrf", token);
//    }
//
//    @Override
//    public void saveToken(CsrfToken csrfToken, HttpServletRequest request, HttpServletResponse response) {
//        if (Objects.nonNull(csrfToken)) {
//            if (!response.getHeaderNames().contains(ACCESS_CONTROL_EXPOSE_HEADERS))
//                response.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, csrfToken.getHeaderName());
//
//            if (response.getHeaderNames().contains(csrfToken.getHeaderName()))
//                response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
//            else
//                response.addHeader(csrfToken.getHeaderName(), csrfToken.getToken());
//        }
//    }
//
//    @Override
//    public CsrfToken loadToken(HttpServletRequest request) {
//        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//    }
//
//    public void clearToken(HttpServletResponse response) {
//        if (response.getHeaderNames().contains(tokenHeaderName))
//            response.setHeader(tokenHeaderName, "");
//    }
//
//    public String getSecretKey() {
//        return secretKey;
//    }
//}