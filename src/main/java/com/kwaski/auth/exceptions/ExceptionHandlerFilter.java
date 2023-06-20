package com.kwaski.auth.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerFilter.class);
    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            LOGGER.error("Jwt exception occurred: {}", e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            ErrorResponse errorResponse = new ErrorResponse(e);
            response.getWriter().write(convertObjectToJson(errorResponse));
        }
        catch (RuntimeException e) {
            LOGGER.error("Runtime exception occurred: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ErrorResponse errorResponse = new ErrorResponse(e);
            response.getWriter().write(convertObjectToJson(errorResponse));
        } //catch 403 exception
        catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ErrorResponse errorResponse = new ErrorResponse(e);
            response.getWriter().write(convertObjectToJson(errorResponse));
        }
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}