package com.nucleusTeqJava2.demo.component;

import org.springframework.stereotype.Component;

@Component
public class ShortMessageFormatter {

    public String format(String content) {
        if (content == null || content.isEmpty()) {
            return "Empty";
        }
        return content.length() > 30 ? content.substring(0, 30) + "..." : content;
    }
}
