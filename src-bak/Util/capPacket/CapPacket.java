package Util.capPacket;

import Model.Model_Capture;
import Model.*;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.DatalinkPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

public class CapPacket {

	
		static Model_Capture model;
	 //��������������    
	   public static void main(String args[]){    
	       try{    
	            //��ȡ�����ϵ�����ӿڶ�������    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	       // for(int i=0;i<devices.length;i++){}   
	     	int i = 0;
	        NetworkInterface nc=devices[i]; 
	        //����ĳ�������ϵ�ץȡ����,���Ϊ2000��    
	        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20);
	        jpcap.setFilter("ip and tcp", true);
	        startCapThread(jpcap);    
	        System.out.println("��ʼץȡ��"+i+"�������ϵ�����");    
     
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("����ʧ��:  "+ef);    
	        }    
	    
	   }   
	   
	   
	   public void capPacket_run(int i){
		    
	       try{    
	            //��ȡ�����ϵ�����ӿڶ�������    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	       // for(int i=0;i<devices.length;i++){}   
	     	//int i = 0;
	    	   
	    	   
	        NetworkInterface nc=devices[i];    
	        //����ĳ�������ϵ�ץȡ����,�������һ����ץ����СΪ2000byte
	        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20); 
	        jpcap.setFilter("ip and tcp", true);
	        startCapThread(jpcap);    
	        System.out.println("��ʼץȡ��"+i+"�������ϵ�����");    
     
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("����ʧ��:  "+ef);    
	        }      
	   }
	    //��Captor�ŵ������߳�������    
	   public static void startCapThread(final JpcapCaptor jpcap ){    
	       JpcapCaptor jp=jpcap;    
	       java.lang.Runnable rnner=new Runnable(){      

			public void run(){    
	               //ʹ�ýӰ�������ѭ��ץ��-1��ʾ�����Ƶ�ץȡ���ݰ�   "192.168.8.101","119.29.42.117"
	        	   while(true){
	        		  TestPacketReceiver pr = new TestPacketReceiver("192.168.8.104","E4:D3:32:E0:7B:82");
	        		  jpcap.loopPacket(1, pr);
	        		  
	        		  //�õ�ץȡ���ģ�ָ����TCPmodel ��Ҫ��model�е����ݽ��з����ͼ��㲢�ҰѼ�������ݷ��س��߳�
	        		  if(pr.getModel() != null){
	        			model =  pr.getModel();
	        		  } 
	        	   }    
	           }    
	       };    
	       new Thread(rnner).start();//����ץ���߳�    
	   }

      
 }    
	    
	
