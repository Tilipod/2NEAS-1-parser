package ru.tilipod.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.tilipod.controller.dto.enums.RegularizationTypeEnum;

@Data
public class RegularizationDto {

    @ApiModelProperty(value = "Требуется ли проводить регуляризацию")
    private Boolean needRegularization;

    @ApiModelProperty(value = "Тип регуляризации")
    private RegularizationTypeEnum regularizationType;

    @ApiModelProperty(value = "Коэффициент регуляризации")
    private Double regularizationFactor;

    @ApiModelProperty(value = "Смещение при регуляризации")
    private Double regularizationBias;
}
