package Test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JbuttonTest {
	private static JFrame frame;

	public static void main(String[] args) {
		frame = new JFrame("JButton Test");
		JButton jbutton = new JButton("Test");
		jbutton.setMnemonic('b');// 这里就是用键盘对Button进行操作 alt+I就可以
		jbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frameChange(evt);
			}
		});
		Container con = frame.getContentPane();
		con.add(jbutton, new BorderLayout().CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
		frame.setSize(200, 300);
		frame.setVisible(true);
	}

	public static void frameChange(ActionEvent evt) {
		frame.setTitle("frame title have change");
	}
}