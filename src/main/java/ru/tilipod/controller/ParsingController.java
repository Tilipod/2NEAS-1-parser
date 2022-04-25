package ru.tilipod.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tilipod.controller.dto.NeuronNetworkDto;
import ru.tilipod.service.ParserService;

@RestController
@RequestMapping("/parsing")
@RequiredArgsConstructor
@Api(description = "Контроллер для парсинга нейронных сетей")
public class ParsingController {

    private final ParserService parserService;

    @PostMapping("/parsing")
    @ApiOperation(value = "Воспроизвести структуру нейронной сети по запросу клиента")
    public ResponseEntity<Void> parseNeuronNetwork(@RequestBody NeuronNetworkDto neuronNetworkDto) {
        parserService.parseNeuronNetwork(neuronNetworkDto);
        return ResponseEntity.ok().build();
    }
}
