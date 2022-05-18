package ru.tilipod.amqp.message;

import lombok.Data;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;

@Data
public class ParserResultSuccessMessage extends ParserResultMessage {

    private String pathTo;

    private QLearning.QLConfiguration conf;

    public static ParserResultSuccessMessage createMessage(Integer taskId, String pathTo) {
        ParserResultSuccessMessage model = new ParserResultSuccessMessage();

        model.setTaskId(taskId);
        model.setPathTo(pathTo);

        return model;
    }

    public static ParserResultSuccessMessage createMessage(Integer taskId, String pathTo, QLearning.QLConfiguration conf) {
        ParserResultSuccessMessage model = new ParserResultSuccessMessage();

        model.setTaskId(taskId);
        model.setPathTo(pathTo);
        model.setConf(conf);

        return model;
    }
}
