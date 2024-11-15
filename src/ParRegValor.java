// PROJETO - ESTRUTURA DE DADOS - TAD 2
// Bruno Germanetti Ramalho - RA 10426491
// Miguel Piñeiro Coratolo Simões - RA 10427085
// 01/11/2024 - 3ºSemestre - Ciências da Computação
// Universidade Presbiteriana Mackenzie - FCI

public class ParRegValor {
    private char variavel;
    private Integer valor;

    // Construtor padrão
    public ParRegValor() {
        this.variavel = ' '; // ou qualquer valor padrão que você preferir
        this.valor = 0; // Valor padrão
    }

    // Construtor com parâmetros
    public ParRegValor(char variavel, int valor) {
        this.variavel = variavel;
        this.valor = valor;
    }
    
    // Construtor sem valor
    public ParRegValor(char variavel) {
        this.variavel = variavel;
        this.valor = null;
    }
    

    // Getters
    public char getVariavel() {
        return variavel;
    }

    public int getValor() {
        return valor;
    }

    // Setters
    public void setVariavel(char variavel) {
        this.variavel = variavel;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
    
    @Override
    public String toString() {
        return "{"+ variavel + ", " + valor + '}';
    }
}

