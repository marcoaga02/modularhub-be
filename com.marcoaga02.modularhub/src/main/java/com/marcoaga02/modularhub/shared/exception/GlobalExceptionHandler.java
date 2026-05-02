package com.marcoaga02.modularhub.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // TODO modificare per evitare di mandare messaggi di stacktrace
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
//        ErrorDTO errorDTO = new ErrorDTO(
//                LocalDateTime.now(),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Internal Server Error",
//                "An unexpected error occurred: " + ex.getMessage()
//        );
//
//        LOGGER.error("Handled exception", ex);
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
//    }

    @Getter
    @AllArgsConstructor
    public class ErrorDTO {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
    }

}
