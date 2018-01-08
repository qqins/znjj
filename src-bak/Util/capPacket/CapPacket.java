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
	 //程序启动主方法    
	   public static void main(String args[]){    
	       try{    
	            //获取本机上的网络接口对象数组    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	       // for(int i=0;i<devices.length;i++){}   
	     	int i = 0;
	        NetworkInterface nc=devices[i]; 
	        //创建某个卡口上的抓取对象,最大为2000个    
	        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20);
	        jpcap.setFilter("ip and tcp", true);
	        startCapThread(jpcap);    
	        System.out.println("开始抓取第"+i+"个卡口上的数据");    
     
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("启动失败:  "+ef);    
	        }    
	    
	   }   
	   
	   
	   public void capPacket_run(int i){
		    
	       try{    
	            //获取本机上的网络接口对象数组    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	       // for(int i=0;i<devices.length;i++){}   
	     	//int i = 0;
	    	   
	    	   
	        NetworkInterface nc=devices[i];    
	        //创建某个卡口上的抓取对象,设置最大一次性抓包大小为2000byte
	        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20); 
	        jpcap.setFilter("ip and tcp", true);
	        startCapThread(jpcap);    
	        System.out.println("开始抓取第"+i+"个卡口上的数据");    
     
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("启动失败:  "+ef);    
	        }      
	   }
	    //将Captor放到独立线程中运行    
	   public static void startCapThread(final JpcapCaptor jpcap ){    
	       JpcapCaptor jp=jpcap;    
	       java.lang.Runnable rnner=new Runnable(){      

			public void run(){    
	               //使用接包处理器循环抓包-1表示无限制的抓取数据包   "192.168.8.101","119.29.42.117"
	        	   while(true){
	        		  TestPacketReceiver pr = new TestPacketReceiver("192.168.8.104","E4:D3:32:E0:7B:82");
	        		  jpcap.loopPacket(1, pr);
	        		  
	        		  //拿到抓取到的，指定的TCPmodel 需要对model中的数据进行分析和计算并且把计算的数据返回出线程
	        		  if(pr.getModel() != null){
	        			model =  pr.getModel();
	        		  } 
	        	   }    
	           }    
	       };    
	       new Thread(rnner).start();//启动抓包线程    
	   }

      
 }    
	    
	
