package hello.advanced.trace;

import java.util.UUID;

public class TraceId {
    private String id;
    private int level; // 해당 로직의 depth 표현

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public TraceId createNextId(){
        return new TraceId(id, level + 1);
    }

    public TraceId createPreviousId(){
        return new TraceId(id, level - 1);
    }

    public boolean isFirstLevel() {
        return level==0;
    }

    public int getLevel() {
        return level;
    }

    public String getId() {
        return id;
    }
}
