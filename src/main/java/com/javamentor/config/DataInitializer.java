package com.javamentor.config;

import com.javamentor.entity.*;
import com.javamentor.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

// @Component  // Disabled - using QuestionDataLoader from questions.json instead
public class DataInitializer implements CommandLineRunner {
    
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    
    public DataInitializer(TopicRepository topicRepository, QuestionRepository questionRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }
    
    @Override
    public void run(String... args) {
        if (topicRepository.count() > 0) {
            return;
        }
        
        List<Topic> topics = new ArrayList<>();
        topics.add(createTopic("oop", "Object-Oriented Programming", "OOP、 SOLID", 5));
        topics.add(createTopic("collection", "Java Collections", "List, Set, Map", 5));
        topics.add(createTopic("thread", "Multi-threading", "線程、同步", 5));
        topics.add(createTopic("jvm", "JVM & Performance", "內存、GC", 5));
        topics.add(createTopic("exception", "Exception Handling", "異常處理", 5));
        topics.add(createTopic("spring", "Spring Framework", "IOC、AOP", 5));
        topics.add(createTopic("sql", "Database & SQL", "SQL優化", 5));
        topics.add(createTopic("design-pattern", "Design Patterns", "GoF設計模式", 5));

        topicRepository.saveAll(topics);

        List<Question> questions = new ArrayList<>();
        questions.addAll(createOOPQuestions(topics.get(0)));
        questions.addAll(createCollectionQuestions(topics.get(1)));
        questions.addAll(createThreadQuestions(topics.get(2)));
        questions.addAll(createJVMQuestions(topics.get(3)));
        questions.addAll(createExceptionQuestions(topics.get(4)));
        questions.addAll(createSpringQuestions(topics.get(5)));
        questions.addAll(createSQLQuestions(topics.get(6)));
        questions.addAll(createDesignPatternQuestions(topics.get(7)));

        questionRepository.saveAll(questions);
    }
    
    private Topic createTopic(String topicId, String name, String description, int count) {
        Topic topic = new Topic();
        topic.setTopicId(topicId);
        topic.setName(name);
        topic.setDescription(description);
        topic.setQuestionCount(count);
        return topic;
    }
    
    private Question createQ(Topic topic, int order, boolean multiSelect, String type,
            String q, String a, String b, String c, String d, String e,
            String correct, String tags, int difficulty,
            String explanation, String followUpCorrect, String followUpWrong) {
        Question question = new Question();
        question.setTopic(topic);
        question.setQuestion(q);
        question.setOptionA(a);
        question.setOptionB(b);
        question.setOptionC(c);
        question.setOptionD(d);
        question.setOptionE(e);
        question.setCorrectAnswer(correct);
        question.setMultiSelect(multiSelect);
        question.setTags(tags);
        question.setDifficulty(difficulty);
        question.setDisplayOrder(order);
        question.setExplanation(explanation);
        question.setFollowUpCorrect(followUpCorrect);
        question.setFollowUpWrong(followUpWrong);
        return question;
    }
    
    private List<Question> createOOPQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "以下邊個唔係SOLID原則既其中一個？", "Single Responsibility Principle", "Open/Closed Principle", "Dynamic Loading Principle", "Liskov Substitution Principle", null, "C", "solid,srp,ocp,lsp", 1, "SOLID五個原則：SRP、OCP、LSP、ISP、DIP。", "SOLID入面邊個最難？", "點解要分開？"));
        list.add(createQ(topic, 2, false, "Single Choice", "邊個係開放封閉原則？", "一個類只應該有一個改變既原因", "類應該對擴展開放，對修改封閉", "子類應該可以替換父類而不影響正確性", "高層模組不應該依賴低層模組", null, "B", "solid,ocp", 1, "類應該對擴展開放，對修改封閉。", "你既project有邊個符合？", "點做？"));
        list.add(createQ(topic, 3, false, "Single Choice", "邊種設計模式確保子類對象可以替換父類對象？", "Factory Method", "Template Method", "Liskov Substitution Principle", "Strategy Pattern", null, "C", "solid,lsp", 1, "LSP規定子類必須能夠替換其父類。", "違反LSP既例子？", "點避免？"));
        list.add(createQ(topic, 4, false, "Single Choice", "以下邊個係DIP既核心？", "面向抽象編程，而不是面向具體編程", "一個類只應該有一個職責", "盡量使用組合而不是繼承", "接口應該細分", null, "A", "solid,dip", 1, "高層模組同低層模組都應該依賴抽象。", "你既project有咩場景？", "Constructor vs Setter？"));
        list.add(createQ(topic, 5, false, "Single Choice", "邊個原則話用多個專門既接口比用單一既總接口好？", "Single Responsibility", "Open/Closed", "Interface Segregation", "Liskov Substitution", null, "C", "solid,isp", 1, "ISP主張將龐大既接口拆分為更細既特定接口。", "設計一個ISP-compliant既接口？", "咁多接口好定少好？"));
        return list;
    }
    
    private List<Question> createCollectionQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "ArrayList既底層實現係咩？", "LinkedList", "Dynamic Array", "Hash Table", "Tree", null, "B", "ArrayList,collection", 1, "ArrayList底層用Object[] array實現。", "add既時間複雜度？", "點解LinkedList慢？"));
        list.add(createQ(topic, 2, false, "Single Choice", "HashMap既key可以為null？", "可以有一個null key", "不可以有null key", "取決於實現", "只可以係String", null, "A", "HashMap,null", 1, "HashMap允許一個null key。", "點處理collision？", "Load factor係咩？"));
        list.add(createQ(topic, 3, false, "Single Choice", "以下邊個係線程安全既Map？", "HashMap", "TreeMap", "ConcurrentHashMap", "LinkedHashMap", null, "C", "ConcurrentHashMap", 1, "ConcurrentHashMap係線程安全既HashMap。", "putIfAbsent同put分別？", "點解有咁多種Map？"));
        list.add(createQ(topic, 4, false, "Single Choice", "TreeMap既底層數據結構係咩？", "Hash Table", "Red-Black Tree", "B+ Tree", "Skip List", null, "B", "TreeMap,tree", 1, "TreeMap底層用Red-Black Tree實現。", "TreeMap同TreeSet既關係？", "點解要用TreeMap？"));
        list.add(createQ(topic, 5, false, "Single Choice", "HashSet既add()既時間複雜度係？", "O(1)", "O(n)", "O(log n)", "O(n^2)", null, "A", "HashSet,complexity", 1, "HashSet底層用HashMap實現，平均O(1)。", "contains()同add()一樣？", "點解咁快？"));
        return list;
    }
    
    private List<Question> createThreadQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "並發同並行既分別？", "並發係單核CPU，並行係多核CPU", "並發係多核CPU，並行係單核CPU", "一樣", "以上都不對", null, "A", "thread,concurrent", 1, "並發係同一時間段內交替執行（單核），並行係同一時刻真正同時執行（多核）。", "IO密集型用邊個？", "CPU密集型用邊個？"));
        list.add(createQ(topic, 2, false, "Single Choice", "wait()方法會令線程進入WAITING狀態？", "sleep()", "wait()", "yield()", "join()", null, "B", "thread,wait", 1, "wait()會令線程進入WAITING狀態。", "wait()同sleep()既分別？", "點解要synchronized block？"));
        list.add(createQ(topic, 3, false, "Single Choice", "Synchronized可以用响邊啲位置？", "Method", "Block", "Static Method", "以上全部", null, "D", "synchronized", 1, "Synchronized可以用响實例方法、static方法、同步塊。", "synchronized既lock係咩對象？", "一個線程可以多次synchronized？"));
        list.add(createQ(topic, 4, false, "Single Choice", "ReentrantLock同synchronized既分別？", "ReentrantLock比較快", "ReentrantLock可以tryLock", "synchronized可以中斷", "冇分別", null, "B", "ReentrantLock,synchronized", 1, "ReentrantLock提供tryLock()等控制。", "點解咁多人用？", "公平鎖既分別？"));
        list.add(createQ(topic, 5, false, "Single Choice", "volatile既作用？", "保證原子性", "保證可見性同有序性", "避免線程飢餓", "實現線程通信", null, "B", "volatile,jmm", 1, "volatile保證可見性同有序性。", "volatile同synchronized既分別？", "DCL點解要volatile？"));
        return list;
    }
    
    private List<Question> createJVMQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "JVM既內存區域中，邊個係線程共享既？", "程序計數器", "Java堆", "Java棧", "本地方法棧", null, "B", "jvm,heap", 1, "Heap同Method Area係線程共享既。", "點解要分共享同私有？", "堆同棧既分別？"));
        list.add(createQ(topic, 2, false, "Single Choice", "以下邊個唔係GC既垃圾回收算法？", "Mark-Sweep", "Copying", "Mark-Compact", "FIFO", null, "D", "jvm,gc", 1, "FIFO係隊列特性，唔係GC算法。", "Mark-Sweep有問題？", "點解要多種GC？"));
        list.add(createQ(topic, 3, false, "Single Choice", "邊個內存區域最容易發生OutOfMemoryError？", "程序計數器", "Java堆", "Java棧", "方法區", null, "B", "jvm,oom", 1, "Java堆係OutOfMemoryError最常發生既地方。", "點解決Heap OOM？", "新生代同老年代比例？"));
        list.add(createQ(topic, 4, false, "Single Choice", "咩係ClassLoader？", "負責加載class文件到JVM", "執行bytecode既引擎", "管理內存既組件", "垃圾回收既組件", null, "A", "jvm,classloader", 1, "ClassLoader負責將class文件既bytecode加載到JVM。", "雙親委派模型？", "點自定義ClassLoader？"));
        list.add(createQ(topic, 5, false, "Single Choice", "Java既內存模型(JMM)主要確保？", "內存容量", "原子性、可見性、有序性", "執行速度", "對象大小", null, "B", "jmm", 1, "JMM確保原子性、可見性、有序性。", "Volatile點保證？", "Synchronized點保證？"));
        return list;
    }
    
    private List<Question> createExceptionQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "以下邊個係RuntimeException？", "IOException", "SQLException", "NullPointerException", "ClassNotFoundException", null, "C", "exception,runtime", 1, "NullPointerException係RuntimeException。", "Checked vs Unchecked？", "幾時自定義Exception？"));
        list.add(createQ(topic, 2, false, "Single Choice", "以下邊個係Checked Exception？", "NullPointerException", "IllegalArgumentException", "IOException", "RuntimeException", null, "C", "exception,checked", 1, "IOException係Checked Exception。", "點解要有Checked Exception？", "Checked vs Unchecked？"));
        list.add(createQ(topic, 3, false, "Single Choice", "try-catch-finally既執行順序？", "try->catch->finally", "try->finally->catch", "finally最先", "取決於catch到定唔到", null, "A", "exception", 1, "正常：try->catch->finally。", "finally一定會執行？", "return响try定finally？"));
        list.add(createQ(topic, 4, false, "Single Choice", "finally入面既return會點？", "覆蓋try既return", "先執行finally", "異常會被忽略", "以上全部", null, "D", "exception,finally", 2, "finally既return會覆蓋try既return。", "點解要避免？", "正確做法？"));
        list.add(createQ(topic, 5, false, "Single Choice", "try-with-resources既用途？", "自動關閉資源", "減少catch", "優雅既異常處理", "以上全部", null, "A", "exception,try-with-resources", 2, "try-with-resources自動關閉實現AutoCloseable既資源。", "邊啲類可以用？", "傳統try-finally既問題？"));
        return list;
    }
    
    private List<Question> createSpringQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "Spring既核心係咩？", "AOP", "IOC/DI", "MVC", "Transaction", null, "B", "spring,ioc,di", 1, "Spring既核心係IOC同DI。", "Constructor vs Setter Injection？", "BeanFactory vs ApplicationContext？"));
        list.add(createQ(topic, 2, false, "Single Choice", "邊個係Spring既依賴注入方式？", "Constructor Injection", "Setter Injection", "Field Injection", "以上全部", null, "D", "spring,di", 1, "Spring支持Constructor、Setter同Field Injection。", "邊個最好？", "點解Constructor Injection推薦？"));
        list.add(createQ(topic, 3, false, "Single Choice", "Bean既作用域？", "Singleton", "Prototype", "Request", "以上全部", null, "D", "spring,bean,scope", 1, "Bean既作用域包括：Singleton、Prototype、Request、Session等。", "默認scope係咩？", "邊個適用於咩場景？"));
        list.add(createQ(topic, 4, false, "Single Choice", "@Autowired既用途？", "自動裝配Bean", "聲明Bean", "配置類", "切面", null, "A", "spring,autowired", 1, "@Autowired用於自動裝配Bean。", "required=false既作用？", "@Qualifier既用途？"));
        list.add(createQ(topic, 5, false, "Single Choice", "@Component同@Service既分別？", "一樣", "Component用於DAO", "Service用於Service層", "功能一樣但語義唔同", null, "D", "spring,component,service", 1, "功能一樣，但語義上@Component係通用既。", "邊啲Stereotype Annotation？", "分別？"));
        return list;
    }
    
    private List<Question> createSQLQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "以下邊個唔係SQL既DDL命令？", "CREATE", "ALTER", "SELECT", "DROP", null, "C", "sql,ddl", 1, "SELECT係DML，唔係DDL。", "DDL同DML既分別？", "TRUNCATE同DELETE既分別？"));
        list.add(createQ(topic, 2, false, "Single Choice", "PRIMARY KEY既特性？", "唯一且非空", "可以有多一個", "可以為null", "以上都不是", null, "A", "sql,primary-key", 1, "PRIMARY KEY必須唯一且非空，一個表只能有一個。", "Composite Key既用途？", "Natural vs Surrogate Key？"));
        list.add(createQ(topic, 3, false, "Single Choice", "UNIQUE KEY既特性？", "可以有多一個", "只能有一個", "可以為null", "以上都不是", null, "A", "sql,unique-key", 1, "UNIQUE KEY可以有多一個。", "PRIMARY KEY vs UNIQUE KEY？", "可以有幾多個UNIQUE？"));
        list.add(createQ(topic, 4, false, "Single Choice", "FOREIGN KEY既用途？", "建立表之間的關係", "確保數據完整性", "防止非法數據", "以上全部", null, "D", "sql,foreign-key", 1, "FOREIGN KEY用於建立同維護表之間的關係。", "ON DELETE CASCADE既作用？", "幾時用邊種referential action？"));
        list.add(createQ(topic, 5, false, "Single Choice", "JOIN既類型？", "INNER JOIN", "LEFT/RIGHT JOIN", "CROSS JOIN", "以上全部", null, "D", "sql,join", 1, "JOIN包括：INNER、LEFT、RIGHT、CROSS、FULL OUTER JOIN。", "INNER vs OUTER JOIN？", "Self JOIN既用途？"));
        return list;
    }
    
    private List<Question> createDesignPatternQuestions(Topic topic) {
        List<Question> list = new ArrayList<>();
        list.add(createQ(topic, 1, false, "Single Choice", "邊種Pattern用於將請求封裝為對象？", "Strategy", "Command", "Observer", "Template Method", null, "B", "design-pattern,command", 1, "Command Pattern將請求封裝為對象。", "Command Pattern既優點？", "Command vs Strategy？"));
        list.add(createQ(topic, 2, false, "Single Choice", "GoF Design Patterns既數量？", "23種", "24種", "26種", "20種", null, "A", "design-pattern,gof", 1, "GoF(Gang of Four)有23種設計模式。", "點樣分類？", "Creational、Structural、Behavioral？"));
        list.add(createQ(topic, 3, false, "Single Choice", "Creational Patterns既數量？", "3種", "5種", "7種", "9種", null, "B", "design-pattern,creational", 1, "Creational Patterns有5種。", "邊個最常用？", "Builder vs Factory？"));
        list.add(createQ(topic, 4, false, "Single Choice", "邊種Pattern確保類只有一個實例？", "Factory", "Singleton", "Builder", "Prototype", null, "B", "design-pattern,singleton", 1, "Singleton確保類只有一個實例。", "實現方式有邊幾種？", "邊種最好？"));
        list.add(createQ(topic, 5, false, "Single Choice", "Factory Method Pattern既特點？", "延遲創建到子類", "統一接口", "創建一系列相關對象", "以上全部", null, "A", "design-pattern,factory-method", 2, "Factory Method將對象創建延遲到子類。", "同Simple Factory既分別？", "Abstract Factory既分別？"));
        return list;
    }
}
