package ru.tilipod.amqp.message;

import lombok.Data;

@Data
public class ParserResultSuccessMessage extends ParserResultMessage {

    private String pathTo;

    public static ParserResultSuccessMessage createMessage(Integer taskId, String pathTo) {
        ParserResultSuccessMessage model = new ParserResultSuccessMessage();

        model.setTaskId(taskId);
        model.setPathTo(pathTo);

        return model;
    }
}
