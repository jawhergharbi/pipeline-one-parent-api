package com.sawoo.pipeline.api.common.exceptions;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ExceptionMessage> handleIllegalArgument(RestException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<ExceptionMessage> handleRepositoryException(RepositoryException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonServiceException.class)
    public ResponseEntity<ExceptionMessage> handleCommonServiceException(CommonServiceException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionMessage> handleUserException(UserException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ExceptionMessage> handleEmailException(EmailException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleResourceNotFoundException(ResourceNotFoundException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionMessage> handleAuthException(AuthException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(error.getMessage(), error.getArgs(), locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleValidationException(ConstraintViolationException error, Locale locale) {
        log.error(error.getMessage(), error);
        List<String> messages = error.getConstraintViolations()
                .stream()
                .map(constrain -> {
                    String message = constrain.getMessage();
                    return messageSource.getMessage(message, new Object[]{constrain.getPropertyPath(), constrain.getRootBean().getClass().getSimpleName()}, locale);
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ExceptionMessage(messages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionMessage> handleMissingRequestParameterException(MissingServletRequestParameterException error, Locale locale) {
        log.error(error.getMessage(), error);
        String errorMessage = messageSource.getMessage(
                ExceptionMessageConstants.COMMON_MISSING_REQUEST_PARAM_EXCEPTION_ERROR,
                new Object[]{error.getParameterType(), error.getParameterName()},
                locale);
        return new ResponseEntity<>(new ExceptionMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handleArgumentNotValidException(MethodArgumentNotValidException error, Locale locale) {
        log.error(error.getMessage(), error);
        BindingResult result = error.getBindingResult();
        List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String message = fieldError.getDefaultMessage();
                    String[] args = new String[]{fieldError.getField(), fieldError.getObjectName()};
                    return messageSource.getMessage(message, args, locale);
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ExceptionMessage(errorMessages), HttpStatus.BAD_REQUEST);
    }
}
