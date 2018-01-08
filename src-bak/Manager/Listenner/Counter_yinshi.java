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
				   
				 
				   //����Ҫα���������α��Ϊ����������������
				   //�ȼ����seq ack
				   
				   long seq = 0;
				   long ack = 0;
				   
				   if(tcpPacket.syn){
					   
					   //SYN����֡
					   
				   }
				   else if(tcpPacket.ack && !tcpPacket.psh){

						   seq = tcpPacket.ack_num;
						   ack = tcpPacket.sequence;       
						   
				   }
				   else if(tcpPacket.ack && tcpPacket.psh){
					   
					   
					   //����SEQ��ACK
					   seq = tcpPacket.ack_num;
					   ack = tcpPacket.sequence + tcpPacket.data.length;	  
				 }   
				   
				   //����������������������ack����330��99����ֱ��ת����������
				   if(tcpPacket.src_ip.getHostAddress().equals(host_ip)){
					   
					   
					   		//if(flag ==1){
					   			//if(tcpPacket.data.length == 0 && tcpPacket.ack&&!tcpPacket.psh){
					   			//α������	
					   
					   				if(flag==0){
					   					int total = 0;
					   					sendPackets.add(tcpPacket);
					   					
					   					for(int i =0;i<sendPackets.size();i++){
					   						
					   						total+=sendPackets.get(i).data.length;
					   					}
					   					System.out.println("�ۼ����ݣ�"+total);
					   					if(total==281){
					   						sendPackets.clear();
					   						////////////////
					   						if(conterData.getCounter_data_list().size()!=0){
							   					
							   					tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
										    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
										    			 yourMAC, hostMAC, changData(conterData.getCounter_data_list().get(0)));
							   					sendPackets.add(tcp);
							   					conterData.getCounter_data_list().remove(0);
							   					
							   					System.out.println("�ѽ��������ݼ��뷢�Ͷ���"+"To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
							   				
							   					//�������ݷ�����ɱ�־
							   					flag = 1;
							   				}
					   						//////////////
					   					}
					   				}
					   
					   				
					   				
					   			//}
					   			
					   		//}
					   		
					   	/* 	else if(flag == 2){
					   			
					   			//flag=2�� ��ʼα�����������ͣ���ʱֻα��ACK����
					   			//������ݷ��ͺ����յ�����֡��ظ�ACK
					   			if(tcpPacket.data.length>0){
					   				
					   				tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
							    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
							    			 yourMAC, hostMAC,null);
				   					sendPackets.add(tcp);
					   				
				   					System.out.println("α��ACK����To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);	
					   			}
							}
					   		
					   	
					  		else {
							  System.out.println("�ȴ�ת������......");
							  tcp =  st.fakePacket( tcpPacket.src_port, tcpPacket.dst_port,
						    			 tcpPacket.sequence, tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
						    			 yourMAC, routeMAC, new Model_a().setData_byte(tcpPacket.data));
							   
							   sendPackets.add(tcp);
							   System.out.println("�ȴ����� To Service   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						   }*/
				   }
				   
				   
				   //��������������������
				   //���ץȡ��216֡ ��α�����ݣ�ץ��80��ACK����ת��
				   else if(tcpPacket.dst_ip.getHostAddress().equals(host_ip)){/*
					   
					   if(flag == 2){
						   
						   //��ʱ����α��������ɺ�ʱ��ķ�������
					   }
					   else if(tcpPacket.data.length == 0&&tcpPacket.ack&&!tcpPacket.psh){
						  System.out.println("׼��ת������......");
						  tcp =  st.fakePacket( tcpPacket.src_port, tcpPacket.dst_port,
					    			 tcpPacket.sequence, tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
					    			 yourMAC, routeMAC, new Model_a().setData_byte(tcpPacket.data));
						   
						   sendPackets.add(tcp);
						   System.out.println("ת������To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						  
						  
					  }else if(tcpPacket.data.length == 142){
						
						  System.out.println("��������......flag=1");
						  tcp =  st.fakePacket( tcpPacket.src_port, tcpPacket.dst_port,
					    			 tcpPacket.sequence, tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
					    			 yourMAC, routeMAC, new Model_a().setData_byte(tcpPacket.data));
						   
						   sendPackets.add(tcp);
						   System.out.println("ת������To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						   
						   
						   //���յ�142֡�Ժ�ͬʱα������
						   if(conterData.getCounter_data_list().size()!=0){
							   
							   Model_a m = changData(conterData.getCounter_data_list().get(0));
							   
			   					tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 (tcpPacket.sequence+tcpPacket.data.length), tcpPacket.ack_num,  tcpPacket.src_ip.getHostAddress(),tcpPacket.dst_ip.getHostAddress(),
						    			 yourMAC, hostMAC, m);
			   					sendPackets.add(tcp);
			   					conterData.getCounter_data_list().remove(0);
			   					
			   					System.out.println("�ѽ��������ݼ��뷢�Ͷ���"+"To host   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
			   				
			   					//�������ݷ�����ɱ�־
			   					flag = 2;
			   				}
						   
						   //����������־
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
							   
							   
							   System.out.println("�ۼ�����:"+total);
							   
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
	 * ��ȡ�����ļ�prameter.properties
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
	           
	           
	           
	         //����Ҫʶ��һ����������·������������������ݣ�����������������ͨ��
				
				List<Model_a> ls = w.ergodic(COUNTER_DATASOURCE);
				List<Model_a> counter_data_list = w.getFrameByindex(INT_INDEX,ls);
				
				if(counter_data_list.size() != 0){
					System.out.println("�����ɹ������ɷ������ݶ���........");
					System.out.println("length:"+counter_data_list.size());
					conterData.setCounter_data_list(counter_data_list);
				}else {
					System.out.println("���ƶ��л�ȡʧ��");
				}
				
				if(HEART_INDEX_UP !=null && HEART_INDEX_UP.length != 0 ){
					List<Model_a> counter_heart_list_toService = w.getFrameByindex(HEART_INDEX_UP, ls);	
					if(counter_heart_list_toService.size() != 0){
						System.out.println("�����ɹ�������α����������toService........");
						System.out.println("length:"+counter_heart_list_toService.size());
						conterData.setCounter_heart_list_toService(counter_heart_list_toService);
					}
				}else {
					System.out.println("δ������������ģʽ����������toServiceδ����");
				}
				
				if(HEART_INDEX_DOWN !=null && HEART_INDEX_DOWN.length != 0 ){
					List<Model_a> counter_heart_list_toHost = w.getFrameByindex(HEART_INDEX_DOWN, ls);	
					if(counter_heart_list_toHost.size() != 0){
						System.out.println("�����ɹ�������α����������toHost........");
						System.out.println("length:"+counter_heart_list_toHost.size());
						conterData.setCounter_heart_list_toHost(counter_heart_list_toHost);
					}
				}else {
					System.out.println("δ������������ģʽ����������toHostδ����");
				}
				
				
				if( STATUS_INDEX_TOSERVICE != null && STATUS_INDEX_TOSERVICE.length != 0){
					List<Model_a> fake_status_list_toService = w.getFrameByindex(STATUS_INDEX_TOSERVICE, ls);	
					if(fake_status_list_toService.size() != 0){
						System.out.println("�����ɹ�������α��״̬����toService........");
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
