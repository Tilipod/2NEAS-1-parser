package ru.tilipod.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;

import java.util.List;

@Data
public class NeuronNetworkDto {

    @ApiModelProperty(value = "ID задачи", required = true)
    private Integer taskId;

    @ApiModelProperty(value = "Путь к файлу сохранения модели сети", required = true)
    private String pathToSave;

    @ApiModelProperty(value = "Кол-во итераций на каждой эпохе обучения")
    private Integer iterations;

    @ApiModelProperty(value = "Число-инициализатор для генератора случайных чисел")
    private Integer seed;

    @ApiModelProperty(value = "Скорость обучения")
    private Double learningRate;

    @ApiModelProperty(value = "Правило инициализации весов")
    private WeightInit weightInit;

    @ApiModelProperty(value = "Алгоритм обучения")
    private OptimizationAlgorithm optimizationAlgo;

    @ApiModelProperty(value = "Настройки регуляризации")
    private RegularizationDto regularization;

    @ApiModelProperty(value = "Настройки оптимизации скорости обучения")
    private UpdaterDto updater;

    @ApiModelProperty(value = "Настройки входных данных")
    private InputTypeDto inputType;

    @ApiModelProperty(value = "Признак предварительного обучения")
    private Boolean pretrain;

    @ApiModelProperty(value = "Использовать обратное распространение ошибки")
    private Boolean backprop;

    @ApiModelProperty(value = "Настройки слоев сети")
    private List<LayerDto> layers;
}
