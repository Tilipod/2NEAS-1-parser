package ru.tilipod.amqp.message;

import lombok.Data;

@Data
public abstract class ParserResultMessage {

    private Integer taskId;
}
