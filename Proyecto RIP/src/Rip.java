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
import java.util.Random;
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
		
		
		// SOLICITAMOS LA CONTRASEÑA
		
		solicitarClave();
		
		
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
		
		if (hasPassword) paquete = new Paquete(password); // Si tiene contraseña se la metemos ...
		else paquete = new Paquete(); // Sino no.
		
		paquete.añadirEntrada(new Paquete.RIPv2(ip, 32, 0)); // Nos metemos en el paquete
		
		for (int i = 0; i < nets.size(); i++) {
			
			paqueteRIP = new Paquete.RIPv2(nets.get(i).getIp(), nets.get(i).getLongitud(), 1); // Nueva entrada con la red i
			
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
		
		/*if(ip.equals("")){
			
			try {
				ip=InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
		}*/
		
	}
	
	/* ******************************** */
	/* ***** Solicitar Contraseña ***** */
	/* ******************************** */
	
	private static void solicitarClave () {
		
		Scanner scan = new Scanner(System.in);
		
		while (true) {
			
			System.out.println("¿Quiere introducir contraseña? (Y/N) "); // Pedimos contraseña
			String rec = scan.nextLine().trim(); // Leemos del teclado
			
			if (rec.equalsIgnoreCase("Y")) break;
			
			if (rec.equalsIgnoreCase("N")) {
				
				scan.close();
				return;
				
			}
			
		}
			
		hasPassword = true; // Activamos el flag
		
		System.out.println("Introtruzca la contraseña: ");
		password = scan.nextLine().trim(); // Leemos la contraseña
		
		scan.close();
		
	}
	
	/* ************************ */
	/* ***** Leer Archivo ***** */
	/* ************************ */
	
	private static void leerArchivo (File archivo, ArrayList<Router> routers, ArrayList<Net> nets) {
		
		Scanner lector = null;
		
		try {
			
			lector = new Scanner(new FileInputStream(archivo)); // Abrimos el archivo
		
		} catch (Exception ex) {
		
			System.out.println("El archivo de IPs no existe.");
			System.exit(-1);
			
		}
		
		while(lector.hasNextLine()) {
			
			String linea = lector.nextLine().trim(); // Leemos una línea
			
			if (linea.contains("/")) { // Si contiene barra => red
				
				String[] argsSeparados = linea.split("/");
				String ipRed = argsSeparados[0];
				int lonRed = 0;
				
				try {
					lonRed = Integer.parseInt(argsSeparados[1]);
				} catch (NumberFormatException ex) {
					System.out.println("Longitud incorrecta");
					System.exit(-1);
				}
				
				nets.add(new Net(ipRed,lonRed));
				
			} else {
				
				if (linea.contains(":")) { // Si contiene ":" => router con puerto
					
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
					
					routers.add(new Router(linea,5512)); // Sino => router sin puerto (se usa el predeterminado)
					
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
			
			// Creamos el paquete datagrama
			dataPack = new DatagramPacket(paquete.obtenerPaquete(), paquete.obtenerPaquete().length, InetAddress.getByName(ipDestino),puertoDestino);
		
		} catch (UnknownHostException e) {
			
			System.out.println("Error al crear el datagrama.");
			e.printStackTrace();
			return false;
			
		}
		
		try {
			
			datagramSocket.send(dataPack); // Enviamos el datagrama
			
		} catch (IOException e) {
			
			System.out.println("Error al enviar el datagrama.");
			e.printStackTrace();
			return false;
			
		}
		
		return true;
		
	}
	
	/* ************************ */
	/* ***** Enviar Tabla ***** */
	/* ************************ */

	private static void enviarTabla (Router router) {
		
		Paquete paquete;
		Paquete.RIPv2 paqueteRIP;
		
		if (hasPassword) paquete = new Paquete(password); // Si hay contraseña, la metemos
		else paquete = new Paquete();
		
		paquete.añadirEntrada(new Paquete.RIPv2(ip, 32, 0)); // Nos metemos en el paquete
		
		Iterator<String> it = tabla.obtenerInterator();
		
		while (it.hasNext()) {  // Recorremos la tabla
			
			String subred = it.next();
			
			if (tabla.obtenerElemento(subred).getVecino().getIp().equalsIgnoreCase(router.getIp()) // Si el vecino es al que le envío la tabla ...
					&& (tabla.obtenerElemento(subred).getVecino().getPuerto() == router.getPuerto())) {
				
				paqueteRIP = new Paquete.RIPv2(
						tabla.obtenerElemento(subred).getSubred(), 
						tabla.obtenerElemento(subred).getMascara(), 
						16); // Coste 16 por el Split Horizon
			
			} else { // Sino ...
				
				paqueteRIP = new Paquete.RIPv2(
						tabla.obtenerElemento(subred).getSubred(), 
						tabla.obtenerElemento(subred).getMascara(), 
						tabla.obtenerElemento(subred).getCoste());
				
			}
			
			if (!paquete.añadirEntrada(paqueteRIP)) { // Si llegamos al máximo tamaño del paquete ...
				
				enviarPaquete(router.getIp(), router.getPuerto(), paquete); // ... enviamos este ...
				
				if (hasPassword) paquete = new Paquete(password); // ... y creamos otro.
				else paquete = new Paquete();
				paquete.añadirEntrada(paqueteRIP);
				
			}
			
		}
		
		enviarPaquete(router.getIp(), router.getPuerto(), paquete); // Enviamos el paquete
		
	}
	
	/* ************************* */
	/* ***** Iniciar Bucle ***** */
	/* ************************* */
	
	private static void iniciarBucle () {
		
		int socketTimeout = obtenerTimeOut();
		Date initialDate = new Date();
		try {
			datagramSocket.setSoTimeout(socketTimeout);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		byte[] recData = new byte[504];
		DatagramPacket datagramPacket = new DatagramPacket(recData, 504);
		
		
		while (true) {
			
			// RECIBIR PAQUETE
			
			try {
				
				for (int k = 0; k < recData.length; k++) recData[k] = (byte) 0x0; // Reiniciamos el array de bytes.
				datagramSocket.receive(datagramPacket);	//Recibimos el paquete RIP
				
				Date currentDate = new Date();
				long elapsedTime = currentDate.getTime() - initialDate.getTime();
				datagramSocket.setSoTimeout(socketTimeout - (int)elapsedTime);
				
				// PROCESAR PAQUETE
				
				// Vemos si tiene contraseña
				
				if (hasPassword == true) {
					
					String claveRecibida=Paquete.obtenerClave(recData);
					
					if(claveRecibida==null) continue;
					else if(!claveRecibida.equals(password)) continue;
					
				} else if(hasPassword==false){
					
					String claveRecibida=Paquete.obtenerClave(recData);
					
					if(claveRecibida!=null) continue;
					
				}
				
				// Registramos que este router sigue enviando
				
				for(int i=0 ;i<routers.size();i++){
					
					if(datagramPacket.getAddress().getHostAddress().equalsIgnoreCase(routers.get(i).getIp()) &&
							(datagramPacket.getPort()==routers.get(i).getPuerto())	){
						
						routers.get(i).setHaContestado(true);
						
					}
					
				}
				
				// Interpretamos el paquete
				
				Paquete.RIPv2[] ripRecibido = Paquete.obtenerEntradas(recData);
				
				for (int k = 0; k < ripRecibido.length; k++) {
					
					String subred = ripRecibido[k].getIp();
					String mascara = ripRecibido[k].getMascara();
					Router vecino = new Router(datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort());
					int coste=16;
					if(ripRecibido[k].getCoste()<16){ // El coste no puede ser mayor que 16
						coste = ripRecibido[k].getCoste() + 1;
					}
					int g = obtenerG(subred,vecino,coste);
					
					if(subred.equalsIgnoreCase(ip)) continue; // No hacemos caso a las referencias a nosotros mismos
					if(subred.equalsIgnoreCase("0.0.0.0")) continue; // No hacemos caso a las IPs vacías (en paquete llegó todo 0s)
					
					if (g == 0) tabla.añadirElemento(subred, mascara, 0, new Router("",0), coste); // Añadimos en la tabla
					else tabla.añadirElemento(subred, mascara, 1, vecino, coste);
					
				}
			
			} catch (SocketTimeoutException ex) {
				
				// IMPRIMIR TABLA
				
				tabla.imprimirTabla();
				
				//COMPROBAR VECINOS CAIDOS
				
				for(int i=0;i<routers.size();i++){
				
					routers.get(i).actualizarContador();
					
					if(routers.get(i).getContador()>=6){ // Está caido si lleva 6 ciclos sin contestar
					
						Tabla.ElementoTabla elemento = tabla.obtenerElemento(routers.get(i).getIp());
						if (elemento != null) elemento.setCoste(16);
						
						Iterator<String> it= tabla.obtenerInterator();
						
						while(it.hasNext()){
							
							Tabla.ElementoTabla dependencia = tabla.obtenerElemento(it.next());
							
							if(dependencia.getVecino().equals(routers.get(i)))
								dependencia.setCoste(16);
							
						}
						
					}
					
					if(routers.get(i).getContador()==0){ // Está bien
					
						Tabla.ElementoTabla elemento = tabla.obtenerElemento(routers.get(i).getIp());
						if (elemento != null) elemento.setCoste(1);
						else tabla.añadirElemento(routers.get(i).getIp(), 32, 0, new Router("",0), 1);
						
					}
					
				}
				
				// ENVIAR PAQUETE
				
				for (int j = 0; j < routers.size(); j++) {
					
					enviarTabla (routers.get(j));
					
				}
				
				// RESETEAMOS EL TIMEOUT
				
				socketTimeout = obtenerTimeOut();
				initialDate = new Date();
				try {
					datagramSocket.setSoTimeout(socketTimeout);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				continue;
				
			} catch (IOException ex) {
				
				ex.printStackTrace();
				
				socketTimeout = obtenerTimeOut();
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
		
		if (subred.equalsIgnoreCase(vecino.getIp())) return 0;
		
		for (int m = 0; m < routers.size(); m++) 
			if (routers.get(m).getIp().equalsIgnoreCase(subred)) return 0;
		
		for (int n = 0; n < nets.size(); n++)
			if (nets.get(n).getIp().equalsIgnoreCase(subred)) return 0;
			
		if (coste < 2) return 0;
		
		return 1;
		
	}
	
	/* *************************** */
	/* ***** Obtener TimeOut ***** */
	/* *************************** */
	
	private static int obtenerTimeOut () { 
		
		// Generamos un timeout aleatorio entre 9750 y 10250 milisegundos
		
		int valBase = 10000;
		int limInferior = -250;
		int limSuperior = +250;
		
		Random rand = new Random();
		int valAleatorio = rand.nextInt(limSuperior-limInferior) + limInferior; 
		
		return valAleatorio + valBase;
		
	}
	
}
