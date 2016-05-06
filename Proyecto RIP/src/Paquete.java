import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class Paquete {
	
	private ArrayList<Byte> paquete;
	private int numEntradas = 0;
	private boolean password = false;
	
	public Paquete () {
		
		añadirCabecera();
		
	}
	
	private void añadirCabecera () {
		
		paquete = new ArrayList<Byte>();
		
		paquete.add((byte) 0x0); // Comando
		paquete.add(BigInteger.valueOf(0x2).toByteArray()[0]);
		
		paquete.add((byte) 0x0); // Version
		paquete.add(BigInteger.valueOf(0x2).toByteArray()[0]);
		
		paquete.add((byte) 0x0);
		paquete.add((byte) 0x0);
		paquete.add((byte) 0x0);
		paquete.add((byte) 0x0);
		
	}
	
	public int añadirEntrada (RIPv2 entrada) {
		
		if ((password == false) && (numEntradas >= 25)) {
			
			return -1; // Ya no entran más
			
		} else if ((password == true) && (numEntradas >= 24)) {
			
			return -1; // Ya no entran más
			
		}
		
		paquete.addAll(Arrays.asList(entrada.obtener())); // Añadimos la entrada
		numEntradas++;
		
		return 0;
		
	}
	
	public byte[] obtener() {
		
		byte[] paqueteBytes = new byte[paquete.size()];
		
		for (int i = 0; i < paquete.size(); i++)
			paqueteBytes[i] = paquete.get(i);
		
		return paqueteBytes;
		
	}
	
	public class RIPv2 {
		
		private String ip;
		private String mascara;
		private int coste;
		
		public RIPv2(String ip, String mascara, int coste) {
			
			this.ip = ip;
			this.mascara = mascara;
			this.coste = coste;
			
		}
		
		public Byte[] obtener() {
			
			Byte[] paquete = new Byte[25];
			
			paquete[0] = (byte) 0x0;
			paquete[1] = BigInteger.valueOf(2).toByteArray()[0];
			paquete[2] = (byte) 0x0;
			paquete[3] = (byte) 0x0;
			
			byte[] ipBytes = convertirIp(ip);
			paquete[4] = ipBytes[0];
			paquete[5] = ipBytes[1];
			paquete[6] = ipBytes[2];
			paquete[7] = ipBytes[3];
			
			byte[] mascaraBytes = convertirMascara(mascara);
			paquete[8] = mascaraBytes[0];
			paquete[9] = mascaraBytes[1];
			paquete[10] = mascaraBytes[2];
			paquete[11] = mascaraBytes[3];
			
			paquete[12] = (byte) 0x0;
			paquete[13] = (byte) 0x0;
			paquete[14] = (byte) 0x0;
			paquete[15] = (byte) 0x0;
			
			byte[] costeBytes = BigInteger.valueOf(coste).toByteArray();
			if (costeBytes.length >= 4) paquete[16] = costeBytes[3];
			else paquete[16] = (byte) 0x0;
			if (costeBytes.length >= 3) paquete[17] = costeBytes[2];
			else paquete[17] = (byte) 0x0;
			if (costeBytes.length >= 2) paquete[18] = costeBytes[1];
			else paquete[18] = (byte) 0x0;
			if (costeBytes.length >= 1) paquete[19] = costeBytes[0];
			else paquete[19] = (byte) 0x0;
			
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
		
		private String convertirLongitudAMascara (int longitud) {
			
			long bits = 0xffffffff ^ (1 << 32 - longitud) - 1;
			String mascara = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

			return mascara;
			
		}
		
		private byte[] convertirMascara (String mascara) {
			
			return convertirIp (mascara);
			
		}
		
	}
	
}
