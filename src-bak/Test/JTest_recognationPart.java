package Test;


import java.util.List;


import Model.Device_model;
import Util.Recognation.Recognation_device;

public class JTest_recognationPart {

	
	public void test() {
		
		Recognation_device re = new Recognation_device();
		List<Device_model> devices = re.checkDevice("F:\\test_pcap\\1122.pcap", "F:\\test_pcap\\output1.csv");

System.out.println("******************************************************************");
System.out.println("device in total:"+devices.size());
System.out.println("******************************************************************");


		Device_model device = devices.get(0);
		
		
		System.out.println("deviceName: "+device.getName());
		System.out.println("deviceMac: "+device.getMac());
		System.out.println("deviceCurIp: "+device.getCurrentIP());
		System.out.println("deviceAPIp: "+device.getCurrentAPIP());
		System.out.println("deviceSerIp: "+device.getCurrentSerIP());
		System.out.println("athomeLen:"+device.getDb_model().getAthome().length);
		System.out.println("outhomeLen:"+device.getDb_model().getOuthome().length);
		
System.out.println("-----------------------------------------------------------------");
System.out.println("-----------------------------------------------------------------");

	}

}
