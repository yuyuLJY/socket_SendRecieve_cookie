package socket_SendRecieve_cookie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class SocketClient1 {
	private static final int PORT = 1011;
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket = null;
		DataOutputStream out = null;
		try{
			//��Server�˷�����Ϣ
			server = new ServerSocket(PORT);
			socket = server.accept();
			out = new DataOutputStream(socket.getOutputStream());
			int count =0;
			while(count==0){
				Thread.sleep(1000);
				out.writeUTF(getRandomStr());
				out.flush();
				count = 1;
				System.out.println("Client��Sever������Ϣ");
			}
			//�����û�����֤�ɹ��Ժ󷢹�������Ϣ
			DataInputStream dis = null;
			InputStream is = null;
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			int number = 0;
			while(number ==0) {
				String ss = dis.readUTF();
				System.out.println("���յ�Sever����������Ϣ:"+ss);
				number = 1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//��Sever������Ϣ
	private static String getRandomStr() throws Exception{
		String str = "";
		int identifyNumber = (int) (Math.random()*30);//��֤��
		String name = "lili";
		String cookie = "123";
		String sanlie1 = encodeBySHA(name+" "+cookie);
		System.out.println("ɢ��1��"+sanlie1);
		String sanlie2 = encodeBySHA(sanlie1+" "+identifyNumber);
		System.out.println("ɢ��2��"+sanlie2);
		//����name��cookie��ɢ��ֵ
		str = name+" "+sanlie2+" "+identifyNumber;
		//���û��� �� ɢ��ֵ1д�����ݿ�
		//TODO ��Ϊ�Ѿ�д�������ݾ֣���ߵ��ԾͲ�д��
		//writeToMysql(name,sanlie1);
		return str;
	}
	
	//д�����ݿ�
	static void writeToMysql(String name ,String sanlie1) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // ����MYSQL JDBC��������
			// Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		Scanner in = new Scanner(System.in);
		try {//�ܲ�������
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/Internet_safty?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false", "root", "198876");
			// ����URLΪ jdbc:mysql//��������ַ/���ݿ��� �������2�������ֱ��ǵ�½�û���������	
			System.out.println("Success connect Mysql server!");
			Statement stmt = connect.createStatement();
			String sql = "insert into userInfo VALUES("+"'"+name+"','"+sanlie1+"')";
			System.out.println(sql);
			try {
				boolean rs = stmt.execute(sql);
			}catch (Exception ee){
				ee.printStackTrace();	
			}
			if (connect != null) {
				connect.close();//���������ˣ�Ҫ�ر�
			}
		} catch (Exception e) {
			System.out.print("get data error!\n");
			e.printStackTrace();
		}
		
	}
	
	
	/** 
	 * SHA1���� ʹ����ϢժҪMessageDigest ���� 
	 * @throws Exception 
	 */ 
	 public static String encodeBySHA(String str) throws Exception{ 
	 MessageDigest sha1; 
	 sha1 = MessageDigest.getInstance("SHA1"); 
	 //�������ֲ����� 
	// sha1 = MessageDigest.getInstance("SHA256"); 
	// sha1 = MessageDigest.getInstance("SHA384"); 
	// sha1 = MessageDigest.getInstance("SHA512"); 
	   
	 sha1.update(str.getBytes()); //�ȸ���ժҪ 
	 byte[] digest = sha1.digest(); //��ͨ��ִ���������֮������ղ�����ɹ�ϣ���㡣�ڵ��ô˷���֮��ժҪ�����á� 
	   
	 /* 
	  * ʹ��ָ���� byte �����ժҪ���������£�Ȼ�����ժҪ���㡣 
	  * Ҳ����˵���˷������ȵ��� update(input)�� 
	  * �� update �������� input ���飬Ȼ����� digest()�� 
	  */ 
	// byte[] digest = sha1.digest(str.getBytes()); 
	   
	 String hex = toHex(digest); 
	 //System.out.println("SHA1ժҪ:" + hex); 
	 return hex; 
	 }
	 
	 /** 
	  * sha1 ժҪת16���� 
	  * @param digest 
	  * @return 
	  */
	  private static String toHex(byte[] digest) { 
	  StringBuilder sb = new StringBuilder(); 
	  int len = digest.length; 
	    
	  String out = null; 
	  for (int i = 0; i < len; i++) { 
	 //  out = Integer.toHexString(0xFF & digest[i] + 0xABCDEF); //������ salt 
	   out = Integer.toHexString(0xFF & digest[i]);//ԭʼ���� 
	   if (out.length() == 1) { 
	   sb.append("0");//���Ϊ1λ ǰ�油��0 
	   } 
	   sb.append(out); 
	  } 
	  return sb.toString(); 
	  } 
}
