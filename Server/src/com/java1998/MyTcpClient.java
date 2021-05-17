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
		MyTcpClient client = new MyTcpClient("���������������");	//������������
		client.setSize(200, 200);
		client.setVisible(true);
		client.connect();
	}
	private PrintWriter writer;		//����PrintWriter�����
	Socket socket;	//����Socket����
	private JTextArea ta = new JTextArea();		//����JTextArea����
	private JTextField tf = new JTextField();	//����JTextField����
	Container cc;	//����Container����
	public MyTcpClient(String title){	//���췽��
		super(title);	//���ø���Ĺ��췽��
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cc = this.getContentPane();	//ʵ��������
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new BevelBorder(BevelBorder.RAISED));
		getContentPane().add(scrollPane,BorderLayout.CENTER);
		scrollPane.setViewportView(ta);
		cc.add(tf,"South");
		tf.addActionListener(new ActionListener() {
			//���¼�
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//���ı�����Ϣд����
				writer.print(tf.getText());
				//���ı����е���Ϣ��ʾ���ı�����
				ta.append(tf.getText()+'\n');
				ta.setSelectionEnd(ta.getText().length());
				tf.setText("");//����ı���
			}
		});
	}
	private void connect(){
		ta.append("��������\n");
		try {
			socket = new Socket("192.168.1.117",8998); //ʵ����Socket����
			writer = new PrintWriter(socket.getOutputStream(),true);
			ta.append("�������\n"); 	//�ı�������ʾ��Ϣ
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
