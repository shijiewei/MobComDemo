package com.example.jack.mobcomdemo.util;

import com.mob.MobSDK;
import com.mob.tools.utils.SharePrefrenceHelper;

public class SPHelper {
	private static final int SDK_SP_VERSION = 2;
	private static final int VCODE_SP_VERSION = 1;

	private static final String KEY_NOT_SHOW_AGAIN = "not_show_again";

	private static SPHelper instance;
	private SharePrefrenceHelper spSDK;
	private SharePrefrenceHelper spVCODE;
	
	public static SPHelper getInstance() {
		if (instance == null) {
			instance = new SPHelper();
		}
		return instance;
	}
	
	private SPHelper() {
		spSDK = new SharePrefrenceHelper(MobSDK.getContext());
		spSDK.open("COMMON_DEMO", SDK_SP_VERSION);
		spVCODE = new SharePrefrenceHelper(MobSDK.getContext());
		spVCODE.open("COMMON_DEMO", VCODE_SP_VERSION);
	}

//	public String getConfig() throws Throwable {
//		String raw = spSDK.getString(KEY_CONFIG);
//		if (TextUtils.isEmpty(raw)) {
//			return null;
//		}
//		String config = Crypto.decodeConfig(raw);
//		if (config != null) {
//			return config;
//		}
//		return null;
//	}
//
//	public void setConfig(String config)  throws Throwable{
//		if (TextUtils.isEmpty(config)) {
//			return;
//		}
//		String encoded = Crypto.encodeConfig(config);
//		spSDK.putString(KEY_CONFIG, encoded);
//	}
//
//	public long getLastRequestTimeMillis(String name) {
//		return spSDK.getLong(name);
//	}
//
//	public void setLastRequestTimeMillis(String name, long lastRequestTimeMills) {
//		spSDK.putLong(name, lastRequestTimeMills);
//	}
//
//	public boolean isAllowReadContact() {
//		return spSDK.getBoolean(KEY_READ_CONTACT);
//	}
//
//	public void setAllowReadContact() {
//		spSDK.putBoolean(KEY_READ_CONTACT, true);
//	}
//
//	public boolean isWarnWhenReadContact() {
//		return spSDK.getBoolean(KEY_READ_CONTACT_WARN);
//	}
//
//	public void setWarnWhenReadContact(boolean warn) {
//		spSDK.putBoolean(KEY_READ_CONTACT_WARN, warn);
//	}
//
////	public String getVerifyCountry() throws Throwable {
////		String rawString = spSDK.getString(KEY_VERIFY_COUNTRY);
////		if (TextUtils.isEmpty(rawString)) {
////			return null;
////		}
////		String key = MobSDK.getAppkey();
////		return (String)Crypto.decodeObject(key,rawString);
////	}
////
////	public void setVerifyCountry(String country) throws Throwable {
////		if (TextUtils.isEmpty(country)){
////			return;
////		}
////		String key = MobSDK.getAppkey();
////		String encodeCountry = Crypto.encodeObject(key,country);
////		spSDK.putString(KEY_VERIFY_COUNTRY, encodeCountry);
////	}
//
//	public String getVerifyPhone() throws Throwable {
//		String rawString = spSDK.getString(KEY_VERIFY_PHONE);
//		if (TextUtils.isEmpty(rawString)) {
//			return null;
//		}
//		String key = MobSDK.getAppkey();
//		return (String)Crypto.decodeObject(key,rawString);
//	}
//
//	public void setVerifyPhone(String phone) throws Throwable {
//		if (TextUtils.isEmpty(phone)) {
//			return;
//		}
//		String key = MobSDK.getAppkey();
//		String encodePhone = Crypto.encodeObject(key,phone);
//		spSDK.putString(KEY_VERIFY_PHONE,encodePhone);
//	}
//
//	public void clearBuffer() {
//		spSDK.remove(KEY_BUFFERED_NEW_FRIENDS);
//		spSDK.remove(KEY_BUFFERED_FRIENDS);
//		spSDK.remove(KEY_LAST_REQUEST_NEW_FRIENDS_TIME);
//		spSDK.remove(KEY_BUFFERED_CONTACT_PHONES);
//	}
//
//	public String getBufferedCountrylist() {
//		return spSDK.getString(KEY_BUFFERED_COUNTRYLIST);
//	}
//
//	public void setBufferedCountrylist(String countrylist) {
//		spSDK.putString(KEY_BUFFERED_COUNTRYLIST,countrylist);
//	}
//
//	public long getLastZoneAt() {
//		return spSDK.getLong(KEY_LAST_ZONE_AT);
//	}
//
//	public void setLastZoneAt(long zoneAt) {
//		spSDK.putLong(KEY_LAST_ZONE_AT, zoneAt);
//	}
//
//	public String getBufferedContactsSignature() {
//		return spSDK.getString(KEY_BUFFERED_CONTACT_SIGNATURE);
//	}
//
//	public void setBufferedContactsSignature(String signature) {
//		spSDK.putString(KEY_BUFFERED_CONTACT_SIGNATURE, signature);
//	}
//
//	@SuppressWarnings("unchecked")
//	public ArrayList<HashMap<String, Object>> getBufferedContacts() {
//		Object o = spSDK.get(KEY_BUFFERED_CONTACT);
//		if (o != null) {
//			return (ArrayList<HashMap<String, Object>>) o;
//		}
//
//		return new ArrayList<HashMap<String, Object>>();
//	}
//
//	public void setBufferedContacts(ArrayList<HashMap<String, Object>> contacts) {
//		spSDK.put(KEY_BUFFERED_CONTACT, contacts);
//	}
//
//	@SuppressWarnings("unchecked")
//	public ArrayList<HashMap<String, Object>> getBufferedFriends() {
//		synchronized (KEY_BUFFERED_FRIENDS) {
//			Object o = spSDK.get(KEY_BUFFERED_FRIENDS);
//			if (o != null) {
//				return (ArrayList<HashMap<String, Object>>) o;
//			}
//
//			return new ArrayList<HashMap<String, Object>>();
//		}
//	}
//
//	public void setBufferedFriends(ArrayList<HashMap<String, Object>> list) {
//		synchronized (KEY_BUFFERED_FRIENDS) {
//			spSDK.put(KEY_BUFFERED_FRIENDS, list);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public ArrayList<HashMap<String, Object>> getBufferedNewFriends() {
//		Object o = spSDK.get(KEY_BUFFERED_NEW_FRIENDS);
//		if (o != null) {
//			return (ArrayList<HashMap<String, Object>>) o;
//		}
//
//		return new ArrayList<HashMap<String, Object>>();
//	}
//
//	public void setBufferedNewFriends(ArrayList<HashMap<String, Object>> list) {
//		spSDK.put(KEY_BUFFERED_NEW_FRIENDS, list);
//	}
//
//	public long getLastRequestNewFriendsTime() {
//		return spSDK.getLong(KEY_LAST_REQUEST_NEW_FRIENDS_TIME);
//	}
//
//	public void setRequestNewFriendsTime() {
//		spSDK.putLong(KEY_LAST_REQUEST_NEW_FRIENDS_TIME, System.currentTimeMillis());
//	}
//
//	public void setBufferedContactPhones(String[] phones) {
//		spSDK.put(KEY_BUFFERED_CONTACT_PHONES, phones);
//	}
//
//	public String[] getBufferedContactPhones() {
//		Object o = spSDK.get(KEY_BUFFERED_CONTACT_PHONES);
//		if (o != null) {
//			return (String[]) o;
//		}
//
//		return new String[0];
//	}
//
//	public String getVCodeHash() {
//		return spVCODE.getString(KEY_VCODE_HASH);
//	}
//
//	public void setVCodeHash(String vcodeHash) {
//		spVCODE.putString(KEY_VCODE_HASH, vcodeHash);
//	}
//
//	public String getSMSID() {
//		return spVCODE.getString(KEY_SMSID);
//	}
//
//	public void setSMSID(String smsID) {
//		spVCODE.putString(KEY_SMSID, smsID);
//	}
//
//	public void setLog(String value) {
//		synchronized (KEY_LOG) {
//			String tmp = getLog();
//			if(!TextUtils.isEmpty(tmp)) {
//				value = tmp + "\r\n" + value;
//			}
//			spVCODE.putString(KEY_LOG, value);
//		}
//	}
//
//	public String getLog() {
//		return spVCODE.getString(KEY_LOG);
//	}
//
//	public void clearLog() {
//		synchronized (KEY_LOG) {
//			spVCODE.remove(KEY_LOG);
//		}
//	}
//
//	public String getToken() {
//		return spSDK.getString(KEY_TOKEN);
//	}
//
//	public void setToken(String token) {
//		if (TextUtils.isEmpty(token)) {
//			return;
//		}
//		spSDK.putString(KEY_TOKEN,token);
//	}

	public boolean isNotShowAgain() {
		return spSDK.getBoolean(KEY_NOT_SHOW_AGAIN);
	}

	public void setNotShowAgain() {
		spSDK.putBoolean(KEY_NOT_SHOW_AGAIN, true);
	}
}
