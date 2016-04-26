
public class PaqueteRIP {
	
	private String convertirMascara (int longitud) {
		
		long bits = 0xffffffff ^ (1 << 32 - longitud) - 1;
		String mascara = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

		return mascara;
		
	}

}
