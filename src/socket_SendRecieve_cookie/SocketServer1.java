package socket_SendRecieve_cookie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Scanner;

public class SocketServer1 {
	// private static final String HOST="172.20.104.179";
	private static final String HOST = "172.20.18.200";
	private static final int PORT = 1011;

	public static void main(String[] args) {
		Socket socket = null;
		DataInputStream dis = null;
		InputStream is = null;

		try {
			socket = new Socket(HOST, PORT);
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			int count = 0;
			while (count == 0) {
				String ss = dis.readUTF();
				System.out.println("接收到Client信息：" + ss);
				String[] info = ss.split(" ");
				count = 1;

				String name = "";
				String sanlie2 = "";
				String identifyNumber = "";
				if (info.length == 3) {
					name = info[0];
					sanlie2 = info[1];
					identifyNumber = info[2];
				}
				// TODO 去数据库取出用户1的散列值1
				String sanlie1 = getSanlie1(name);
				System.out.println("散列1：" + sanlie1);
				// TODO 依旧用散列函数，判断散列值1+认证码 ？=散列值2
				boolean flag = judgeCorrect(sanlie1, identifyNumber, sanlie2);
				if (flag == true) {// 正确认证
					System.out.println("认证成功");
					sendInfo(socket);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 去数据库取出用户1的散列值1
	static String getSanlie1(String name) {
		String sanlie1 = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // 加载MYSQL JDBC驱动程序
			// Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		Scanner in = new Scanner(System.in);
		try {// 能不能连上
			Connection connect = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Internet_safty?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false",
					"root", "198876");
			// 连接URL为 jdbc:mysql//服务器地址/数据库名 ，后面的2个参数分别是登陆用户名和密码
			System.out.println("Success connect Mysql server!");
			String sql = "select sanlie1 from userInfo where userInfo.name = '" + name + "';";
			System.out.println("sql:" + sql);
			try {
				Statement stmt = connect.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount(); // 列数
				// 打印数据
				while (rs.next()) {// 按行
					for (int i = 1; i <= columnCount; i++) {
						sanlie1 = rs.getString(i);
					}
				}
				if (connect != null) {
					connect.close();// 结束流程了，要关闭
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		} catch (Exception e) {
			System.out.print("get data error!\n");
			e.printStackTrace();
		}
		return sanlie1;
	}

	// 比较是否认证成功
	static boolean judgeCorrect(String sanlie1, String identifyNumber, String sanlie2) throws Exception {
		String sanlie2_count = encodeBySHA(sanlie1 + " " + identifyNumber);
		System.out.println("散列2：" + sanlie2);
		if (!sanlie2_count.equals(sanlie2)) {// 不相等返回false
			return false;
		}
		return true;
	}

	/**
	 * SHA1加密 使用消息摘要MessageDigest 处理
	 * 
	 * @throws Exception
	 */
	public static String encodeBySHA(String str) throws Exception {
		MessageDigest sha1;
		sha1 = MessageDigest.getInstance("SHA1");

		sha1.update(str.getBytes()); // 先更新摘要
		byte[] digest = sha1.digest(); // 再通过执行诸如填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置。

		/*
		 * 使用指定的 byte 数组对摘要进行最后更新，然后完成摘要计算。 也就是说，此方法首先调用 update(input)， 向 update 方法传递
		 * input 数组，然后调用 digest()。
		 */
		// byte[] digest = sha1.digest(str.getBytes());

		String hex = toHex(digest);
		// System.out.println("SHA1摘要:" + hex);
		return hex;
	}

	/**
	 * sha1 摘要转16进制
	 * 
	 * @param digest
	 * @return
	 */
	private static String toHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		int len = digest.length;

		String out = null;
		for (int i = 0; i < len; i++) {
			// out = Integer.toHexString(0xFF & digest[i] + 0xABCDEF); //加任意 salt
			out = Integer.toHexString(0xFF & digest[i]);// 原始方法
			if (out.length() == 1) {
				sb.append("0");// 如果为1位 前面补个0
			}
			sb.append(out);
		}
		return sb.toString();
	}

	// TODO 认证正确，把信息发给Client
	static void sendInfo(Socket socket) {
		DataOutputStream out = null;
		try {
			System.out.println("往client发送信息");
			out = new DataOutputStream(socket.getOutputStream());
			int count = 0;
			while (count == 0) {
				Thread.sleep(1000);
				out.writeUTF("这是我写给你的信息");
				out.flush();
				count = 1;
				System.out.println("sever给client发送信息完毕");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
