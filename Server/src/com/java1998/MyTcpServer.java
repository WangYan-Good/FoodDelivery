package com.java1998;
import java.io.*;
import java.net.*;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import Ant_Colony_Algorithm_2.Ant;
import com.sun.prism.Image;

public class MyTcpServer {
	private BufferedReader reader;//创建BufferReader对象
	private ServerSocket serverSocket;//创建ServerSocket
	private Socket socket;//创建Socket对象
	void getServer(){
		try {
			serverSocket = new ServerSocket(12500);//实例化Socket对象
			System.out.println("服务器套接字已经创建成功");	//输出信息
		
			System.out.println("等待客户机连接...");	//输出信息
			socket = serverSocket.accept();		//实例化Socket对象
			InputStream input = socket.getInputStream();//输入字节流
			InputStreamReader inputStR = new InputStreamReader(input);//将输入字节流转为字符流
			BufferedReader buffer = new BufferedReader(inputStR);//为输入流增加缓冲信息
			getClientMessage(buffer);
			buffer.close();
			inputStR.close();
			input.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//接收客户机数据
	private void getClientMessage(BufferedReader buffer){
		try {
			
			//获得客户端信息
			String info = "";
			while((info = buffer.readLine())!= null){
				System.out.println("客户机："+info);
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
		int order_number;//订单数目
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
        //调用蚁群算法
        Ant ant;//声明蚂蚁类
		MWNumericArray input1=null;
		MWNumericArray input2=null;
		Object[] result = null;
		String[] rsString;
		String rs="";//发送到客户端的字符串
		try{
			ant = new Ant();
			input1 = new MWNumericArray(b_point,MWClassID.DOUBLE);
			input2 = new MWNumericArray(c_point,MWClassID.DOUBLE);
			result = ant.Ant_Colony_Algorithm_2(1, input1,input2);
			rsString = result[0].toString().split("\\s*");//保存返回结果

			for(int i=0;i<rsString.length;i++){
				if (rsString[i].length()==0) {
					rs+=",";
				}else {
					rs+=rsString[i].charAt(0);
				}
			}
			//处理字符前缀
			
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
		sendClientMessage(rs);//将算法完成后的序列发送给客户端
	}
	//向客户机发送数据
	private void sendClientMessage(String info){
		try{
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);//将响应信息打包为打印流
			writer.write(info);
			writer.flush();
			//关闭资源
			writer.close();
			outputStream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){//主方法
		while(true){
			new MyTcpServer().getServer();//创建本类对象
		}
	}
}
