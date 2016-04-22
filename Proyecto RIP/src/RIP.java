import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class RIP {

	public static void main (String[] args) {
		
		// OBTENEMOS LA IP Y PUERTO DE LOS PARÁMETROS DE ENTRADA
		
		String ip = "";
		int puerto = 0;
		
		if (args.length == 1) { // Si recibimos un parámetro ...
			
			String ipYpuerto = args[0];
			
			if(ipYpuerto.contains(":")) { // ... si contiene ":" es IP:Puerto
				
				String[] argsSeparados = ipYpuerto.split(":");
				ip = argsSeparados[0];
				
				try {
					puerto = Integer.parseInt(argsSeparados[1]);
				} catch (NumberFormatException ex) {
					System.out.println("Parámetro de entrada incorrecto");
					System.exit(-1);
				}
				
			} else { // ... sino, el parámetro es sólo la IP
				
				ip = ipYpuerto;
				puerto = 5512;
				
			}
			
		} else if (args.length == 0) { // Si no recibimos ningún parámetro ...
			
			puerto = 5512; // ... se usan los valores por defecto
			
			// Obtenemos la IP de la interfaz eth0
			
			try {
				
				NetworkInterface interfaz = NetworkInterface.getByName("eth0"); // "Seleccionamos" la interfaz
				Enumeration<InetAddress> listaIPs = interfaz.getInetAddresses(); // Obtenemos la lista de IPs
				
				while (listaIPs.hasMoreElements()) { // Buscamos la IP deseada
					
					InetAddress posibleIP = listaIPs.nextElement();
					byte posibleIPb[] = posibleIP.getAddress();
					
					if (posibleIPb.length == 4 && posibleIPb[0] != 127) {
						
						ip = posibleIP.getCanonicalHostName();  // Obtenemos la IP
						// ip = posibleIP.getCanonicalHostName();
						
						break;
					}
					
				}
				
			} catch (Exception ex) {
				
				System.out.println("Imposible obtener la IP de la interfaz \"eth0\".");
				System.exit(-1);
				
			}
			
		} else { // Si no es ninguno de esos casos, el número de parámetros es incorrecto
			
			System.out.println("Número de parámetros incorrecto");
			System.exit(-1);
			
		}
		
		
		
		System.out.println("IP: " + ip);
		System.out.println("Puerto: " + puerto);
		
		
		
	}
	
}
