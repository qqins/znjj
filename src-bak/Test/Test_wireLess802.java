package Test;

import java.util.List;

import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;

public class Test_wireLess802 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			List<Model_a> ls = new wireless802_airdump1().ergodic("D:\\javaDEV\\PcapTest\\1122.pcap");
			
			String ip = ls.get(0).getnLayer().getIp_des();
			
			System.out.println(ip.substring(0, 10)+"1");
	}

}
