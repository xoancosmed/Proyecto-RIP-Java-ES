
public class Router {

	// ATRIBUTOS
	
	private String ip;
	private int puerto;
	private int distancia; // TODO BORRAR ?
	
	
	// CONSTRUCTORES
	
	public Router (String ip, int puerto) {
		
		this.ip = ip;
		this.puerto = puerto;
		
	}
	
	public Router ( int distancia,String ip) {
		
		this.ip = ip;
		this.distancia = distancia;
		
	}
	
	
	// MÉTODOS
	
	public String toString() {
		
		if (distancia == 0) {
			return ip + ":" + puerto;
		}
		
		if (puerto == 0) {
			return ip + "\t\t" + distancia;
		}
		
		return ip + ":" + puerto + "\t\t" + distancia;
		
	}
	
	
	// GETTERS & SETTERS

	public String getIp() {
		return ip;
	}
	
	public int getDistancia() {
		return distancia;
	}
	
	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}
	
}
