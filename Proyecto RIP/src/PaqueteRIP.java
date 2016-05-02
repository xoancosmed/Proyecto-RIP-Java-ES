import java.math.BigInteger;
import java.net.InetAddress;

public class PaqueteRIP {
	
	private int comando; // 1 -> Solicitud, 2 -> Respuesta
	private int version; // Versión de RIP (1 o 2)
	private String ip;
	private int metrica;
	
	private byte[] crearPaqueteRIP () {
		
		byte[] paquete = new byte[25];
		
		paquete[0] = BigInteger.valueOf(comando).toByteArray()[0]; // Comando
		paquete[1] = BigInteger.valueOf(version).toByteArray()[0]; // Versión
		
		paquete[4] = 0; // address-family-idenfifier
		paquete[5] = 0; // """""""""""""""""""""""""
		
		byte[] ipBytes = convertirIp(ip);
		paquete[8] = ipBytes[0];
		paquete[9] = ipBytes[1];
		paquete[10] = ipBytes[2];
		paquete[11] = ipBytes[3];
		
		paquete[20] = BigInteger.valueOf(metrica).toByteArray()[0];
		
		return paquete;
		
	}
	
	private byte[] convertirIp (String ipString) {
		
		byte[] ipBytes = null;
		
		try {
			ipBytes = InetAddress.getByName(ip).getAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		
		return ipBytes;
		
	}
	
	private static String convertirMascara (int longitud) {
		
		long bits = 0xffffffff ^ (1 << 32 - longitud) - 1;
		String mascara = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

		return mascara;
		
	}

}
