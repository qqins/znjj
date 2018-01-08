package Manager.Listenner;

import java.util.List;

import Model.ConterData;
import Model.Model_a;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

public interface PackageListenner {
	
	void listenPackage(String  yourMAC,String hostMAC,Packet Packet,String routeMAC,String host_ip);
	
	Packet instance();
	
	int successFlag();;
}
