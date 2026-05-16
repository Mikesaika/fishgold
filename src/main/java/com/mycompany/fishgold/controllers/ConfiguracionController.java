package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.Configuracion;
import com.mycompany.fishgold.models.ConfiguracionDAO;
import com.mycompany.fishgold.views.ConfiguracionPanel;
import com.mycompany.fishgold.util.Validator;
import javax.swing.Timer;

public class ConfiguracionController {
    private final ConfiguracionPanel view;
    private final ConfiguracionDAO dao;

    public ConfiguracionController(ConfiguracionPanel view, ConfiguracionDAO dao) {
        this.view = view;
        this.dao = dao;
        init();
    }

    private void init() {
        cargarDatos();
        view.getBtnGuardar().addActionListener(e -> guardar());
    }

    private void cargarDatos() {
        Configuracion conf = dao.getActual();
        view.getTxtPagoBase().setText(String.valueOf(conf.getPagoKiloBase()));
    }

    private void guardar() {
        if (!Validator.isDecimal(view.getTxtPagoBase())) {
            view.setFeedback("Ingrese un valor numérico válido.");
            return;
        }

        double nuevoPrecio = Double.parseDouble(view.getTxtPagoBase().getText());

        if (dao.update(nuevoPrecio)) {
            view.setFeedback("Configuración guardada correctamente.");
            // Ocultar mensaje de éxito tras 3 segundos
            new Timer(3000, e -> view.setFeedback(" ")).start();
        }
    }
}