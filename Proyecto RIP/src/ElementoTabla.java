
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
