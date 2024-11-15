// PROJETO - ESTRUTURA DE DADOS - TAD 2
// Bruno Germanetti Ramalho - RA 10426491
// Miguel Piñeiro Coratolo Simões - RA 10427085
// 01/11/2024 - 3ºSemestre - Ciências da Computação
// Universidade Presbiteriana Mackenzie - FCI

public class Interpreter {
    private ListaEncadeada<String> listaAssembly;
    private FilaCircular<ParRegValor> filaRegistradores;

    public Interpreter(ListaEncadeada<String> listaAssembly) {
        this.listaAssembly = listaAssembly;
        this.filaRegistradores = new FilaCircular<>(26);
        for (char reg = 'A'; reg <= 'Z'; reg++) {filaRegistradores.enqueue(new ParRegValor(reg));}
    }

    public void run() {
        Node<String> nodeInstrucao = listaAssembly.getHead();

        while (nodeInstrucao != null) {
        	String instrucaoCompleta = nodeInstrucao.getDado().toUpperCase().trim();
            String[] partes = instrucaoCompleta.split(" ", 2);
            String numeroLinha = partes[0];
            String instrucao = partes[1];
            
            // Divide a instrução para obter o comando e os argumentos
            String[] comandoArgs = instrucao.split(" ");
            String comando = comandoArgs[0];
            
            // Verifica se há argumentos e armazena
            String arg1 = (comandoArgs.length > 1) ? comandoArgs[1] : null;
            String arg2 = (comandoArgs.length > 2) ? comandoArgs[2] : null;
            
            try {
                switch (comando) {
                    case "MOV": mov(arg1, arg2);                 break;
                    case "INC": inc(arg1);                       break;
                    case "DEC": dec(arg1);                       break;
                    case "ADD": add(arg1, arg2);                 break;
                    case "SUB": sub(arg1, arg2);                 break;
                    case "MUL": mul(arg1, arg2);                 break;
                    case "DIV": div(arg1, arg2);                 break;
                    case "OUT": out(arg1);                       break;
                    case "JNZ": nodeInstrucao = jnz(nodeInstrucao, arg1, arg2); break;

                    default:
                        System.out.println("Instrução desconhecida: " + comando);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Erro na Linha "+ numeroLinha + ": (" + instrucao + ") -> " + e.getMessage());
                break; 
            }
            
            nodeInstrucao = nodeInstrucao.getProx(); // Avança para a próxima linha
            //System.out.println(filaRegistradores);
        }
    }
    
    // Atualiza o valor de um registrador na fila
    public void atualizarRegistrador(char variavel, int novoValor) {
        try {
            for (int i = 0; i < filaRegistradores.totalElementos(); i++) {
                ParRegValor par = filaRegistradores.dequeue();
                if (par.getVariavel() == variavel) {
                    par.setValor(novoValor); 
                    filaRegistradores.enqueue(par); 
                    return; 
                }
                filaRegistradores.enqueue(par); 
            }
            System.out.println("Registrador " + variavel + " não encontrado.");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar o registrador: " + e.getMessage());
        }
    }

    
    public Integer obterValorRegistrador(char variavel) {
        try {
            // Itera sobre todos os elementos na fila de registradores
            for (int i = 0; i < filaRegistradores.totalElementos(); i++) {
                ParRegValor par = filaRegistradores.dequeue(); // Remove da fila para verificar
                if (par.getVariavel() == variavel) {
                    filaRegistradores.enqueue(par); // Reinsere o elemento na fila
                    return par.getValor(); // Retorna o valor, podendo ser `null`
                }
                filaRegistradores.enqueue(par); // Reinsere o elemento na fila
            }
            System.out.println("Registrador " + variavel + " não encontrado.");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    
    
    public void mov(String arg1, String arg2) throws IllegalArgumentException {
        char x = arg1.charAt(0);

        if (Character.isDigit(x)) {
            throw new IllegalArgumentException("O primeiro argumento deve ser um registrador");
        }

        if (Character.isDigit(arg2.charAt(0))) { // Se `arg2` for um número
            atualizarRegistrador(x, Integer.parseInt(arg2));
        } else { // Se `arg2` for um registrador
            Integer yValue = obterValorRegistrador(arg2.charAt(0));
            if (yValue == null) {
                throw new IllegalArgumentException("O registrador " + arg2 + " não foi inicializado.");
            }
            atualizarRegistrador(x, yValue);
        }
    }


    public void inc(String arg1) {
        Character x = arg1.charAt(0);
        if (Character.isDigit(x) || arg1.length() > 1) {
            throw new IllegalArgumentException("O Primeiro argumento deve ser um registrador");
        } else {
        	if(obterValorRegistrador(x) != null) {
        		atualizarRegistrador(x, obterValorRegistrador(x)+1);
        	} else {
        		throw new IllegalArgumentException("O registrador " + x + " não foi inicializado.");
        	}
        }
    }

    public void dec(String arg1) {
        Character x = arg1.charAt(0);
        if (Character.isDigit(x) || arg1.length() > 1) {
            throw new IllegalArgumentException("O Primeiro argumento deve ser um registrador");
        } else {
        	if(obterValorRegistrador(x) != null) {
        		atualizarRegistrador(x, obterValorRegistrador(x)-1);
        	} else {
        		throw new IllegalArgumentException("O registrador " + x + " não foi inicializado.");
        	}
        }
    }

    public void add(String arg1, String arg2) {
        // Verifica se arg1 é um único caractere e não é um dígito
        if (arg1.length() != 1 || Character.isDigit(arg1.charAt(0))) {
            throw new IllegalArgumentException("O primeiro argumento deve ser um registrador");
        }

        // Obtém o valor do registrador `arg1`
        char x = arg1.charAt(0);
        Integer xValue = obterValorRegistrador(x);
        
        // Verifica se o registrador `arg1` está inicializado
        if (xValue == null) {
            throw new IllegalArgumentException("O registrador " + x + " não foi inicializado.");
        }

        // Verifica se `arg2` é um número inteiro ou um registrador
        int change;
        if (arg2.matches("-?\\d+")) { // `arg2` é um número inteiro se corresponder ao regex
            change = Integer.parseInt(arg2);
        } else if (arg2.length() == 1 && Character.isLetter(arg2.charAt(0))) { // `arg2` é um registrador
            Integer yValue = obterValorRegistrador(arg2.charAt(0));
            if (yValue == null) {
                throw new IllegalArgumentException("O registrador " + arg2 + " não foi inicializado.");
            }
            change = yValue;
        } else {
            throw new IllegalArgumentException("O segundo argumento deve ser um registrador ou um número inteiro.");
        }

        // Atualiza o valor do registrador `x` com a soma
        atualizarRegistrador(x, xValue + change);
    }

    public void sub(String arg1, String arg2) {
        // Verifica se arg1 é um único caractere e não é um dígito
        if (arg1.length() != 1 || Character.isDigit(arg1.charAt(0))) {
            throw new IllegalArgumentException("O primeiro argumento deve ser um registrador");
        }

        // Obtém o valor do registrador `arg1`
        char x = arg1.charAt(0);
        Integer xValue = obterValorRegistrador(x);
        
        // Verifica se o registrador `arg1` está inicializado
        if (xValue == null) {
            throw new IllegalArgumentException("O registrador " + x + " não foi inicializado.");
        }

        // Verifica se `arg2` é um número inteiro ou um registrador
        int change;
        if (arg2.matches("-?\\d+")) { // `arg2` é um número inteiro se corresponder ao regex
            change = Integer.parseInt(arg2);
        } else if (arg2.length() == 1 && Character.isLetter(arg2.charAt(0))) { // `arg2` é um registrador
            Integer yValue = obterValorRegistrador(arg2.charAt(0));
            if (yValue == null) {
                throw new IllegalArgumentException("O registrador " + arg2 + " não foi inicializado.");
            }
            change = yValue;
        } else {
            throw new IllegalArgumentException("O segundo argumento deve ser um registrador ou um número inteiro.");
        }

        // Atualiza o valor do registrador `x` com a subtração
        atualizarRegistrador(x, xValue - change);
    }

    public void mul(String arg1, String arg2) {
        // Verifica se arg1 é um único caractere e não é um dígito
        if (arg1.length() != 1 || Character.isDigit(arg1.charAt(0))) {
            throw new IllegalArgumentException("O primeiro argumento deve ser um registrador");
        }

        // Obtém o valor do registrador `arg1`
        char x = arg1.charAt(0);
        Integer xValue = obterValorRegistrador(x);
        
        // Verifica se o registrador `arg1` está inicializado
        if (xValue == null) {
            throw new IllegalArgumentException("O registrador " + x + " não foi inicializado.");
        }

        // Verifica se `arg2` é um número inteiro ou um registrador
        int change;
        if (arg2.matches("-?\\d+")) { // `arg2` é um número inteiro se corresponder ao regex
            change = Integer.parseInt(arg2);
        } else if (arg2.length() == 1 && Character.isLetter(arg2.charAt(0))) { // `arg2` é um registrador
            Integer yValue = obterValorRegistrador(arg2.charAt(0));
            if (yValue == null) {
                throw new IllegalArgumentException("O registrador " + arg2 + " não foi inicializado.");
            }
            change = yValue;
        } else {
            throw new IllegalArgumentException("O segundo argumento deve ser um registrador ou um número inteiro.");
        }

        // Atualiza o valor do registrador `x` com a multiplicação
        atualizarRegistrador(x, xValue * change);
    }

    public void div(String arg1, String arg2) {
        // Verifica se arg1 é um único caractere e não é um dígito
        if (arg1.length() != 1 || Character.isDigit(arg1.charAt(0))) {
            throw new IllegalArgumentException("O primeiro argumento deve ser um registrador");
        }

        // Obtém o valor do registrador `arg1`
        char x = arg1.charAt(0);
        Integer xValue = obterValorRegistrador(x);
        
        // Verifica se o registrador `arg1` está inicializado
        if (xValue == null) {
            throw new IllegalArgumentException("O registrador " + x + " não foi inicializado.");
        }

        // Verifica se `arg2` é um número inteiro ou um registrador
        int divisor;
        if (arg2.matches("-?\\d+")) { // `arg2` é um número inteiro se corresponder ao regex
            divisor = Integer.parseInt(arg2);
            if (divisor == 0) {
                throw new IllegalArgumentException("Divisão por zero");
            }
        } else if (arg2.length() == 1 && Character.isLetter(arg2.charAt(0))) { // `arg2` é um registrador
            Integer yValue = obterValorRegistrador(arg2.charAt(0));
            if (yValue == null) {
                throw new IllegalArgumentException("O registrador " + arg2 + " não foi inicializado.");
            }
            if (yValue == 0) {
                throw new IllegalArgumentException("Divisão por zero");
            }
            divisor = yValue;
        } else {
            throw new IllegalArgumentException("O segundo argumento deve ser um registrador ou um número inteiro.");
        }

        // Atualiza o valor do registrador `x` com a divisão
        atualizarRegistrador(x, xValue / divisor);
    }


    public Node<String> jnz(Node<String> nodeInstrucao, String arg1, String arg2) {
        // Obtém o valor de arg1
        Integer valor;
        if (Character.isDigit(arg1.charAt(0))) {
            valor = Integer.parseInt(arg1);
        } else {
            valor = obterValorRegistrador(arg1.charAt(0));
        }
        
        int linhaBuscada;
        if (Character.isDigit(arg2.charAt(0))) {
        	 linhaBuscada = Integer.parseInt(arg2);
        } else {
        	linhaBuscada = obterValorRegistrador(arg2.charAt(0));
        }
        // Se o valor for diferente de zero, realizamos o salto
        if (valor != 0) {
            Node<String> nodeAtual = listaAssembly.getHead();
            Node<String> nodeAnterior = null; // Variável para armazenar o nó anterior

            // Navega pela lista encadeada
            
            while (nodeAtual != null) {
                String linha = nodeAtual.getDado().trim();
                // Verifica se a linha atual é igual a arg2
                if (linha.startsWith(Integer.toString(linhaBuscada) + " ")) {
                    // Retorna o nó anterior ou o nó atual se for o primeiro
                    return nodeAnterior != null ? nodeAnterior : nodeAtual; 
                } 
                // Atualiza o nó anterior antes de avançar
                nodeAnterior = nodeAtual;
                nodeAtual = nodeAtual.getProx();
            }
            throw new IllegalArgumentException("Rótulo não encontrado: " + arg2); // Mensagem de erro se não encontrar o rótulo
        }

        return nodeInstrucao; // Retorna o nó atual se o salto não ocorrer
    }

    public void out(String arg1) {
    	Character x = arg1.charAt(0);
        if (Character.isDigit(x) || arg1.length() > 1) {
            throw new IllegalArgumentException("O Primeiro argumento deve ser um registrador");
        } else {
        	System.out.println(obterValorRegistrador(x));
        }
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}