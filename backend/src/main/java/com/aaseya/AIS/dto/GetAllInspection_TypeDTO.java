package com.aaseya.AIS.dto;

public class GetAllInspection_TypeDTO {
    private long id;
    private String name;

    public GetAllInspection_TypeDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
