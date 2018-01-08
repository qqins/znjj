package Model;

public class Device_model {
	String name;
	String mac;
	String currentIP;
	
	//目的IP可能有多个，但对于目前简单家居设备来说，只有一个
	String currentSerIP;
	String currentAPIP;
	
	DB_csv_device db_model;
	Model_group group;
	

	public Device_model() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

	public Device_model(DB_csv_device db_model, Model_group group) {
		super();
		
		
		this.db_model = db_model;
		this.group = group;
		
		this.name=db_model.getInfo();
		this.currentIP = group.getDes_ip();
		this.mac = group.getDes_mac();
		
		//暂时只考虑有一个目的IP的情况
		this.currentSerIP = group.getSrc_ip();
		
		if(this.currentIP!=null){
			
			String ss = this.currentIP.substring(0, 10);
			
			this.currentAPIP = ss+"1";
		}
		
	}




	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public DB_csv_device getDb_model() {
		return db_model;
	}

	public void setDb_model(DB_csv_device db_model) {
		this.db_model = db_model;
	}

	public Model_group getGroup() {
		return group;
	}

	public void setGroup(Model_group group) {
		this.group = group;
	}

	public String getCurrentSerIP() {
		return currentSerIP;
	}

	public void setCurrentSerIP(String currentSerIP) {
		this.currentSerIP = currentSerIP;
	}

	public String getCurrentAPIP() {
		return currentAPIP;
	}

	public void setCurrentAPIP(String currentAPIP) {
		this.currentAPIP = currentAPIP;
	}

}
