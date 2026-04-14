package com.nucleusTeqJava2.demo.service;

import com.nucleusTeqJava2.demo.component.ShortMessageFormatter;
import com.nucleusTeqJava2.demo.component.LongMessageFormatter;
import org.springframework.stereotype.Service;

@Service
public class MessageFormatterService {
    private ShortMessageFormatter shortMessageFormatter;
    private LongMessageFormatter longMessageFormatter;

    public MessageFormatterService(ShortMessageFormatter shortMessageFormatter,
            LongMessageFormatter longMessageFormatter) {
        this.shortMessageFormatter = shortMessageFormatter;
        this.longMessageFormatter = longMessageFormatter;
    }

    public String formatMessage(String content, String type) {
        switch (type.toLowerCase()) {
            case "short":
                return shortMessageFormatter.format(content);
            case "long":
                return longMessageFormatter.format(content);
            default:
                throw new IllegalArgumentException("Type must be SHORT or LONG");
        }
    }
}
