package com.example.demo.infrastructure.adapter.output.google;

import com.example.demo.domain.port.output.ExternalFolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class GoogleDriveAdapter implements ExternalFolderService {

    private final WebClient webClient;

    public GoogleDriveAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com/drive/v3").build();
    }

    @Override
    public void createFolder(String folderName, String accessToken) {
        Map<String, String> body = Map.of(
                "name", folderName,
                "mimeType", "application/vnd.google-apps.folder"
        );

        try {
            String response = webClient.post()
                    .uri("/files")
                    .headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Lo hacemos síncrono para tu flujo MVC

            log.info("✅ Carpeta creada en Drive: {}", response);
        } catch (Exception e) {
            log.error("❌ Error al crear carpeta: {}", e.getMessage());
        }
    }
}
