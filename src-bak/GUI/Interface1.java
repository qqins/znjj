package GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.InputMap;
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

import Model.Device_model;
import Model.Routing_model;
import Util.Recognation.Recognation_device;
import Util.Shell.CloseThread;
import Util.Shell.ShellThread;
import Util.Timer.ShowDeviceOnTable;
import Util.Timer.TimerDialog;

public class Interface1 {

	private JFrame frmAutocountersystem;
	private JTable table;
	private JTextPane begin_text;
	private JScrollPane scrollPane_APtable;
	private int step = 1;
	
	
	//装载所有扫描出来的路由设备
	private List<Routing_model> routings;
	
	//properties参数
	String ScanAP_CSV_path = null;
	String SHRLL_PATH = null;
	int COUNTER_TYPE = 0;
	int SCAN_TIMES=0;
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
					Interface1 window = new Interface1();
					window.frmAutocountersystem.setVisible(true);
					window.frmAutocountersystem.requestFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAutocountersystem = new JFrame();
		//frmAutocountersystem.setAlwaysOnTop(true);
		
		frmAutocountersystem.addKeyListener(new KeyAdapter(){  
            public void keyPressed(KeyEvent e){  
                char charA=e.getKeyChar();  
                System.out.println("你按了《"+charA+"》键");  
            }  
        });  
		frmAutocountersystem.setTitle("Dandelion");
		frmAutocountersystem.setBounds(100, 100, 480, 320);
		frmAutocountersystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAutocountersystem.getContentPane().setLayout(null);
		
		
		
		scrollPane_APtable = new JScrollPane();
		scrollPane_APtable.setBounds(10, 28, 335, 228);
		frmAutocountersystem.getContentPane().add(scrollPane_APtable);
		
		begin_text = new JTextPane();
		begin_text.setEditable(false);
		
		
		
		
		/*************************************************
		 * 初始化按钮
		 *************************************************/
		JButton begin_bt = new JButton("Init");
		begin_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
					step = 2;	
					//显示提示
					setBinginText();
					
					//显示table
				//scrollPane_APtable.setViewportView(getTable(ScanAP_CSV_path));
					
			}
		});
		begin_bt.setBounds(355, 28, 93, 23);
		frmAutocountersystem.getContentPane().add(begin_bt);
		
		
		
		/*************************************************
		 * 识别按钮
		 *************************************************/
		
		JButton recognation_bt = new JButton("SReg");
		recognation_bt.addMouseListener(new MouseAdapter() {
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
					dt.addColumn("HomeCenter",reminds);
	
					
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
		});
		recognation_bt.setBounds(355, 61, 93, 23);
		frmAutocountersystem.getContentPane().add(recognation_bt);
		
		
		
		/*************************************************
		 * 破密按钮
		 *************************************************/
		
		JButton password_bt = new JButton("PWCrack");
		password_bt.setBounds(355, 181, 93, 23);
		frmAutocountersystem.getContentPane().add(password_bt);
		
		
		
		/*************************************************
		 * 反制按钮
		 *************************************************/
		
		JButton counter_bt = new JButton("ACrack");
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
							alerting.append("Target | "+routings.get(selectRow).getName());
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
								 setAfterSelectText_crack(routings.get(selectRow),routings.get(selectRow).getDevices().get(selectColumn-4));
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
		counter_bt.setBounds(355, 94, 93, 23);
		frmAutocountersystem.getContentPane().add(counter_bt);
		
		
		/*************************************************
		 * 退出按钮
		 *************************************************/
		JButton exit_bt = new JButton("EXIT");
		exit_bt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//提示
				TimerDialog dialog = new TimerDialog();
				dialog.showDialog(frmAutocountersystem, "System Exit", 1);
				System.exit(0);
			}
		});
		exit_bt.setBounds(355, 233, 93, 23);
		frmAutocountersystem.getContentPane().add(exit_bt);
		
		
		/*************************************************
		 * 还原按钮，重置设备到反制之前的状态
		 *************************************************/
		JButton Recover_bt = new JButton("ReStus");
		Recover_bt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				frmAutocountersystem.requestFocus();
			}
		});
		
		///测试添加键盘事件
		/*InputMap ancestorMap = Recover_bt.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		InputMap ownMap = Recover_bt.getInputMap(JComponent.WHEN_FOCUSED);*/
		
		//Text
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
					setAfterSelectText_REcrack();
					//Crack_resultText(scrollPane_APtable);
				}
				
				
				//frmAutocountersystem.requestFocus();
			}
		});
		Recover_bt.setBounds(355, 127, 93, 23);
		frmAutocountersystem.getContentPane().add(Recover_bt);
	}
	
	
	
	/*************************************************
	 * 反制显示内容
	 *************************************************/
	public void setAfterSelectText_crack(Routing_model routing,Device_model device){
		
		JTextPane jp = new JTextPane();
		jp.setEditable(false);
		
		scrollPane_APtable.setViewportView(jp);
		//Vector<String> counterText = new Vector<String>();
		jp.setText("|-------Cracking--------| "+routing.getName()+" | "+device.getName());
		try {
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n |****AP MAC****| "+routing.getMac(), new SimpleAttributeSet());
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n |****Channel****| "+routing.getChannel(), new SimpleAttributeSet());
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n Please wait......", new SimpleAttributeSet());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	/*************************************************
	 * 还原反制显示内容
	 *************************************************/
	public void setAfterSelectText_REcrack(){
		
		JTextPane jp = new JTextPane();
		jp.setEditable(false);
		
		scrollPane_APtable.setViewportView(jp);
		//Vector<String> counterText = new Vector<String>();
		jp.setText("|-------Re_Cracking--------| ");
		try {
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n Beyond the sensor range !", new SimpleAttributeSet());
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n Please wait......", new SimpleAttributeSet());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/*************************************************
	 * 还原反制显示内容
	 *************************************************/
	public void Crack_resultText(JScrollPane scrollPane_APtable){
		
		JTextPane jp = new JTextPane();
		jp.setEditable(false);
		
		scrollPane_APtable.setViewportView(jp);
		//Vector<String> counterText = new Vector<String>();
		jp.setText("|************************| ");
		try {
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n ******SUCCESS!******", new SimpleAttributeSet());
			jp.getDocument().insertString(jp.getDocument().getLength(), "\n|************************| ", new SimpleAttributeSet());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/*************************************************
	 * 改函数设置一个Table个格式,并且返回一个Table
	 *************************************************/
	public JTable getTable(String csvPath){
		
		
		table = new JTable();
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
				arr[0] = "(权限限制访问)";	
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
		begin_text.setText("系统初始化......");
		List<String> ss = new ArrayList<String>();
		ss.add("系统资源连接成功......");
		ss.add("启动成功");
		ss.add("开始嗅探周围路由AP设备.......");
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
	    	String[] ss= ScanAP_CSV_path.split("?");
	    	
	    	String csv_path = null;
	    	if(ss.length==2){
	    		
	    		StringBuilder sb = new StringBuilder();
	    		sb.append(ss[0]);
	    		sb.append(n);
	    		sb.append(ss[1]);
	    		csv_path = sb.toString();
	    	}else {
				csv_path = ScanAP_CSV_path;
			}
	    	
	    	
	    	scrollPane_APtable.setViewportView(getTable(csv_path));
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
	    	 int  n = 0;
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
		  				//scrollPane_APtable.setViewportView(getTable(ScanAP_CSV_path));
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
