package com.example.jack.mobcomdemo;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

	@Test
	public void testUnicode() throws Exception {
		String expected = "http://www.cmpassport.com/unisdk/rs/getphonescrip";
		char[] URL_GET_PHONE_SCRIPT = new char[] {0x0068,0x0074,0x0074,0x0070,0x003a,0x002f,0x002f,0x0077
				,0x0077,0x0077,0x002e,0x0063,0x006d,0x0070,0x0061,0x0073,0x0073,0x0070,0x006f,0x0072,0x0074,0x002e,0x0063
				,0x006f,0x006d,0x002f,0x0075,0x006e,0x0069,0x0073,0x0064,0x006b,0x002f,0x0072,0x0073,0x002f,0x0067,0x0065
				,0x0074,0x0070,0x0068,0x006f,0x006e,0x0065,0x0073,0x0063,0x0072,0x0069,0x0070};
		String text = String.valueOf(URL_GET_PHONE_SCRIPT);
		Assert.assertEquals(expected, text);
	}

	@Test
	public void testReference() throws Exception {
		Singleton s1 = Singleton.getInstance();
		Singleton s2 = Singleton.getInstance();
		boolean reference = s1 == s2;
		boolean content = s1.equals(s2);
		Assert.assertTrue(reference);
		Assert.assertTrue(content);

		String str1 = new String("1");
		String str2 = str1;
		Assert.assertTrue(str1 == str2);
	}

	private static class Singleton {
		private static Singleton instance;

		private Singleton() {

		}

		public static synchronized Singleton getInstance() {
			if (instance == null) {
				instance = new Singleton();
			}
			return instance;
		}
	}
}