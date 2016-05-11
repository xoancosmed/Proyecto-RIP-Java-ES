import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Scanner;

public class Rip {

	private static String ip = "";
	private static int puerto = 0;
	
	private static String password = "";
	private static boolean hasPassword = false;
	
	private static DatagramSocket datagramSocket = null;
	
	private static ArrayList<Router> routers;
	private static ArrayList<Net> nets;
	
	private static Tabla tabla;
	
	/* **************** */
	/* ***** MAIN ***** */
	/* **************** */
	
	public static void main (String[] args) {
		
		// PROCESAMOS LOS ARGUMENTOS DE ENTRADA
		
		obtenerIPyPuerto(args);
		
		
		// LEEMOS EL ARCHIVO
		
		File archivo = new File(System.getProperty("user.dir"),"ripconf-"+ip+".topo");
		
		routers = new ArrayList<Router>();
		nets = new ArrayList<Net>();
	
		leerArchivo(archivo,routers,nets);
		
		
		// ABRIMOS EL SOCKET
		
		try {
			
			datagramSocket = new DatagramSocket(puerto,InetAddress.getByName(ip));
			
		} catch (SocketException | UnknownHostException e1) {
			
			System.out.println("Error al abrir el socket.");
			e1.printStackTrace();
			
		}
		
		
		// INICIAMOS LA TABLA
		
		tabla = new Tabla();
		
		
		// AÑADIR A LA TABLA LOS ROUTER VECINOS Y REDES
		
		for (int i = 0; i < nets.size(); i++) {
			
			tabla.añadirElemento(nets.get(i).getIp(), nets.get(i).getLongitud(), 0, new Router("",0), 1);
			
		}
		
		for (int j = 0; j < routers.size(); j++) {
			
			tabla.añadirElemento(routers.get(j).getIp(), 32, 0, new Router("",0), 1);
			
		}
		
		// IMPRIMIR TABLA
		
		tabla.imprimirTabla();
		
		
		// ENVÍO INICIAL
		
		Paquete paquete;
		Paquete.RIPv2 paqueteRIP;
		
		if (hasPassword) paquete = new Paquete(password);
		else paquete = new Paquete();
		
		paquete.añadirEntrada(new Paquete.RIPv2(ip, 32, 0));
		
		for (int i = 0; i < nets.size(); i++) {
			
			paqueteRIP = new Paquete.RIPv2(nets.get(i).getIp(), nets.get(i).getLongitud(), 1);
			
			if (!paquete.añadirEntrada(paqueteRIP)) { // Si no entran más paquetes ...
				
				for (int j = 0; j < routers.size(); j++) { // ... enviamos el actual ...
					
					enviarPaquete(routers.get(j).getIp(), routers.get(j).getPuerto(), paquete);
					
				}
				
				if (hasPassword) paquete = new Paquete(password); // ... y creamos uno nuevo.
				else paquete = new Paquete(); 
				paquete.añadirEntrada(paqueteRIP);
				
			}
			
		}
		
		for (int j = 0; j < routers.size(); j++) { // Enviamos el paquete
			
			enviarPaquete(routers.get(j).getIp(), routers.get(j).getPuerto(), paquete);
			
		}
		
		
		// INICIAMOS EL BUCLE
		
		iniciarBucle();
		
	}
	
	
	/* ******************************* */
	/* ***** Obtener IP y Puerto ***** */
	/* ******************************* */
	
	private static void obtenerIPyPuerto (String[] argumentos) {
		
		if (argumentos.length == 1) { // Si recibimos un parámetro ...
			
			String ipYpuerto = argumentos[0];
			
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
			
		} else if (argumentos.length == 0) { // Si no recibimos ningún parámetro ...
			
			puerto = 5512; // ... se usan los valores por defecto
			
			// Obtenemos la IP de la interfaz eth0
			
			try {
				
				NetworkInterface interfaz = NetworkInterface.getByName("eth0"); // "Seleccionamos" la interfaz
				Enumeration<InetAddress> listaIPs = interfaz.getInetAddresses(); // Obtenemos la lista de IPs
				
				while (listaIPs.hasMoreElements()) { // Buscamos la IP deseada
					
					InetAddress posibleIP = listaIPs.nextElement();
					byte posibleIPb[] = posibleIP.getAddress();
					
					if (posibleIPb.length == 4 && posibleIPb[0] != 127) {
						
						ip = posibleIP.getHostAddress();  // Obtenemos la IP
						
						break;
					}
					
				}
				
			} catch (Exception ex) {
				
				System.out.println("Imposible obtener la IP de la interfaz \"eth0\".");
				//System.exit(-1);
				
			}
			
		} else { // Si no es ninguno de esos casos, el número de parámetros es incorrecto
			
			System.out.println("Número de parámetros incorrecto");
			System.exit(-1);
			
		}
		
		// TODO BORRAR VVVV

		if(ip.equals("")){
			
			try {
				ip=InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
		}
		
		// BORRAR ^^^
		
	}
	
	/* ******************************** */
	/* ***** Solicitar Contraseña ***** */
	/* ******************************** */
	
	private void solicitarClave () {
		
		Scanner scan = new Scanner(System.in);
		
		while (true) {
			
			System.out.println("¿Quiere introducir contraseña? (Y/N) ");
			String rec = scan.nextLine().trim();
			
			if (rec.equalsIgnoreCase("Y")) break;
			if (rec.equalsIgnoreCase("N")) return;
			
		}
			
		hasPassword = true;
		
		System.out.println("Introtruzca la contraseña: ");
		password = scan.nextLine().trim();
		
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
	
	/* ************************** */
	/* ***** Enviar Paquete ***** */
	/* ************************** */
	
	private static boolean enviarPaquete (String ipDestino, int puertoDestino, Paquete paquete) {
		
		DatagramPacket dataPack = null;
		
		try {
			
			dataPack = new DatagramPacket(paquete.obtenerPaquete(), paquete.obtenerPaquete().length, InetAddress.getByName(ipDestino),puertoDestino);
		
		} catch (UnknownHostException e) {
			
			System.out.println("Error al crear el datagrama.");
			e.printStackTrace();
			return false;
			
		}
		
		try {
			
			datagramSocket.send(dataPack);
			
		} catch (IOException e) {
			
			System.out.println("Error al enviar el datagrama.");
			e.printStackTrace();
			return false;
			
		}
		
		return true;
		
	}
	
	/* ************************* */
	/* ***** Split Horizon ***** */
	/* ************************* */

	private static void splitHorizon (Router router) {
		
		Paquete paquete;
		Paquete.RIPv2 paqueteRIP;
		
		if (hasPassword) paquete = new Paquete(password);
		else paquete = new Paquete();
		
		paquete.añadirEntrada(new Paquete.RIPv2(ip, 32, 0));
		
		Iterator<String> it = tabla.obtenerInterator();
		
		while (it.hasNext()) {
			
			String subred = it.next();
			
			if (tabla.obtenerElemento(subred).getVecino().getIp().equalsIgnoreCase(router.getIp())
					&& (tabla.obtenerElemento(subred).getVecino().getPuerto() == router.getPuerto())) {
				
				paqueteRIP = new Paquete.RIPv2(
						tabla.obtenerElemento(subred).getSubred(), 
						tabla.obtenerElemento(subred).getMascara(), 
						16);
			
			} else {
				
				paqueteRIP = new Paquete.RIPv2(
						tabla.obtenerElemento(subred).getSubred(), 
						tabla.obtenerElemento(subred).getMascara(), 
						tabla.obtenerElemento(subred).getCoste());
				
			}
			
			if (!paquete.añadirEntrada(paqueteRIP)) {
				
				enviarPaquete(router.getIp(), router.getPuerto(), paquete);
				
				if (hasPassword) paquete = new Paquete(password);
				else paquete = new Paquete();
				paquete.añadirEntrada(paqueteRIP);
				
			}
			
		}
		
		enviarPaquete(router.getIp(), router.getPuerto(), paquete);
		
	}
	
	/* ************************* */
	/* ***** Iniciar Bucle ***** */
	/* ************************* */
	
	private static void iniciarBucle () {
		
		int socketTimeout = 10000;
		Date initialDate = new Date();
		try {
			datagramSocket.setSoTimeout(socketTimeout);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		byte[] recData = new byte[504]; // 512 ??
		DatagramPacket datagramPacket = new DatagramPacket(recData, 504);
		
		while (true) {
			
			// RECIBIR PAQUETE
			
			try {
				
				datagramSocket.receive(datagramPacket);	//Recibimos el paquete RIP
				
				Date currentDate = new Date();
				long elapsedTime = currentDate.getTime() - initialDate.getTime();
				datagramSocket.setSoTimeout(socketTimeout - (int)elapsedTime);
				
				// TODO PROCESAR PAQUETE (revisar)
				
				if (hasPassword == true) {
					
					if (!Paquete.obtenerClave(recData).equals(password)) continue;
					
				}
				
				Paquete.RIPv2[] ripRecibido = Paquete.obtenerEntradas(recData);
				
				for (int k = 0; k < ripRecibido.length; k++) {
					
					String subred = ripRecibido[k].getIp();
					String mascara = ripRecibido[k].getMascara();
					Router vecino = new Router(datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort());
					int coste = ripRecibido[k].getCoste() + 1;
					int g = obtenerG(subred,vecino,coste);
					
					if(subred.equalsIgnoreCase(ip)) continue;
					if(subred.equalsIgnoreCase("0.0.0.0")) continue;
					
					if (g == 0) tabla.añadirElemento(subred, mascara, 0, new Router("",0), coste);
					else tabla.añadirElemento(subred, mascara, 1, vecino, coste);
					
				}
			
			} catch (SocketTimeoutException ex) {
				
				// IMPRIMIR TABLA
				
				tabla.imprimirTabla();
				
				// ENVIAR PAQUETE
				
				for (int j = 0; j < routers.size(); j++) {
					
					splitHorizon (routers.get(j));
					
				}
				
				// RESETEAMOS EL TIMEOUT
				
				socketTimeout = 10000;
				initialDate = new Date();
				try {
					datagramSocket.setSoTimeout(socketTimeout);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				continue;
				
			} catch (IOException ex) {
				
				ex.printStackTrace();
				
				socketTimeout = 10000;
				initialDate = new Date();
				try {
					datagramSocket.setSoTimeout(socketTimeout);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				
				continue;
				
			}
 			
		}
		
	}
	
	/* ********************* */
	/* ***** Obtener G ***** */
	/* ********************* */
	
	private static int obtenerG (String subred, Router vecino, int coste) {
		
		if (coste < 2) return 0;
		
		if (subred.equalsIgnoreCase(vecino.getIp())) return 0;
		
		for (int m = 0; m < routers.size(); m++) 
			if (routers.get(m).getIp().equalsIgnoreCase(subred)) return 0;
		
		for (int n = 0; n < nets.size(); n++)
			if (nets.get(n).getIp().equalsIgnoreCase(subred)) return 0;
			
		
		return 1;
		
	}
	
}
