package Util.Timer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Model.Device_model;
import Model.Routing_model;

public class ShowDeviceOnTable02  {
	
	
	private JTable table;
	private int index = 0;
	private JScrollPane jp;
	private List<Device_model> devices_exsit;
	List<Routing_model> routings = new ArrayList<Routing_model>();
	
	//private List<Routing_model> route_exsit = new ArrayList<Routing_model>();
	
	private List<Integer> router_exsit = new ArrayList<Integer>();
	
	public ShowDeviceOnTable02(List<Routing_model> routings ,JTable table,JScrollPane jp) {
		
		this.routings = routings;
		this.table = table;
		
		this.jp = jp;
	}

	
	public JTable show() {
			if(index < routings.size()){
				
				if(routings.get(index).getDevices()!=null){
					
					//�ȼ�����ڵ�·������route_exsit
					//route_exsit.add(routings.get(index));
					router_exsit.add(index);
					//home_device������
					for(Device_model device:routings.get(index).getDevices()){
						devices_exsit.add(device);
					}
				}
				index++;
			}else {
				
				new TimerDialog().showDialog((JFrame) table.getTopLevelAncestor(), "Success��"+devices_exsit.size()+" Devices in total!", 3);
				
				//���������
				//��ʾ���ڼҾ��豸��router�豸�Լ�������ļҾ��豸
				int row = table.getModel().getRowCount();
				
				//��ɾ���������
				for(;row>0;row--){	
					if(router_exsit.size()>0){
						for(int index:router_exsit){
							if(row==(index+1)){
								break;
							}else{
								((DefaultTableModel) table.getModel()).removeRow(row-1);
							}
							
						}
						
					}
					
				}
				
				
				//���
				
				if(router_exsit.size()>0){
					
					for(int j = 0;j<router_exsit.size();j++){
						
						int deviceNum = routings.get(router_exsit.get(j)).getDevices().size();	
						
						
						if(deviceNum>0) {
							
							DefaultTableModel dt = (DefaultTableModel)table.getModel();
							
							Routing_model router = routings.get(router_exsit.get(j));
							
							for(int i = 0 ;i<(deviceNum);i++){
								
								dt.addColumn("home_device");	
								table.setValueAt(router.getDevices().get(i).getName(), j, table.getColumnCount()-1);
							}
							
							for(int i = 0;i<table.getColumnModel().getColumnCount();i++){
								
								if(i<4){
									table.getColumnModel().getColumn(i).setPreferredWidth(60);
									table.getColumnModel().getColumn(i).setMinWidth(27);  
									table.getColumnModel().getColumn(i).setMaxWidth(600);
									
								}
								else{
									table.getColumnModel().getColumn(i).setPreferredWidth(95);
									table.getColumnModel().getColumn(i).setMinWidth(30);
									table.getColumnModel().getColumn(i).setMaxWidth(500);
								}
								
							}
							
						}
					}
					
				}
				
				//jp.repaint();
			}
			
			
			return this.table;
	}
	
	public List<Integer> getRouterhaveDevice(){
		
		return this.router_exsit;
	}

}
