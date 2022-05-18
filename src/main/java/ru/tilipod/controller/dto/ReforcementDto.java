package ru.tilipod.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReforcementDto {

    @ApiModelProperty(value = "Кол-во эпох для обучения")
    private Integer countEpoch;

    @ApiModelProperty(value = "Итерация, после которой происходит обновление нейронной сети (для RL)")
    private Integer updateStart;

    @ApiModelProperty(value = "Коэффициент коррекции вознаграждения")
    private Double rewardFactor;

    @ApiModelProperty(value = "Гамма из уравнения Белла")
    private Double gamma;

    @ApiModelProperty(value = "Допустимый разброс при накапливании вознаграждения")
    private Double errorClamp;

    @ApiModelProperty(value = "Минимальный эпсилон (см. про Exploration в RL)")
    private Float minEpsilon;

    @ApiModelProperty(value = "Знаменатель при вычислении случайного эпсилон")
    private Integer epsilonStep;
}
