package com.example.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * TODO Entity class
 * @author Pavel Shatrov
 */
@Entity
public class TODOItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String description;
    private  Boolean isDone;
    private  Date date;

    public TODOItem() {
    }

    public TODOItem(String description, Boolean isDone) {
        this.description = description;
        this.isDone = isDone;
        this.date = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getDate());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TODOItem)) {
            return false;
        }
        TODOItem other = (TODOItem) object;
        return Objects.equals(getDescription(), other.getDescription()) && getDate().equals(getDate());     
    }

    @Override
    public String toString() {
        return "com.example.data.TODOItem[ id=" + id + " ]";
    }
    
}
