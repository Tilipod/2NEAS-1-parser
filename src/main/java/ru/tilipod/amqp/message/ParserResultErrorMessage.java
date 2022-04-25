package ru.tilipod.amqp.message;

import lombok.Data;

@Data
public class ParserResultErrorMessage extends ParserResultMessage {

    private String className;

    private String message;

    public static ParserResultErrorMessage createMessage(Integer taskId, Exception e) {
        ParserResultErrorMessage model = new ParserResultErrorMessage();

        model.setMessage(e.getMessage());
        model.setClassName(e.getClass().getName());
        model.setTaskId(taskId);

        return model;
    }
}
