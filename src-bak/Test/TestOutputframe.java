package Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

import Model.DB_csv_device;
import Model.MyKeyAdapter;
import Util.DealPcap.Outputframe;

public class TestOutputframe {
	
	
	
	
	public static void main(String[] args) {
		
		Outputframe outputframe = new Outputframe();
		/*
		String csvfilepath = new String("E:\\test\\output.csv");
		DB_csv_device device = outputframe.checkPort(csvfilepath, 49157); //根据端口拿到csv文件中的数据
*/		
		
	
		
//		DB_csv_device device = outputframe.checkPort1(3221); //写入pcap数据到csv文件中

		//追加数据测试
		DB_csv_device model = new DB_csv_device();
		
		model.setDesPort(32243154);
		byte[] byte11 = {3,56,87,87};
		byte[] byte12 = {3,56,56,43};
		model.setOuthome(byte11);  
		model.setAthome(byte12);	
		model.setprotocol("TCP");
		
		
		String csvfilepath = new String("E:\\test\\output5.csv");

		boolean flag = outputframe.addcsv(csvfilepath, model);
		
		
		System.out.println(flag);
	}

}
