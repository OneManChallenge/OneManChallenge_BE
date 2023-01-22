package com.hanghae.onemanitnews.common.exception;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hanghae.onemanitnews.common.response.FailResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

	/** Common 예외 핸들러 **/
	@ExceptionHandler(value = {CommonException.class})
	protected ResponseEntity<?> CommonExceptionHanlder(CommonException e) {
		return new ResponseEntity<>(FailResponse.builder().msg(e.getErrorEnum().getMsg()).build(),
			HttpStatus.BAD_REQUEST);
	}

	/** Validation 예외처리 **/
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	protected ResponseEntity<?> ValidExceptionHandler(MethodArgumentNotValidException e) {
		return new ResponseEntity<>(
			FailResponse.builder().msg(e.getBindingResult().getFieldError().getDefaultMessage()),
			HttpStatus.BAD_REQUEST);
	}

	/** todoContorller @Validated @PathVariable 예외처리 **/
	@ExceptionHandler(value = {ConstraintViolationException.class})
	protected ResponseEntity<?> ValidatedExceptionHandle(ConstraintViolationException e) {
		return new ResponseEntity<>(
			FailResponse.builder().msg(e.getConstraintViolations().iterator().next().getMessage()).build(),
			HttpStatus.BAD_REQUEST);
	}

	/** 지원하지 않는 HTTP 요청 예외처리 **/
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<?> HttpRequestMethodNotSupportExceptionHandler() {
		return new ResponseEntity<>(FailResponse.builder().msg("비정상적인 요청입니다."), HttpStatus.BAD_REQUEST);
	}

	/** 그외 나머지 예외 처리 **/
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> GlocalExceptionHanlder(Exception e) {
		log.info(e.getMessage()); //예외 로그
		return new ResponseEntity<>(FailResponse.builder().msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
	}
}
