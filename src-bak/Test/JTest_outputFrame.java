package Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import Model.DB_csv_device;
import Model.Model_a;
import Util.DealPcap.Outputframe;
import Util.DealPcap.wireless802_airdump1;

public class JTest_outputFrame {

	
	public void readClassTypeAndValue() {
		
		Outputframe outputframe = new Outputframe();
		
		
		//DB_csv_device device = outputframe.checkPort(465436); //根据端口拿到csv文件中的数据
		
		DB_csv_device model = new DB_csv_device();
		
		model.setDesPort(3242231);
		byte[] byte11 = {3,56,87,87};
		byte[] byte12 = {3,56,87,43};
		model.setOuthome(byte11);
		model.setAthome(null);	
		
		
		    Field[] fields=model.getClass().getDeclaredFields();  
		        String[] fieldNames=new String[fields.length];  
		        List list = new ArrayList();  
		        Map infoMap=null;  
		        
		    for(int i=0;i<fields.length;i++){  
		        infoMap = new HashMap();  
		        infoMap.put("type", fields[i].getType().toString());  
		        infoMap.put("name", fields[i].getName());  
		        infoMap.put("value", getFieldValueByName(fields[i].getName(), model));  
		        list.add(infoMap);  
		    }  
		    
		    System.out.println(list.size());
		    for(int i = 0;i<list.size();i++){
		    	 Map<String, Object> map = (Map)list.get(i);
				 System.out.println(map.get("type"));
				 System.out.println(map.get("name"));
				 System.out.println(map.get("value"));
		    	
		    }
		 
	}
	
	  private Object getFieldValueByName(String fieldName, Object o) {  
	       try {    
	           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
	           String getter = "get" + firstLetter + fieldName.substring(1);    
	           java.lang.reflect.Method method = o.getClass().getMethod(getter, new Class[] {});    
	           Object value = method.invoke(o, new Object[] {});    
	           return value;    
	       } catch (Exception e) {    
	             
	           return null;    
	       }    
	   }  
	  
	  
	  
	
		public void checkportAndRetrun() {
			
			Outputframe outputframe = new Outputframe();
			
			
			DB_csv_device device = outputframe.checkPort("E:\\test\\output.csv",49157); //根据端口拿到csv文件中的数据
			
			//DB_csv_device model = new DB_csv_device();
			
			
			if(device == null){
				
				System.out.println("model is null,port not exist");
			}
			else{
				
				
				System.out.println(device.getOuthome().length);
				
			}
			   
			 
		}
	  
	  
	  

			public void checkModelByte() {
				
				Outputframe outputframe = new Outputframe();
				
				
				DB_csv_device device = outputframe.checkPort("E:\\test\\output.csv",49157); //根据端口拿到csv文件中的数据
				
				//DB_csv_device model = new DB_csv_device();
				
				
				if(device == null){
					
					System.out.println("model is null,port not exist");
				}
				else{
					
					System.out.println(device.getOuthome().length);
					
					byte[] b;
			    	String c;
			    	wireless802_airdump1 w1 = new wireless802_airdump1();
			    	//属性文件的路径
			    	
			    	List<Model_a> s = w1.ergodic("E:\\test_pcap\\1122.pcap"); 
					
					int[] s1= {7852,12762}; //s1[0]布防；s1[1]撤防

					
					List<Model_a> s2 = w1.getFrameByindex(s1, s);
					
					byte[] source = s2.get(1).getData_byte();
					
					for(int i =0;i<source.length;i++){
						
						if(source[i]==device.getAthome()[i]);
						else{
							System.out.println("not the same!");
							break;
						}
					}
					
				}
			}
	  
	
	  public void addcsv(){
		  
		  
		  
	  }

}
