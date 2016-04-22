import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Date;

public class RIP_E {
	
	public static void main2 (String[] args) {
		
		int localPrt = 5512; // CAMBIAR
		String localIP = "localhost"; // CAMBIAR
		
		// Código Estela vvvvv
		
		DataSocket ripSocket = new DataSocket(localPrt, localIP);
		int socketTimeout = 10000;
		Date initialDate = new Date();
		ripSocket.setSoTimeout(socketTimeout);
		
		while (true) {
			
			try {
				
				byte[] recData = new byte[];
				DatagramSocket ds = new DatagramSocket(recDate, );
				ripSocket.receive(ds);
				Date currentDate = new Date();
				long elapsedTime = currentDate.getTime() - initialDate.getTime();
				ripSocket.setSoTimeout(socketTimeout - (int)elapsedTime);
				
				// Codigo aquí
				
			} catch (SocketTimeoutException e) {
				
				break;
				
			}
			
		}
		
		// Código Estela ^^^^^
		
	}

}
