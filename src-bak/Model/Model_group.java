package Model;

import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

public class Model_group {
	
	
	private String des_mac;
	private String src_ip;
	private String des_ip;
	private List<Integer> src_ports = new ArrayList<Integer>();
	private List<Integer> des_ports = new ArrayList<Integer>();
	private String type;
	
	private List<Model_a> datalist = new ArrayList<Model_a>();

	//以目的IP为分组的分组 其先关组是以目的IP为源IP的分组
	private List<Model_group> relatedGroups = new ArrayList<Model_group>();
	
	//construct
	public Model_group() {
		super();
	}


	public String getSrc_ip() {
		return src_ip;
	}

	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}

	public String getDes_ip() {
		return des_ip;
	}

	public void setDes_ip(String des_ip) {
		this.des_ip = des_ip;
	}

	

	public List<Model_a> getdataList() {
		return datalist;
	}

	public void addData(Model_a model) {
		this.datalist.add(model);
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}


	public List<Model_group> getRelatedGroups() {
		return relatedGroups;
	}


	public void addRelatedGroup(Model_group group) {
		
		this.relatedGroups.add(group);
	}


	public List<Integer> getSrc_ports() {
		return src_ports;
	}


	public void setSrc_ports(List<Integer> src_ports) {
		this.src_ports = src_ports;
	}


	public List<Integer> getDes_ports() {
		return des_ports;
	}


	public void setDes_ports(List<Integer> des_ports) {
		this.des_ports = des_ports;
	}


	public String getDes_mac() {
		return des_mac;
	}


	public void setDes_mac(String des_mac) {
		this.des_mac = des_mac;
	}





}
