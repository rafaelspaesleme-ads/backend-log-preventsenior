package br.com.preventsr.logs.utils.functions;

public class MessagesFunctions {
    public static String customMsgSuccess(String object, String option) {
        return object + " " + option + " com sucesso!";
    }

    public static String msgCountDataAdd(String size) {
        return "Quantidade de dados adicionados: ".concat(size).concat(".");
    }

    public static String msgConsult(String object, String name, boolean success) {

        return success
                ? "Consulta de log(s) de " + object + " " + name + " retornado com sucesso!"
                : "Não há log(s) cadastrados para o(a) " + object + " " + name + ".";
    }

    public static String msgDelete(String object, String name, boolean success) {

        return success
                ? "Log de " + object + " " + name + " deletado com sucesso!"
                : "Ação para deletar log de " + object + " " + name + ", não foi aceito.";
    }

    public static String msgCounts(String object, String name, long count, boolean success) {
        if (success) {
            return count > 0
                    ? "Você tem " + count + " logs registrados de " + object + " " + name + "."
                    : "Você não tem logs registrados de " + object + " " + name + ".";
        } else {
            return "Não foi possivel retornar os logs de " + object + " " + name + ".";
        }
    }
}
