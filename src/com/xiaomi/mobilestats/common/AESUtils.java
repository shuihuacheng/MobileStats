package com.xiaomi.mobilestats.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AESUtils {
	private static final String AESTYPE = "AES/CBC/PKCS5Padding";
	public static final String ENCODE_KEY = "c0e9fcff59ecc3b8";
	private static final String IV = "b92939a1a2724a44";

	public static String encrypt(String content, String key) {
		try {
			Cipher cipher = Cipher.getInstance(AESTYPE);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(content.getBytes());
			return new String(Base64.encode(encrypted, Base64.NO_WRAP));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String desEncrypt(String content, String key) {
		try {
			byte[] encrypted = Base64.decode(content, Base64.NO_WRAP);

			Cipher cipher = Cipher.getInstance(AESTYPE);
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] original = cipher.doFinal(encrypted);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
