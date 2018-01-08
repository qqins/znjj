package Model;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;

import Test.JbuttonTest;

public class MyKeyAdapter extends KeyAdapter{

	private Robot robot = null;
	
	List<JComponent> components = new ArrayList<JComponent>();
	
	int flag = 0;
	
	
	public MyKeyAdapter(List<JComponent> components2) {
		// TODO Auto-generated constructor stub
		
		this.components = components2;
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		super.keyPressed(e);
		
		 char charA=e.getKeyChar();
		 if(charA=='s'){
			 flag++;
				if(flag==6){
					flag=0;
				} 
				
				if(components.get(flag)!=null){
					components.get(flag).requestFocus();
				}
				
			
		 }else if(charA=='w') {
			
			 flag--;
			 if(flag==-1){
				 flag=5;
			 }
			 
			 if(components.get(flag)!=null){
					components.get(flag).requestFocus();
				}
			
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			
//System.out.println("切换到表格中");
			if(components.size()>6){
				//
				if(components.size()==8){
					components.remove(6);
				}
				components.get(6).requestFocus();
			}
		}else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			
			//使用rebot移动鼠标去点击的策略		
			/*Point pm = MouseInfo.getPointerInfo().getLocation();
			Point pb = components.get(flag).getLocation();
			System.out.println("fasdfsdx:"+pb.x+"fsdfsdy:"+pb.y);
			System.out.println("fasdfsdx:"+pm.x+"fsdfsdy:"+pm.y);
			robot.delay(500);
			robot.mousePress(InputEvent.BUTTON1_MASK);
	        robot.mouseRelease(InputEvent.BUTTON1_MASK);
	        robot.mouseMove(pm.x, pm.y);*/
			
			//使用快捷按键的策略
	       /*robot.keyPress(KeyEvent.VK_ALT);
	       robot.keyPress(KeyEvent.VK_M);
	       robot.keyRelease(KeyEvent.VK_ALT);
	       robot.keyRelease(KeyEvent.VK_M);
	       robot.delay(300);*/
			
			//jbutton自带的点击方案，用action监听
			if(components.get(flag) instanceof JButton ){
				JButton j =(JButton)components.get(flag);
				j.doClick();
			}
			
		}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			
			/*robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.keyRelease(KeyEvent.VK_ESCAPE);*/
			//robot.delay(200);
		}
		
//System.out.println("你按了《"+charA+"》键");
	}
	
	public void setComponent(JComponent component){
		
		this.components.add(component);
		
	}
	
	public List<JComponent> deleteComponent(int index){
		
		this.components.remove(index);
		
		return this.components;
	}
}
