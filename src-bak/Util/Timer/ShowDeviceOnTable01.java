package Util.Timer;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Model.Device_model;
import Model.Routing_model;
import Util.Recognation.Recognation_device;

public class ShowDeviceOnTable01 extends TimerTask {
	
	
	private JTable table;
	private int index = 0;
	private Timer timer;
	private JScrollPane jp;
	private List<Device_model> devices_exsit = new ArrayList<Device_model>();
	List<Routing_model> routings = new ArrayList<Routing_model>();
	
	//private List<Routing_model> route_exsit = new ArrayList<Routing_model>();
	
	private List<Integer> router_exsit = new ArrayList<Integer>();
	
	public ShowDeviceOnTable01(List<Routing_model> routers,JTable table,Timer timer,JScrollPane jp) {
		
		this.routings = routers;
		this.table = table;
		this.timer = timer;
		this.jp = jp;
		//this.devices_exsit = regDevice.getDevices_exsit(); 
	}

	@Override
	public void run() {
			if(index < routings.size()){
				
				if(routings.get(index).getDevices()!=null){
					
					//先加入存在的路由容器route_exsit
					//route_exsit.add(routings.get(index));
					router_exsit.add(index);
					//home_device的数量
					for(Device_model device:routings.get(index).getDevices()){
						
						devices_exsit.add(device);
					}
					
				
				}else{
					
					//table.setValueAt("Null Device!", index, 4);
				}
					
				//int rowCount = table.getColumnCount(); 
				//table.getSelectionModel().setSelectionInterval(rowCount-1 , rowCount-1); 
				//Rectangle rect = table.getCellRect(index ,table.getColumnCount()-1 , true);  
				
				/*Rectangle rect = table.getCellRect(index ,0 , true); 
				
				jp.repaint();
				table.scrollRectToVisible(rect);*/
				index++;
			}else {
				
				if(devices_exsit.size()>0){
					
					//清空所有行
					//显示存在家居设备的router设备以及其包含的家居设备
					int row = table.getModel().getRowCount();
					
					//先删除，再添加
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
					
					//添加
					
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
					
					jp.repaint();
					timer.cancel();
					
					new TimerDialog().showDialog((JFrame) table.getTopLevelAncestor(), "Success！"+devices_exsit.size()+" Devices in total!", 5);
					
				}else {
					new TimerDialog().showDialog((JFrame) table.getTopLevelAncestor(), "Failed！no device in this router", 5);
				}
				
				
			}
	}
	
	public List<Integer> getRouterhaveDevice(){
		
		return this.router_exsit;
	}

}
