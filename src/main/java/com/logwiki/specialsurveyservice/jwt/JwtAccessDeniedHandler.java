package com.logwiki.specialsurveyservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logwiki.specialsurveyservice.api.utils.ApiResponse;
import com.logwiki.specialsurveyservice.api.utils.ApiUtils;
import com.logwiki.specialsurveyservice.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ForbiddenException forbiddenException = new ForbiddenException("FORBIDDEN MESSAGE");
        ApiResponse<?> apiResponse = ApiUtils.error(
                forbiddenException.getMessage(),
                1000
        );

        String json = objectMapper.writeValueAsString(apiResponse);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
