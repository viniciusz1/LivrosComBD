package br.senai.sc.livros.model.dao;

import br.senai.sc.livros.model.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LivroDAO {

    private static Collection<Livro> listaLivros = new HashSet<>();
    private Connection conn;

    static{

        listaLivros.add(new Livro(new Autor("123", "Bernadete", "#@!", "123@321", Genero.FEMININO, "123"),
                "O fogo", Status.AGUARDANDO_REVISAO, 568, 1234));
        listaLivros.add(new Livro(new Autor("123", "Bernadete", "#@!", "autor@", Genero.FEMININO, "123"),
                "A pedra", Status.APROVADO, 346, 2542));
        listaLivros.add(new Livro(new Autor("123", "Bernadete", "#@!", "123@321", Genero.FEMININO, "123"),
                "O Henrique", Status.EM_REVISAO, 467, 4367));
        listaLivros.add(new Livro(new Autor("123", "Bernadete", "#@!", "123@321", Genero.FEMININO, "123"),
                "A Camilly", Status.REPROVADO, 346, 2542));
        listaLivros.add(new Livro(new Autor("123", "Bernadete", "#@!", "123@321", Genero.FEMININO, "123"),
                "O Impiedoso Romárinho", Status.PUBLICADO, 467, 4367));
    }


    public boolean inserir(Livro livro){
        boolean existe = !listaLivros.contains(livro);
        listaLivros.add(livro);
        return existe;
    }

    public void remover(Livro livro){
        listaLivros.remove(livro);
    }

    public Livro selecionar(int isbn){
        String sql = "select * from livros where isbn = ?";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setInt(1, isbn);
            try (ResultSet resultSet = prtm.executeQuery()) {
                if (resultSet.next()) {
                    return extrairObjetoLivro(resultSet);
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro na execução do comando SQL!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL!");
        }
        throw new RuntimeException("Algo deu ruim!");

    }

    public Livro extrairObjetoLivro(ResultSet resultSet) throws SQLException {
        String cpf = resultSet.getString("AUTOR_cpf");
        PessoaDAO pessoaDAO = new PessoaDAO();
        return new Livro(
                (Autor) pessoaDAO.selecionarPorCPF(cpf),
                resultSet.getString("titulo"),
                Status.getStatusCorreto(resultSet.getString("status")),
                resultSet.getInt("qtdPaginas"),
                resultSet.getInt("isbn")
        );
    }

    public void atualizar(int isbn, Livro livroAtualizado){
        for(Livro livro : listaLivros){
            if(livro.getISBN() == isbn){
                listaLivros.remove(livro);
                listaLivros.add(livroAtualizado);
            };
        }

        List<Livro> lista = new ArrayList<>(listaLivros);
        int i = lista.indexOf(selecionar(isbn));
        lista.set(i, livroAtualizado);
        listaLivros.clear();
        listaLivros.addAll(lista);
    }

    public Collection<Livro> getAllLivros(){
    };

    public Collection<Livro> selecionarPorAutor(Pessoa pessoa){
        ArrayList<Livro> livros = new ArrayList();
        System.out.println(pessoa.getCPF());
        String sql = "select * from livros";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {

            System.out.println(pessoa.getCPF());
            prtm.setString(1, pessoa.getCPF());
//            try (ResultSet resultSet = prtm.executeQuery()) {
//                while (resultSet.next()) {
//                    livros.add(extrairObjetoLivro(resultSet));
//                    System.out.println(livros);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException("Erro na execução do comando SQL! selecionarPorAutor");
//            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL! selecionarPorAutor");
        }
        return null;
    }

    public Collection<Livro> selecionarPorStatus(Status status){
        Collection<Livro> livrosStatus = new ArrayList<>();
        for(Livro livro : getAllLivros()){
            if(livro.getStatus().equals(status)){
                livrosStatus.add(livro);
            }
        }
        return livrosStatus;
    }
    public Collection<Livro> selecionarAtividadesAutor(Pessoa pessoa){
        Collection<Livro> livrosAutor = new ArrayList<>();
        for(Livro livro : getAllLivros()){
            if(livro.getAutor() == pessoa && livro.getStatus().equals(Status.AGUARDANDO_EDICAO)){
                livrosAutor.add(livro);
            };
        }
        return livrosAutor;
    }




}
