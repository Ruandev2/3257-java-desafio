package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.Dados;
import br.com.alura.tabelafipe.model.Modelos;
import br.com.alura.tabelafipe.model.Veiculo;
import br.com.alura.tabelafipe.service.ConsumoApi;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL = "https://parallelum.com.br/fipe/api/v1/";
private void  menu() {
    System.out.println("""
            ***********  OPÇÕES VEICULOS  ************
            * Carro                                  *
            * Moto                                   *
            * Caminhão                               *
            * Sair                                   *  
            *                                        *
            * Digite uma das opções para consulta:   *
            *                                        *
            ****************************************** 
            """);
}
    public void exibeMenu() {
       try{
           menu();
           String opcao;
           do {
               opcao = leitura.nextLine();
               String endereco;
               if (!opcao.equalsIgnoreCase("sair")) {
                   if (opcao.toLowerCase().contains("carr")) {
                       endereco = URL + "carros/marcas";
                   } else if (opcao.toLowerCase().contains("mot")) {
                       endereco = URL + "motos/marcas";
                   } else {
                       endereco = URL + "caminhoes/marcas";
                   }
                   var json = consumo.obterDados(endereco);
                   System.out.println(json);
                   var marcas = conversor.obterLista(json, Dados.class);
                   marcas.stream()
                           .sorted(Comparator.comparing(Dados::codigo))
                           .forEach(System.out::println);

                   System.out.println("Informe o código da marca para consulta: ");
                   var codigoMarca = leitura.nextLine();

                   endereco = endereco + "/" + codigoMarca + "/modelos";
                   json = consumo.obterDados(endereco);
                   var modeloLista = conversor.obterDados(json, Modelos.class);

                   System.out.println("\nModelos dessa marca: ");
                   modeloLista.modelos().stream()
                           .sorted(Comparator.comparing(Dados::codigo))
                           .forEach(System.out::println);

                   System.out.println("\nDigite um trecho do nome do carro a ser buscado");
                   var nomeVeiculo = leitura.nextLine();

                   List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                           .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                           .collect(Collectors.toList());
                   System.out.println("************************************************************************");
                   System.out.println("\nModelos filtrados");
                   modelosFiltrados.forEach(System.out::println);
                   System.out.println("************************************************************************");

                   System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação: ");
                   var codigoModelo = leitura.nextLine();

                   endereco = endereco + "/" + codigoModelo + "/anos";
                   json = consumo.obterDados(endereco);
                   List<Dados> anos = conversor.obterLista(json, Dados.class);
                   List<Veiculo> veiculos = new ArrayList<>();

                   for (int i = 0; i < anos.size(); i++) {
                       var enderecoAnos = endereco + "/" + anos.get(i).codigo();
                       json = consumo.obterDados(enderecoAnos);
                       Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                       veiculos.add(veiculo);
                   }
                   System.out.println("************************************************************************");
                   System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
                   veiculos.forEach(System.out::println);
                   System.out.println("************************************************************************");
               }
           } while (!opcao.equalsIgnoreCase("sair"));
       }catch (IllegalStateException | NullPointerException e) {
           System.out.println("Falha na Execução");
       }
        System.out.println("O programa foi encerrado.");

    }
}
