package Model;

public class DB_csv_device {
	
	private int ID ;
	private String info;
	private int desport;
	private String protocol;
	private byte[] outhome;
	private byte[] athome;


	public int getId() {
		return ID;
	}

	public void setId(int ID) {
		this.ID = ID;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getDesPort() {
		return desport;
	}

	public void setDesPort(int desport) {
		this.desport = desport;
	}

	public byte[] getAthome() {
		return athome;
	}

	public void setAthome(byte[] athome) {
		this.athome = athome;
	}

	public byte[] getOuthome() {
		return outhome;
	}

	public void setOuthome(byte[] outhome) {
		this.outhome = outhome;
	}

	public String getprotocol() {
		return protocol;
	}

	public void setprotocol(String protocol) {
		this.protocol = protocol;
	}

}
