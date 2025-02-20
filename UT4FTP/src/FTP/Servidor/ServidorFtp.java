package FTP.Servidor;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServidorFtp {
	protected static String dirRoot = "C:\\Users\\elija\\Desktop\\ServidorFTP";
	protected static final String FILES_DIR = "files";
	protected static final String USERS_DIR = FILES_DIR + File.separator + "users";
	protected static final String USERS_FILE = USERS_DIR + File.separator + "users.txt";
	
    private static void checkDirs() {
		File dir = new File(FILES_DIR);
		if (!dir.exists()) {
			System.out.println("\nERROR CRÍTICO: La carpeta 'files' no existe en la raiz del programa");
			return;
		}
		
		File file = new File(USERS_FILE);
		if (!file.exists()) {
			System.out.println("\nERROR CRÍTICO: El fichero 'users.txt' no se encuentra en la carpeta 'file'");
			return;
		}
    }
	
    private static void setRoot(Scanner sc) {
    	String input;
    	File file;
    	
        do {        	
        	do {
            	System.out.println("\nIntroduzca el directorio raiz del servidor FTP");
            	System.out.print(":> ");
        		
        		input = sc.nextLine();
        		if (input.isBlank()) {
        			System.out.println("\nLa dirección root del servidor no puede estar vacía");
        			input = null;	
        		}
        	} while (input == null);

        	dirRoot = input;
        	file = new File(dirRoot);
        	if (!file.exists()) 
        		System.out.println("\nLa dirección root proporcionada para el servidor no existe, pruebe de nuevo.");	
        	
        } while (!file.exists());
    }
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService execute = null;
        Scanner sc = new Scanner(System.in);
        int controlPort = 21; 

        
        System.out.println(" _____                 _     _              _____ _____ _____");
		System.out.println("/  ___|               (_)   | |            |  ___|_   _| ___ \\");
		System.out.println("\\ `--.  ___ _ ____   ___  __| | ___  _ __  | |_    | | | |_/ /");
		System.out.println(" `--. \\/ _ \\ '__\\ \\ / / |/ _` |/ _ \\| '__| |  _|   | | |  __/ ");
		System.out.println("/\\__/ /  __/ |   \\ V /| | (_| | (_) | |    | |     | | | |    ");
		System.out.println("\\____/ \\___|_|    \\_/ |_|\\__,_|\\___/|_|    \\_|     \\_/ \\_|    ");
		System.out.println("\nBy Eduardo Díaz");
		
		checkDirs();
		setRoot(sc);
        
        execute = Executors.newCachedThreadPool();
       
        try (ServerSocket server = new ServerSocket(controlPort)) {
            System.out.println("\nServidor FTP iniciado en el puerto de control " + controlPort);
            
            while (true) {
                Socket client = server.accept();
                execute.execute(new Thread(new HandlerClientFtp(client)));
            }

        } catch (IOException e) {
            System.err.println("\nError en el servidor: " + e.getMessage());
        } 
    }
}