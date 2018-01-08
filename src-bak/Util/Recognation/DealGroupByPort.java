package Util.Recognation;

import java.util.ArrayList;
import java.util.List;

import Model.Model_a;
import Model.Model_group;


public class DealGroupByPort {
	
	
	
	/**
	 * @param group
	 * ��ÿ��group�Ķ˿ڽ���ȷ��
	 * �еĶ˿ںſ��ܲ�ֹһ��
	 */
	public Model_group portDeal(Model_group group){
		
		if(group.getdataList().size()!=0){
			
			for(Model_a data:group.getdataList()){
				
				if(group.getDes_ports().size()==0){
					
					group.getDes_ports().add(data.getaLayer().getPort_des());
				}else {
					
					boolean flag = checkPortExsit(data.getaLayer().getPort_des(),group.getDes_ports());
					
					if(!flag){
						
						group.getDes_ports().add(data.getaLayer().getPort_des());
					}
				}
				
				
			}
			
		}
		
		return group;
	}
	
	
	
	/**
	 * @param port
	 * @param ports
	 * ���� �򷵻�true �����ڷ���false
	 */
	private boolean checkPortExsit(int port,List<Integer> ls){
		
		List<Integer> ports = new ArrayList<Integer>(ls);
		/*if(port == ports.get(0)){
			
			return true;
		}else {
			
			if(ports.size()!=0){
				
				ports.remove(0);
				
				if(checkPortExsit(port, ports)){
					
					return true;
				}
			}
				return false;
			
		}*/
		for(Integer i : ports){
			
			if(port==i){
				
				return true;
			}
			
		}
		
		
		return false;
	}
	
	

}
