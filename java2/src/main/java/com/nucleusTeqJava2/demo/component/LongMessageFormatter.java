package com.nucleusTeqJava2.demo.component;

import org.springframework.stereotype.Component;

@Component
public class LongMessageFormatter {

    public String format(String content) {
        if (content == null || content.isEmpty()) {
            return "No content provided";
        }
        return "=== MESSAGE ===\n" + content + "\n=== END OF MESSAGE ===";
    }
}
