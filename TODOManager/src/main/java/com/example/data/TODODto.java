package com.example.data;

/**
 *
 * @author Pavel Shatrov
 */
public class TODODto {
    private  String description;
    private  boolean isDone;

    public TODODto() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
    
}
