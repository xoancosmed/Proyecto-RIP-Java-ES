import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/* ***************** */
/* **** Paquete **** */
/* ***************** */

public class Paquete {
	
	// ATRIBUTOS
	
	private ArrayList<Byte> paquete = new ArrayList<Byte>();
	
	private ArrayList<RIPv2> entradasRIPv2 = new ArrayList<RIPv2>(); // TODO BORRAR
	private int numEntradas = 0;
	
	private int comando = 0x02;
	private int version = 0x02;
	private String password = "";
	private boolean hasPassword = false;
	
	// CONSTRUCTOR
	
	public Paquete () {
		
		añadirCabecera();
		
	}
	
	public Paquete (String password) {
		
		this.password = password;
		this.hasPassword = true;
		
		añadirCabecera();
		añadirClave();
		
	}
	
	// MÉTODOS
	
	private void añadirCabecera () {
		
		paquete = new ArrayList<Byte>();
		
		paquete.add(BigInteger.valueOf(comando).toByteArray()[0]); // Comando
		
		paquete.add(BigInteger.valueOf(version).toByteArray()[0]); // Version
		
		paquete.add((byte) 0x0);
		paquete.add((byte) 0x0);
		
	}
	
	private void añadirClave () {
		
		paquete.add((byte) 0xFF);
		paquete.add((byte) 0xFF);
		
		paquete.add((byte) 0x00); // Tipo autentificación
		paquete.add((byte) 0x02);
		
		// Introduciomos la contraseña (y rellenamos con 0)
		
		byte[] passwordBytes = password.getBytes(Charset.forName("UTF-8"));
		
		for (int i = 0, j = 0; i < 16; i++) {
			
			if (j >= passwordBytes.length) {
				
				paquete.add((byte) 0);
				
			}
			else {
				
				paquete.add(passwordBytes[j]);
				j++;
				
			}
			
		}
		
	}
	
	public boolean añadirEntrada (RIPv2 entrada) {
		
		if ((hasPassword == false) && (numEntradas >= 25)) {
			
			return false; // Ya no entran más
			
		} else if ((hasPassword == true) && (numEntradas >= 24)) {
			
			return false; // Ya no entran más
			
		}
		
		paquete.addAll(Arrays.asList(entrada.obtener())); // Añadimos la entrada
		
		entradasRIPv2.add(entrada);
		numEntradas++;
		
		return true;
		
	}
	
	public byte[] obtenerPaquete() {
		
		byte[] paqueteBytes = new byte[paquete.size()];
		
		for (int i = 0; i < paquete.size(); i++) 
			paqueteBytes[i] = paquete.get(i);

		return paqueteBytes;
		
	}
	
	public static RIPv2[] obtenerEntradas (byte[] paqueteBytes) {
		
		ArrayList<RIPv2> paquetesRIPv2 = new ArrayList<RIPv2>();
		
		int i;
		
		if ((paqueteBytes[4] == 0xFF) && (paqueteBytes[5] == 0xFF)) {
			
			i = 24;
			
		} else i = 4;
		
		while (i < paqueteBytes.length) {
			
			byte[] paqueteRIPv2 = new byte[20];
			
			for (int j = 0; j < 20; j++) {
				
				paqueteRIPv2[j] = paqueteBytes[i];
				i++;
				
			}
			
			paquetesRIPv2.add(new RIPv2(paqueteRIPv2));
			
		}
		
		RIPv2[] entradas = new RIPv2[paquetesRIPv2.size()];
		
		for (int k = 0; k < paquetesRIPv2.size(); k++)
			entradas[k] = paquetesRIPv2.get(k);
		
		return entradas;
		
	}
	
	public static String obtenerClave (byte[] paqueteBytes) {
		
		String passwordString = null;
		
		if ((paqueteBytes[4] == 0xFF) && (paqueteBytes[5] == 0xFF)) {
			
			byte[] passwordBytes = new byte[16];
			
			for (int i = 0, j = 8; i < 16; i++, j++) 
				passwordBytes[i] = paqueteBytes[j];
			
			passwordString = new String(passwordBytes, StandardCharsets.UTF_8).trim();
			
		}
		
		return passwordString;
		
	}
			
	
	/* *************** */
	/* **** RIPv2 **** */
	/* *************** */
	
	public static class RIPv2 {
		
		// ATRIBUTOS
		
		private String ip;
		private String mascara;
		private int coste;
		
		// CONSTRUCTORES
		
		public RIPv2 (String ip, String mascara, int coste) {
			
			this.ip = ip;
			this.mascara = mascara;
			this.coste = coste;
			
		}
		
		public RIPv2 (String ip, int longitud, int coste) {
			
			this.ip = ip;
			this.mascara = convertirLongitudAMascara(longitud);
			this.coste = coste;
			
		}
		
		public RIPv2 (byte[] paquete) {
			
			byte[] ipBytes = new byte[4];
			ipBytes[0] = paquete[4];
			ipBytes[1] = paquete[5];
			ipBytes[2] = paquete[6];
			ipBytes[3] = paquete[7];
			ip = convertirIp(ipBytes);
			
			byte[] mascaraBytes = new byte[4];
			mascaraBytes[0] = paquete[8];
			mascaraBytes[1] = paquete[9];
			mascaraBytes[2] = paquete[10];
			mascaraBytes[3] = paquete[11];
			mascara = convertirMascara(mascaraBytes);
			
			byte[] costeBytes = new byte[4];
			costeBytes[0] = paquete[16];
			costeBytes[1] = paquete[17];
			costeBytes[2] = paquete[18];
			costeBytes[3] = paquete[19];
			coste = convertirCoste(costeBytes);
			
		}
		
		public RIPv2 (Byte[] paquete) {
			
			byte[] ipBytes = new byte[4];
			ipBytes[0] = paquete[4];
			ipBytes[1] = paquete[5];
			ipBytes[2] = paquete[6];
			ipBytes[3] = paquete[7];
			ip = convertirIp(ipBytes);
			
			byte[] mascaraBytes = new byte[4];
			mascaraBytes[0] = paquete[8];
			mascaraBytes[1] = paquete[9];
			mascaraBytes[2] = paquete[10];
			mascaraBytes[3] = paquete[11];
			mascara = convertirMascara(mascaraBytes);
			
			byte[] costeBytes = new byte[4];
			costeBytes[0] = paquete[16];
			costeBytes[1] = paquete[17];
			costeBytes[2] = paquete[18];
			costeBytes[3] = paquete[19];
			coste = convertirCoste(costeBytes);
			
		}
		
		// MÉTODOS
		
		public Byte[] obtener() {
			
			Byte[] paquete = new Byte[20];
			
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
			
			byte[] costeBytes = convertirCoste(coste);
			paquete[16] = costeBytes[0];
			paquete[17] = costeBytes[1];
			paquete[18] = costeBytes[2];
			paquete[19] = costeBytes[3];
			
			return paquete;
			
		}
		
		private byte[] convertirIp (String ipString) {
			
			byte[] ipBytes = null;
			
			try {
				ipBytes = InetAddress.getByName(ipString).getAddress();
			} catch (java.net.UnknownHostException ex) {
				ex.printStackTrace();
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
			
			return ipBytes;
			
		}
		
		private String convertirIp (byte[] ipBytes) {
			
			String ipString = "";
			int i = 4;
			
			for (byte raw : ipBytes) {
				
				ipString += (raw & 0xFF);
				if (--i > 0) ipString += ".";
				
			}
			
			return ipString;
			
		}
		
		private String convertirLongitudAMascara (int longitud) {
			
			long bits = 0xffffffff ^ (1 << 32 - longitud) - 1;
			String mascara = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

			return mascara;
			
		}
		
		private byte[] convertirMascara (String mascaraString) {
			
			return convertirIp (mascaraString);
			
		}
		
		private String convertirMascara (byte[] mascaraBytes) {
			
			return convertirIp (mascaraBytes);
			
		}
		
		private byte[] convertirCoste (int coste) {
			
			return ByteBuffer.allocate(4).putInt(coste).array();
			
		}
		
		private int convertirCoste (byte[] costeBytes) {
			
			return ByteBuffer.wrap(costeBytes).getInt();
			
		}
		
		public void aumentarCoste () {
			
			coste++;
			
		}
		
		// GETTERS & SETTERS
		
		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getMascara() {
			return mascara;
		}

		public void setMascara(String mascara) {
			this.mascara = mascara;
		}

		public int getCoste() {
			return coste;
		}

		public void setCoste(int coste) {
			this.coste = coste;
		}
		
	}
	
}
