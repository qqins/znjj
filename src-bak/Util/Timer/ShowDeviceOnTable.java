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

public class ShowDeviceOnTable extends TimerTask {
	
	
	private JTable table;
	private int index = 0;
	private Timer timer;
	private JScrollPane jp;
	private List<Device_model> devices_exsit;
	List<Routing_model> routings = new ArrayList<Routing_model>();
	
	//private List<Routing_model> route_exsit = new ArrayList<Routing_model>();
	
	private List<Integer> router_exsit = new ArrayList<Integer>();
	
	public ShowDeviceOnTable(Recognation_device regDevice,JTable table,Timer timer,JScrollPane jp) {
		
		this.routings = regDevice.getRoutings();
		this.table = table;
		this.timer = timer;
		this.jp = jp;
		this.devices_exsit = regDevice.getDevices_exsit(); 
	}

	@Override
	public void run() {
			if(index < routings.size()){
				
				if(routings.get(index).getDevices()!=null){
					
					//�ȼ�����ڵ�·������route_exsit
					//route_exsit.add(routings.get(index));
					router_exsit.add(index);
//System.out.println("3r3w4rwerfwe:"+index+"fsdfsdfs"+routings.get(index).getName());
					//home_device������
					int deviceNum = routings.get(index).getDevices().size();	
					table.setValueAt(routings.get(index).getDevices().get(0).getName(), index, table.getColumnCount()-1);
					if(deviceNum>1) {
						
						DefaultTableModel dt = (DefaultTableModel)table.getModel();
						for(int i = 0 ;i<(deviceNum-1);i++){
							
							dt.addColumn("home_device");	
							table.setValueAt(routings.get(index).getDevices().get(i+1).getName(), index, table.getColumnCount()-1);
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
					
					
				}else{
					
					table.setValueAt("Null Device!", index, 4);
				}
					
				int rowCount = table.getColumnCount(); 
				table.getSelectionModel().setSelectionInterval(rowCount-1 , rowCount-1); 
				Rectangle rect = table.getCellRect(index ,table.getColumnCount()-1 , true);  
				
				jp.repaint();
				
				//����
				//jp.updateUI();
				//SwingUtilities.updateComponentTreeUI(table);	
				//SwingUtilities.invokeLater(null);
				table.scrollRectToVisible(rect);
				index++;
			}else {
				
				//table.getTopLevelAncestor();
				//table.updateUI();
				new TimerDialog().showDialog((JFrame) table.getTopLevelAncestor(), "Success��"+devices_exsit.size()+" Devices in total!", 3);
				 //table.getSelectionModel().
				
				
				//���������
				//��ʾ���ڼҾ��豸��router�豸�Լ�������ļҾ��豸
				int row = table.getModel().getRowCount();
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
				jp.repaint();
				
				
				
				
				timer.cancel();
			}
	}
	
	public List<Integer> getRouterhaveDevice(){
		
		return this.router_exsit;
	}

}
