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
				System.out.println("���յ�Client��Ϣ��" + ss);
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
				// TODO ȥ���ݿ�ȡ���û�1��ɢ��ֵ1
				String sanlie1 = getSanlie1(name);
				System.out.println("ɢ��1��" + sanlie1);
				// TODO ������ɢ�к������ж�ɢ��ֵ1+��֤�� ��=ɢ��ֵ2
				boolean flag = judgeCorrect(sanlie1, identifyNumber, sanlie2);
				if (flag == true) {// ��ȷ��֤
					System.out.println("��֤�ɹ�");
					sendInfo(socket);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ȥ���ݿ�ȡ���û�1��ɢ��ֵ1
	static String getSanlie1(String name) {
		String sanlie1 = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // ����MYSQL JDBC��������
			// Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		Scanner in = new Scanner(System.in);
		try {// �ܲ�������
			Connection connect = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Internet_safty?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false",
					"root", "198876");
			// ����URLΪ jdbc:mysql//��������ַ/���ݿ��� �������2�������ֱ��ǵ�½�û���������
			System.out.println("Success connect Mysql server!");
			String sql = "select sanlie1 from userInfo where userInfo.name = '" + name + "';";
			System.out.println("sql:" + sql);
			try {
				Statement stmt = connect.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount(); // ����
				// ��ӡ����
				while (rs.next()) {// ����
					for (int i = 1; i <= columnCount; i++) {
						sanlie1 = rs.getString(i);
					}
				}
				if (connect != null) {
					connect.close();// ���������ˣ�Ҫ�ر�
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

	// �Ƚ��Ƿ���֤�ɹ�
	static boolean judgeCorrect(String sanlie1, String identifyNumber, String sanlie2) throws Exception {
		String sanlie2_count = encodeBySHA(sanlie1 + " " + identifyNumber);
		System.out.println("ɢ��2��" + sanlie2);
		if (!sanlie2_count.equals(sanlie2)) {// ����ȷ���false
			return false;
		}
		return true;
	}

	/**
	 * SHA1���� ʹ����ϢժҪMessageDigest ����
	 * 
	 * @throws Exception
	 */
	public static String encodeBySHA(String str) throws Exception {
		MessageDigest sha1;
		sha1 = MessageDigest.getInstance("SHA1");

		sha1.update(str.getBytes()); // �ȸ���ժҪ
		byte[] digest = sha1.digest(); // ��ͨ��ִ���������֮������ղ�����ɹ�ϣ���㡣�ڵ��ô˷���֮��ժҪ�����á�

		/*
		 * ʹ��ָ���� byte �����ժҪ���������£�Ȼ�����ժҪ���㡣 Ҳ����˵���˷������ȵ��� update(input)�� �� update ��������
		 * input ���飬Ȼ����� digest()��
		 */
		// byte[] digest = sha1.digest(str.getBytes());

		String hex = toHex(digest);
		// System.out.println("SHA1ժҪ:" + hex);
		return hex;
	}

	/**
	 * sha1 ժҪת16����
	 * 
	 * @param digest
	 * @return
	 */
	private static String toHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		int len = digest.length;

		String out = null;
		for (int i = 0; i < len; i++) {
			// out = Integer.toHexString(0xFF & digest[i] + 0xABCDEF); //������ salt
			out = Integer.toHexString(0xFF & digest[i]);// ԭʼ����
			if (out.length() == 1) {
				sb.append("0");// ���Ϊ1λ ǰ�油��0
			}
			sb.append(out);
		}
		return sb.toString();
	}

	// TODO ��֤��ȷ������Ϣ����Client
	static void sendInfo(Socket socket) {
		DataOutputStream out = null;
		try {
			System.out.println("��client������Ϣ");
			out = new DataOutputStream(socket.getOutputStream());
			int count = 0;
			while (count == 0) {
				Thread.sleep(1000);
				out.writeUTF("������д�������Ϣ");
				out.flush();
				count = 1;
				System.out.println("sever��client������Ϣ���");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
