package com.javamentor.config;

import com.javamentor.entity.*;
import com.javamentor.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataFixer implements CommandLineRunner {
    
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    
    public DataFixer(QuestionRepository questionRepository, TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
    }
    
    @Override
    public void run(String... args) {
        // 只在有 topics 但冇 tags 既時候先 run
        if (topicRepository.count() == 0) {
            return;
        }
        
        List<Question> questions = questionRepository.findAll();
        boolean needsFix = questions.stream().anyMatch(q -> q.getTags() == null || q.getTags().isBlank());
        
        if (!needsFix) {
            System.out.println("All questions already have tags, skipping fix.");
            return;
        }
        
        System.out.println("Fixing questions with tags...");
        
        for (Question q : questions) {
            String topicId = q.getTopic().getTopicId();
            String tags = generateTags(topicId, q);
            q.setTags(tags);
            
            // Set difficulty based on question number
            int displayOrder = q.getDisplayOrder() != null ? q.getDisplayOrder() : 1;
            if (displayOrder <= 3) {
                q.setDifficulty(1);
            } else if (displayOrder <= 7) {
                q.setDifficulty(2);
            } else {
                q.setDifficulty(3);
            }
        }
        
        questionRepository.saveAll(questions);
        System.out.println("Fixed " + questions.size() + " questions with tags!");
    }
    
    private String generateTags(String topicId, Question q) {
        String question = q.getQuestion().toLowerCase();
        
        switch (topicId) {
            case "oop":
                return generateOOPTags(question);
            case "collection":
                return generateCollectionTags(question);
            case "thread":
                return generateThreadTags(question);
            case "jvm":
                return generateJVMTags(question);
            default:
                return topicId + ",java";
        }
    }
    
    private String generateOOPTags(String q) {
        List<String> tags = new ArrayList<>();
        tags.add("oop");
        
        if (q.contains("solid")) tags.addAll(Arrays.asList("solid", "principle"));
        if (q.contains("single responsibility")) tags.add("srp");
        if (q.contains("open/closed") || q.contains("open-closed")) tags.add("ocp");
        if (q.contains("liskov")) tags.add("lsp");
        if (q.contains("interface segregation")) tags.add("isp");
        if (q.contains("dependency inversion") || q.contains("dip")) tags.add("dip");
        if (q.contains("singleton")) tags.add("singleton");
        if (q.contains("design pattern") || q.contains("pattern")) tags.add("design-pattern");
        if (q.contains("composition") || q.contains("has-a")) tags.add("composition");
        if (q.contains("inheritance") || q.contains("inherit")) tags.add("inheritance");
        if (q.contains("polymorphism")) tags.add("polymorphism");
        if (q.contains("decorator")) tags.add("decorator");
        if (q.contains("template method")) tags.add("template-method");
        
        return String.join(",", tags);
    }
    
    private String generateCollectionTags(String q) {
        List<String> tags = new ArrayList<>();
        tags.add("collection");
        
        if (q.contains("arraylist")) tags.add("ArrayList");
        if (q.contains("linkedlist")) tags.add("LinkedList");
        if (q.contains("hashmap")) tags.add("HashMap");
        if (q.contains("hashset")) tags.add("HashSet");
        if (q.contains("treemap") || q.contains("treeset")) tags.add("TreeMap");
        if (q.contains("queue") || q.contains("deque")) tags.add("Queue");
        if (q.contains("stack")) tags.add("Stack");
        if (q.contains("iterator")) tags.add("Iterator");
        if (q.contains("thread-safe") || q.contains("synchroniz")) tags.add("thread-safe");
        if (q.contains("complexity") || q.contains("時間") || q.contains("o(")) tags.add("complexity");
        if (q.contains("performance")) tags.add("performance");
        
        return String.join(",", tags);
    }
    
    private String generateThreadTags(String q) {
        List<String> tags = new ArrayList<>();
        tags.add("thread");
        
        if (q.contains("synchroniz")) tags.add("synchronized");
        if (q.contains("volatile")) tags.add("volatile");
        if (q.contains("thread pool")) tags.add("thread-pool");
        if (q.contains("wait") || q.contains("notify")) tags.add("wait-notify");
        if (q.contains("lock") || q.contains("reentrant")) tags.add("lock");
        if (q.contains("countdownlatch") || q.contains("cyclicbarrier")) tags.add("concurrent");
        if (q.contains("completablefuture") || q.contains("future")) tags.add("async");
        if (q.contains("deadlock") || q.contains("飢餓")) tags.add("deadlock");
        if (q.contains("race condition")) tags.add("race-condition");
        
        return String.join(",", tags);
    }
    
    private String generateJVMTags(String q) {
        List<String> tags = new ArrayList<>();
        tags.add("jvm");
        
        if (q.contains("gc") || q.contains("garbage")) tags.add("gc");
        if (q.contains("heap") || q.contains("堆")) tags.add("heap");
        if (q.contains("stack") || q.contains("棧")) tags.add("stack");
        if (q.contains("classloader") || q.contains("class loader")) tags.add("classloader");
        if (q.contains("memory model") || q.contains("jmm")) tags.add("jmm");
        if (q.contains("oom") || q.contains("outofmemory")) tags.add("oom");
        if (q.contains("string") || q.contains("stringpool")) tags.add("string");
        if (q.contains("tlab")) tags.add("tlab");
        
        return String.join(",", tags);
    }
}
