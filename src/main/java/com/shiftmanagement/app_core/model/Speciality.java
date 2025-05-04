package com.shiftmanagement.app_core.model;
import org.springframework.data.annotation.Id;

public class Speciality {
    @Id
    private String id;
    private String name;          // e.g., General Medicine, Dentistry
    private String codePrefix;    // e.g., "M", "D", "P" for turn codes

    public String getName() {
        return name;
    }
    public String getCodePrefix() {
        return codePrefix;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCodePrefix(String codePrefix) {
        this.codePrefix = codePrefix;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    
}
