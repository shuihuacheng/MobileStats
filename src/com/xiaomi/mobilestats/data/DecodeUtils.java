package com.xiaomi.mobilestats.data;

public class DecodeUtils {
	final static int x = 188366965;

	public static byte[] getEncode(String line) {
		byte[] bytes = line.getBytes();
		int[] vResult = new int[bytes.length % 4 == 0 ? bytes.length / 4 : bytes.length / 4 + 1];
		int nPos = 0;
		for (int i = 0; i < bytes.length; i += 4) {
			byte b0 = (i >= bytes.length ? 0 : bytes[i]);
			byte b1 = (i + 1 >= bytes.length ? 32 : bytes[i + 1]);
			byte b2 = (i + 2 >= bytes.length ? 32 : bytes[i + 2]);
			byte b3 = (i + 3 >= bytes.length ? 32 : bytes[i + 3]);
			int cur = BitConverter.toInt(new byte[] { b0, b1, b2, b3 });
			// System.out.print(cur);
			if (nPos % 2 == 0) {
				vResult[nPos] = cur ^ x;
			} else {
				vResult[nPos] = ~cur;
			}
			// System.out.println("  ->  " + vResult[nPos]);
			nPos++;
		}
		byte[] byteDen = new byte[vResult.length * 4];
		nPos = 0;
		for (int i = 0; i < vResult.length; i++) {
			byte[] result = BitConverter.getBytes(vResult[i]);
			for (int j = 0; j < result.length; j++) {
				byteDen[nPos] = result[j];
				nPos++;
			}
		}
		return byteDen;
	}

	public static String getDenCode(byte[] bytes) {
		int[] vResult = new int[bytes.length % 4 == 0 ? bytes.length / 4 : bytes.length / 4 + 1];
		int nPos = 0;
		for (int i = 0; i < bytes.length; i += 4) {

			byte b0 = (i >= bytes.length ? 0 : bytes[i]);
			byte b1 = (i + 1 >= bytes.length ? 32 : bytes[i + 1]);
			byte b2 = (i + 2 >= bytes.length ? 32 : bytes[i + 2]);
			byte b3 = (i + 3 >= bytes.length ? 32 : bytes[i + 3]);
			int cur = BitConverter.toInt(new byte[] { b0, b1, b2, b3 });
			// System.out.print(cur);
			if (nPos % 2 == 0) {
				vResult[nPos] = cur ^ x;
			} else {
				vResult[nPos] = ~cur;
			}
			// System.out.println("  ->  " + vResult[nPos]);
			nPos++;
		}
		byte[] byteDen = new byte[vResult.length * 4];
		nPos = 0;
		for (int i = 0; i < vResult.length; i++) {
			byte[] result = BitConverter.getBytes(vResult[i]);
			for (int j = 0; j < result.length; j++) {
				byteDen[nPos] = result[j];
				nPos++;
			}
		}
		String result = new String(byteDen);
		return result;
	}
}
