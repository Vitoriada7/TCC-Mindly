package com.example.tcc_backend.service.integracao;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.type.LogicalType;

public final class JsonIntegracao {

    private JsonIntegracao() {}

    public static ObjectMapper mapperEstrito(ObjectMapper mapper) {
        return mapper.rebuild()
                .withCoercionConfig(LogicalType.Textual, coercao -> coercao
                        .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                        .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                        .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail))
                .build();
    }

}
