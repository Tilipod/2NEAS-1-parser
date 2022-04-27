package ru.tilipod.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import ru.tilipod.controller.dto.enums.LayerTypeEnum;

@Data
public class LayerDto {

    @ApiModelProperty(value = "Тип слоя", required = true)
    private LayerTypeEnum layerType;

    @ApiModelProperty(value = "Номер слоя", required = true)
    private Integer layerNumber;

    @ApiModelProperty(value = "Кол-во входов")
    private Integer countInput;

    @ApiModelProperty(value = "Кол-во выходов")
    private Integer countOutput;

    @ApiModelProperty(value = "Высота шага")
    private Integer strideHeight;

    @ApiModelProperty(value = "Ширина шага")
    private Integer strideWeight;

    @ApiModelProperty(value = "Высота фильтра")
    private Integer kernelHeight;

    @ApiModelProperty(value = "Ширина фильтра")
    private Integer kernelWeight;

    @ApiModelProperty(value = "Способ инициализации весов нейронов слоя")
    private WeightInit weightInitType;

    @ApiModelProperty(value = "Функция активации для нейронов слоя")
    private Activation activationType;

    @ApiModelProperty(value = "Тип пулинга")
    private SubsamplingLayer.PoolingType poolingType;
}
