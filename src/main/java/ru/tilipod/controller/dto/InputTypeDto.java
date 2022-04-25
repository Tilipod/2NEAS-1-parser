package ru.tilipod.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.tilipod.controller.dto.enums.InputTypeEnum;

@Data
public class InputTypeDto {

    @ApiModelProperty(value = "Тип входных данных", required = true)
    private InputTypeEnum inputType;

    @ApiModelProperty(value = "Высота изображения (для СНС)")
    private Integer height;

    @ApiModelProperty(value = "Ширина изображения (для СНС)")
    private Integer weight;

    @ApiModelProperty(value = "Кол-во каналов (для СНС)")
    private Integer channels;
}
