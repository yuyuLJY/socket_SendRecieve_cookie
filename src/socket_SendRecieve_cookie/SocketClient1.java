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
			//向Server端发送信息
			server = new ServerSocket(PORT);
			socket = server.accept();
			out = new DataOutputStream(socket.getOutputStream());
			int count =0;
			while(count==0){
				Thread.sleep(1000);
				out.writeUTF(getRandomStr());
				out.flush();
				count = 1;
				System.out.println("Client向Sever发送信息");
			}
			//监听用户端验证成功以后发过来的信息
			DataInputStream dis = null;
			InputStream is = null;
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			int number = 0;
			while(number ==0) {
				String ss = dis.readUTF();
				System.out.println("接收到Sever发过来的信息:"+ss);
				number = 1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//向Sever发送信息
	private static String getRandomStr() throws Exception{
		String str = "";
		int identifyNumber = (int) (Math.random()*30);//认证码
		String name = "lili";
		String cookie = "123";
		String sanlie1 = encodeBySHA(name+" "+cookie);
		System.out.println("散列1："+sanlie1);
		String sanlie2 = encodeBySHA(sanlie1+" "+identifyNumber);
		System.out.println("散列2："+sanlie2);
		//产生name和cookie的散列值
		str = name+" "+sanlie2+" "+identifyNumber;
		//把用户名 和 散列值1写入数据库
		//TODO 因为已经写入了数据局，后边调试就不写了
		//writeToMysql(name,sanlie1);
		return str;
	}
	
	//写进数据库
	static void writeToMysql(String name ,String sanlie1) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // 加载MYSQL JDBC驱动程序
			// Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		Scanner in = new Scanner(System.in);
		try {//能不能连上
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/Internet_safty?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false", "root", "198876");
			// 连接URL为 jdbc:mysql//服务器地址/数据库名 ，后面的2个参数分别是登陆用户名和密码	
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
				connect.close();//结束流程了，要关闭
			}
		} catch (Exception e) {
			System.out.print("get data error!\n");
			e.printStackTrace();
		}
		
	}
	
	
	/** 
	 * SHA1加密 使用消息摘要MessageDigest 处理 
	 * @throws Exception 
	 */ 
	 public static String encodeBySHA(String str) throws Exception{ 
	 MessageDigest sha1; 
	 sha1 = MessageDigest.getInstance("SHA1"); 
	 //以下三种不可用 
	// sha1 = MessageDigest.getInstance("SHA256"); 
	// sha1 = MessageDigest.getInstance("SHA384"); 
	// sha1 = MessageDigest.getInstance("SHA512"); 
	   
	 sha1.update(str.getBytes()); //先更新摘要 
	 byte[] digest = sha1.digest(); //再通过执行诸如填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置。 
	   
	 /* 
	  * 使用指定的 byte 数组对摘要进行最后更新，然后完成摘要计算。 
	  * 也就是说，此方法首先调用 update(input)， 
	  * 向 update 方法传递 input 数组，然后调用 digest()。 
	  */ 
	// byte[] digest = sha1.digest(str.getBytes()); 
	   
	 String hex = toHex(digest); 
	 //System.out.println("SHA1摘要:" + hex); 
	 return hex; 
	 }
	 
	 /** 
	  * sha1 摘要转16进制 
	  * @param digest 
	  * @return 
	  */
	  private static String toHex(byte[] digest) { 
	  StringBuilder sb = new StringBuilder(); 
	  int len = digest.length; 
	    
	  String out = null; 
	  for (int i = 0; i < len; i++) { 
	 //  out = Integer.toHexString(0xFF & digest[i] + 0xABCDEF); //加任意 salt 
	   out = Integer.toHexString(0xFF & digest[i]);//原始方法 
	   if (out.length() == 1) { 
	   sb.append("0");//如果为1位 前面补个0 
	   } 
	   sb.append(out); 
	  } 
	  return sb.toString(); 
	  } 
}
