package com.base.basemodule.entity;

public class BaseEventEntity {

    String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BaseEventEntity() {
    }

    public BaseEventEntity(String state) {
        this.state = state;
    }
}
