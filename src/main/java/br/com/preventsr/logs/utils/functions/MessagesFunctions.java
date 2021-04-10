package br.com.preventsr.logs.utils.functions;

public class MessagesFunctions {
    public static String customMsgSuccess(String object, String option) {
        return object.concat(" ").concat(option).concat(" com sucesso!");
    }
}
