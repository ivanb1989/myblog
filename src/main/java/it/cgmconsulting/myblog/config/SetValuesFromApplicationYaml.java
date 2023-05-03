package it.cgmconsulting.myblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SetValuesFromApplicationYaml {

    public static String JWT_SECRET;
    public static int JWT_EXPIRATION_IN_SECONDS;

    @Autowired
    public void getJwtProperties(
            @Value("${app.jwtSecret}") String jwtSecret, 
            @Value("${app.jwtExpirationInSeconds}") int jwtExpirationInSeconds) {
        JWT_SECRET = jwtSecret;
        JWT_EXPIRATION_IN_SECONDS = jwtExpirationInSeconds;
    }
}
