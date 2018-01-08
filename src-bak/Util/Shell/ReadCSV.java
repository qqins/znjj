package Util.Shell;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;

public class ReadCSV {
	ArrayList<String[]> csvList;
	
	public ReadCSV(String csvPath){
		   
        try {       
                ArrayList<String[]> csvList = new ArrayList<String[]>(); //������������  
                String csvFilePath = csvPath;   
                CsvReader reader = new CsvReader(csvFilePath,',',Charset.forName("UTF-8"));    //һ�����������Ϳ�����      
                    
                reader.readHeaders(); // ������ͷ   �����Ҫ��ͷ�Ļ�����Ҫд��䡣  
                    
                while(reader.readRecord()){ //���ж������ͷ������  
                	if(reader.getValues().length>9) 	
                    csvList.add(reader.getValues());   
                }               
                reader.close();
                
                if(csvList!=null)
                this.csvList = csvList;
                
            } catch (Exception ex) {   
                    System.out.println(ex);   
                }   
	}
	
	 public  List<String>  readCsv(int cloum){ 
		 
		 List<String> cells_byCloum = new ArrayList<String>();
		 
	        try {   
	        		if(csvList!=null){
	        			for(int row=0;row<csvList.size();row++){   
	                     String  cell = csvList.get(row)[cloum]; //ȡ�õ�row�е�0�е�����  
	                     //System.out.println(cell); 
	                     cells_byCloum.add(cell);
	        			}   		
	        		}
	                    
	            } catch (Exception ex) {   
	                    System.out.println(ex);   
	            }  
	        
	        return cells_byCloum;
	    }   
	 	
}
