import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class RIP {

	public static void main (String[] args) throws UnknownHostException, InterruptedException {
		
		// OBTENEMOS LA IP Y PUERTO DE LOS PARÃ�METROS DE ENTRADA
		
		String ip = "";
		int puerto = 0;
		
		if (args.length == 1) { // Si recibimos un parÃ¡metro ...
			
			String ipYpuerto = args[0];
			
			if(ipYpuerto.contains(":")) { // ... si contiene ":" es IP:Puerto
				
				String[] argsSeparados = ipYpuerto.split(":");
				ip = argsSeparados[0];
				
				try {
					puerto = Integer.parseInt(argsSeparados[1]);
				} catch (NumberFormatException ex) {
					System.out.println("ParÃ¡metro de entrada incorrecto");
					System.exit(-1);
				}
				
			} else { // ... sino, el parÃ¡metro es sÃ³lo la IP
				
				ip = ipYpuerto;
				puerto = 5512;
				
			}
			
		} else if (args.length == 0) { // Si no recibimos ningÃºn parÃ¡metro ...
			
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
			
		} else { // Si no es ninguno de esos casos, el nÃºmero de parÃ¡metros es incorrecto
			
			System.out.println("NÃºmero de parÃ¡metros incorrecto");
			System.exit(-1);
			
		}
			if(ip.equals("")){
				InetAddress hola;
				hola=InetAddress.getLocalHost();
				ip=hola.getHostAddress();
			}
		System.out.println("IP: " + ip);
		System.out.println("Puerto: " + puerto);
		
	
		// CARGAR EL ARCHIVO
	
		File archivo = new File(System.getProperty("user.dir"),"ripconf-"+ip+".topo");
		
		ArrayList<Router> routers = new ArrayList<Router>();
		ArrayList<Net> nets = new ArrayList<Net>();
	
		leerArchivo(archivo,routers,nets);
		
		System.out.println("");
		System.out.println("");
		System.out.println("LECTURA DEL FICHERO");
		System.out.println("");
		
		for (int i = 0; i < routers.size(); i++) {
			
			System.out.println(routers.get(i));
			
		}
		
		System.out.println("");
		
		for (int i = 0; i < nets.size(); i++) {
			
			System.out.println(nets.get(i));
			
		}
		
		
		try {
			EstablecerConexion(routers);
		} catch (IOException e) {
			System.out.println("Errores en EnviarPAquete");
			
			
		}
		

	}
	
	
	/* ************************ */
	/* ***** Leer Archivo ***** */
	/* ************************ */
	
	private static void leerArchivo (File archivo, ArrayList<Router> routers, ArrayList<Net> nets) {
		
		Scanner lector = null;
		
		try {
			
			lector = new Scanner(new FileInputStream(archivo));
		
		} catch (Exception ex) {
		
			System.out.println("El archivo de IPs no existe.");
			System.exit(-1);
			
		}
		
		while(lector.hasNextLine()) {
			
			String linea = lector.nextLine().trim();
			
			if (linea.contains("/")) {
				
				String[] argsSeparados = linea.split("/");
				String ipRed = argsSeparados[0];
				int lonRed = 0;
				
				try {
					lonRed = Integer.parseInt(argsSeparados[1]);
				} catch (NumberFormatException ex) {
					System.out.println("Puerto incorrecto");
					System.exit(-1);
				}
				
				nets.add(new Net(ipRed,lonRed));
				
			} else {
				
				if (linea.contains(":")) {
					
					String[] argsSeparados = linea.split(":");
					String ipRouter = argsSeparados[0];
					int puertoRouter = 0;
					
					try {
						puertoRouter = Integer.parseInt(argsSeparados[1]);
					} catch (NumberFormatException ex) {
						System.out.println("Puerto incorrecto");
						System.exit(-1);
					}
					
					routers.add(new Router(ipRouter,puertoRouter));
					
				} else {
					
					// Por ahora damos por hecho que no hay errores
					
					routers.add(new Router(linea,5512));
					
				}
				
			}
			
		}
		
	}
	
	
	private static void EnviarPaquete(String IpRemota) throws IOException{

		
		PaqueteRIP PacketEnvio = new PaqueteRIP(1,1,IpRemota,1); //Creamos el paquete para enviar
		InetAddress address = InetAddress.getByName(IpRemota);
		DatagramPacket packet = new DatagramPacket(PacketEnvio.obtenerPaquete(),PacketEnvio.obtenerPaquete().length,address,5512);
		DatagramSocket datagramSocket = new DatagramSocket();
		
        datagramSocket.send(packet);
        System.out.println("Paquete enviado a "+IpRemota+ " Yo Envie : "+ PacketEnvio.obtenerPaquete()+" De longitud "+PacketEnvio.obtenerPaquete().length);
		
       // datagramSocket.receive(packet);
        //System.out.println("Paquete respuesta recibido a "+packet.getData().toString());
        
	}
	
	
	private static void EstablecerConexion(ArrayList routers) throws IOException, InterruptedException{
		
		for(int i=0;i<routers.size();i++){
			
			EnviarPaquete(((Router)routers.get(i)).getIp());
			Thread.sleep(3000);
			
		}
		
		
	}
	
	
}
