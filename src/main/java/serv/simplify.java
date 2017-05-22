package serv;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class simplify {
	
	public static String simplifyUTF8Name(String name) {
		byte ptext[] = name.getBytes(ISO_8859_1);
		String newline = new String(ptext, UTF_8);
		
		char[] a = new char[newline.length()];
		for (int c = 0; c<newline.length(); c++) {
			int charVal = (int)newline.charAt(c);
			if (charVal < 128) {
				a[c] = newline.charAt(c);
			} else if (charVal > 127) {
				char newChar = (char)simplifyChar(charVal);
				a[c] = newChar;
			}
		}
		String synName = new String(a);
		
		return synName;
	}
	
	
	public static String simplifyName(String name) {
		String newline = name;
		
		char[] a = new char[newline.length()];
		for (int c = 0; c<newline.length(); c++) {
			int charVal = (int)newline.charAt(c);
			if (charVal < 128) {
				a[c] = newline.charAt(c);
			} else if (charVal > 127) {
				char newChar = (char)simplifyChar(charVal);
				a[c] = newChar;
			}
		}
		String synName = new String(a);
		
		return synName;
	}
	
	

	public static int simplifyChar(int n) {
		
		if (n >= 192 && n <=197) {
			return 65; //A
		} else if (n == 199) {
			return 67; //C
		} else if (n >=200 && n<=203) {
			return 69; //E
		} else if (n>=204 && n<=207) {
			return 73; //I
		} else if (n == 208) {
			return 68; //D
		} else if (n == 209) {
			return 78; //N
		} else if (n>= 210 && n<= 214) {
			return 79; //O
		} else if (n == 215) {
			return 88; //X
		} else if (n == 216) {
			return 79; //O
		} else if (n>= 218 && n<=220) {
			return 85; //U
		} else if (n == 221) {
			return 85; //U
		} else if (n == 223) {
			return 115; //s
		} else if (n>= 224 && n<229) {
			return 97; //a
		} else if (n == 231) {
			return 99; //c
		} else if (n>=232 && n<= 235) {
			return 101;
		} else if (n>= 236 && n<= 239) {
			return 105; //i
		} else if (n == 241) {
			return 110; //n
		} else if (n>=242 && n<=246) {
			return 111; //o
		} else if (n == 248) {
			return 111; //o
		} else if (n>= 249 && n<=252) {
			return 117; //u
		} else if (n == 253 || n == 255) {
			return 121; //y
		} else if (n == 256 || n == 258 || n == 260) {
			return 65; //A
		} else if (n == 257 || n == 259 || n == 261) {
			return 97; //a
		} else if (n == 262 || n == 264 || n == 266 | n == 268) {
			return 67; //C
		} else if (n == 263 || n == 265 || n == 267 || n == 269) {
			return 99; //c
		} else if (n == 270 || n == 272) {
			return 68; //D
		} else if (n == 271 || n == 273) {
			return 100; //d
		} else if (n == 274 || n == 276 || n == 278 || n == 280 || n == 282) {
			return 69; //E
		} else if (n == 275 || n == 277 || n == 279 || n == 281 || n == 281) {
			return 101; //e
		} else if (n == 285 || n == 287 || n == 289 || n == 291) {
			return 103; //g
		} else if (n == 284 || n == 286 || n == 288 || n == 290) {
			return 71; //G
		} else if (n == 292) {
			return 72; //H
		} else if (n >= 293 && n <= 295) {
			return 104; //h
		} else if (n>= 296 && n<= 299) {
			return 73; //I
		} else {
			return 63; //?
		}

	}


}
