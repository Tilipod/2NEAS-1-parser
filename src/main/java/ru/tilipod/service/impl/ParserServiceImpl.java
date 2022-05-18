package ru.tilipod.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tilipod.amqp.message.ParserResultErrorMessage;
import ru.tilipod.amqp.message.ParserResultSuccessMessage;
import ru.tilipod.controller.dto.InputTypeDto;
import ru.tilipod.controller.dto.LayerDto;
import ru.tilipod.controller.dto.NeuronNetworkDto;
import ru.tilipod.controller.dto.RegularizationDto;
import ru.tilipod.controller.dto.UpdaterDto;
import ru.tilipod.exception.InvalidRequestException;
import ru.tilipod.exception.SystemError;
import ru.tilipod.service.ParserService;
import ru.tilipod.service.RabbitSender;
import ru.tilipod.util.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParserServiceImpl implements ParserService {

    private final RabbitSender rabbitSender;

    private MultiLayerNetwork parseNetwork(NeuronNetworkDto nn) {
        try {
            NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                    .iterations(Objects.requireNonNullElse(nn.getIterations(), Constants.DEFAULT_ITERATIONS))
                    .seed(Objects.requireNonNullElse(nn.getSeed(), Constants.DEFAULT_SEED))
                    .learningRate(Objects.requireNonNullElse(nn.getLearningRate(), Constants.DEFAULT_LEARNING_RATE))
                    .weightInit(Objects.requireNonNullElse(nn.getWeightInit(), Constants.DEFAULT_WEIGHT_INIT))
                    .optimizationAlgo(Objects.requireNonNullElse(nn.getOptimizationAlgo(), Constants.DEFAULT_OPTIMIZATION_ALGORITHM));

            configureRegularization(builder, nn.getRegularization(), nn.getTaskId());
            configureUpdater(builder, nn.getUpdater(), nn.getTaskId());

            MultiLayerConfiguration config = configureLayers(builder, nn.getLayers(), nn.getTaskId())
                    .setInputType(createInputType(nn.getInputType(), nn.getTaskId()))
                    .pretrain(Objects.requireNonNullElse(nn.getPretrain(), Constants.DEFAULT_PRETRAIN))
                    .backprop(Objects.requireNonNullElse(nn.getBackprop(), Constants.DEFAULT_BACKPROP))
                    .build();

            MultiLayerNetwork net = new MultiLayerNetwork(config);
            net.init();

            saveNetwork(net, nn.getPathToSave(), nn.getTaskId());
            return net;
        } catch (Exception e) {
            log.error("Ошибка парсинга сети. Задача: {}, ошибка: {}", nn.getTaskId(), e.getMessage());
            rabbitSender.sendErrorToScheduler(ParserResultErrorMessage.createMessage(nn.getTaskId(), e));
            return null;
        }
    }

    private QLearning.QLConfiguration parseQLConfiguration(NeuronNetworkDto nn) {
        return QLearning.QLConfiguration.builder()
                .seed(Objects.requireNonNullElse(nn.getSeed(), Constants.DEFAULT_SEED))
                .maxEpochStep(Objects.requireNonNullElse(nn.getReforcement().getCountEpoch(), Constants.DEFAULT_COUNT_EPOCH))
                .maxStep(Objects.requireNonNullElse(nn.getIterations(), Constants.DEFAULT_ITERATIONS))
                .batchSize(Constants.DEFAULT_BATCH_SIZE)
                .expRepMaxSize(Constants.DEFAULT_EXP_REP_SIZE)
                .targetDqnUpdateFreq(Constants.DEFAULT_TARGET_DQN_UPDATE_FREQ)
                .updateStart(Objects.requireNonNullElse(nn.getReforcement().getUpdateStart(), Constants.DEFAULT_UPDATE_START))
                .rewardFactor(Objects.requireNonNullElse(nn.getReforcement().getRewardFactor(), Constants.DEFAULT_REWARD_FACTOR))
                .gamma(Objects.requireNonNullElse(nn.getReforcement().getGamma(), Constants.DEFAULT_GAMMA))
                .errorClamp(Objects.requireNonNullElse(nn.getReforcement().getErrorClamp(), Constants.DEFAULT_ERROR_CLAMP))
                .minEpsilon(Objects.requireNonNullElse(nn.getReforcement().getMinEpsilon(), Constants.DEFAULT_MIN_EPSILON))
                .epsilonNbStep(Objects.requireNonNullElse(nn.getReforcement().getEpsilonStep(), Constants.DEFAULT_EPSILON_STEP))
                .doubleDQN(true)
                .build();
    }

    @Override
    @Async
    public void parseRl4jNetwork(NeuronNetworkDto nn) {
        MultiLayerNetwork net = parseNetwork(nn);
        if (net == null) {
            return;
        }

        QLearning.QLConfiguration conf = parseQLConfiguration(nn);

        rabbitSender.sendSuccessToScheduler(ParserResultSuccessMessage.createMessage(nn.getTaskId(), nn.getPathToSave(), conf));
        log.info("Завершен парсинг нейронной сети по задаче {}", nn.getTaskId());
    }

    @Override
    @Async
    public void parseDl4jNetwork(NeuronNetworkDto nn) {
        MultiLayerNetwork net = parseNetwork(nn);
        if (net == null) {
            return;
        }

        rabbitSender.sendSuccessToScheduler(ParserResultSuccessMessage.createMessage(nn.getTaskId(), nn.getPathToSave()));
        log.info("Завершен парсинг нейронной сети по задаче {}", nn.getTaskId());
    }

    private void saveNetwork(MultiLayerNetwork net, String pathToSave, Integer taskId) {
        try {
            ModelSerializer.writeModel(net, pathToSave, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemError(String.format("Ошибка сохранения нейронной сети. Задача с id = %d", taskId), taskId);
        }
    }

    private void configureRegularization(NeuralNetConfiguration.Builder builder, RegularizationDto regularization, Integer taskId) {
        if (regularization != null && regularization.getNeedRegularization() != null && regularization.getNeedRegularization()) {
            builder.regularization(true);

            switch (Objects.requireNonNullElse(regularization.getRegularizationType(), Constants.DEFAULT_REGULARIZATION_TYPE)) {
                case L1 -> builder.l1(Objects.requireNonNullElse(regularization.getRegularizationFactor(), Constants.DEFAULT_REGULARIZATION_FACTOR))
                        .l1Bias(Objects.requireNonNullElse(regularization.getRegularizationBias(), Constants.DEFAULT_REGULARIZATION_BIAS));
                case L2 -> builder.l2(Objects.requireNonNullElse(regularization.getRegularizationFactor(), Constants.DEFAULT_REGULARIZATION_FACTOR))
                        .l2Bias(Objects.requireNonNullElse(regularization.getRegularizationBias(), Constants.DEFAULT_REGULARIZATION_BIAS));
                default -> throw new InvalidRequestException(String.format("Неподдерживаемый тип регуляризации: %s. Задача с id = %d",
                        regularization.getRegularizationType(), taskId), taskId);
            }
        } else {
            builder.regularization(false);
        }
    }

    private void configureUpdater(NeuralNetConfiguration.Builder builder, UpdaterDto updater, Integer taskId) {
        if (updater != null) {
            switch (Objects.requireNonNullElse(updater.getUpdaterType(), Constants.DEFAULT_UPDATER_TYPE)) {
                case NESTEROVS -> builder.updater(new Nesterovs(Objects.requireNonNullElse(updater.getUpdaterFactor(), Constants.DEFAULT_UPDATER_FACTOR)));
                default -> throw new InvalidRequestException(String.format("Неподдерживаемый тип адаптера: %s. Задача с id = %d",
                        updater.getUpdaterType(), taskId), taskId);
            }
        } else {
            builder.regularization(false);
        }
    }

    private NeuralNetConfiguration.ListBuilder configureLayers(NeuralNetConfiguration.Builder builder, List<LayerDto> layers, Integer taskId) {
        NeuralNetConfiguration.ListBuilder result = builder.list();

        layers.forEach(l -> configureLayer(result, l, taskId));

        return result;
    }

    private void configureLayer(NeuralNetConfiguration.ListBuilder builder, LayerDto layer, Integer taskId) {
        if (layer.getLayerType() == null) {
            throw new InvalidRequestException(String.format("Не указан тип слоя. Задача с id = %d", taskId), taskId);
        }

        if (layer.getLayerNumber() == null) {
            throw new InvalidRequestException(String.format("Не указан номер слоя. Задача с id = %d", taskId), taskId);
        }

        switch (layer.getLayerType()) {
            case CONVOLUTIONAL -> builder.layer(layer.getLayerNumber(), createConvolutionalLayer(layer));
            case BATCH_NORMALIZATION -> builder.layer(layer.getLayerNumber(), createBatchNormalizationLayer(layer));
            case SUBSAMPLING -> builder.layer(layer.getLayerNumber(), createSubsamplingLayer(layer));
            case DENSE -> builder.layer(layer.getLayerNumber(), createDenseLayer(layer));
            case OUTPUT -> builder.layer(layer.getLayerNumber(), createOutputLayer(layer));
            default -> throw new InvalidRequestException(String.format("Неподдерживаемый тип слоя %s. Задача с id = %d", layer.getLayerType(),
                    taskId), taskId);
        }
    }

    private InputType createInputType(InputTypeDto inputTypeDto, Integer taskId) {
        if (inputTypeDto == null || inputTypeDto.getInputType() == null) {
            return null;
        }

        return switch (inputTypeDto.getInputType()) {
            case CONVOLUTIONAL -> InputType.convolutionalFlat(inputTypeDto.getHeight(), inputTypeDto.getWeight(), inputTypeDto.getChannels());
            default -> throw new InvalidRequestException(String.format("Неподдерживаемый тип входных данных: %s. Задача с id = %d",
                    inputTypeDto.getInputType(), taskId), taskId);
        };
    }

    private ConvolutionLayer createConvolutionalLayer(LayerDto layer) {
        return new ConvolutionLayer.Builder()
                .nIn(layer.getCountInput())
                .nOut(layer.getCountOutput())
                .stride(Objects.requireNonNullElse(layer.getStrideHeight(), Constants.DEFAULT_STRIDE_HEIGHT),
                        Objects.requireNonNullElse(layer.getStrideWeight(), Constants.DEFAULT_STRIDE_WEIGHT))
                .weightInit(Objects.requireNonNullElse(layer.getWeightInitType(), Constants.DEFAULT_WEIGHT_INIT))
                .activation(Objects.requireNonNullElse(layer.getActivationType(), Constants.DEFAULT_ACTIVATION))
                .build();
    }

    private BatchNormalization createBatchNormalizationLayer(LayerDto layer) {
        return new BatchNormalization.Builder()
                .nIn(layer.getCountInput())
                .nOut(layer.getCountOutput())
                .weightInit(Objects.requireNonNullElse(layer.getWeightInitType(), Constants.DEFAULT_WEIGHT_INIT))
                .activation(Objects.requireNonNullElse(layer.getActivationType(), Constants.DEFAULT_ACTIVATION))
                .build();
    }

    private SubsamplingLayer createSubsamplingLayer(LayerDto layer) {
        return new SubsamplingLayer.Builder()
                .poolingType(layer.getPoolingType())
                .kernelSize(Objects.requireNonNullElse(layer.getKernelHeight(), Constants.DEFAULT_STRIDE_HEIGHT),
                            Objects.requireNonNullElse(layer.getKernelWeight(), Constants.DEFAULT_STRIDE_WEIGHT))
                .stride(Objects.requireNonNullElse(layer.getStrideHeight(), Constants.DEFAULT_STRIDE_HEIGHT),
                        Objects.requireNonNullElse(layer.getStrideWeight(), Constants.DEFAULT_STRIDE_WEIGHT))
                .build();
    }

    private DenseLayer createDenseLayer(LayerDto layer) {
        DenseLayer.Builder builder = new DenseLayer.Builder()
                .nOut(layer.getCountOutput())
                .activation(Objects.requireNonNullElse(layer.getActivationType(), Constants.DEFAULT_ACTIVATION));

        if (layer.getCountInput() != null) {
            builder.nIn(layer.getCountInput());
        }

        if (layer.getWeightInitType() != null) {
            builder.weightInit(layer.getWeightInitType());
        }

        return builder.build();
    }

    private OutputLayer createOutputLayer(LayerDto layer) {
        OutputLayer.Builder builder = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(layer.getCountOutput())
                .activation(Objects.requireNonNullElse(layer.getActivationType(), Activation.SOFTMAX));

        if (layer.getCountInput() != null) {
            builder.nIn(layer.getCountInput());
        }

        if (layer.getWeightInitType() != null) {
            builder.weightInit(layer.getWeightInitType());
        }

        return builder.build();
    }
}
