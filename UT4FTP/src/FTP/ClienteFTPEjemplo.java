package FTP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTPEjemplo {

	public static void main(String[] args) {
		FTPClient ftpClient = new FTPClient();
		
		try {
			// Conectamos al servidor FTP
			ftpClient.connect("ftp.gnu.org");
			if (ftpClient.isConnected()) {
				System.out.println("Conexión exitosa al servidor");
			} else {
				System.out.println("Conexión fallida");
				return;
			}
			
			// Hacemos login (usuario, password)
			boolean login = ftpClient.login("anonymous", "");
			if (login) {
				System.out.println("Login existoso");
			} else {
				System.out.println("Login fallido");
				return;
			}
			
			// Vamos a listar todos los ficheros del servidor
			FTPFile[] files = ftpClient.listFiles();
			for (FTPFile file : files) {
				System.out.println(file.getName());
			}
			
			// Vamos a bajarnos el fichero MISSING-FILES.README
			/*String remoteFile = "/MISSING-FILES.README";
			String localFile = "/Users/edu/MISSING-FILES-MAG.README";
			
			try (FileOutputStream fileOutputStrem = new FileOutputStream(localFile)){
				boolean success = ftpClient.retrieveFile(remoteFile, fileOutputStrem);
				if (success) {
					System.out.println("Fichero descargado con exito");
				} else {
					System.out.println("Error al descargar el fichero: ");
					System.out.println("Codigo e error: " + ftpClient.getReplyCode());
					System.out.println("Mensaje de error: " + ftpClient.getReplyString());
				}
			}*/
			
			// Subir un fichero
			String localFielUpload = "/Users/edu/prueba.txt";
			String remoteFileUpload = "/tmp/prueba.txt";
			try (FileInputStream fileInputStream = new FileInputStream(localFielUpload)) {
				boolean success = ftpClient.storeFile(remoteFileUpload, fileInputStream);
				if (success) {
					System.out.println("Fichero subido con exito");
				} else {
					System.out.println("Error al subir el fichero: ");
					System.out.println("Codigo e error: " + ftpClient.getReplyCode());
					System.out.println("Mensaje de error: " + ftpClient.getReplyString());
				}
			}
			
			// Hacemos logout
			ftpClient.logout();
			
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
