package Util.Recognation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;

import Model.DB_csv_device;
import Model.Device_model;
import Model.Model_a;
import Model.Model_group;
import Model.Routing_model;
import Util.DealPcap.Outputframe;
import Util.DealPcap.wireless802_airdump1;

public class Recognation_device {

	//���еĴ��ڵļҾ��豸
	List<Device_model> devices_exsit = new ArrayList<Device_model>();
	
	
	//��ʱ����
	List<Device_model> devices = new ArrayList<Device_model>();
	//private List<Routing_model> routings;
	
	//���е�·����
	List<Routing_model> routings = new ArrayList<Routing_model>();
	
	/*************************************************
	 * constructor
	 *************************************************/
	public Recognation_device(List<Routing_model> routings) {
		
		
		super();
		//this.routings = routings;
		
		for(Routing_model routing:routings){
			
			
			if(routing.getName().equals("7B82")){
				Device_model device = new Device_model();
				
				device.setName("device1");
				//��ʱ����IP
				device.setCurrentIP("");
				//
				devices_exsit.add(device);
				
				routing.setDevices(new ArrayList<Device_model>());
				routing.getDevices().add(device);
				
				
				//������
				Device_model device1 = new Device_model();
				device1.setName("device2");
				//��ʱ����IP
				device1.setCurrentIP("");
				routing.getDevices().add(device1);
				devices_exsit.add(device1);
			}
		}
		
		this.routings = routings;
	}
	
	public Recognation_device(){
		
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	//��ָ��·���µ�cap����ʶ���豸
	public List<Device_model> checkDevice(String cap_path,String DBPath){
		
		
		wireless802_airdump1 w1 = new wireless802_airdump1();
		
		List<Model_a> all = w1.ergodic(cap_path);
		
		
		//���port
		Outputframe outputframe = new Outputframe();
		DB_csv_device db_model = new DB_csv_device();
		
		//11Ϊudp����  06Ϊtcp����
		List<Model_a> tcpList = w1.getFrameByType("06", all);
		
System.out.println("Total frame: "+tcpList.size());
		
		Map<String,Model_group> groups = new GroupByDesIP(tcpList).group();
		
		
System.out.println("Groups:"+groups.size());
System.out.println("-----------------------------------------------------------------");
System.out.println("-----------------------------------------------------------------");


		if(groups.size()!=0){
			
			int i = 0;
			for(Map.Entry<String,Model_group> entry : groups.entrySet()){
				
				i++;
System.out.println("index:" + i + "  DesIP: "+ entry.getKey());
				
				Model_group group = entry.getValue();
				
				group = new DealGroupByPort().portDeal(group);
				
				//jdk 1.8 ֮��Ĺ���
				//groups.replace(entry.getKey(), group);
				
				groups.put(entry.getKey(), group);
				
				
				int j = 1 ;
				for(int port:group.getDes_ports()){
					
System.out.print("          desPort"+j+": "+port+"  ");
				
					//Ŀǰֻ��һ��һ�����ң�֮������������ҵĹ���
					db_model = outputframe.checkPort(DBPath, port);
					
					if(db_model!=null){
						
						Device_model device = new Device_model(db_model,group);
						devices.add(device);
						break;
					}
					
				}
				
System.out.println();
System.out.println("-----------------------------------------------------------------");
System.out.println("-----------------------------------------------------------------");
			}
		}
	
		
		
		return this.devices;
	}
	


	
	public List<Device_model> getDevices() {
		return devices;
	}

	public void setDevices(List<Device_model> devices) {
		this.devices = devices;
	}
	public List<Device_model> getDevices_exsit() {
		return devices_exsit;
	}
	public void setDevices_exsit(List<Device_model> devices_exsit) {
		this.devices_exsit = devices_exsit;
	}
	public List<Routing_model> getRoutings() {
		return routings;
	}
	public void setRoutings(List<Routing_model> routings) {
		this.routings = routings;
	}
}
