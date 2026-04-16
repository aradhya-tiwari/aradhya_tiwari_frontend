package com.nucleusTeqJava2.demo.controller;

import com.nucleusTeqJava2.demo.service.MessageFormatterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageFormatterController {
    private MessageFormatterService messageFormatterService;

    public MessageFormatterController(MessageFormatterService messageFormatterService) {
        this.messageFormatterService = messageFormatterService;
    }

    @GetMapping
    public String formatMessage(
            @RequestParam String content,
            @RequestParam String type) {
        return messageFormatterService.formatMessage(content, type);
    }
}
