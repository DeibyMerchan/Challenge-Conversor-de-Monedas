package com.miempresa.conversor;

// Modelo para respuesta de API de conversión
public class CurrencyResponse {
    // Tasa de cambio entre monedas
    private double conversion_rate;

    // Obtiene la tasa de conversión actual
    public double getConversion_rate() {
        return conversion_rate;
    }
}