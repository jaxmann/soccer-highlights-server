import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class simplify {
	
	public static String simplifyName(String name) {
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
	
	

	public static int simplifyChar(int n) {

		switch (n) {
		case 224: 
			n = 97; //a
			break;
		case 225: 
			n = 97; //a
			break;
		case 226: 
			n = 97; //a
			break;
		case 227: 
			n = 97; //a
			break;
		case 228: 
			n = 97; //a
			break;
		case 353: 
			n = 115; //s
			break;
		case 231: 
			n = 99; //c
			break;
		case 232: 
			n = 101; //e
			break;
		case 233: 
			n = 101; //e
			break;
		case 234: 
			n = 101; //e
			break;
		case 235: 
			n = 101; //e
			break;
		case 237: 
			n = 105; //i
			break;
		case 238: 
			n = 105; //i
			break;
		case 239: 
			n = 105; //i
			break;
		case 241: 
			n = 110; //n
			break;
		case 242: 
			n = 111; //o
			break;
		case 243: 
			n = 111; //o
			break;
		case 244: 
			n = 111; //o
			break;
		case 246: 
			n = 111; //o
			break;
		case 250: 
			n = 117; //u
			break;
		case 252: 
			n = 117; //u
			break;
		}
		return n;
	}


}
