package ru.tilipod.util;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import ru.tilipod.controller.dto.enums.RegularizationTypeEnum;
import ru.tilipod.controller.dto.enums.UpdaterTypeEnum;

public class Constants {
    public static final int DEFAULT_ITERATIONS = 1;
    public static final int DEFAULT_SEED = 123;
    public static final int DEFAULT_COUNT_EPOCH = 100;
    public static final int DEFAULT_BATCH_SIZE = 1;
    public static final int DEFAULT_TARGET_DQN_UPDATE_FREQ = 100;
    public static final int DEFAULT_UPDATE_START = 10;
    public static final int DEFAULT_EPSILON_STEP = 10000;
    public static final double DEFAULT_REWARD_FACTOR = 1.0;
    public static final double DEFAULT_GAMMA = 0.99;
    public static final double DEFAULT_ERROR_CLAMP = 1.0;
    public static final float DEFAULT_MIN_EPSILON = 0.1f;
    public static final double DEFAULT_LEARNING_RATE = 0.00001;
    public static final WeightInit DEFAULT_WEIGHT_INIT = WeightInit.XAVIER;
    public static final OptimizationAlgorithm DEFAULT_OPTIMIZATION_ALGORITHM = OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
    public static final RegularizationTypeEnum DEFAULT_REGULARIZATION_TYPE = RegularizationTypeEnum.L2;
    public static final double DEFAULT_REGULARIZATION_FACTOR = 0.00005;
    public static final double DEFAULT_REGULARIZATION_BIAS = 0.0;
    public static final UpdaterTypeEnum DEFAULT_UPDATER_TYPE = UpdaterTypeEnum.NESTEROVS;
    public static final double DEFAULT_UPDATER_FACTOR = 0.9;
    public static final boolean DEFAULT_PRETRAIN = false;
    public static final boolean DEFAULT_BACKPROP = true;
    public static final int DEFAULT_STRIDE_HEIGHT = 2;
    public static final int DEFAULT_STRIDE_WEIGHT = 2;
    public static final Activation DEFAULT_ACTIVATION = Activation.RELU;
}
