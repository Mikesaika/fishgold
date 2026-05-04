package com.mycompany.fishgold.util;

import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.border.Border;

public class Validator {
    private static final Pattern SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$");
    private static final Pattern SOLO_NUMEROS = Pattern.compile("^\\d*$");
    private static final Pattern TELEFONO_FORMATO = Pattern.compile("^\\+?[0-9\\s\\-]{7,15}$");

    // Bordes estandarizados para el diseño moderno de FishGold
    private static final Border BORDER_ERROR = BorderFactory.createLineBorder(new Color(239, 68, 68), 2); // Rojo
                                                                                                          // vibrante
    private static final Border BORDER_NORMAL = BorderFactory.createLineBorder(new Color(203, 213, 225), 1); // Gris
                                                                                                             // Slate

    /**
     * Valida que un campo no esté vacío y aplica feedback visual.
     */
    public static boolean isNotBlank(JTextField field) {
        String text = field.getText();
        boolean isValid = text != null && !text.trim().isEmpty();
        applyStyle(field, isValid);
        return isValid;
    }

    /**
     * Valida si el texto contiene solo letras y aplica estilo.
     */
    public static boolean isAlpha(JTextField field) {
        String text = field.getText();
        boolean isValid = text != null && SOLO_LETRAS.matcher(text).matches() && !text.trim().isEmpty();
        applyStyle(field, isValid);
        return isValid;
    }

    /**
     * Valida si el texto es decimal positivo (Meta y Monto) con feedback.
     */
    public static boolean isDecimal(JTextField field) {
        String text = field.getText();
        boolean isValid = false;
        try {
            double val = Double.parseDouble(text);
            isValid = val >= 0;
        } catch (NumberFormatException e) {
            isValid = false;
        }
        applyStyle(field, isValid);
        return isValid;
    }

    /**
     * Valida formato de teléfono.
     */
    public static boolean isValidPhone(JTextField field) {
        String text = field.getText();
        boolean isValid = text != null && TELEFONO_FORMATO.matcher(text).matches();
        applyStyle(field, isValid);
        return isValid;
    }

    /**
     * Método privado para aplicar el diseño UI dependiendo del resultado.
     */
    private static void applyStyle(JTextField field, boolean isValid) {
        if (isValid) {
            field.setBorder(BORDER_NORMAL);
        } else {
            field.setBorder(BORDER_ERROR);
        }
    }

    // Mantenemos tus métodos originales por si necesitas validar Strings puros
    public static boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }
}