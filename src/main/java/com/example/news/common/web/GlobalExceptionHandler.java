package com.example.news.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.common.exception.StorageException;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException exception) {
        return buildErrorPage(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu", exception.getMessage());
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ModelAndView handleInvalidOperation(InvalidOperationException exception) {
        return buildErrorPage(HttpStatus.BAD_REQUEST, "Thao tác không hợp lệ", exception.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    public ModelAndView handleStorage(StorageException exception) {
        return buildErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi lưu trữ", exception.getMessage());
    }

    private ModelAndView buildErrorPage(HttpStatus status, String title, String message) {
        ModelAndView modelAndView = new ModelAndView("error/general");
        modelAndView.setStatus(status);
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("errorTitle", title);
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }
}
