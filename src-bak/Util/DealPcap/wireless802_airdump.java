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
 * //�ļ�ͷ ���ݰ�ͷ+���ݱ�  ���ݰ�ͷ +���ݱ�  .......
 	pcap���ṹΪ��24�̶��ֽڵ�Pcapͷ+16�̶��ֽڵ�����ͷ+�������ݣ���mac֡��ip֡��tcp֡��http֡�ȣ�
  */
public class wireless802_airdump {
	
	/*************************************************
	 * �˷��������j֡�������ݵĳ���
	 * LΪǰ��֡���������ܺͣ����������ݰ�ͷ����jΪ��Ҫ�����֡�Ĵ���
	 * �˷�������������ȫ���Ƶ�������
	 *************************************************/
	public int frameLength(byte[] by, int L, int j) {
		// lenΪ���صĵ�j֡�������ݵĳ���
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
//System.out.println(len + "�ֽ�\t");
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú��������ж�802.11������ /802.11b g ������������
	 *************************************************/
	public String channelType(int ethloc,byte by[]){
		
		String nettype;
		
		//����Ƿ�Ϊ802.11bЭ�� 0xa000
		nettype = Integer.toString((by[ethloc + 12] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 13] & 0xff) + 0x100, 16).substring(1);
//System.out.print(nettype);
		if(nettype.equals("a000")){
			
System.out.print("\t�ŵ����ͣ�802.11b");
			return "802.11b";
		}
		else if(nettype.equals("8004")){
			System.out.print("\t�ŵ����ͣ�802.11g");
			return "802.11g";
		}
		else {
			System.out.print("\t�ŵ����ͣ�other");
			return "other";
		}
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú������802.11��֡����     ��ʼλ��+18
	 *************************************************/
	public String frametype(byte by[],int ethloc){

		String datatype = Integer.toString((by[ethloc] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		String bString = wireless802_airdump.hexString2binaryString(datatype);
		
//System.out.print("1.Frame Control field 2�ֽ� 16bit ��ǰֵ"+datatype +" >>> "+ bString+": ");		
		//ȡ����ʾ��֡���͵�bitֵ
		String subdatatype = bString.substring(0, 4);
//System.out.print("	 ��֡����:"+subdatatype);
		
		String datatype2 = bString.substring(4, 6);
		String version = bString.substring(6, 8);
		if(datatype2.equals("00")){
//System.out.print("\t֡����:����֡00");
//System.out.print("	�汾:"+version);
			return "00";
		}
		else if(datatype2.equals("01")){
//System.out.print("\t֡����:����֡01");
//System.out.print("	�汾:"+version);
			return "01";
		}
		else {
//System.out.print("\t֡����:����֡10");
//System.out.print("	�汾:"+version);
			return "10";
		}
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú����������ʱ���ֶ�
	 *************************************************/
	public String duration(byte by[],int ethloc){
		
		String duration = Integer.toString((by[ethloc+2] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 3] & 0xff) + 0x100, 16).substring(1);
		String bString = wireless802_airdump.hexString2binaryString(duration);
//System.out.print("2.Duration time 2�ֽ� 16bit ��ǰֵ"+duration +" >>> "+ bString);	
		return bString;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú����������֡���շ�MAC��ַ+28
	 *************************************************/
	public String macAddress(String frametype,byte by[],int ethloc){
		//����֡
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
		
		//����֡
		else if(frametype.equals("10")){
			ethloc += 4;
			StringBuilder sb = new StringBuilder();
			//System.out.print("\tTansAddress(BSSID)");
			for (int m = 0; m < 6; m++) {	
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.print("3.ReceiverAddress 6�ֽ� 48bit ��ǰֵ"+sb.toString());	
//System.out.println("");
//System.out.print("  DestinationAddress 6�ֽ� 48bit ��ǰֵ(��3����)");
//System.out.println("");		
			for (int n = 6; n < 12; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.print("4.TransmitterAddress 6�ֽ� 48bit ��ǰֵ"+sb.substring(18, 36));
//System.out.println("");
//System.out.print("  BSSID 6�ֽ� 48bit ��ǰֵ(��5����)");
//System.out.println("");

			for (int n = 12; n < 18; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.print("5.SourceAddress 6�ֽ� 48bit  ��ǰֵ"+sb.substring(36, 54));
			return sb.toString();
		}
		
		//����֡  δ����RTS ACK��
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
	 * �ú�������fraNum��seqNum
	 *************************************************/
	
	public void fraNum(byte by[],int ethloc){
		ethloc += 22;
		StringBuilder sb = new StringBuilder();
		for (int m = 0; m < 2; m++) {
			sb.append(Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));

		}
		
			
//System.out.println("6.fragment num��sequence num 2�ֽ� 16bit ��ǰֵ:"+sb.toString());
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú�������fraNum��seqNum
	 *************************************************/
	
	public void qosControl(byte by[],int ethloc){
		ethloc += 24;
		StringBuilder sb = new StringBuilder();
		for (int m = 0; m < 2; m++) {
			sb.append(Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));

		}
		
			
//System.out.println("7.Qos control 2�ֽ� 16bit ��ǰֵ:"+sb.toString());
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú������ڴ����߼���·���Ʋ������е�DSAP
	 *************************************************/
	public void dsap(byte by[],int ethloc){
		ethloc += 26;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
		
//System.out.println("8.DSAP 1�ֽ� 8bit ��ǰֵ:"+sb.toString()+">>>"+bString);	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú������ڴ����߼���·���Ʋ������е�SSAP
	 *************************************************/
	public void ssap(byte by[],int ethloc){
		ethloc += 27;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
//System.out.println("9.SSAP 1�ֽ� 8bit ��ǰֵ:"+sb.toString()+">>>"+bString);	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú������ڴ����߼���·���Ʋ������е�control field
	 *************************************************/
	public void controlField(byte by[],int ethloc){
		ethloc += 28;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
//System.out.println("10.control field 1�ֽ� 8bit ��ǰֵ:"+sb.toString()+">>>"+bString);	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú������ڴ����߼���·���Ʋ������е�organizationCode
	 *************************************************/
	public void organizationCode(byte by[],int ethloc){
		ethloc += 29;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<3;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
//System.out.println("11.organization code 3�ֽ� 24bit ��ǰֵ:"+sb.toString());	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú������ڴ����߼���·���Ʋ������е�organizationCode
	 *************************************************/
	public void ipType(byte by[],int ethloc){
		ethloc += 32;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		
		//if(sb.toString().equals("0800"))
//System.out.println("12.IP type 2�ֽ� 16bit ��ǰֵ:"+sb.toString()+"(IPV4)");	
		//else
//System.out.println("12.IP type 2�ֽ� 16bit ��ǰֵ:"+sb.toString());	
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú������ڴ���IP�������е�ͷ���汾�ź�ͷ������
	 *************************************************/
	public int ipHeader(byte by[],int ethloc){
		ethloc += 34;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		String bString = hexString2binaryString(sb.toString());
		bString.substring(0, 4);
//System.out.println("13.IP �汾��+IPͷ���� 1�ֽ� 8bit ��ǰֵ:"+sb.toString()+">>>"+bString+"	      ǰ4λ:"+bString.substring(0, 4)+"	��4λ:"+bString.substring(4, 8));	
		
		int ipheadlen = 4 * Integer.parseInt(sb.toString().substring(1), 16);
		return ipheadlen;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú������ڴ����߼���·���Ʋ������е�differentiated services field
	 *************************************************/
	public void differSerField(byte by[],int ethloc){
		ethloc += 35;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc] & 0xff) + 0x100, 16).substring(1));
		
//System.out.println("14.differentiated services field 1�ֽ� 8bit ��ǰֵ:"+sb.toString());	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú�������IP���е�total len�ֶ�
	 *************************************************/
	
	public int totalLen(byte by[],int ethloc){
		ethloc += 36;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		
		int len = Integer.parseInt(sb.toString(), 16);
//System.out.println("15.total length 2�ֽ� 16bit  ��ǰֵ:"+sb.toString()+"		ʮ���Ƴ���:"+len);	
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������IP���е�total len�ֶ�
	 *************************************************/
	
	public int identification(byte by[],int ethloc){
		ethloc += 38;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		
		int len = Integer.parseInt(sb.toString(), 16);
//System.out.println("16.identification 2�ֽ� 16bit  ��ǰֵ:"+sb.toString()+"	ʮ���Ƴ���:"+len);	
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú�������IP���е�total len�ֶ�
	 *************************************************/
	
	public void flag_ip(byte by[],int ethloc){
		ethloc += 40;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}
		String bString = hexString2binaryString(sb.toString());
		
//System.out.print("17.flag and fragment offset 2�ֽ� 16bit  ��ǰֵ:"+sb.toString()+" 	ǰ3λflag:"+bString.substring(0, 3) );	
//System.out.print("  Reserved bit"+bString.substring(0, 1));
//System.out.print("  don't fragment"+bString.substring(1, 2));
//System.out.print("  more fragment"+bString.substring(2, 3));
//System.out.println("    ��13λλƫ��:"+bString.substring(3, 16));
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������IP���е�time to live ����ʱ��
	 *************************************************/
	
	public void timetolive(byte by[],int ethloc){
		ethloc += 42;
		StringBuilder sb = new StringBuilder();
			sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
			int len = Integer.parseInt(sb.toString(), 16);
//System.out.println("18.time to live 1�ֽ� 8bit  ��ǰֵ:"+sb.toString()+"	ʮ���Ƴ���:"+len);	
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������IP���е�protocol
	 *************************************************/
	
	public String protocol_ip(byte by[],int ethloc){
		ethloc += 43;
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1));
		//if(sb.toString().equals("06"))
//System.out.println("19.protocol(��װ����һ��) 1�ֽ� 8bit  ��ǰֵ:"+sb.toString()+"(TCP)");	
		//else
//System.out.println("19.protocol(��װ����һ��) 1�ֽ� 8bit  ��ǰֵ:"+sb.toString());	
		return sb.toString();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú�������IP���е�checksum�ֶ�
	 *************************************************/
	
	public void headerChecksum(byte by[],int ethloc){
		ethloc += 44;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<2;i++){
			sb.append(Integer.toString((by[ethloc + i] & 0xff) + 0x100, 16).substring(1));
		}

//System.out.println("20.header checksum 2�ֽ� 16bit  ��ǰֵ:"+sb.toString());	
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������IP���е�ԴIP��ַ
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

//System.out.println("21.ipAddress_sou 4�ֽ� 32bit  ��ǰֵ:"+sb.toString());
		return sb.toString();
		
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú�������IP���е�Ŀ��IP��ַ
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

//System.out.println("22.ipAddress_des 4�ֽ� 32bit  ��ǰֵ:"+sb.toString());
		return sb.toString();
		
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������TCP���еĶ˿ں�
	 *************************************************/
	
	public String port_sou(int ipheadlen,int ethloc,byte by[]){
		
		ethloc+=34;
		
			StringBuffer port1 = new StringBuffer();
			for (int i = 0; i < 2; i++) {
				port1.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("23.tcp sou_port 2�ֽ� 16bit ��ǰֵ��"+Integer.parseInt(port1.toString(), 16));
			
			return port1.toString();
			
		}


		public String port_des(int ipheadlen,int ethloc,byte by[]){
	
			ethloc+=36;
			StringBuffer port2 = new StringBuffer();
			for (int i = 0; i < 2; i++) {
				port2.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("24.tcp des_port 2�ֽ� 16bit ��ǰֵ��"+Integer.parseInt(port2.toString(), 16));
			return port2.toString();
			
		}
		/*************************************************
		 * end
		 *************************************************/
	
		
		
		
		
		/*************************************************
		 * �ú������seq num ��ACK num
		 *************************************************/
		public long seqNumber(int ethloc,int ipheadlen,byte by[]){
			ethloc+=38;
			StringBuffer seq = new StringBuffer();
			for (int i = 0; i < 4; i++) {
				seq.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("25.tcp seqNumber 4�ֽ� 32bit ��ǰֵ:"+Long.parseLong(seq.toString(), 16));
			return Long.parseLong(seq.toString(), 16);
		}

		public long ackNumber(int ethloc,int ipheadlen,byte by[]){
			ethloc+=42;
			StringBuffer seq = new StringBuffer();
			for (int i = 0; i < 4; i++) {
				seq.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
						.substring(1));
			}
//System.out.println("26.tcp ackNumber 4�ֽ� 32bit ��ǰֵ:"+Long.parseLong(seq.toString(), 16));
			return Long.parseLong(seq.toString(), 16);
		}


		/*************************************************
		 * end
		 *************************************************/
	
	
	
	
	/*************************************************
	 * �ú������TCPͷ������
	 *************************************************/
	public int tcpHeadlen(byte by[],int ethloc,int ipheadlen){
		
			ethloc += 46;
			String x1 = Integer.toString((by[ethloc + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1);
			String x2 = x1.substring(1) + x1.substring(0, 1);
			int tcpheadlen = 4 * Integer.parseInt(x2, 16);
//System.out.println("27.tcp headerLen 1�ֽ�  8bit  ��ǰֵ ��"+x1+"  ʮ���Ƴ���:"+tcpheadlen);
	
			return tcpheadlen;
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú�����TCP���ͽ����ж�(SYN  SYN/ACK ACK PUSH/ACK FIN)
	 *************************************************/
	public String tcpType(String nettype,int ethloc,byte by[],int ipheadlen){
		
		if(nettype.equals("06")){
			ethloc+=47;
			String tcpType = Integer.toString((by[ethloc + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1);
			
			//02��SYN��־
			if(tcpType.equals("02")){
//System.out.println("28.tcp flags 1.5�ֽ�  12bit  ��ǰֵ ��"+tcpType +"(SYN)");	
			}
			
			//12ΪSYN/ACK
			else if(tcpType.equals("12")){
				
//System.out.println("28.tcp flags 1.5�ֽ�  12bit  ��ǰֵ ��"+tcpType +"(SYN/ack)");				
			}
			//10ΪACK
			else if(tcpType.equals("10")){
//System.out.println("28.tcp flags 1.5�ֽ�  12bit  ��ǰֵ ��"+tcpType +"(ack)");	
			}
			//18ΪPUSH/ACK
			else if(tcpType.equals("18")){
//System.out.println("28.tcp flags 1.5�ֽ�  12bit  ��ǰֵ ��"+tcpType +"(push/ack)");		
			}
			//11ΪFIN/ACK��������
			else if(tcpType.equals("11")){
		
//System.out.println("28.tcp flags 1.5�ֽ�  12bit  ��ǰֵ ��"+tcpType +"(FIN)");
			}
			
			return tcpType;
			
		}
		return "0";
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������TCP windowSIze
	 *************************************************/
	public int windowSize(byte by[],int ethloc,int ipheadlen){
		
		ethloc+=48;
		StringBuffer seq = new StringBuffer();
		for (int i = 0; i < 2; i++) {
			seq.append(Integer.toString((by[ethloc + i + ipheadlen] & 0xff) + 0x100, 16)
					.substring(1));
		}
//System.out.println("29.tcp windowSize 2�ֽ� 16bit ��ǰֵ:"+Integer.parseInt(seq.toString(), 16));
		
		return Integer.parseInt(seq.toString(), 16);	
	}
		
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * �ú�������֡��Я��������data_tcp
	 *************************************************/
	public List<String> data_tcp(byte by[],int ethloc,int ipheader,int tcpheader,int len){
		
		
		//ȷ��������ʼλ��
		
		
		ethloc += 34 + ipheader+tcpheader;
		//ȷ�����ݳ���	
		int dataLen = len - (34 + ipheader+tcpheader);
		StringBuilder sb = new StringBuilder();
		List<String> ls = new ArrayList<String>();
		
		if(dataLen >0 ){
			
			for (int f = 0; f < dataLen; f++) {
				
				sb.append(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));	
				ls.add(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.println("30.Data ��:"+dataLen+"�ֽ�");	

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
	 * �ú�������֡��Я��������data_udp
	 *************************************************/
	public List<String> data_udp(byte by[],int ethloc,int ipheader,int udplen){
		
			
		//ȷ��������ʼλ��
		ethloc += 34 + ipheader+8;
		//ȷ�����ݳ���	
		int dataLen = udplen-8;
				
		StringBuilder sb = new StringBuilder();
		List<String> ls = new ArrayList<String>();
		
		if(dataLen >0 ){
			
			for (int f = 0; f < dataLen; f++) {
				
				sb.append(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));	
				ls.add(Integer.toString((by[ethloc + f ] & 0xff) + 0x100, 16).substring(1));
			}
//System.out.println("30.Data ��:"+dataLen+"�ֽ�");	

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
	 * �ú�������֡��Я��������data_byte
	 *************************************************/
	public byte[] data_byte_tcp(byte by[],int ethloc,int ipheader,int tcpheader,int len){
		
		
		//ȷ��������ʼλ��
		
		
		ethloc += 34 + ipheader+tcpheader;
		//ȷ�����ݳ���	
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
	 * �ú�������֡��Я��������data_byte_udp
	 *************************************************/
	public byte[] data_byte_udp(byte by[],int ethloc,int ipheader,int udplen){
		
		byte[] data_byte = null;
		//ȷ��������ʼλ��
		ethloc += 34 + ipheader+8;
		//ȷ�����ݳ���	
		int dataLen = udplen-8;
		
		if(dataLen >0 ){
			
			data_byte = new byte[dataLen];
			
			for (int f = 0; f < dataLen; f++) {
				
				data_byte[f] = by[ethloc + f ];
			}	
		}
		
//System.out.println("26 ���ݳ���:"+data_byte.length);
		return data_byte;
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	
	/*************************************************
	 * �ú��������жϱ�802.11��װ����ʵЭ������+48
	 *************************************************/
	public String nettype(int ethloc,byte by[]){
		ethloc += 48; //(48) type
		String nettype;
		nettype = Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		
		if(nettype.equals("0806")){
			System.out.print("	ʵ�ʷ�װЭ��:");
			System.out.print("ARPЭ��  ("+nettype+")");
			return "0806";
		}
		else if(nettype.equals("86dd")){
			System.out.print("	ʵ�ʷ�װЭ��:");
			System.out.print("IPv6Э��(SSDP,DHCPV6) ("+nettype+")");
			return "86dd";
		}
		else if(nettype.equals("0800")) {
			System.out.print("	ʵ�ʷ�װЭ��:");
			System.out.print("IPV4Э��(NBNS,LLMNR,IGMPv3,QICQ) ("+nettype+")");
			return "0800";
		}
		return "is802.11";
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú�����16���Ƶ�ת��Ϊ2����
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
	 * �ú������udp����
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
			
//System.out.println("25.udp headerLen 2�ֽ�  8bit  ��ǰֵ ��"+len+"  ʮ���Ƴ���:"+udpheadlen);
	
			return udpheadlen;
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú�����ȡradiotap header����
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
	 * �ú�����ȡ�����־flag
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
	 * ������static void main(String[] args)
	 * List<Model_a> ergodic()
	 *************************************************/
	public List<Model_a> ergodic(String fileName,String hostIP,String net,File file) {
		
		/*************************************************
		 * ���������
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
		 * ���ݳ�ʼ������
		 *************************************************/
		wireless802_airdump wireless802 = new wireless802_airdump();
		try {
//			if (!file2.exists()) {
//				file2.createNewFile();
//			}
			FileInputStream fis = null;
			while(!file1.renameTo(file1)){
//System.out.println("�ļ���ռ��");
			}
			fis = new FileInputStream(file1);
//			FileOutputStream fos = new FileOutputStream(file2);
			int size = fis.available();
			//System.out.println("��" + size+"�ֽ�");
			byte by[] = new byte[size];
			fis.read(by);
			fis.close();
		/*************************************************
		 * end
		 *************************************************/
			
			/*************************************************
			 * ѭ����ʼ����ÿһ֡����
			 *************************************************/
			while (ss < size) {
				//System.out.print("\n" + "��" + j + "֡����ȥ�����ݰ�ͷ����");
				
				//��һ��ѭ��ʱ  by�Ƕ�����������,L=0��j=1,�����ÿһ֡�ĳ���
				len = wireless802.frameLength(by, L, j);
				
				// ��ǰ֡����������ʼλ�ã�ȥ��pcapͷ�����ݰ�ͷ
				ethloc = 40 + L + 16 * (j - 1);
				
				//System.out.println("<<--802.11-->>");
				//��airdump��ץȡ��802.11֡������ײ��֡��û��radioheardͷ���ķ�װ
				//�ж�֡����
				frametype = wireless802.frametype(by, ethloc);
				
				//��ȡ������Ϣ
				TOorFrom = wireless802.flags(ethloc, by);
//System.out.println("");
			

				//ֻ��802.11������֡���д���,�������֡�ݲ�����
				if(frametype == "10"){
					//����ʱ��
					duration = wireless802.duration(by, ethloc);
//System.out.println("");	
			

				//������֡�У���ʾMAC��ַ����3���ֶΣ���ʵ���й���5��MAC��ʾ ����3��5�ֶ��Ƕ�MAC��ַ�Ĵ���
					macAddress = wireless802.macAddress(frametype, by, ethloc);
//System.out.println("");	
				

				//fragNumber.sequenceNUm�Լ�QOS control
				//��ʱû�о��崦��
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
						//�汾��IPͷ����
						ipHeaderLen = wireless802.ipHeader(by, ethloc);
						//differentiated services field
						wireless802.differSerField(by, ethloc);
						//total len
						totalLen_ip = wireless802.totalLen(by, ethloc);
						
						//identification
						wireless802.identification(by, ethloc);
						
						//flag_ip��λƫ�ƹ�2�ֽ� ǰ��ռ3λ ����13λ
						wireless802.flag_ip(by, ethloc);
						
						//timeTolive����ʱ��
						wireless802.timetolive(by, ethloc);
						
						//����㣨��һ��Э�飩
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
						
						//����װ�� װ��ָ�����͵�����
						if(nettype.equals(net) && ipAddress_sou.equals(hostIP) || ipAddress_des.equals(hostIP)){
							Model_a model = new Model_a();
							ApplicationLayer aLayer = new ApplicationLayer();
							NetworkLayer nLayer = new NetworkLayer();
							WireLess802 wLayer = new WireLess802(); 
							
							//tcp��ģ��
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
							
							//ip��
							nLayer.setIp_des(ipAddress_des);
							nLayer.setIp_sou(ipAddress_sou);
							nLayer.setNettype(nettype);
							
							
							//802.11
							wLayer.setTOorFrom(TOorFrom);
							
							
							//֡ģ��
							model.setIndex(j);
							model.setTotalLen(len);
							model.setaLayer(aLayer);
							model.setnLayer(nLayer);
							model.setwLayer(wLayer);
							
							
							//װ��models
							models.add(model);		
						}
						
	
					}
				
				}
				
				//����һ֡��ʼλ�õ���
				ss = ss + 16 + len;
				j++;
				L = L + len;
				len = 0;
//System.out.println("");				
			}
			/*************************************************
			 * end
			 *************************************************/
			
			//System.out.println("\n�ܹ�֡����" + (j - 1));
			
			
			fis.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	//����ֵ	
	return models;
	}

}
