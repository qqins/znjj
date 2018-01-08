package Util.DealPcap;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import Model.Model_a;
import Model.Layer.ApplicationLayer;
import Model.Layer.NetworkLayer;
import Model.Layer.WireLess802;

/*
 * //文件头 数据包头+数据报  数据包头 +数据报  .......
 	pcap包结构为：24固定字节的Pcap头+16固定字节的数据头+数据内容（含mac帧、ip帧、tcp帧、http帧等）
  */
public class wireless802_airdump {
	
	/*************************************************
	 * 此方法求出第j帧数据内容的长度
	 * L为前多帧数据内容总和（不包含数据包头），j为需要求出的帧的代号
	 * 此方法建立在流完全复制到数组里
	 *************************************************/
	public int frameLength(byte[] by, int L, int j) {
		// len为返回的第j帧数据内容的长度
		int len = 0; 
		StringBuffer s1 = new StringBuffer();
		
		
		try {
			int s = 20 + 16 * j + L;
			for (int i = s; i < s + 4; i++) {
				// System.out.print(Integer.toString((by[i] & 0xff) + 0x100,
				// 16).substring(1) + " ");
				s1.append(Integer.toString((by[s + s + 3 - i] & 0xff) + 0x100, 16).substring(1));
			}
			// System.out.println();
			String s2 = s1.toString();
			len = Integer.parseInt(s2, 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
//System.out.println(len + "字节\t");
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数用于判断802.11的类型 /802.11b g 或者其他类型
	 *************************************************/
	public String channelType(int ethloc,byte by[]){
		
		String nettype;
		
		//检查是否为802.11b协议 0xa000
		nettype = Integer.toString((by[ethloc + 12] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 13] & 0xff) + 0x100, 16).substring(1);
//System.out.print(nettype);
		if(nettype.equals("a000")){
			
System.out.print("\t信道类型：802.11b");
			return "802.11b";
		}
		else if(nettype.equals("8004")){
			System.out.print("\t信道类型：802.11g");
			return "802.11g";
		}
		else {
			System.out.print("\t信道类型：other");
			return "other";
		}
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数求的802.11的帧类型     起始位置+18
	 *************************************************/
	public String frametype(byte by[],int ethloc){

		String datatype = Integer.toString((by[ethloc] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		String bString = wireless802_airdump.hexString2binaryString(datatype);
		
//System.out.print("1.Frame Control field 2字节 16bit 当前值"+datatype +" >>> "+ bString+": ");		
		//取到表示子帧类型的bit值
		String subdatatype = bString.substring(0, 4);
//System.out.print("	 子帧类型:"+subdatatype);
		
		String datatype2 = bString.substring(4, 6);
		String version = bString.substring(6, 8);
		if(datatype2.equals("00")){
//System.out.print("\t帧类型:管理帧00");
//System.out.print("	版本:"+version);
			return "00";
		}
		else if(datatype2.equals("01")){
//System.out.print("\t帧类型:控制帧01");
//System.out.print("	版本:"+version);
			return "01";
		}
		else {
//System.out.print("\t帧类型:数据帧10");
//System.out.print("	版本:"+version);
			return "10";
		}
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数求出传输时间字段
	 *************************************************/
	public String duration(byte by[],int ethloc){
		
		String duration = Integer.toString((by[ethloc+2] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 3] & 0xff) + 0x100, 16).substring(1);
		String bString = wireless802_airdump.hexString2binaryString(duration);
//System.out.print("2.Duration time 2字节 16bit 当前值"+duration +" >>> "+ bString);	
		return bString;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数求出各类帧的收发MAC地址+28
	 *************************************************/
	public String macAddress(String frametype,byte by[],int ethloc){
		//管理帧
		if(frametype.equals("00")){
			
			StringBuilder sb = new StringBuilder();
			ethloc += 28;
			sb.append("	TansAddress(souAd)");
			for (int m = 0; m < 6; m++) {	
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
			
			sb.append("	BSSID");
			for (int n = 6; n < 12; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
				//System.out.print(":" + Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//sb.deleteCharAt(0);		
			System.out.print(sb.toString());
			return sb.toString();
		}
		
		//数据帧
		else if(frametype.equals("10")){
			ethloc += 4;
			StringBuilder sb = new StringBuilder();
			//System.out.print("\tTansAddress(BSSID)");
			for (int m = 0; m < 6; m++) {	
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.print("3.ReceiverAddress 6字节 48bit 当前值"+sb.toString());	
//System.out.println("");
//System.out.print("  DestinationAddress 6字节 48bit 当前值(与3共用)");
//System.out.println("");		
			for (int n = 6; n < 12; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.print("4.TransmitterAddress 6字节 48bit 当前值"+sb.substring(18, 36));
//System.out.println("");
//System.out.print("  BSSID 6字节 48bit 当前值(与5共用)");
//System.out.println("");

			for (int n = 12; n < 18; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.print("5.SourceAddress 6字节 48bit  当前值"+sb.substring(36, 54));
			return sb.toString();
		}
		
		//控制帧  未区分RTS ACK等
		else if(frametype.equals("01")){
			ethloc += 22;
			StringBuilder sb = new StringBuilder();
			sb.append("	ReceiverAddress(.)");
			//System.out.print("\tReceiverAddress(.)");
			for (int m = 0; m < 6; m++) {
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
				//System.out.print(":" + Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
			
			System.out.print(sb.toString());
			return sb.toString();
		}
		return null;
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数处理fraNum和seqNum
	 *************************************************/
	
	public void fraNum(byte by[],int ethloc){
		ethloc += 22;
		StringBuilder sb = new StringBuilder();
		for (int m = 0; m < 2; m++) {
			sb.append(Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));

		}
		
			
//System.out.println("6.fragment num和sequence num 2字节 16bit 当前值:"+sb.toString());
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数处理fraNum和seqNum
	 *************************************************/
	
	public void qosControl(byte by[],int ethloc){
		ethloc += 24;
		StringBuilder sb = new StringBuilder();
		for (int m = 0; m < 2; m++) {
			sb.append(Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));

		}
		
			
//System.out.println("7.Qos control 2字节 16bit 当前值:"+sb.toString());
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数用于处理逻辑链路控制层数据中的DSAP
	 *************************************************/
	public void dsap(byte by[],int ethloc){
		ethloc += 26;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
		
//System.out.println("8.DSAP 1字节 8bit 当前值:"+sb.toString()+">>>"+bString);	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数用于处理逻辑链路控制层数据中的SSAP
	 *************************************************/
	public void ssap(byte by[],int ethloc){
		ethloc += 27;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
//System.out.println("9.SSAP 1字节 8bit 当前值:"+sb.toString()+">>>"+bString);	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数用于处理逻辑链路控制层数据中的control field
	 *************************************************/
	public void controlField(byte by[],int ethloc){
		ethloc += 28;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
//System.out.println("10.control field 1字节 8bit 当前值:"+sb.toString()+">>>"+bString);	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数用于处理逻辑链路控制层数据中的organizationCode
	 *************************************************/
	public void organizationCode(byte by[],int ethloc){
		ethloc += 29;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<3;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
//System.out.println("11.organization code 3字节 24bit 当前值:"+sb.toString());	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数用于处理逻辑链路控制层数据中的organizationCode
	 *************************************************/
	public void ipType(byte by[],int ethloc){
		ethloc += 32;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		
		//if(sb.toString().equals("0800"))
//System.out.println("12.IP type 2字节 16bit 当前值:"+sb.toString()+"(IPV4)");	
		//else
//System.out.println("12.IP type 2字节 16bit 当前值:"+sb.toString());	
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数用于处理IP层数据中的头部版本号和头部长度
	 *************************************************/
	public int ipHeader(byte by[],int ethloc){
		ethloc += 34;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
		bString.substring(0, 4);
//System.out.println("13.IP 版本号+IP头长度 1字节 8bit 当前值:"+sb.toString()+">>>"+bString+"	      前4位:"+bString.substring(0, 4)+"	后4位:"+bString.substring(4, 8));	
		
		int ipheadlen = 4 * Integer.parseInt(sb.toString().substring(1), 16);
		return ipheadlen;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数用于处理逻辑链路控制层数据中的differentiated services field
	 *************************************************/
	public void differSerField(byte by[],int ethloc){
		ethloc += 35;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc] & 0xff) + 0x100, 16).substring(1));
		
//System.out.println("14.differentiated services field 1字节 8bit 当前值:"+sb.toString());	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数处理IP层中的total len字段
	 *************************************************/
	
	public int totalLen(byte by[],int ethloc){
		ethloc += 36;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		
		int len = Integer.parseInt(sb.toString(), 16);
//System.out.println("15.total length 2字节 16bit  当前值:"+sb.toString()+"		十进制长度:"+len);	
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数处理IP层中的total len字段
	 *************************************************/
	
	public int identification(byte by[],int ethloc){
		ethloc += 38;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		
		int len = Integer.parseInt(sb.toString(), 16);
//System.out.println("16.identification 2字节 16bit  当前值:"+sb.toString()+"	十进制长度:"+len);	
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数处理IP层中的total len字段
	 *************************************************/
	
	public void flag_ip(byte by[],int ethloc){
		ethloc += 40;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		String bString = hexString2binaryString(sb.toString());
		
//System.out.print("17.flag and fragment offset 2字节 16bit  当前值:"+sb.toString()+" 	前3位flag:"+bString.substring(0, 3) );	
//System.out.print("  Reserved bit"+bString.substring(0, 1));
//System.out.print("  don't fragment"+bString.substring(1, 2));
//System.out.print("  more fragment"+bString.substring(2, 3));
//System.out.println("    后13位位偏移:"+bString.substring(3, 16));
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数处理IP层中的time to live 生存时间
	 *************************************************/
	
	public void timetolive(byte by[],int ethloc){
		ethloc += 42;
		StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
			int len = Integer.parseInt(sb.toString(), 16);
//System.out.println("18.time to live 1字节 8bit  当前值:"+sb.toString()+"	十进制长度:"+len);	
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数处理IP层中的protocol
	 *************************************************/
	
	public String protocol_ip(byte by[],int ethloc){
		ethloc += 43;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		//if(sb.toString().equals("06"))
//System.out.println("19.protocol(封装的下一层) 1字节 8bit  当前值:"+sb.toString()+"(TCP)");	
		//else
//System.out.println("19.protocol(封装的下一层) 1字节 8bit  当前值:"+sb.toString());	
		return sb.toString();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 该函数处理IP层中的checksum字段
	 *************************************************/
	
	public void headerChecksum(byte by[],int ethloc){
		ethloc += 44;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}

//System.out.println("20.header checksum 2字节 16bit  当前值:"+sb.toString());	
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数处理IP层中的源IP地址
	 *************************************************/
	public String ipAddress_sou(byte by[],int ethloc){
		
		ethloc += 46;
		StringBuilder sb = new StringBuilder();
		for (int f = 0; f < 4; f++) {
			sb.append(Integer.parseInt(Integer.toString((by[ethloc + f] & 0xff) + 0x100, 16).substring(1), 16));
			if(f !=3 ){
				sb.append(".");
			}
		}

//System.out.println("21.ipAddress_sou 4字节 32bit  当前值:"+sb.toString());
		return sb.toString();
		
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数处理IP层中的目的IP地址
	 *************************************************/
	public String ipAddress_des(byte by[],int ethloc){
		
		ethloc += 50;
		StringBuilder sb = new StringBuilder();
		for (int f = 0; f < 4; f++) {
			sb.append(Integer.parseInt(Integer.toString((by[ethloc + f] & 0xff) + 0x100, 16).substring(1), 16));
			if(f !=3 ){
				sb.append(".");
			}
		}

//System.out.println("22.ipAddress_des 4字节 32bit  当前值:"+sb.toString());
		return sb.toString();
		
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数处理TCP层中的端口号
	 *************************************************/
	
	public String port_sou(int ipheadlen,int ethloc,byte by[]){
		
		ethloc+=34;
		
			StringBuffer port1 = new StringBuffer();
			for (int i = 0; i < 2; i++) {
				port1.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("23.tcp sou_port 2字节 16bit 当前值："+Integer.parseInt(port1.toString(), 16));
			
			return port1.toString();
			
		}


		public String port_des(int ipheadlen,int ethloc,byte by[]){
	
			ethloc+=36;
			StringBuffer port2 = new StringBuffer();
			for (int i = 0; i < 2; i++) {
				port2.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("24.tcp des_port 2字节 16bit 当前值："+Integer.parseInt(port2.toString(), 16));
			return port2.toString();
			
		}
		/*************************************************
		 * end
		 *************************************************/
	
		
		
		
		
		/*************************************************
		 * 该函数求出seq num 和ACK num
		 *************************************************/
		public long seqNumber(int ethloc,int ipheadlen,byte by[]){
			ethloc+=38;
			StringBuffer seq = new StringBuffer();
			for (int i = 0; i < 4; i++) {
				seq.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("25.tcp seqNumber 4字节 32bit 当前值:"+Long.parseLong(seq.toString(), 16));
			return Long.parseLong(seq.toString(), 16);
		}

		public long ackNumber(int ethloc,int ipheadlen,byte by[]){
			ethloc+=42;
			StringBuffer seq = new StringBuffer();
			for (int i = 0; i < 4; i++) {
				seq.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("26.tcp ackNumber 4字节 32bit 当前值:"+Long.parseLong(seq.toString(), 16));
			return Long.parseLong(seq.toString(), 16);
		}


		/*************************************************
		 * end
		 *************************************************/
	
	
	
	
	/*************************************************
	 * 该函数求出TCP头部长度
	 *************************************************/
	public int tcpHeadlen(byte by[],int ethloc,int ipheadlen){
		
			ethloc += 46;
			String x1 = Integer.toString((by[ethloc + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1);
			String x2 = x1.substring(1) + x1.substring(0, 1);
			int tcpheadlen = 4 * Integer.parseInt(x2, 16);
//System.out.println("27.tcp headerLen 1字节  8bit  当前值 ："+x1+"  十进制长度:"+tcpheadlen);
	
			return tcpheadlen;
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数对TCP类型进行判断(SYN  SYN/ACK ACK PUSH/ACK FIN)
	 *************************************************/
	public String tcpType(String nettype,int ethloc,byte by[],int ipheadlen){
		
		if(nettype.equals("06")){
			ethloc+=47;
			String tcpType = Integer.toString((by[ethloc + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1);
			
			//02是SYN标志
			if(tcpType.equals("02")){
//System.out.println("28.tcp flags 1.5字节  12bit  当前值 ："+tcpType +"(SYN)");	
			}
			
			//12为SYN/ACK
			else if(tcpType.equals("12")){
				
//System.out.println("28.tcp flags 1.5字节  12bit  当前值 ："+tcpType +"(SYN/ack)");				
			}
			//10为ACK
			else if(tcpType.equals("10")){
//System.out.println("28.tcp flags 1.5字节  12bit  当前值 ："+tcpType +"(ack)");	
			}
			//18为PUSH/ACK
			else if(tcpType.equals("18")){
//System.out.println("28.tcp flags 1.5字节  12bit  当前值 ："+tcpType +"(push/ack)");		
			}
			//11为FIN/ACK结束传输
			else if(tcpType.equals("11")){
		
//System.out.println("28.tcp flags 1.5字节  12bit  当前值 ："+tcpType +"(FIN)");
			}
			
			return tcpType;
			
		}
		return "0";
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数处理TCP windowSIze
	 *************************************************/
	public int windowSize(byte by[],int ethloc,int ipheadlen){
		
		ethloc+=48;
		StringBuffer seq = new StringBuffer();
		for (int i = 0; i < 2; i++) {
			seq.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1));
		}
//System.out.println("29.tcp windowSize 2字节 16bit 当前值:"+Integer.parseInt(seq.toString(), 16));
		
		return Integer.parseInt(seq.toString(), 16);	
	}
		
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * 该函数处理帧中携带的数据data_tcp
	 *************************************************/
	public List<String> data_tcp(byte by[],int ethloc,int ipheader,int tcpheader,int len){
		
		
		//确认数据起始位置
		
		
		ethloc += 34 + ipheader+tcpheader;
		//确认数据长度	
		int dataLen = len - (34 + ipheader+tcpheader);
		StringBuilder sb = new StringBuilder();
		List<String> ls = new ArrayList<String>();
		
		if(dataLen >0 ){
			
			for (int f = 0; f < dataLen; f++) {
				
				sb.append(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));	
				ls.add(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.println("30.Data 共:"+dataLen+"字节");	

			for(int i = 0;i<ls.size();i++){
				
//System.out.print("  "+ls.get(i));	
				if((i+1)%16 ==0 && i>0){	
//System.out.println(" ");	
				}
			}
//System.out.println(" ");	
		}
		return ls;	
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	
	
	/*************************************************
	 * 该函数处理帧中携带的数据data_udp
	 *************************************************/
	public List<String> data_udp(byte by[],int ethloc,int ipheader,int udplen){
		
			
		//确认数据起始位置
		ethloc += 34 + ipheader+8;
		//确认数据长度	
		int dataLen = udplen-8;
				
		StringBuilder sb = new StringBuilder();
		List<String> ls = new ArrayList<String>();
		
		if(dataLen >0 ){
			
			for (int f = 0; f < dataLen; f++) {
				
				sb.append(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));	
				ls.add(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.println("30.Data 共:"+dataLen+"字节");	

			/*for(int i = 0;i<ls.size();i++){
				
//System.out.print("  "+ls.get(i));	
				if((i+1)%16 ==0 && i>0){	
//System.out.println(" ");	
				}
			}*/
//System.out.println(" ");	
		}
		return ls;	
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	
	/*************************************************
	 * 该函数处理帧中携带的数据data_byte
	 *************************************************/
	public byte[] data_byte_tcp(byte by[],int ethloc,int ipheader,int tcpheader,int len){
		
		
		//确认数据起始位置
		
		
		ethloc += 34 + ipheader+tcpheader;
		//确认数据长度	
		int dataLen = len - (34 + ipheader+tcpheader);
		byte[] data_byte = null;
		if(dataLen >0 ){
			
			data_byte = new byte[dataLen];
			
			for (int f = 0; f < dataLen; f++) {
				
				data_byte[f] = by[ethloc + f ];
			}	
		}
		return data_byte;
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数处理帧中携带的数据data_byte_udp
	 *************************************************/
	public byte[] data_byte_udp(byte by[],int ethloc,int ipheader,int udplen){
		
		byte[] data_byte = null;
		//确认数据起始位置
		ethloc += 34 + ipheader+8;
		//确认数据长度	
		int dataLen = udplen-8;
		
		if(dataLen >0 ){
			
			data_byte = new byte[dataLen];
			
			for (int f = 0; f < dataLen; f++) {
				
				data_byte[f] = by[ethloc + f ];
			}	
		}
		
//System.out.println("26 数据长度:"+data_byte.length);
		return data_byte;
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	
	/*************************************************
	 * 该函数用于判断被802.11封装的真实协议类型+48
	 *************************************************/
	public String nettype(int ethloc,byte by[]){
		ethloc += 48; //(48) type
		String nettype;
		nettype = Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		
		if(nettype.equals("0806")){
			System.out.print("	实际封装协议:");
			System.out.print("ARP协议  ("+nettype+")");
			return "0806";
		}
		else if(nettype.equals("86dd")){
			System.out.print("	实际封装协议:");
			System.out.print("IPv6协议(SSDP,DHCPV6) ("+nettype+")");
			return "86dd";
		}
		else if(nettype.equals("0800")) {
			System.out.print("	实际封装协议:");
			System.out.print("IPV4协议(NBNS,LLMNR,IGMPv3,QICQ) ("+nettype+")");
			return "0800";
		}
		return "is802.11";
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数把16进制的转化为2进制
	 *************************************************/
	public static  String hexString2binaryString(String hexString)
	{
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++)
		{
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(hexString
							.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * 该函数求出udp长度
	 *************************************************/
	public int udpHeadlen(byte by[],int ethloc,int ipheadlen){
		
			ethloc += 38;
			
			StringBuilder len = new StringBuilder(); 
			for(int i = 0;i<2;i++){
				len.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));	
			}
			
		/*	String x1 = Integer.toString((by[ethloc + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1);
			String x2 = x1.substring(1) + x1.substring(0, 1);*/
			
			int udpheadlen = Integer.parseInt(len.toString(), 16);
			
//System.out.println("25.udp headerLen 2字节  8bit  当前值 ："+len+"  十进制长度:"+udpheadlen);
	
			return udpheadlen;
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数读取radiotap header长度
	 *************************************************/
	public int radioHead(int ethloc ,byte by[]){
		String ss;
		ss = Integer.toString((by[ethloc + 3 ] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 2] & 0xff) + 0x100, 16).substring(1);
		
		int radioHead = Integer.parseInt(ss, 16);
System.out.print("   radiotap headLen:"+radioHead);		
		return radioHead;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数读取管理标志flag
	 *************************************************/
	public String flags(int ethloc,byte by[]){
		
		
		String ss = Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
				
		String bString = wireless802_airdump.hexString2binaryString(ss);
		
//System.out.print("	flags:"+bString);

//System.out.print("	oidFlag "+bString.substring(0, 1));			
//System.out.print("	protect "+bString.substring(1, 2));	
//System.out.print("	moreData "+bString.substring(2, 3));	
//System.out.print("	PWR_mgt "+bString.substring(3, 4));	
//System.out.print("	retry "+bString.substring(4, 5));	
//System.out.print("	moreFragments "+bString.substring(5, 6));	

		String to = bString.substring(6, 8);
		if(to.equals("01")){
//System.out.print(" ToAP "+to);		
		}
		else if(to.equals("10")){
//System.out.print(" FromAP "+to);		
		}
		else{
//System.out.print(" NOdes 00 ");	
		}
		return to;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 主函数static void main(String[] args)
	 * List<Model_a> ergodic()
	 *************************************************/
	public List<Model_a> ergodic(String fileName,String hostIP,String net,File file) {
		
		/*************************************************
		 * 定义参数区
		 *************************************************/
		//File file1 = new File("F:\\1122.pcap");
		File file1 = null;
		if(file == null){
			file1 = new File("F:\\test_pcap\\"+fileName);
		}
		else{
			file1 = file;
		}
		//File file2 = new File("F:\\result.txt");
		int ss = 24;
		int L = 0;
		int len;
		int j = 1;
		int ethloc;
		
		
		int fragmentNum;
		int sequenceNum;
		int ipHeaderLen;
		int totalLen_ip;
		int tcpHeaderLen = 0;
		int windowSize = 0;
		int udplen = 0;
		
		long seqnumber = 0;
		long acknumber = 0;
		
		
		// String iplen;
		String channeltype;
		String nettype;
		String frametype;
		String macAddress;
		String flags;
		String duration;
		String ipAddress_sou;
		String ipAddress_des;
		String port_sou = null;
		String port_des = null;
		String tcpflag = null;
		String TOorFrom;
		
		List<Model_a> models = new ArrayList<Model_a>();
		List<String> dataList = new ArrayList<String>();
		byte[] data_byte = null;
		/*************************************************
		 * end
		 *************************************************/
		
		
		/*************************************************
		 * 数据初始化部分
		 *************************************************/
		wireless802_airdump wireless802 = new wireless802_airdump();
		try {
//			if (!file2.exists()) {
//				file2.createNewFile();
//			}
			FileInputStream fis = null;
			while(!file1.renameTo(file1)){
//System.out.println("文件被占用");
			}
			fis = new FileInputStream(file1);
//			FileOutputStream fos = new FileOutputStream(file2);
			int size = fis.available();
			//System.out.println("总" + size+"字节");
			byte by[] = new byte[size];
			fis.read(by);
			fis.close();
		/*************************************************
		 * end
		 *************************************************/
			
			/*************************************************
			 * 循环开始解析每一帧数据
			 *************************************************/
			while (ss < size) {
				//System.out.print("\n" + "第" + j + "帧（已去掉数据包头）：");
				
				//第一次循环时  by是读进来的数据,L=0，j=1,先求出每一帧的长度
				len = wireless802.frameLength(by, L, j);
				
				// 当前帧数据内容起始位置，去掉pcap头和数据包头
				ethloc = 40 + L + 16 * (j - 1);
				
				//System.out.println("<<--802.11-->>");
				//在airdump下抓取的802.11帧属于最底层的帧，没有radioheard头部的封装
				//判断帧类型
				frametype = wireless802.frametype(by, ethloc);
				
				//读取管理信息
				TOorFrom = wireless802.flags(ethloc, by);
//System.out.println("");
			

				//只对802.11的数据帧进行处理,管理控制帧暂不处理
				if(frametype == "10"){
					//传输时长
					duration = wireless802.duration(by, ethloc);
//System.out.println("");	
			

				//在数据帧中，表示MAC地址的有3个字段，但实际中共有5个MAC显示 以下3到5字段是对MAC地址的处理
					macAddress = wireless802.macAddress(frametype, by, ethloc);
//System.out.println("");	
				

				//fragNumber.sequenceNUm以及QOS control
				//暂时没有具体处理
					wireless802.fraNum(by, ethloc); 
					wireless802.qosControl(by, ethloc);

					//System.out.println("<<--802.11 end-->>");
				
					if(len > 24){
						//System.out.println("<<--logical link control-->>");
						//DSAP
						wireless802.dsap(by, ethloc);
						//SSAP
						wireless802.ssap(by, ethloc);
						//control field
						wireless802.controlField(by, ethloc);
						//organization code
						wireless802.organizationCode(by, ethloc);
						//ipType
						wireless802.ipType(by, ethloc);
						//System.out.println("<<--logical link control end-->>");
						
						
						//System.out.println("<<--IP(IPV4)-->>");
						//版本和IP头长度
						ipHeaderLen = wireless802.ipHeader(by, ethloc);
						//differentiated services field
						wireless802.differSerField(by, ethloc);
						//total len
						totalLen_ip = wireless802.totalLen(by, ethloc);
						
						//identification
						wireless802.identification(by, ethloc);
						
						//flag_ip和位偏移共2字节 前者占3位 后者13位
						wireless802.flag_ip(by, ethloc);
						
						//timeTolive生存时间
						wireless802.timetolive(by, ethloc);
						
						//网络层（下一层协议）
						nettype = wireless802.protocol_ip(by, ethloc);
						
						//header checksum
						wireless802.headerChecksum(by, ethloc);
						
						//sourceIP
						ipAddress_sou = wireless802.ipAddress_sou(by, ethloc);
						
						//destinationIP
						ipAddress_des = wireless802.ipAddress_des(by, ethloc);
						
						//System.out.println("<<--IP(IPv4) end-->>");
						
						if(nettype.equals("06")){
							//System.out.println("<<--TCP-->>"+"  "+j);
							
							//port
							port_sou = wireless802.port_sou(ipHeaderLen, ethloc, by);
							port_des = wireless802.port_des(ipHeaderLen, ethloc, by);
							
							//seqNum
							seqnumber = wireless802.seqNumber(ethloc, ipHeaderLen, by);
							acknumber = wireless802.ackNumber(ethloc, ipHeaderLen, by);
							
							//TCP header Len
							tcpHeaderLen = wireless802.tcpHeadlen(by, ethloc, ipHeaderLen);
							
							//TCP flag
							tcpflag = wireless802.tcpType(nettype, ethloc, by, ipHeaderLen);
							
							//windowzize
							windowSize = wireless802.windowSize(by, ethloc, ipHeaderLen);
							
							//System.out.println("<<--TCP end-->>");
							
							if(tcpflag.equals("18")){
								//System.out.println("<<-- Data -->>");
								//data 
								dataList = wireless802.data_tcp(by, ethloc, ipHeaderLen, tcpHeaderLen, len);
								data_byte = wireless802.data_byte_tcp(by, ethloc, ipHeaderLen, tcpHeaderLen, len);
								//System.out.println("<<--Data end-->>");
								
							}
							
							
						}else if(nettype.equals("11")){
							
							//System.out.println("<<--udp-->>"+"  "+j);
							//port
							port_sou = wireless802.port_sou(ipHeaderLen, ethloc, by);
							port_des = wireless802.port_des(ipHeaderLen, ethloc, by);
							udplen = wireless802.udpHeadlen(by, ethloc, ipHeaderLen);
							data_byte = wireless802.data_byte_udp(by, ethloc, ipHeaderLen, udplen);
							dataList = wireless802.data_udp(by, ethloc, ipHeaderLen, udplen);
							//System.out.println("end");
							
						}
						
						//数据装载 装载指定类型的数据
						if(nettype.equals(net) && ipAddress_sou.equals(hostIP) || ipAddress_des.equals(hostIP)){
							Model_a model = new Model_a();
							ApplicationLayer aLayer = new ApplicationLayer();
							NetworkLayer nLayer = new NetworkLayer();
							WireLess802 wLayer = new WireLess802(); 
							
							//tcp层模型
							if(net.equals("06")){
								aLayer.setAcknumber(acknumber);
								aLayer.setSeqnumber(seqnumber);
								aLayer.setPort_des(Integer.parseInt(port_des.toString(),16));
								aLayer.setPort_sou(Integer.parseInt(port_sou.toString(),16));
								aLayer.setTcpflag(tcpflag);
								aLayer.setWindowSize(windowSize);
								aLayer.setHeaderLen(tcpHeaderLen);
								
								if(tcpflag.equals("18")){
									if(dataList.size() > 6){
										model.setData(dataList);
										model.setData_byte(data_byte);
									}	
								}
								
							}
							else if(net.equals("11")){
								aLayer.setPort_des(Integer.parseInt(port_des.toString(),16));
								aLayer.setPort_sou(Integer.parseInt(port_sou.toString(),16));
								aLayer.setHeaderLen(udplen);
								model.setData(dataList);
								model.setData_byte(data_byte);		
							}
							
							//ip层
							nLayer.setIp_des(ipAddress_des);
							nLayer.setIp_sou(ipAddress_sou);
							nLayer.setNettype(nettype);
							
							
							//802.11
							wLayer.setTOorFrom(TOorFrom);
							
							
							//帧模型
							model.setIndex(j);
							model.setTotalLen(len);
							model.setaLayer(aLayer);
							model.setnLayer(nLayer);
							model.setwLayer(wLayer);
							
							
							//装入models
							models.add(model);		
						}
						
	
					}
				
				}
				
				//对下一帧起始位置调整
				ss = ss + 16 + len;
				j++;
				L = L + len;
				len = 0;
//System.out.println("");				
			}
			/*************************************************
			 * end
			 *************************************************/
			
			//System.out.println("\n总共帧数：" + (j - 1));
			
			
			fis.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	//返回值	
	return models;
	}

}
