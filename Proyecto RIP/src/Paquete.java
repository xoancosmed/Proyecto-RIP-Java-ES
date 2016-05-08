import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/* ***************** */
/* **** Paquete **** */
/* ***************** */

public class Paquete {
	
	// ATRIBUTOS
	
	private ArrayList<Byte> paquete;
	
	private ArrayList<RIPv2> entradasRIPv2;
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
		añadirCifrado();
		
	}
	
	// MÉTODOS
	
	private void añadirCabecera () {
		
		paquete = new ArrayList<Byte>();
		
		paquete.add(BigInteger.valueOf(comando).toByteArray()[0]); // Comando
		
		paquete.add(BigInteger.valueOf(version).toByteArray()[0]); // Version
		
		paquete.add((byte) 0x0);
		paquete.add((byte) 0x0);
		
	}
	
	private void añadirCifrado () {
		
		paquete.add(BigInteger.valueOf(0xFF).toByteArray()[0]);
		paquete.add(BigInteger.valueOf(0xFF).toByteArray()[0]);
		
		paquete.add(BigInteger.valueOf(0x00).toByteArray()[0]); // Tipo autentificación
		paquete.add(BigInteger.valueOf(0x02).toByteArray()[0]);
		
		// TODO PONER CONTRASEÑA VVVVVV
		
		for (int i = 0; i < 16; i++)
			paquete.add((byte) 0);
		
		// XXXX PONER CONTRASEÑA ^^^^^
		
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
			
			byte[] paqueteRIPv2 = new byte[19];
			
			for (int j = 0; j < 19; j++)
				paqueteRIPv2[j] = paqueteBytes[i++];
			
			paquetesRIPv2.add(new RIPv2(paqueteRIPv2));
			
		}
		
		return (RIPv2[]) paquetesRIPv2.toArray();
		
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
			mascaraBytes[0] = paquete[4];
			mascaraBytes[1] = paquete[5];
			mascaraBytes[2] = paquete[6];
			mascaraBytes[3] = paquete[7];
			mascara = convertirMascara(mascaraBytes);
			
			byte[] costeBytes = new byte[4];
			mascaraBytes[0] = paquete[16];
			mascaraBytes[1] = paquete[17];
			mascaraBytes[2] = paquete[18];
			mascaraBytes[3] = paquete[19];
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
			mascaraBytes[0] = paquete[4];
			mascaraBytes[1] = paquete[5];
			mascaraBytes[2] = paquete[6];
			mascaraBytes[3] = paquete[7];
			mascara = convertirMascara(mascaraBytes);
			
			byte[] costeBytes = new byte[4];
			mascaraBytes[0] = paquete[16];
			mascaraBytes[1] = paquete[17];
			mascaraBytes[2] = paquete[18];
			mascaraBytes[3] = paquete[19];
			coste = convertirCoste(costeBytes);
			
		}
		
		// MÉTODOS
		
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
			
			byte[] costeBytes = convertirCoste(coste);
			paquete[16] = costeBytes[0];
			paquete[16] = costeBytes[1];
			paquete[16] = costeBytes[2];
			paquete[16] = costeBytes[3];
			
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
		
		private String convertirIp (byte[] ipBytes) {
			
			String ipString = null;
			
			try {
				ip = InetAddress.getByAddress(ipBytes).getCanonicalHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
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
