import java.util.TreeMap;

public class Tabla {
	
	private TreeMap<String,ElementoTabla> tabla;
	
	public Tabla() {
		
		tabla = new TreeMap<String,ElementoTabla>();
		
	}
	
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
	
	public void añadirElemento (String subred, String mascara, int g, String vecino, int coste) {
		
		if (tabla.get(subred) == null) {
			
			tabla.put(subred, new ElementoTabla(subred, mascara, g, vecino, coste));
			
		} else if (tabla.get(subred).getCoste() > coste) {
			
			tabla.get(subred).setMascara(mascara);
			tabla.get(subred).setG(g);
			tabla.get(subred).setVecino(vecino);
			tabla.get(subred).setCoste(coste);
			
		}
		
	}
	
	public ElementoTabla obtenerElemento (String subred) {
		
		return tabla.get(subred);
		
	}
	
	public class ElementoTabla {

		private String subred;
		private String mascara;
		private int g;
		private String vecino;
		private int coste;
		
		public ElementoTabla(String subred, String mascara, int g, String vecino, int coste) {
			super();
			this.subred = subred;
			this.mascara = mascara;
			this.g = g;
			this.vecino = vecino;
			this.coste = coste;
		}

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

		public String getVecino() {
			return vecino;
		}

		public void setVecino(String vecino) {
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
