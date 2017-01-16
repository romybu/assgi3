package bgu.spl171.net.impl;

import bgu.spl171.net.api.MessagingProtocol;

import java.time.LocalDateTime;
import java.util.Map;

public class ExampleProtocol implements MessagingProtocol<String> {

    private boolean shouldTerminate = false;

    private Map<String, String> shared;

    public ExampleProtocol(Map<String, String> shared) {
        this.shared = shared;
    }

    @Override
    public String process(String msg) {
        shouldTerminate = "bye".equals(msg);
        System.out.println("[" + LocalDateTime.now() + "]: " + msg);
        return createEcho(msg);
    }

    private String createEcho(String message) {
        String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
        return message + " .. " + echoPart + " .. " + echoPart + " ..";
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
