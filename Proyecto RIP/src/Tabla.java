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
		
		if (tabla.get(nuevo.getSubred()) == null) {
			
			tabla.put(nuevo.getSubred(), new ElementoTabla(nuevo.getSubred(), nuevo.getMascara(), nuevo.getG(), nuevo.getVecino(), nuevo.getCoste()));
			
		} else if (tabla.get(nuevo.getSubred()).getCoste() > nuevo.getCoste()) {
			
			tabla.get(nuevo.getSubred()).setMascara(nuevo.getMascara());
			tabla.get(nuevo.getSubred()).setG(nuevo.getG());
			tabla.get(nuevo.getSubred()).setVecino(nuevo.getVecino());
			tabla.get(nuevo.getSubred()).setCoste(nuevo.getCoste());
			
		}
		
	}
	
	public void añadirElemento (String subred, String mascara, int g, Router vecino, int coste) {
		
		if (tabla.get(subred) == null) {
			
			tabla.put(subred, new ElementoTabla(subred, mascara, g, vecino, coste));
			
		} else if (tabla.get(subred).getCoste() > coste) {
			
			tabla.get(subred).setMascara(mascara);
			tabla.get(subred).setG(g);
			tabla.get(subred).setVecino(vecino);
			tabla.get(subred).setCoste(coste);
			
		}
		
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
		
		System.out.println("");
		System.out.println("SUBRED ------- MASCARA ------- G ------- Vecino ------- Coste");
		System.out.println("");
		
		Iterator<String> it = tabla.keySet().iterator();
		while(it.hasNext()) System.out.println(tabla.get(it.next()).toString());
		
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
		
	}

}
