
public class Router {

	// ATRIBUTOS
	
	private String ip;
	private int puerto;
	private int distancia;
	
	
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
		
		return ip + ":" + puerto;
		
	}
	
	
	// GETTERS & SETTERS

	public String getIp() {
		return ip;
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
