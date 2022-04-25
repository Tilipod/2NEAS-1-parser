package ru.tilipod.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.tilipod.controller.dto.enums.UpdaterTypeEnum;

@Data
public class UpdaterDto {

    @ApiModelProperty(value = "Тип оптимизации скорости обучения")
    private UpdaterTypeEnum updaterType;

    @ApiModelProperty(value = "Коэффициент оптимизации скорости обучения")
    private Double updaterFactor;
}
