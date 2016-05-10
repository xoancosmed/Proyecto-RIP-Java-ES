
public class Router {

	// ATRIBUTOS
	
	private String ip;
	private int puerto;
	
	
	// CONSTRUCTORES
	
	public Router (String ip, int puerto) {
		
		this.ip = ip;
		this.puerto = puerto;
		
	}
	
	
	// MÉTODOS
	
	public String toString() {
		
		if (puerto == 0) {
			return ip;
		}
		
		return ip + ":" + puerto + "\t\t";
		
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
