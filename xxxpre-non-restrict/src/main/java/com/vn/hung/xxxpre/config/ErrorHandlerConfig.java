//package com.vn.hung.xxxpre.config;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.coyote.Response;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.Map;
//
//@ControllerAdvice
//public class ErrorHandlerConfig {
//    private static final Logger log = LoggerFactory.getLogger(ErrorHandlerConfig.class);
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Response> exception(Exception ex) {
//        log.error("Exception: {}", ex.getMessage());
//        log.error("Exception: {}", ex.getStackTrace());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//    }
//
//}