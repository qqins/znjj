package Manager.Listenner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import Model.ConterData;
import Model.DB_csv_device;
import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetTCP;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

public class basicListenner implements PackageListenner {

	//private Packet packet;
	private String hostMAC = null; 
	//private String dataType = "TCP";
	//private ConterData conterData = new ConterData();
	
	private DB_csv_device dbModel = null;
	BlockingQueue<Packet> sendQueue;
	
	
	//标志位
	private int flag = 0;
	
	//construct
	public basicListenner(BlockingQueue<Packet> sendQueue,String hostMac,DB_csv_device dbModel){
		
		this.sendQueue = sendQueue;
		this.hostMAC = hostMac;	
		this.dbModel = dbModel;
	}
	
	public basicListenner(){
		
				
	}

	
	@Override
	public void listenPackage(String  yourMAC,String hostMAC,Packet Packet,String routeMAC,String host_ip) {
		
		
		if(this.hostMAC.equals(hostMAC)){
			
			if(Packet instanceof TCPPacket){
				
				TCPPacket tcpPacket =(TCPPacket)Packet;
				
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
				   
				   //主机到服务器数据
				   if(tcpPacket.src_ip.getHostAddress().equals(host_ip)){
					   
					   if(seq != 0){
						   
						   if(this.dbModel!=null){
							   
							   if(dbModel.getAthome().length>0&&flag==0){
								   Model_a model_a = new Model_a();
								   
								   //填充数据
								   model_a.setData_byte(dbModel.getAthome());
								   
								   System.out.println("伪造发往设备主机的数据中.....");
								   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
							    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
							    			 yourMAC, hostMAC, model_a);
								   //
								  // conterData.getCounter_data_list().remove(0);
								   //
								   this.flag=1;
					System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
								    
							   }
							   
							   else {	   
								   //当发送完成反制队列的数据后，如果主机回复包含数据，才继续伪造ACK回复，否则不回复
								   if(tcpPacket.data.length != 0&&flag==1){ 
										   System.out.println("伪造发往设备主机的数据中.....");
										   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
									    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
									    			 yourMAC, hostMAC, null);
							System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 
										   
									flag=2;
									System.out.println("flag:"+flag);
								   }   
							   }
						   }
						   
						  
						  	   
					   }   
				   }
				   
				   //服务器到主机数据
				   else if(tcpPacket.dst_ip.getHostAddress().equals(host_ip)){
					  
				   }
				 
				   if(tcp != null){
					   
					   //this.packet = tcp;
					   
					   try {
						sendQueue.put(tcp);
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
			
			//return this.packet;
			return null;
		}
		
	public BlockingQueue<Packet> getSendQueue() {
		return sendQueue;
	}


	public void setSendQueue(BlockingQueue<Packet> sendQueue) {
		this.sendQueue = sendQueue;
	}


	@Override
	public int successFlag() {
		return this.flag;
	}

}
