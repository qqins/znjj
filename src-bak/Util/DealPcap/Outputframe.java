package Util.DealPcap;


import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;
//import java.io.FileNotFoundException
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.csvreader.CsvReader;


import Model.DB_csv_device;
import Model.Model_a;


public class Outputframe {
	
	 int a = 0;
	 String d1;
	 String d2;
	 String temp;
	 byte[] b;
	 byte[] temp1;
	 byte[] temp2;
	 List<DB_csv_device> dbList = new ArrayList();
	 List<DB_csv_device> csvList = new ArrayList<DB_csv_device>(); 
	 private String DATA_PATH;
	 private String device_Name;
	 private String Csv_PATH;
	 {
		 //readProperties();
		 
	 }
	 
/*	 public static void main(String[] args) {
		 Outputframe example = new Outputframe();
		 example.GetData();   
		 example.createCsv();
	     example.getCsvRows();
	     example.readCsv();	
	     example.checkPort(49157);
	    }*/
	 
	     //获取pcap包设备数据信息
	 public void GetData(){
	    	
	    	//s为提取的数据包的存放,a 端口 ，b 数据
//	    	int a; 
	    	byte[] b;
	    	String c;
	    	wireless802_airdump1 w1 = new wireless802_airdump1();
	    	//属性文件的路径
	    	
	    	List<Model_a> s = w1.ergodic(DATA_PATH); 
			
			int[] s1= {7852,12762}; //s1[0]布防；s1[1]撤防

			
			List<Model_a> s2 = w1.getFrameByindex(s1, s);
			
			List<String> c1 = new ArrayList<String>();
			
/*			//验证
			Model_a s3 = s2.get(0);		
			a = s3.getaLayer().getPort_des();
			b = s3.getData_byte();
			temp = new sun.misc.BASE64Encoder().encodeBuffer(b);
			temp = temp.replaceAll("\r\n", " ");
			*/
			
			
			for(int i = 0; i < s1.length; i++){
				
				Model_a s3 = s2.get(i);		
				a = s3.getaLayer().getPort_des();
				b = s3.getData_byte();
/*				c = Arrays.toString(b);
				c = c.replaceAll(",", " ");		
				c1.add(c);	//byte转string 	
*/				String temp = new sun.misc.BASE64Encoder().encodeBuffer(b);
				temp = temp.replaceAll("\r\n", " ");	//将回车换行符替换成空格
				c1.add(temp);		
			}
			
			
			//存放在CSV中的布防/撤防数据命令   d1布防/d2撤防
		    d1 = c1.get(0);  
//		    System.out.println(d1);
			d2 = c1.get(1);
			
		}
	 
	     
    /**
     * 创建CSV文件
     */
    public void createCsv(){
        //换行符
        final String NEW_LINE = "\n";
        //文件名称
        String fileName = Csv_PATH;
         
        try {
            //标题头
            String title = "ID,info,desport,protocol,OuthomeData,AthomeData";
             
            StringBuilder csvStr = new StringBuilder();
            csvStr.append(title).append(NEW_LINE);
             
            //数据行
           
           for(String csvData : getCsvRows()){
                csvStr.append(csvData).append(NEW_LINE);
            }
             
            //写文件
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName)), "GB2312"));
            writer.write(csvStr.toString());
            writer.flush();
            writer.close();
             
        } catch (Exception e) {
            e.printStackTrace();
        }
         
    }
    /**
     * 获取CSV行数据，各个值之间使用英文逗号分隔
     * @return List<String>
     */
    public List<String> getCsvRows(){
        List<String> result = new ArrayList<String>();
       
        result.add("1,Counter_kerui,"+a+",TCP,"+d1+","+d2+""); 
//        result.add("2,Counter_kerui,"+a+",TCP,"+d1+","+d2+""); 
        return result;
    }
    
    //读取CSV文件
    public void readCsv(){
    	try{
		CsvReader reader = new CsvReader(Csv_PATH, ',',Charset.forName("GBK"));
    	//读取表头
    	reader.readHeaders();
    	//逐条读取记录，直至读完
    	while(reader.readRecord()){  		
/*    		//读取整行
    		System.out.println(reader.getRawRecord());*/
    		//按列读取布防撤防
    		String s1 = reader.get("OuthomeData");
    		String s2 = reader.get("AthomeData");
    		s1 = s1.replaceAll(" ", "\r\n");
    		s2 = s2.replaceAll(" ", "\r\n");
//    		System.out.println(s1);
    		//转byte[]
    		temp1 = new sun.misc.BASE64Decoder().decodeBuffer(s1); //转byte[]
    	    temp2 = new sun.misc.BASE64Decoder().decodeBuffer(s2); 
    				
    	 } 
    	}catch (Exception e){
    		e.printStackTrace();
    	
    	}
    	//封装byte数据在dbList容器
    	DB_csv_device model = new DB_csv_device();
		
		model.setDesPort(a);
		model.setOuthome(temp1);
		model.setAthome(temp2);		
		dbList.add(model);
		
    	
/*		//判断byte数组是否相等
    	System.out.println(Arrays.equals(b, temp1));*/
    	    	
    }
 
    
	  /*************************************************
	 * 读取配置文件prameter.properties
	 *************************************************/
    private void readProperties(){
		  	
			Properties prop = new Properties();
			InputStream in = Object. class .getResourceAsStream( "/csv.properties" );    
	        try  {    
	           prop.load(in);    
	           DATA_PATH = prop.getProperty( "DATA_PATH" ).trim();   
	           Csv_PATH =  prop.getProperty( "Csv_PATH" ).trim();
	          // device_Name =  prop.getProperty( "device_Name" ).trim();
	        }  catch  (IOException e) {    
	           e.printStackTrace();    
	       }
	       readCsv();
	  }
	public List<DB_csv_device> getDbList() {
		return dbList;
	}
	public void setDbList(List<DB_csv_device> dbList) {
		this.dbList = dbList;
	}
	//判断是否存在该CSV文件，如果存在就读取该路径下的CSV文件，不存在新建一个新的    
	//追加数据到csv文件中
	public boolean addcsv(String csvfilepath,DB_csv_device model){  
		String outhome = null;
		String athome = null;
		int device_ID = 0;
		Field field;
		Field[] fields = DB_csv_device.class.getDeclaredFields(); 


		if(model.getAthome() != null && model.getOuthome() != null){
			byte[] b = model.getOuthome();
			outhome = new sun.misc.BASE64Encoder().encodeBuffer(b);
			outhome = outhome.replaceAll("\r\n", " ");	//将回车换行符替换成空格
			byte[] a = model.getAthome();
			athome = new sun.misc.BASE64Encoder().encodeBuffer(a);
			athome = athome.replaceAll("\r\n", " ");	//将回车换行符替换成空格
		}
		else if(model.getOuthome() != null && model.getAthome() ==null){
			byte[] b = model.getOuthome();
			outhome = new sun.misc.BASE64Encoder().encodeBuffer(b);
			outhome = outhome.replaceAll("\r\n", " ");	//将回车换行符替换成空格
			athome = "";
		}
		else if(model.getOuthome() == null && model.getAthome() !=null){
			outhome = "";
			byte[] a = model.getAthome();
			athome = new sun.misc.BASE64Encoder().encodeBuffer(a);
			athome = athome.replaceAll("\r\n", " ");	//将回车换行符替换成空格
		}
		else if(model.getOuthome() == null && model.getAthome() ==null){
			outhome = "";
			athome = "";
		}
		File csvfile = new File(Csv_PATH);
		File path = new File(csvfilepath);
		//判断所给路径是否存在
		if(path.exists()){
/*			GetData(); 
			("1,Counter_kerui,"+a+",TCP,"+d1+","+d2+""); */
			//路径相同执行读取csv文件函数
			//			readCsv();
			FileWriter fw1 =null;
			//追加数据(数据传进来了，剩下追加了)数据流形式
			try{
				CsvReader reader = new CsvReader(csvfilepath, ',',Charset.forName("UTF-8"));
				//读取表头
				reader.readHeaders();
				//逐条读取记录，直至读完
				while(reader.readRecord()){  		
					device_ID = Integer.parseInt(reader.get("ID"));	   
					device_ID = device_ID + 1;
				}
				reader.close();
				fw1 = new FileWriter(path,true); 
				fw1.write(""+device_ID+","+model.getInfo()+","+model.getDesPort()+","+model.getprotocol()+","+outhome+","+athome+"\r\n");  
				fw1.flush();
				fw1.close();	
				return true;

			}catch (IOException e) {    
				e.printStackTrace();    
			}finally{
				if(null != fw1){  
					try  
					{  
						fw1.close();  
					} catch (IOException e)  
					{  
						e.printStackTrace();  
					}  
				}     	
			}
		}
				else{
					//不存在创建新的csv文件,将新数据写入csv文件
					FileWriter fw2 = null;  
					try{			
						//写入标题
						fw2 = new FileWriter(path,true);
						for (int i = 0; i < fields.length; i++) {  
							field = fields[i];  
							fw2.write(""+fields[i].getName()+",");
							if(i == fields.length - 1){
								fw2.write("\r\n");
							}
						}
						//写入数据  
						/*				for (int i = 0; i < fields.length; i++){
					fw2.write(""+z+",");
					 if(i == fields.length - 1){
				        	fw2.write("\r\n");
				        }
				}  */
						fw2.write("1,"+model.getInfo()+","+model.getDesPort()+","+model.getprotocol()+","+outhome+","+athome+"\r\n");
						fw2.flush();
						fw2.close();	
						return true;
					}catch(IOException e){
						e.printStackTrace();
					}finally {
						if(null != fw2){  
							try  
							{  
								fw2.close();  
							} catch (IOException e)  
							{  
								e.printStackTrace();  
							}  
						}
					}
				}
				return false;	
			}


	
	
	//判断端口是否在csv文件中
	public DB_csv_device checkPort(String csvfilepath, int desport){
		File path = new File(csvfilepath);
		if(path.exists()){
			try{
				BufferedReader reader = new BufferedReader(new FileReader(csvfilepath));
				String line = null;
				List<String> csvList = new ArrayList<String>();
				//读取行数  csvList.size()为csv文件的行数
				while((line = reader.readLine())!=null){
					csvList.add(line);
				}
				//逐行读取
				for(int i = 1;i < csvList.size();i++){
					//将带有逗号的字符串转成string数组
					String[] result=csvList.get(i).split(",");
//					System.out.println("12"); 
					if(desport == Integer.parseInt(result[2])){
						//result[4]布防，result[5]撤防
						result[4] = result[4].replaceAll(" ", "\r\n");
						result[5] = result[5].replaceAll(" ", "\r\n");
						//    		System.out.println(s1);
						//转byte[]
						temp1 = new sun.misc.BASE64Decoder().decodeBuffer(result[4]); //转byte[]
						temp2 = new sun.misc.BASE64Decoder().decodeBuffer(result[5]); 
						//封装byte数据在dbList容器
						DB_csv_device model = new DB_csv_device();
						model.setId(Integer.parseInt(result[0]));
						model.setInfo(result[1]);
						model.setDesPort(Integer.parseInt(result[2]));
						model.setprotocol(result[3]);
						model.setOuthome(temp1);
						model.setAthome(temp2);		
						dbList.add(model);
						System.out.println("The model info load success!!!");
						return model;
					}else if(i == (csvList.size()-1)){
						System.out.println("The desport not exist!!!");
					}
					/*while(i == (csvList.size()-1)){
						System.out.println("The desport not exist!!");
						break;
					}*/
				}

			}catch (Exception e){
				e.printStackTrace();

			}
		}else{
			System.out.println("The filepath not exist!!!");
		}
		return null;
	}
	
	

	//判断端口是否在DB_CSV中(将pcap中的数据导入到csv文件中)
	public DB_csv_device checkPort1(int desport){
		GetData();   
		createCsv();
	    getCsvRows();
	    readCsv();
		for(int i = 1;i <= dbList.size(); i++){
			if (desport == dbList.get(i).getDesPort()){
				return dbList.get(i);
			}else{
				return null;
			}
		}
		return null;
	}

}