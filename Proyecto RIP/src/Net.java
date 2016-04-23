
public class Net {
	
	// ATRIBUTOS
	
	private String ip;
	private int longitud;
	
	
	// CONSTRUCTORES
	
	public Net (String ip, int longitud) {
		
		this.ip = ip;
		this.longitud = longitud;
		
	}
	
	
	// MÉTODOS
	
	public String toString() {
			
		return ip + "/" + longitud;
			
	}
	
	
	// GETTERS & SETTERS

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

}
