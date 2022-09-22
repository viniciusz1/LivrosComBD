package br.senai.sc.livros.model.entities;

public enum Genero {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    OUTRO("Outro");
    String nome;
    Genero(String nome){
        this.nome = nome;
    }

    public static Genero getGeneroCorreto(Integer intGenero) {
        for (Genero genero : Genero.values()) {
            if (genero.ordinal() == intGenero) {
                return genero;
            }
        }
        throw new RuntimeException("Gênero não encontrado!");
    }

    private String getNome() {
        return this.nome;
    }
}
