package GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Text_GUI {

	public static void main(String[] args) {
		System.out.println("ϵͳ����");
		System.out.println("�ȴ��û���������");
		System.out.print("A����ʼ������        ");
		System.out.print("B��ϵͳ�˳�        ");                                  
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			System.out.println(br.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
