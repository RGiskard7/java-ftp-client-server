package FTP.Servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HandlerClientFtp implements Runnable {
	private Socket controlSocket;
	private ServerSocket dataSocketPasv;
	private Socket dataSocketAct;
	private String conexionMode;
	private String serverClientAddress;
	private BufferedReader in;
	private PrintWriter out;
	private User user;
	private String command;

	public HandlerClientFtp(Socket controlSocket) throws IOException {
		this.controlSocket = controlSocket;
		user = new User();
	}

	private void handlerUserCredentials(String command, PrintWriter out) {
		/*String user = null;
		String pass = null;*/
		
		System.out.println("Comando recibido: " + command);

		if (command.startsWith("USER")) {
			user.setUsername(command.split(" ")[1]);
			sendReply(331, "Username okay, need password");

		} else if (command.startsWith("PASS")) {
			user.setPassword(command.split(" ")[1]);

			if (checkUserCredentials(user.getUsername(), user.getPassword())) {
				sendReply(230, "User logged in, proceed");
			} else {
				sendReply(530, "Authentication failed");
			}
		}
	}
 
	private boolean checkUserCredentials(String usernameInput, String passwordInput) {
		try (FileInputStream inputStream = new FileInputStream(new File(ServidorFtp.USERS_FILE));
				InputStreamReader reader = new InputStreamReader(inputStream);
				BufferedReader buffer = new BufferedReader(reader)) {

			String line;
			while ((line = buffer.readLine()) != null) {
				System.out.println(line);
				String[] credentials = line.split(":");

				if (credentials[0].equals(usernameInput) && credentials[1].equals(passwordInput)) {
					user = new User(credentials[0], credentials[1], UserProfile.valueOf(credentials[2]));
					return true;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void sendReply(int code, String message) {
		out.printf("%d %s%n", code, message);
	}

	@Override
	public void run() {
		System.out.println("\n[SOLICITUD RECIBIDA]");
		System.out.println("\nConexión con el cliente " + controlSocket.getInetAddress());

		try {
			in = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
			out = new PrintWriter(controlSocket.getOutputStream(), true);

			sendReply(220, "Welcome to the FTP server");

			while ((command = in.readLine()) != null) {
				switch (command.split(" ")[0]) {
				case "USER":
					handlerUserCredentials(command, out);
					break;
				case "PASS":
					handlerUserCredentials(command, out);
					break;
				case "PASV":
					System.out.println("Comando recibido: " + command);
					conexionMode = "PASSIVE";
					
					//sendReply(227, "Entering Passive Mode");
					
					break;
				case "PORT":
					System.out.println("Comando recibido: " + command);
					conexionMode = "ACTIVE";
					serverClientAddress = command.split(" ")[1];
					
					//sendReply(502, "Comando no implementado");
					
					break;
				case "LIST":
					break;
				default:
					System.out.println("Comando desconocido recibido: " + command);
					sendReply(502, "Comando no implementado");
					break;
				}
			}

		} catch (IOException e) {
			System.err.println("Error con el cliente: " + e.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				controlSocket.close();
			} catch (IOException e) {
				System.err.println("Error al cerrar la conexión del cliente: " + e.getMessage());
			}
		}
	}
}