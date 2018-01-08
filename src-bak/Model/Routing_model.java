package Model;

import java.util.ArrayList;
import java.util.List;

import Util.Shell.ReadCSV;

public class Routing_model {
	
	
	String name;
	String power;
	String channel;
	String privacy;
	String mac;
	List<Device_model> devices;
	List<Routing_model> routings = new ArrayList<Routing_model>();
	
	public Routing_model (){
		
		
			
	}
	public Routing_model(String csvPath){
		
		ReadCSV rc = new ReadCSV(csvPath);
		List<String> name = rc.readCsv(13);
		List<String> power = rc.readCsv(8);
		List<String> channel = rc.readCsv(3);
		List<String> privacy = rc.readCsv(5);
		List<String> mac = rc.readCsv(0);
		
		for(int i = 0;i<name.size();i++){
			Routing_model routing_model = new Routing_model();
			routing_model.setName(name.get(i));
			routing_model.setChannel(channel.get(i));
			routing_model.setPower(power.get(i));
			routing_model.setPrivacy(privacy.get(i));	
			routing_model.setMac(mac.get(i));
			this.routings.add(routing_model);
		}
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	public List<Device_model> getDevices() {
		return devices;
	}
	public void setDevices(List<Device_model> devices) {
		this.devices = devices;
	}


	public List<Routing_model> getRoutings() {
		return routings;
	}


	public void setRoutings(List<Routing_model> routings) {
		this.routings = routings;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}

}
