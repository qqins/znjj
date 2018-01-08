package Util.Recognation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Model_a;
import Model.Model_group;

public class GroupByDesIP {

	private List<Model_a> all = null;
	
	private Map<String, Model_group> groups = new HashMap<String, Model_group>();
	
	public GroupByDesIP(List<Model_a> ls) {
		super();
		
		this.all = ls;
	}
	
	/**
	 * ����
	 * ����Ŀ��IP��ͬ���з���
	 */
	public Map<String, Model_group> group(){
		
	
		
		if(all != null){
			for(Model_a model:all){
				
				String desIP = model.getnLayer().getIp_des();
				
				
				if(groups.containsKey(desIP)){
					
					groups.get(desIP).addData(model);
					
				}
				else{
					Model_group group = new Model_group();
					group.setDes_ip(desIP);
					
					group.addData(model);
					//group.getDes_ports().add(model.getaLayer().getPort_des());
					group.setDes_mac(model.getwLayer().getDes_mac());
					
					//��ʱֻ�趨��һ��Ŀ��IP���豸
					group.setSrc_ip(model.getnLayer().getIp_sou());
					
					groups.put(desIP, group);
					
				}
			}
		}
		
		
		return this.groups;
		
	}
	
	

}
