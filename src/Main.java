// PROJETO - ESTRUTURA DE DADOS - TAD 2
// Bruno Germanetti Ramalho - RA 10426491
// Miguel Piñeiro Coratolo Simões - RA 10427085
// 01/11/2024 - 3ºSemestre - Ciências da Computação
// Universidade Presbiteriana Mackenzie - FCI
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.lang.Thread;

public class Main {
    private static boolean unsavedChanges = false;
	private static String currentFile = null;
	private static Scanner scanner = new Scanner(System.in);
	private static ListaEncadeada<String> listaAssembly = new ListaEncadeada<>();
	public static void main(String[] args) {
		
		// Bugs: Pode inserir linhas negativas ainda
		
		// REPL Loop
		while (true) {
			System.out.print("> ");
			String comando = scanner.nextLine().trim();
			String[] partes = comando.split(" ", 2); 
			String instrucaoComando = partes[0].toUpperCase(); 
			String argumentos = (partes.length > 1) ? partes[1] : "";
			
			switch (instrucaoComando) {
				case "LOAD": loadFile(argumentos);   break;
				case "LIST": listCode();             break;
                case "RUN":	 runCode();              break;
                case "INS":  insertLine(argumentos); break;
                case "DEL":  deleteLine(argumentos); break;
                case "SAVE": saveFile(argumentos);   break;
                case "HELP": printHelp();            break;
                case "EXIT": exit();                 break;
                default:
                    System.out.println("Comando não reconhecido.\nEscreva \"HELP\" para visualizar a lista de comandos");
                    break;
			}	
		}	
	}
	
	private static void loadFile(String arguments) {
	    // Check if the arguments are empty
	    if (arguments == null || arguments.isEmpty()) {
	        System.out.println("Comando inválido. Use: LOAD <ARQUIVO.ED1>");
	        return;
	    }
	    
	    String fileName = arguments;

	    // Check if there are unsaved changes before loading a new file
	    if (currentFile != null && unsavedChanges) {
	        System.out.print("Existem alterações não salvas. Deseja salvar antes de carregar outro arquivo? (S/N): ");
	        String response = scanner.nextLine().trim();
	        
	        // Ensure valid input (only 'S' or 'N')
	        if (response.equalsIgnoreCase("S")) {
	            saveFile(arguments);
	            // No need for sleep; handle file loading properly
	        } else if (!response.equalsIgnoreCase("N")) {
	            System.out.println("Resposta inválida. Nenhuma ação será tomada.");
	            return; // Exit if invalid response
	        }
	    }

	    // Check if the file exists before attempting to load
	    File arquivo = new File(fileName);
	    if (!arquivo.exists()) {
	        System.out.println("Erro: O arquivo " + fileName + " não foi encontrado.");
	        return;
	    }
	    
	    // Attempt to load the file into the linked list
	    try (Scanner input = new Scanner(arquivo)) {  // Use try-with-resources for automatic resource management
	        listaAssembly.clear();  // Clear the list before loading new content
	        System.out.println(listaAssembly);
	        while (input.hasNextLine()) {
	            String linha = input.nextLine();
	            listaAssembly.addLast(linha);  // Add line to linked list
	        }
	        
	        currentFile = fileName;
	        unsavedChanges = false;
	        System.out.println("Arquivo carregado com sucesso: " + fileName);
	        System.out.println(listaAssembly);

	    } catch (IOException e) {
	        System.out.println("Erro ao carregar o arquivo: " + e.getMessage());
	    }
	}


	
    private static void listCode() {
        
        // Caso não tenha um arquivo carregado
        if (listaAssembly.isEmpty()) {
            System.out.println("Nenhum código carregado na memória.");
            return;
        }
        
        int currentLine = 1;
        Node<String> pAnda = listaAssembly.getHead();
        
        System.out.println("Código-fonte: ");
        while (pAnda != null) {
            System.out.println(pAnda.getDado()); // Exibe o número da linha e o conteúdo
            pAnda = pAnda.getProx(); // Avança para o próximo nó
            currentLine++; // Incrementa o contador de linhas
            
            // Exibe 20 linhas e pausa a exibição
            if ((currentLine - 1) % 20 == 0) {
                System.out.println("Pressione Enter para continuar...");
                new Scanner(System.in).nextLine(); // Espera o usuário pressionar Enter       
            }
        }
    }

    
    
    private static void runCode() {        
        if (listaAssembly.isEmpty()) {
            System.out.println("Nenhum código carregado na memória.");
            return;
        }
        
        Interpreter interpreter = new Interpreter(listaAssembly);
        interpreter.run();
    }

    
    private static void insertLine(String arguments) {
        String[] insParts = arguments.split(" ", 2);
        int lineNumber = Integer.parseInt(insParts[0]);

        // Verificações
        if (insParts.length < 2) {
            System.out.println("Comando inválido. Use: INS <LINHA> <INSTRUÇÃO>");
            return;
        }
        
        if (lineNumber < 0) {
        	System.out.println("Comando inválido. A linha não pode ser negativa.");
        	return;
        }

        try {
            String instruction = insParts[1];
            
            // Encontra posição
            Node<String> pAnda = listaAssembly.getHead();
            Node<String> pAnt = null;
            boolean exists = false;

            while (pAnda != null) {
            	int currentLineNumber = Integer.parseInt(pAnda.getDado().split(" ")[0]); // Pega o número da linha
                if (currentLineNumber == lineNumber) {
                    exists = true; // Linha já existe
                    break;
                }
                if (currentLineNumber > lineNumber) {
                    break;
                }
                pAnt = pAnda; 
                pAnda = pAnda.getProx();
            
            }
            
            String newLine = lineNumber + " " + instruction;
            Node<String> newNode = new Node<>(newLine, null);
            if (exists) {
                System.out.print("A linha " + lineNumber + " já existe. Deseja sobrescrever? (S/N): ");
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("S")) {
                    return; // Não sobrescreve
                }
                // Se o usuário optar por sobrescrever, podemos remover a linha existente antes de inserir a nova
                listaAssembly.remove(pAnda.getDado()); 
            }

            // Insere na cabeça
            if (listaAssembly.isEmpty() || lineNumber < Integer.parseInt(listaAssembly.getHead().getDado().split(" ")[0])) {
                newNode.setProx(listaAssembly.getHead());
                listaAssembly.insertHead(newLine);
            } 
            // Insere na cauda
            else if (pAnda == null) {
                pAnt.setProx(newNode); 
            } 
            // Insere nas demais posições
            else {
            	if (exists) {
            		newNode.setProx(pAnda.getProx());
            	} else {
            		newNode.setProx(pAnda);
            		}
                if (pAnt != null) {
                    pAnt.setProx(newNode); 
                }
            }
            
            System.out.println("Linha inserida: " + newLine);
            unsavedChanges = true;
            
        } catch (NumberFormatException e) {
            System.out.println("Número de linha inválido.");
        }
    }

    
    private static void deleteLine(String arguments) {
        String[] delParts = arguments.split(" ",2);
        try {
            if (delParts.length == 1) {
                int lineNumber = Integer.parseInt(delParts[0]);
                
                Node<String> pAnda = listaAssembly.getHead();
                Node<String> pAnt = null; 
                boolean found = false;

                // Procura a linha
                String instruction = "";
                while (pAnda != null) {
                    int currentLineNumber = Integer.parseInt(pAnda.getDado().split(" ")[0]); // Pega o número da linha
                    instruction = pAnda.getDado().split(" ", 2)[1];
                    if (currentLineNumber == lineNumber) {
                        found = true;
                        break;
                    }
                    pAnt = pAnda; 
                    pAnda = pAnda.getProx();
                }

                // Se a linha foi encontrada, removê-la
                if (found) {
                    if (pAnt == null) {
                        // Caso a linha a ser removida seja a cabeça
                        listaAssembly.setHead(pAnda.getProx());
                    } else {
                        pAnt.setProx(pAnda.getProx());
                    }
                    System.out.println("Linha " + lineNumber + " removida com sucesso. " + "(" + instruction + ")");
                    unsavedChanges = true;
                } else {
                    System.out.println("Linha " + lineNumber + " não encontrada.");
                }

            } else if (delParts.length == 2) {
                int startLine = Integer.parseInt(delParts[0]);
                int endLine = Integer.parseInt(delParts[1]);
                
                // Verificações
                if (startLine > endLine) {
                	System.out.println("Comando Inválido. O segundo argumento deve ser maior que o primeiro.");
                	return;
                }
                
                
                
                // Remove as linhas no intervalo especificado
                Node<String> pAnda = listaAssembly.getHead();
                Node<String> pAnt = null; // O nó anterior para atualizar o ponteiro corretamente

                while (pAnda != null) {
                    int currentLineNumber = Integer.parseInt(pAnda.getDado().split(" ")[0]);
                    String currentCommand = pAnda.getDado().split(" ", 2)[1];
                    // Verifica se a linha atual está no intervalo
                    if (currentLineNumber >= startLine && currentLineNumber <= endLine) {
                        if (pAnt == null) {
                            // Caso a linha a ser removida seja a cabeça
                            listaAssembly.setHead(pAnda.getProx());
                            pAnda = listaAssembly.getHead(); 
                        } else {
                            pAnt.setProx(pAnda.getProx());
                            pAnda = pAnt.getProx();
                        }
                        System.out.println("Linha " + currentLineNumber + " removida com sucesso. " + "(" + currentCommand + ")");
                        unsavedChanges = true;
                    } else {
                        pAnt = pAnda;
                        pAnda = pAnda.getProx();
                    }
                }
            } else {
                System.out.println("Comando inválido. Use: DEL <LINHA> ou DEL <LINHA_I> <LINHA_F>");
            }
        } catch (NumberFormatException e) {
            System.out.println("Número de linha inválido.");
        }
    }

    
    private static void saveFile(String arguments) {
        String fileName = null;
        
        // If no argument is provided and a file is already loaded, save the current file
        if (arguments == null || arguments.isEmpty()) {
            if (currentFile != null) {
                fileName = currentFile;
            } else {
                System.out.println("Nenhum arquivo carregado. Use LOAD <ARQUIVO.ED1> para especificar um arquivo.");
                return;
            }
        } 
        // If an argument is provided, save as the specified file
        else {
            fileName = arguments;
        }

        File arquivo = new File(fileName);
        
        // Check if the directory exists; if not, create it (if it's a new file path)
        File parentDir = arquivo.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Create the directory if it doesn't exist
        }

        // Use try-with-resources to automatically close the writer
        try (PrintWriter writer = new PrintWriter(arquivo)) {
            // Save each line from the linked list to the file
            Node<String> currentNode = listaAssembly.getHead();
            System.out.println(listaAssembly);
            while (currentNode != null) {
                writer.println(currentNode.getDado());
                currentNode = currentNode.getProx();
            }
            
            // After saving, update the current file and reset the unsaved changes flag
            System.out.println("Arquivo salvo com sucesso: " + arquivo.getAbsolutePath());
            currentFile = arquivo.getName();  // Update the current file name
            unsavedChanges = false;  // Reset the unsaved changes flag
            
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }


    
    private static void printHelp() {
        System.out.println("Comandos disponíveis:");
        System.out.println("LOAD <ARQUIVO.ED1>  - Carrega um arquivo para a lista encadeada.");
        System.out.println("LIST                 - Exibe o código-fonte carregado.");
        System.out.println("RUN                  - Executa o código carregado.");
        System.out.println("INS <LINHA> <INSTRUÇÃO> - Insere uma nova linha de instrução na posição especificada.");
        System.out.println("DEL <LINHA>         - Remove a linha especificada.");
        System.out.println("DEL <LINHA_I> <LINHA_F> - Remove um intervalo de linhas.");
        System.out.println("SAVE [<ARQUIVO.ED1>] - Salva o código atual. Se <ARQUIVO.ED1> não for especificado, salva o arquivo atual.");
        System.out.println("HELP                 - Exibe esta lista de comandos.");
        System.out.println("EXIT                 - Encerra o programa.");
    }
    
    
    private static void exit() {
        if (unsavedChanges) {
            System.out.print("Existem alterações não salvas. Deseja salvar antes de sair? (S/N): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("S")) {
                saveFile("");
            }
        }
        System.out.println("Saindo do programa. Até logo!");
        scanner.close(); // Fecha o scanner
        System.exit(0); // Encerra o programa
    }
}
