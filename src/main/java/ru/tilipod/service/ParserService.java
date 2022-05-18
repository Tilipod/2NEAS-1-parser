package ru.tilipod.service;

import ru.tilipod.controller.dto.NeuronNetworkDto;

public interface ParserService {

    void parseRl4jNetwork(NeuronNetworkDto nn);

    void parseDl4jNetwork(NeuronNetworkDto nn);
}
