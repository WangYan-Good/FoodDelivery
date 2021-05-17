package com.java1998;
import java.io.*;
import java.net.*;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import Ant_Colony_Algorithm_2.Ant;
import com.sun.prism.Image;

public class MyTcpServer {
	private BufferedReader reader;//����BufferReader����
	private ServerSocket serverSocket;//����ServerSocket
	private Socket socket;//����Socket����
	void getServer(){
		try {
			serverSocket = new ServerSocket(12500);//ʵ����Socket����
			System.out.println("�������׽����Ѿ������ɹ�");	//�����Ϣ
		
			System.out.println("�ȴ��ͻ�������...");	//�����Ϣ
			socket = serverSocket.accept();		//ʵ����Socket����
			InputStream input = socket.getInputStream();//�����ֽ���
			InputStreamReader inputStR = new InputStreamReader(input);//�������ֽ���תΪ�ַ���
			BufferedReader buffer = new BufferedReader(inputStR);//Ϊ���������ӻ�����Ϣ
			getClientMessage(buffer);
			buffer.close();
			inputStR.close();
			input.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//���տͻ�������
	private void getClientMessage(BufferedReader buffer){
		try {
			
			//��ÿͻ�����Ϣ
			String info = "";
			while((info = buffer.readLine())!= null){
				System.out.println("�ͻ�����"+info);
				dealClientMessage(info);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try {
			if(reader != null){
				reader.close();
			}
			if (socket != null) {
				socket.close();
				serverSocket.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void dealClientMessage(String info){
		int order_number;//������Ŀ
		String pointStr = info.substring(0,info.length()-1);
		String[] point = pointStr.split(",");
		for(int i =0 ;i<point.length;i++){
			System.out.print(point[i]+" ");
		}
		System.out.println();
		order_number = Integer.parseInt(point[0]);
		double[][] c_point = new double[order_number][2];
        double[][] b_point = new double[order_number][2];
        for(int i =0;i<order_number;i++){
        	c_point[i][0] = Double.parseDouble(point[4*i+1]);
        	c_point[i][1] = Double.parseDouble(point[4*i+2]);
        	b_point[i][0] = Double.parseDouble(point[4*i+3]);
        	b_point[i][1] = Double.parseDouble(point[4*i+4]);
        }
        System.out.println();
        //������Ⱥ�㷨
        Ant ant;//����������
		MWNumericArray input1=null;
		MWNumericArray input2=null;
		Object[] result = null;
		String[] rsString;
		String rs="";//���͵��ͻ��˵��ַ���
		try{
			ant = new Ant();
			input1 = new MWNumericArray(b_point,MWClassID.DOUBLE);
			input2 = new MWNumericArray(c_point,MWClassID.DOUBLE);
			result = ant.Ant_Colony_Algorithm_2(1, input1,input2);
			rsString = result[0].toString().split("\\s*");//���淵�ؽ��

			for(int i=0;i<rsString.length;i++){
				if (rsString[i].length()==0) {
					rs+=",";
				}else {
					rs+=rsString[i].charAt(0);
				}
			}
			//�����ַ�ǰ׺
			
			for(int i = 0;i<rs.length();i++){
				if (rs.charAt(i)>='0'&&rs.charAt(i)<='9') {
					rs = rs.substring(i);
					break;
				}
			}
			
		}catch(MWException e){
			e.printStackTrace();
		}
		System.out.println(rs);
		sendClientMessage(rs);//���㷨��ɺ�����з��͸��ͻ���
	}
	//��ͻ�����������
	private void sendClientMessage(String info){
		try{
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);//����Ӧ��Ϣ���Ϊ��ӡ��
			writer.write(info);
			writer.flush();
			//�ر���Դ
			writer.close();
			outputStream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){//������
		while(true){
			new MyTcpServer().getServer();//�����������
		}
	}
}
