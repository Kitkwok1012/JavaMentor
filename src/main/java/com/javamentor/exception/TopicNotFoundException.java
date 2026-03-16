package com.javamentor.exception;

public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(String topicId) {
        super("Topic not found: " + topicId);
    }
}
