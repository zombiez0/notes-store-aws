package com.handler;


import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class NotesModel {
    public NotesModel() {}

    private String user_id;
    private Integer timestamp;
    private Integer note_id;

    public NotesModel(String user_id, Integer timestamp, Integer note_id) {
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.note_id = note_id;
    }


    public String getUser_id() {
        return user_id;
    }

    @DynamoDbPartitionKey
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    @DynamoDbSortKey
    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getNote_id() {
        return note_id;
    }

    public void setNote_id(Integer note_id) {
        this.note_id = note_id;
    }

}
