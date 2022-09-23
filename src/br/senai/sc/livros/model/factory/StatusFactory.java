package br.senai.sc.livros.model.factory;

import br.senai.sc.livros.model.entities.Genero;
import br.senai.sc.livros.model.entities.Status;

public class StatusFactory {
    public static Status getStatus(Integer ordinal) {
        switch (ordinal) {
            case 1 -> {
                return Status.AGUARDANDO_REVISAO;
            }
            case 2 -> {
                return Status.EM_REVISAO;
            }
            case 3 -> {
                return Status.APROVADO;
            }
            case 4 -> {
                return Status.AGUARDANDO_EDICAO;
            }
            case 5 -> {
                return Status.REPROVADO;
            }
            default -> {
                return Status.PUBLICADO;
            }
        }
    }
}
