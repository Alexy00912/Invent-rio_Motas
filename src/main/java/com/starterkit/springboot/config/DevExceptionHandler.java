package com.starterkit.springboot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class DevExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DevExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("Unhandled exception", ex);

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        model.addAttribute("exceptionMessage", ex.getMessage());
        model.addAttribute("stacktrace", sw.toString());

        return "error/debug";
    }
}
