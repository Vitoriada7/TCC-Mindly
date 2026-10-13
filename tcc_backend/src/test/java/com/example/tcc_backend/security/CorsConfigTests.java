package com.example.tcc_backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTests {
    @Test
    void permiteSomenteOrigensConfiguradas() {
        var source = new CorsConfig().corsConfigurationSource("https://mindly.vercel.app, http://localhost:4200");
        var configuration = source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/tarefas"));
        assertNotNull(configuration);
        assertEquals("https://mindly.vercel.app", configuration.checkOrigin("https://mindly.vercel.app"));
        assertNull(configuration.checkOrigin("https://outro-site.com"));
        assertNull(configuration.checkOrigin("https://preview.vercel.app"));
        assertTrue(configuration.getAllowedHeaders().contains("Authorization"));
        assertFalse(configuration.getAllowedMethods().contains("TRACE"));
    }

    @Test
    void rejeitaOrigensVaziasOuCuringas() {
        assertThrows(IllegalArgumentException.class, () -> new CorsConfig().corsConfigurationSource(" "));
        assertThrows(IllegalArgumentException.class, () -> new CorsConfig().corsConfigurationSource("*"));
        assertThrows(IllegalArgumentException.class, () -> new CorsConfig().corsConfigurationSource("https://*.vercel.app"));
    }
}
