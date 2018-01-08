package Util.DealPcap;

import java.io.File;
import java.io.FileInputStream;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.lang.reflect.Method;

/*
 * //文件头 数据包头+数据报  数据包头 +数据报  .......
 	pcap包结构为：24固定字节的Pcap头+16固定字节的数据头+数据内容（含mac帧、ip帧、tcp帧、http帧等）
  */
public class wireless802 {
	
	/*************************************************
	 * 此方法求出第j帧数据内容的长度
	 * L为前多帧数据内容总和（不包含数据包头），j为需要求出的帧的代号
	 * 此方法建立在流完全复制到数组里
	 *************************************************/
	public static int frameLength(byte[] by, int L, int j) {
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
		System.out.println(len + "字节\t");
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
	public String frametype(String nettype,byte by[],int ethloc){
		
		String datatype = Integer.toString((by[ethloc] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		String bString = wireless802.hexString2binaryString(datatype);
		
		//取到表示子帧类型的bit值
		String subdatatype = bString.substring(0, 4);
System.out.print("\t	子帧类型:"+subdatatype);
		
		String datatype2 = bString.substring(4, 6);
		
		if(datatype2.equals("00")){
			System.out.print("\t帧类型:00管理帧");
			return "00";
		}
		else if(datatype2.equals("01")){
			System.out.print("\t帧类型:01控制帧");
			return "01";
		}
		else {
			System.out.print("\t帧类型:10数据帧");
			return "10";
		}
	
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
			ethloc += 28;
			StringBuilder sb = new StringBuilder();
			sb.append("	TansAddress(BSSID)");
			//System.out.print("\tTansAddress(BSSID)");
			for (int m = 0; m < 6; m++) {	
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
			
			sb.append("	souAd");
			for (int n = 6; n < 12; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
				//System.out.print(":" + Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//sb.deleteCharAt(0);		
			System.out.print(sb.toString());
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
	public void flags(int ethloc,byte by[]){
		
		
		String ss = Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
				
		String bString = wireless802.hexString2binaryString(ss);
		
System.out.print("	flags:"+bString);

		String protect = bString.substring(1, 2);
System.out.print("	protect:"+protect);	
		String to = bString.substring(6, 8);
		
		if(to.equals("01")){
System.out.print("	To   AP:"+to);		
		}
		else if(to.equals("10")){
System.out.print("	From AP:"+to);		
		}
		else{
System.out.print("	        ");	
		}

	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 主函数
	 *************************************************/
	public static void main(String[] args) {
		
		/*************************************************
		 * 定义参数区
		 *************************************************/
		File file1 = new File("F:\\222.pcap");
		//File file2 = new File("F:\\result.txt");
		int ss = 24;
		int L = 0;
		
		int len;
		int j = 1;
		int ethloc;
		int radioHead;
		
		// String iplen;
		String channeltype;
		String nettype;
		String frametype;
		String macAddress;
		String flags;
		/*************************************************
		 * end
		 *************************************************/
		
		
		/*************************************************
		 * 数据初始化部分
		 *************************************************/
		wireless802 wireless802 = new wireless802();
		try {
//			if (!file2.exists()) {
//				file2.createNewFile();
//			}
			FileInputStream fis = new FileInputStream(file1);

//			FileOutputStream fos = new FileOutputStream(file2);
			int size = fis.available();
			System.out.println("总" + size);
			byte by[] = new byte[size];
			fis.read(by);
		/*************************************************
		 * end
		 *************************************************/
			
			/*************************************************
			 * 循环开始解析每一帧数据
			 *************************************************/
			while (ss < size) {
				System.out.print("\n" + "第" + j + "帧（已去掉数据包头）：");
				
				//第一次循环时  by是读进来的数据,L=0，j=1,先求出每一帧的长度
				len = wireless802.frameLength(by, L, j);
				
				// 当前帧数据内容起始位置，去掉pcap头和数据包头
				ethloc = 40 + L + 16 * (j - 1);
				
				//提取radioHeader长度
				radioHead = wireless802.radioHead(ethloc, by);
				
				
				//判断信道类型
				channeltype  = wireless802.channelType(ethloc, by);
				
				//判断帧类型
				ethloc += radioHead;
				frametype = wireless802.frametype(channeltype, by, ethloc);
				
				//读取管理信息
				wireless802.flags(ethloc, by);

				//查寻真实协议类型,只对802.11进行处理,封装为其他协议的暂不处理
				//nettype = wireless802.nettype(ethloc, by);
				
				//对802.11协议的帧的分析
			/*	if(nettype.equals("is802.11")){
					//MAC地址
					macAddress = wireless802.macAddress(frametype, by, ethloc);
				}
				
				//对管理帧处理
				if(frametype.equals("00")){
					
				//需要处理的数据待定	
System.out.print("	处理数据待定");
				}*/
				
				
				//对下一帧起始位置调整
				ss = ss + 16 + len;
				j++;
				L = L + len;
				len = 0;
System.out.println("");				
			}
			/*************************************************
			 * end
			 *************************************************/
			
			System.out.print("\n总共帧数：" + (j - 1));

			fis.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
