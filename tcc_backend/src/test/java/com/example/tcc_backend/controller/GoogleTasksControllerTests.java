package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.response.GoogleTasksAutorizacaoResponse;
import com.example.tcc_backend.controller.response.GoogleTasksSincronizacaoResponse;
import com.example.tcc_backend.service.GoogleTasksService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GoogleTasksControllerTests {

    private final GoogleTasksService service = mock(GoogleTasksService.class);

    private final GoogleTasksController controller = new GoogleTasksController(service);

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void preservaContratoJsonDaAutorizacao() {
        when(service.autorizar()).thenReturn("https://accounts.google.com/oauth");
        ResponseEntity<GoogleTasksAutorizacaoResponse> resposta = controller.autorizar();
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        assertThat(mapper.writeValueAsString(resposta.getBody())).isEqualTo("{\"url\":\"https://accounts.google.com/oauth\"}");
    }

    @Test
    void preservaContratoJsonDaSincronizacao() {
        when(service.sincronizar()).thenReturn(3);
        ResponseEntity<GoogleTasksSincronizacaoResponse> resposta = controller.sincronizar();
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        assertThat(mapper.writeValueAsString(resposta.getBody())).isEqualTo("{\"tarefas\":3}");
    }

}
