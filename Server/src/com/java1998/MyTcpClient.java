package com.java1998;

import java.io.PrintWriter;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;


public class MyTcpClient extends JFrame{
	public static void main(String[] args){
		MyTcpClient client = new MyTcpClient("向服务器发送数据");	//创建本例对象
		client.setSize(200, 200);
		client.setVisible(true);
		client.connect();
	}
	private PrintWriter writer;		//声明PrintWriter类对象
	Socket socket;	//声明Socket对象
	private JTextArea ta = new JTextArea();		//创建JTextArea对象
	private JTextField tf = new JTextField();	//创建JTextField对象
	Container cc;	//声明Container对象
	public MyTcpClient(String title){	//构造方法
		super(title);	//调用父类的构造方法
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cc = this.getContentPane();	//实例化对象
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new BevelBorder(BevelBorder.RAISED));
		getContentPane().add(scrollPane,BorderLayout.CENTER);
		scrollPane.setViewportView(ta);
		cc.add(tf,"South");
		tf.addActionListener(new ActionListener() {
			//绑定事件
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//将文本框信息写入流
				writer.print(tf.getText());
				//将文本框中的信息显示在文本域中
				ta.append(tf.getText()+'\n');
				ta.setSelectionEnd(ta.getText().length());
				tf.setText("");//清空文本框
			}
		});
	}
	private void connect(){
		ta.append("尝试连接\n");
		try {
			socket = new Socket("192.168.1.117",8998); //实例化Socket对象
			writer = new PrintWriter(socket.getOutputStream(),true);
			ta.append("完成连接\n"); 	//文本域中提示信息
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
