package com.mycompany.fishgold.util;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$");
    private static final Pattern SOLO_NUMEROS = Pattern.compile("^\\d*$");
    private static final Pattern TELEFONO_FORMATO = Pattern.compile("^\\+?[0-9\\s\\-]{7,15}$");
    private static final Pattern MATRICULA_FORMATO = Pattern.compile("^[A-Z0-9-]{3,20}$");

    /**
     * Verifica que el texto no esté vacío o compuesto solo por espacios.
     */
    public static boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Valida si el texto contiene solo letras y espacios (Ideal para nombres).
     */
    public static boolean isAlpha(String text) {
        return text != null && SOLO_LETRAS.matcher(text).matches();
    }

    /**
     * Valida si el texto es estrictamente numérico (sin decimales).
     */
    public static boolean isNumeric(String text) {
        return text != null && SOLO_NUMEROS.matcher(text).matches();
    }

    /**
     * Valida si un string puede ser un número decimal válido.
     */
    public static boolean isDecimal(String text) {
        if (text == null)
            return false;
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida formato de teléfono internacional o local.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && TELEFONO_FORMATO.matcher(phone).matches();
    }

    /**
     * Valida que el año de compra sea coherente (ej: entre 1900 y 2100).
     */
    public static boolean isValidYear(String yearStr) {
        if (!isNumeric(yearStr))
            return false;
        int year = Integer.parseInt(yearStr);
        int currentYear = java.time.Year.now().getValue();
        return year >= 1900 && year <= currentYear;
    }
}