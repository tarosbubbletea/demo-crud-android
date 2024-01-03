package com.example.myapplication2;

import java.util.regex.Pattern;

public class RutValidator {

    public static String validateRut(String rut) {
        if (rut == null || rut.length() < 3) {
            return "RUT vacío o con menos de 3 caracteres.";
        }

        String numericalPart = rut.substring(0, rut.length() - 2);
        if (!Pattern.compile("^[0-9]*$").matcher(numericalPart).matches()) {
            return "La parte numérica del RUT sólo debe contener números.";
        }

        String verifierAndHyphen = rut.substring(rut.length() - 2);
        if (verifierAndHyphen.length() != 2) {
            return "Error en el largo del dígito verificador.";
        }

        if (!Pattern.compile("^[-]{1}[0-9kK]$").matcher(verifierAndHyphen).matches()) {
            return "El dígito verificador no cuenta con el patrón requerido";
        }

        if (!Pattern.compile("^[0-9.]+[-]?[0-9kK]{1}").matcher(rut).matches()) {
            return "Error al digitar el RUT";
        }

        //noinspection RegExpRedundantEscape
        String dv = rut.replaceAll("[\\.\\-]", "");

        char verificatorDigit = dv.charAt(dv.length()-1);

        int total = 0;
        int multiplier = 2;

        for(int i= dv.length() -2 ; i>=0; i--){
            total += Integer.parseInt(Character.toString(dv.charAt(i))) * multiplier;
            multiplier++;
            if(multiplier > 7)
                multiplier = 2;
        }

        char verifierResult = '0';
        switch (11 - (total % 11)) {
            case 10:
                verifierResult = 'K';
                break;
            case 11:
                verifierResult = '0';
                break;
            default:
                verifierResult = Character.forDigit((11 - (total % 11)),10);
        }

        if(verifierResult == Character.toUpperCase(verificatorDigit)){
            return "1";
        }else{
            return "El RUT ingresado no es válido.";
        }
    }
}