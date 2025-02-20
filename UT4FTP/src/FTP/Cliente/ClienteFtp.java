package FTP.Cliente;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class ClienteFtp {
	
	private static String[] captureHostname(Scanner sc) {
		String hostname = "localhost"; 
		String port = "21";
		String input = null;
		
		System.out.println("\nIntroduce el hostname del servidor FTP (localhost por defecto)");
		System.out.print(":> ");
		input = sc.nextLine().trim();
		if (input != null && !input.isBlank())
			hostname = input;

		System.out.println("\nIntroduce el puerto (puerto 21 por defecto)");
		System.out.print(":> ");
		input = sc.nextLine().trim();
		if (input != null && !input.isBlank())
			port = input;
		
		return new String[] {hostname, port};		
	}
	
	private static String[] inputUserCredentials(Scanner sc) {
		String input = null;
		String user = "";
		String pass = "";
		
		do {
			System.out.println("\nIntroduce el nombre de usuario");
			System.out.print(":> ");
			input = sc.nextLine().trim();
			
			if (input.isEmpty() || input.isBlank()) {
				System.out.println("\nError: El nombre de usuario no puede estar vacío");
				input = null;
			}	
			
		} while (input == null);
		
		user = input;
		
		do {
			System.out.println("\nIntroduce la contraseña");
			System.out.print(":> ");
			input = sc.nextLine().trim();
			
			if (input.isEmpty() || input.isBlank()) {
				System.out.println("Error: La contraseña no puede estar vacía");
				input = null;
			}	
			
		} while (input == null);
		
		pass = input;
		
		return new String[] {user, pass};
	}
	
	private static String selectMode(Scanner sc) {
		String mode = "PASIVE";
		String input = null;
		
		do {
			System.out.println("\nIntroduce el modo de conexión PASSIVE/ACTIVE");
			System.out.print(":> ");
			input = sc.nextLine().trim();
			
			if (input.isEmpty() || input.isBlank()) {
				System.out.println("\nError: El campo no puede estar vacío, elige un modo de conexión: PASSIVE/ACTIVE");
				input = null;
			} else if (!input.toUpperCase().matches("PASSIVE|ACTIVE")) {
				System.out.println("\nError: Debe elegir un modo de conexión CORECTO: PASSIVE/ACTIVE");
				input = null;
			}
			
		} while (input == null);
		
		mode = input.toUpperCase();
		
		return mode;
	}
	
	public static void handlerActiveMode() {
		
	}

	public static void main(String[] args) {
		FTPClient ftpClient;
		String[] hostname;
		int replyCode;
		String[] userCredentials;
		boolean login;
		String mode;
		
		System.out.println("Cliente FTP iniciado");
		
		ftpClient = new FTPClient();
		
		try (Scanner sc = new Scanner(System.in)) {
			hostname = captureHostname(sc);
		
			ftpClient.connect(hostname[0], Integer.parseInt(hostname[1]));
			replyCode = ftpClient.getReplyCode();			
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				throw new IOException("Error de conexión: " + replyCode);
			}
			
			System.out.println("\n" + ftpClient.getReplyString());

			userCredentials = inputUserCredentials(sc);
			login = ftpClient.login(userCredentials[0], userCredentials[1]);
			if (!login) {
				replyCode = ftpClient.getReplyCode();	
				throw new SecurityException("Autenticación fallida: " + replyCode);
			} 
			
			System.out.println("\n" + ftpClient.getReplyString());
			
			mode = selectMode(sc);
			if (mode.equals("PASSIVE")) {
				ftpClient.enterLocalPassiveMode();
				ftpClient.sendCommand("PASV");
				
				System.out.println("\nModo pasivo configurado");  
				
			} else if (mode.equals("ACTIVE")) {
				InetAddress inetAdress = ftpClient.getLocalAddress();
				String hostAddress = inetAdress.getHostAddress().replace(".", ",");
				
		        System.out.println("\nPuerto para datos (ej. 5500 o superior)");  
		        System.out.print(":> ");
		        int dataPort = Integer.parseInt(sc.nextLine());  

		        int p1 = dataPort / 256;  
		        int p2 = dataPort % 256;  

		        String comandoPort = String.format("PORT %s,%d,%d", hostAddress, p1, p2); 
		        
		        ftpClient.enterLocalActiveMode(); 
		        ftpClient.sendCommand(comandoPort); 
		        System.out.println("\nModo activo configurado");  
		        
			} else {
				System.err.println("Error: Modo de conexión elegido desconocido");
			}
			
			ftpClient.disconnect();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}
