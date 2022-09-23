package br.senai.sc.livros.model.dao;

import br.senai.sc.livros.model.entities.*;
import br.senai.sc.livros.model.factory.ConexaoFactory;
import br.senai.sc.livros.model.factory.StatusFactory;

import javax.swing.*;
import java.sql.*;
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

// aqui tinha a verificação se o livro existia
    public boolean inserir(Livro livro){
        String sql = "insert into livros(isbn, titulo, status, qtdPaginas, AUTOR_cpf) values(?,?,?,?,?)";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setInt(1, livro.getISBN());
            prtm.setString(2, livro.getTitulo());
            prtm.setInt(3, Status.AGUARDANDO_REVISAO.ordinal());
            prtm.setInt(4, livro.getQntdPaginas());
            prtm.setString(5, livro.getAutor().getCPF());
            try{
                prtm.execute();
                JOptionPane.showMessageDialog(null,"livro cadastrado com sucesso!");
                return true;
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("Erro na execução do comando SQL!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL!");
        }
    }

    public void remover(Livro livro){
        String sql = "delete livros where isbn = ?";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setInt(1, livro.getISBN());
            try (ResultSet resultSet = prtm.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("livro removido com sucesso!");
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro na execução do comando SQL!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL!");
        }
        throw new RuntimeException("Algo deu ruim!");
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
    public Collection<Livro> selecionarPorAutor(Pessoa pessoa){
        Collection<Livro> livros = new ArrayList();
        String sql = "select * from livros where AUTOR_cpf = ?";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setString(1, pessoa.getCPF());
            try (ResultSet resultSet = prtm.executeQuery()) {
                while (resultSet.next()) {
                    livros.add(extrairObjetoLivro(resultSet));
                }
                return livros;
            } catch (Exception e) {
                throw new RuntimeException("Erro na execução do comando SQL! selecionarPorAutor");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL! selecionarPorAutor");
        }
    }

    public Livro extrairObjetoLivro(ResultSet resultSet) throws SQLException {
        String cpf = resultSet.getString("AUTOR_cpf");
        PessoaDAO pessoaDAO = new PessoaDAO();
        Autor autor = (Autor) pessoaDAO.selecionarPorCPF(cpf);
        return new Livro(
                autor,
                resultSet.getString("titulo"),
                StatusFactory.getStatus(resultSet.getInt("status")),
                resultSet.getInt("qtdPaginas"),
                resultSet.getInt("isbn")
        );
    }

    public void atualizar(int isbn, Livro livroAtualizado){
        String sql = "update livros set titulo = ?, status = ?, qtdPaginas = ? where isbn = ?";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setInt(4, livroAtualizado.getISBN());
            prtm.setString(1, livroAtualizado.getTitulo());
            prtm.setInt(2, livroAtualizado.getStatus().ordinal());
            prtm.setInt(3, livroAtualizado.getQntdPaginas());
            try{
                prtm.execute();
                JOptionPane.showMessageDialog(null,"livro editado com sucesso!");
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("Erro na execução do comando SQL!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL!");
        }
    }

    public LivroDAO(){
        this.conn = new ConexaoFactory().connectDB();
    }

    public Collection<Livro> getAllLivros(){
        Collection<Livro> livros = new ArrayList();
        String sql = "select * from livros";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            try (ResultSet resultSet = prtm.executeQuery()) {
                while (resultSet.next()) {
                    livros.add(extrairObjetoLivro(resultSet));
                }
                return livros;
            } catch (Exception e) {
                throw new RuntimeException("Erro na execução do comando SQL! getAllLivros");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL! getAllLivros");
        }
    };

    public Collection<Livro> selecionarPorStatus(Status status){
        Collection<Livro> livros = new ArrayList();
        String sql = "select * from livros where status = ?";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setInt(1, status.ordinal());
            try (ResultSet resultSet = prtm.executeQuery()) {
                while (resultSet.next()) {
                    livros.add(extrairObjetoLivro(resultSet));
                }
                return livros;
            } catch (Exception e) {
                throw new RuntimeException("Erro na execução do comando SQL! selecionarPorStatus");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL! selecionarPorStatus");
        }
    }

    public Collection<Livro> selecionarAtividadesAutor(Pessoa pessoa){
        Collection<Livro> livros = new ArrayList();
        String sql = "select * from livros where AUTOR_cpf = ? and status = ?;";
        try (PreparedStatement prtm = conn.prepareStatement(sql)) {
            prtm.setString(1, pessoa.getCPF());
            prtm.setInt(2, Status.AGUARDANDO_EDICAO.ordinal());
            try (ResultSet resultSet = prtm.executeQuery()) {
                while (resultSet.next()) {
                    livros.add(extrairObjetoLivro(resultSet));
                }
                if(!livros.isEmpty()){
                    return livros;
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro na execução do comando SQL! selecionarAtividadesAutor");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na preparação do comando SQL! selecionarAtividadesAutor");
        }
        return livros;
    }
}
