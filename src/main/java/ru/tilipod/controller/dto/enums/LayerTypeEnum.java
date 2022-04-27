package ru.tilipod.controller.dto.enums;

public enum LayerTypeEnum {
    CONVOLUTIONAL, // Сверточный слой
    BATCH_NORMALIZATION, // Слой нормализации
    SUBSAMPLING, // Подвыборочный слой
    DENSE, // Полносвязный слой
    OUTPUT // Выходной слой
}
