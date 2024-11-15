// PROJETO - ESTRUTURA DE DADOS - TAD 2
// Bruno Germanetti Ramalho - RA 10426491
// Miguel Piñeiro Coratolo Simões - RA 10427085
// 01/11/2024 - 3ºSemestre - Ciências da Computação
// Universidade Presbiteriana Mackenzie - FCI

public class Node <T>{
	private T dado; // dado (tipo genérico) a ser armazenado no Node
	private Node<T> prox; // ponteiro para o próximo Node (nó) da lista ligada

	public Node() {
		this(null, null);
	}
	
	public Node(T dado, Node<T> prox) {
		this.dado = dado;
		this.prox = prox;
	}
	public Node<T> getProx() { return prox; };
	public T getDado(){ return dado; };
	public void setProx(Node<T> prox) { this.prox = prox; };
	public void setDado(T dado) { this.dado = dado;	};	
	
	@Override
    public String toString() {
        return "Node{" +
               "dado=" + dado +
               ", prox=" + (prox != null ? "Node@" + Integer.toHexString(System.identityHashCode(prox)) : "null") +
               '}';
    }
}
