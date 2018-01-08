package Test;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

public class TestBindKey {
	JFrame jf = new JFrame("测试");
	JTextArea ta = new JTextArea(3, 30);
	JButton bt = new JButton("发送");
	JTextField tf = new JTextField(15);
	
	JPanel jp;
	
	JButton bt1 = new JButton("发送1");

	public void init() {
		jf.add(ta);
		jp = new JPanel();
		jp.add(tf);
		jp.add(bt);
		jp.add(bt1);
		jf.add(jp, BorderLayout.SOUTH);
		
		//bt.requestFocus();
		
		AbstractAction sendmsg = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ta.append(tf.getText() + "\n");
				tf.setText("");
			}
		};
		
		
		AbstractAction sendmsg1 = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ta.append(tf.getText() + "\n");
				tf.setText("");
				
				System.out.println("dfasfasdfsda");
			}
		};
		
		
		/*ActionListener sendMsg = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ta.append(tf.getText() + "\n");
				tf.setText("");
			}
		};*/
		//bt.addActionListener(sendmsg);
		// 将Ctrl+Enter键和“send”关联
		/*tf.getInputMap().put(
				KeyStroke.getKeyStroke('\n',
						java.awt.event.InputEvent.CTRL_MASK), "send");*/
		
		//tf.getInputMap(JComponent.WHEN_FOCUSED).put(
		//		KeyStroke.getKeyStroke('b'), "send");
		// 将"send"和sendMsg Action关联
		//tf.getActionMap().put("send",sendmsg);
		
		jp.getInputMap().put(KeyStroke.getKeyStroke('b'), "comfirm");
		
		//jp.getActionMap().setParent(bt.getActionMap());
		//JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
		bt.getInputMap().setParent(jp.getInputMap());
		
		bt1.getInputMap().setParent(jp.getInputMap());
		
		bt.getActionMap().put("comfirm",sendmsg);
		
		bt1.getActionMap().put("comfirm",sendmsg1);
		
		jf.pack();
		jf.setVisible(true);

	}

	public static void main(String[] args) {
		new TestBindKey().init();
		System.out.println("Hello World!");
	}
}




////////////////

	