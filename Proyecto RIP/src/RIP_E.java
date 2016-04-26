import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Date;

public class RIP_E {
	
	public static void main2 (String[] args) throws Exception {
		
		int localPrt = 5512; // CAMBIAR
		String ip = "localhost"; // CAMBIAR
		
		InetAddress localIP = InetAddress.getByName(ip);
		
		// Código Estela vvvvv
		
		DatagramSocket ripSocket = new DatagramSocket(localPrt, localIP);
		int socketTimeout = 10000;
		Date initialDate = new Date();
		ripSocket.setSoTimeout(socketTimeout);
		
		while (true) {
			
			try {
				
				byte[] recData = new byte[25];
				DatagramPacket ds = new DatagramPacket(recData, 25);
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
