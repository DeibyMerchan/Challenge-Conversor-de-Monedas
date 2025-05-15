package com.miempresa.conversor;

import com.miempresa.conversor.service.CurrencyService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class App extends Application {

    private static final List<String> MONEDAS = List.of("USD", "EUR", "MXN", "GBP", "COP", "ARS", "BOB", "BRL", "CLP", "VES" );
    private static final Map<String, String> MONEDA_NOMBRE = Map.of(
            "USD", "Dólar estadounidense",
            "EUR", "Euro",
            "MXN", "Peso mexicano",
            "GBP", "Libra esterlina",
            "COP", "Peso colombiano",
            "ARS", "Peso Argentino",
            "BOB", "Boliviano",
            "BRL", "Real brasileño",
            "CLP", "Peso chileno",
            "VES", "Bolívar soberano"

    );

    private static final int WINDOW_WIDTH = 740;
    private static final int WINDOW_HEIGHT = 450;

    private final CurrencyService currencyService = new CurrencyService();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private ComboBox<String> monedaOrigen;
    private ComboBox<String> monedaDestino;
    private TextField cantidad;
    private Label resultado;
    private Label error;


    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setId("main-container");

        Label titulo = new Label("Conversor de Divisas");
        titulo.getStyleClass().add("titulo");

        cantidad = new TextField();
        cantidad.setPromptText("Ingrese el monto a convertir");
        cantidad.setOnAction(e -> realizarConversion());

        HBox selectors = new HBox(10);
        selectors.setAlignment(Pos.CENTER);

        monedaOrigen = crearSelectorMoneda();
        monedaDestino = crearSelectorMoneda();
        // Manejador de teclas para el ComboBox de la moneda de origen
        monedaOrigen.setOnKeyPressed(event -> {
            int index = monedaOrigen.getSelectionModel().getSelectedIndex();

            if (event.getCode() == KeyCode.UP) {
                // Mover hacia arriba
                if (index > 0) {
                    monedaOrigen.getSelectionModel().select(index - 1);
                } else {
                    // Si ya estamos en la primera opción, volver a la última
                    monedaOrigen.getSelectionModel().select(MONEDAS.size() - 1);
                }
            } else if (event.getCode() == KeyCode.DOWN) {
                // Mover hacia abajo
                if (index < MONEDAS.size() - 1) {
                    monedaOrigen.getSelectionModel().select(index + 1);
                } else {
                    // Si estamos en la última opción, volver a la primera
                    monedaOrigen.getSelectionModel().select(0);
                }
            }
        });

// Manejador de teclas para el ComboBox de la moneda de destino
        monedaDestino.setOnKeyPressed(event -> {
            int index = monedaDestino.getSelectionModel().getSelectedIndex();

            if (event.getCode() == KeyCode.UP) {
                // Mover hacia arriba
                if (index > 0) {
                    monedaDestino.getSelectionModel().select(index - 1);
                } else {
                    // Si ya estamos en la primera opción, volver a la última
                    monedaDestino.getSelectionModel().select(MONEDAS.size() - 1);
                }
            } else if (event.getCode() == KeyCode.DOWN) {
                // Mover hacia abajo
                if (index < MONEDAS.size() - 1) {
                    monedaDestino.getSelectionModel().select(index + 1);
                } else {
                    // Si estamos en la última opción, volver a la primera
                    monedaDestino.getSelectionModel().select(0);
                }
            }
        });

        Button intercambiar = new Button();
        intercambiar.setId("btn-intercambiar");
        intercambiar.setText("↔");
        intercambiar.setOnAction(e -> intercambiarMonedas());

        selectors.getChildren().addAll(monedaOrigen, intercambiar, monedaDestino);

        Button convertir = new Button("Convertir");
        convertir.getStyleClass().add("btn-convertir");
        convertir.setOnAction(e -> realizarConversion());

        resultado = new Label("");
        resultado.getStyleClass().add("resultado");
        resultado.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        error = new Label();
        error.setId("error-label");
        error.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: red;");

        root.getChildren().addAll(titulo, cantidad, selectors, convertir, resultado, error);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Conversor de Divisas");
        primaryStage.show();
        Platform.runLater(() -> root.requestFocus());
    }

    private ComboBox<String> crearSelectorMoneda() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(MONEDAS);

        combo.setCellFactory(lv -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/flags/" + item.toLowerCase() + ".png")));
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    setGraphic(imageView);
                    setText(item + " - " + MONEDA_NOMBRE.get(item));
                }
            }
        });

        combo.setButtonCell(combo.getCellFactory().call(null));
        combo.setValue("USD");
        return combo;

    }

    private void intercambiarMonedas() {
        String origen = monedaOrigen.getValue();
        monedaOrigen.setValue(monedaDestino.getValue());
        monedaDestino.setValue(origen);
    }

    private void realizarConversion() {
        error.getStyleClass().remove("mostrar");
        resultado.getStyleClass().remove("mostrar");

        error.setText("");
        resultado.setText("0.00");

        String origen = monedaOrigen.getValue();
        String destino = monedaDestino.getValue();

        if (origen == null || destino == null || cantidad.getText().isEmpty()) {
            error.setText("Complete todos los campos correctamente.");
            error.getStyleClass().add("mostrar");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(cantidad.getText());
            if (monto < 0) {
                error.setText("Formato inválido: no se permiten valores negativos.");
                error.getStyleClass().add("mostrar");
                return;
            }
        } catch (NumberFormatException e) {
            error.setText("Monto inválido");
            error.getStyleClass().add("mostrar");
            return;
        }

        new Thread(() -> {
            try {
                double resultadoConversion = currencyService.convertirMoneda(origen, destino, monto);
                Platform.runLater(() -> {
                    resultado.setText(decimalFormat.format(resultadoConversion) + " " + destino);
                    resultado.getStyleClass().add("mostrar");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    error.setText("Error en la conversión");
                    error.getStyleClass().add("mostrar");
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
