import java.util.Iterator;
import java.util.TreeMap;

/* *************** */
/* **** Tabla **** */
/* *************** */

public class Tabla {
	
	// ATRIBUTO
	
	private TreeMap<String,ElementoTabla> tabla;
	
	// CONSTRUCTOR
	
	public Tabla() {
		
		tabla = new TreeMap<String,ElementoTabla>();
		
	}
	
	// MÉTODOS
	
	public void añadirElemento (ElementoTabla nuevo) {
		
		// Si no está en la tabla se añade sí o sí
		if (tabla.get(nuevo.getSubred()) == null) {
			
			if (nuevo.getCoste() >= 16) return;
			
			tabla.put(nuevo.getSubred(), nuevo);
			return;
				
		}
		
		if (tabla.get(nuevo.getSubred()).getG() == 0) return;
		
		// Si el vecino es el mismo, entonces le hacemos siempre caso.
		if (tabla.get(nuevo.getSubred()).getVecino().getIp().equalsIgnoreCase(nuevo.getVecino().getIp()) &&
				(tabla.get(nuevo.getSubred()).getVecino().getPuerto() == nuevo.getVecino().getPuerto())) {
			
			tabla.get(nuevo.getSubred()).setCoste(nuevo.getCoste());
			
		}
		
		// Si el coste es menor que el que tenemos en la tabla, lo reemplazamos.
		if (tabla.get(nuevo.getSubred()).getCoste() > nuevo.getCoste()) {
			
			tabla.get(nuevo.getSubred()).setMascara(nuevo.getMascara());
			tabla.get(nuevo.getSubred()).setG(nuevo.getG());
			tabla.get(nuevo.getSubred()).setVecino(nuevo.getVecino());
			tabla.get(nuevo.getSubred()).setCoste(nuevo.getCoste());
			
		}
		
	}
	
	public void añadirElemento (String subred, String mascara, int g, Router vecino, int coste) {
		
		añadirElemento (new ElementoTabla(subred, mascara, g, vecino, coste));
		
	}
	
	public void añadirElemento (String subred, int longitud, int g, Router vecino, int coste) {
		
		añadirElemento (subred, convertirLongitudAMascara(longitud), g, vecino, coste);
		
	}
	
	private String convertirLongitudAMascara (int longitud) {
		
		long bits = 0xffffffff ^ (1 << 32 - longitud) - 1;
		String mascara = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

		return mascara;
		
	}
	
	public ElementoTabla obtenerElemento (String subred) {
		
		return tabla.get(subred);
		
	}
	
	public Iterator<String> obtenerInterator () {
		
		return tabla.keySet().iterator();
		
	}
	
	public TreeMap<String,ElementoTabla> obtenerTabla () {
		
		return tabla;
		
	}
	
	public void imprimirTabla() {
		
		// Imprimimos la tabla
		
		System.out.println("");
		System.out.println("SUBRED ------- MASCARA ------- G ------- Vecino ------- Coste");
		System.out.println("");
		
		if (tabla == null) return;
		if (tabla.isEmpty()) return;
		
		// Aprovechamos para revisar qué elementos hay que borrar de la tabla
		
		Iterator<String> it = tabla.keySet().iterator();
		
		while(it.hasNext()) {
			
			ElementoTabla elemento = tabla.get(it.next());
			
			if(elemento.getCoste() >= 16) elemento.setDeathCounter(elemento.getDeathCounter()+1);
			if(elemento.getDeathCounter()>=4) it.remove();
			else System.out.println(elemento.toString());
				
		}
		
	}
	
	
	/* *********************** */
	/* **** ElementoTabla **** */
	/* *********************** */
	
	public class ElementoTabla {

		// ATRIBUTOS
		
		private String subred;
		private String mascara;
		private int g;
		private Router vecino;
		private int coste;
		
		private int deathCounter;
		
		// CONSTRUCTORES
		
		public ElementoTabla () {
			
		}
		
		public ElementoTabla (String subred, String mascara, int g, Router vecino, int coste) {
			
			this.subred = subred;
			this.mascara = mascara;
			this.g = g;
			this.vecino = vecino;
			this.coste = coste;
			
		}
		
		// MÉTODO
		
		public String toString () {
			
			String string;
			
			string = subred;
			string += "\t";
			string += mascara;
			string += "\t";
			string += Integer.toString(g);
			string += "\t";
			string += vecino.toString();
			string += "\t";
			string += Integer.toString(coste);
			
			return string;
			
		}
		
		// GETTERS & SETTERS

		public String getSubred() {
			return subred;
		}

		public void setSubred(String subred) {
			this.subred = subred;
		}

		public String getMascara() {
			return mascara;
		}

		public void setMascara(String mascara) {
			this.mascara = mascara;
		}

		public int getG() {
			return g;
		}

		public void setG(int g) {
			this.g = g;
		}

		public Router getVecino() {
			return vecino;
		}

		public void setVecino(Router vecino) {
			this.vecino = vecino;
		}

		public int getCoste() {
			return coste;
		}

		public void setCoste(int coste) {
			this.coste = coste;
		}

		public int getDeathCounter() {
			return deathCounter;
		}

		public void setDeathCounter(int deathCounter) {
			this.deathCounter = deathCounter;
		}
		
	}

}
