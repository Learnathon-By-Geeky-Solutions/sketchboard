package com.example.lostnfound.mailing;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AbstractEmailContext {

    private  String to;
    private String from;
    private String subject;
    private String email;
    private String templateLocation;
    private Map<String, Object> context;

    public AbstractEmailContext() {
        this.context = new HashMap<>();
    }

    public <T> void init(T context) {
        throw new UnsupportedOperationException("init() must be overridden in a subclass");
    }
    
    public void put(String key, Object value) {
        if (key != null) {
            this.context.put(key.intern(), value);
        }
    }
}
