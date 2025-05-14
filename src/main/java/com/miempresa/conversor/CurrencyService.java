package com.miempresa.conversor;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;

// Servicio para conversión de monedas usando ExchangeRate API
public class CurrencyService {
    private final String API_KEY = "b5e11a5e41934cfea70834b4";
    private final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Convierte una cantidad de dinero entre dos monedas
    public double convertirMoneda(String de, String a, double cantidad) throws IOException, InterruptedException {
        // Prepara URL de la API
        String uri = BASE_URL + API_KEY + "/pair/" + de + "/" + a;
        
        // Configura petición HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        // Obtiene tasa de cambio de la API
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Procesa respuesta JSON
        CurrencyResponse currencyResponse = gson.fromJson(response.body(), CurrencyResponse.class);
        
        // Calcula conversión final
        return cantidad * currencyResponse.getConversion_rate();
    }
}