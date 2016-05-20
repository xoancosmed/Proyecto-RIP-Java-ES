
public class Router {

	// ATRIBUTOS
	
	private String ip;
	private int puerto;
	private int contador=0;
	private boolean haContestado=false;
	
	// CONSTRUCTORES
	
	public Router (String ip, int puerto) {
		
		this.ip = ip;
		this.puerto = puerto;
		
	}
	
	
	// MÉTODOS
	
	public boolean equals(Router otro){

		if (this.getIp().equalsIgnoreCase(otro.getIp()) && (this.getPuerto() == otro.getPuerto())) 
			return true;
		return false;
	}
	
	public void actualizarContador(){
		
		if(haContestado){
			
			contador=0;
			
		}
		
		if(!haContestado){
			
			if (contador >= 10) contador = 10;
			else contador++;
			
		}
		
		haContestado=false;
	}
	
	public String toString() {
		
		if (puerto == 0) {
			return ip;
		}
		
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


	public int getContador() {
		return contador;
	}


	public void setContador(int contador) {
		this.contador = contador;
	}


	public boolean isHaContestado() {
		return haContestado;
	}


	public void setHaContestado(boolean haContestado) {
		this.haContestado = haContestado;
	}
	
}
