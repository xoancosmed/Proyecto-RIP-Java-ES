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

}
