package ru.tilipod.service;

import ru.tilipod.amqp.message.ParserResultErrorMessage;
import ru.tilipod.amqp.message.ParserResultSuccessMessage;

public interface RabbitSender {

    void sendErrorToScheduler(ParserResultErrorMessage model);

    void sendSuccessToScheduler(ParserResultSuccessMessage model);

}
