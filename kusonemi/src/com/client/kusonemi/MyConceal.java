package com.client.kusonemi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

public class MyConceal {
	private Crypto crypto;
	private Context context;
	private final String KEY_PREF = "key_pref";
	private final String ACCESS_KEY = "access_key";

	MyConceal(Context ct) throws Exception{
		crypto = new Crypto(
				new SharedPrefsBackedKeyChain(ct)
				, new SystemNativeCryptoLibrary());

		// Check for whether the crypto functionality is available
		// This might fail if android does not load libaries correctly.
		if(!crypto.isAvailable()){
			throw new Exception();
		}
		context = ct;
	}

	//暗号化
	//成功すればbyte[]を返す。無理ならnull
	public byte[] Encrypting(String plaintext){
		//keyを用意・格納
		SharedPreferences pf = context.getSharedPreferences(
				KEY_PREF, Context.MODE_PRIVATE);
		String key = pf.getString(ACCESS_KEY, null);
		if(key == null){
			key = UUID.randomUUID().toString();
		}

		Editor et = pf.edit();
		et.putString(ACCESS_KEY, key);
		et.apply();

		byte[] cipherText = null;
		try{
			//UTF-8でbyte[]変換->暗号化,keyは復号化で必要
			cipherText = crypto.encrypt(plaintext.getBytes("utf-8")
					, new Entity(key));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (KeyChainException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (CryptoInitializationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return cipherText;
	}

	//復号化
	//成功すればStringが返る、失敗でnull
	public String Decrypting(byte[] cipherText){
		//keyを引き出し
		SharedPreferences pf = context.getSharedPreferences(
				KEY_PREF, Context.MODE_PRIVATE);
		String key = pf.getString(ACCESS_KEY, null);
		if(key == null){
			Log.d(context.getPackageName(),"key is null");
			return null;
		}

		String decryptData = null;
		try{
			//復号化されたbyte[]をキャッシュ
			byte[] decrypted = crypto.decrypt(cipherText, new Entity(key));

			decryptData = new String(decrypted, "utf-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (KeyChainException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (CryptoInitializationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return decryptData;
	}

}
