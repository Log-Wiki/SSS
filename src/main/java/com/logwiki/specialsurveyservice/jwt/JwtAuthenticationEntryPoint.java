package com.logwiki.specialsurveyservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.logwiki.specialsurveyservice.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        UnauthorizedException unauthorizedException = new UnauthorizedException("UNAUTHORIZED MESSAGE");
        ApiResponse<?> apiResponse = ApiUtils.error(
                unauthorizedException.getMessage(),
                1000
        );

        String json = objectMapper.writeValueAsString(apiResponse);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
