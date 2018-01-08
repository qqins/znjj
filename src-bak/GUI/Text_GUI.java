package GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Text_GUI {

	public static void main(String[] args) {
		System.out.println("系统启动");
		System.out.println("等待用户操作操作");
		System.out.print("A键初始化功能        ");
		System.out.print("B键系统退出        ");                                  
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			System.out.println(br.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
