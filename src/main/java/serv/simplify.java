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
			} else if (charVal > 127 && charVal < 301) {
				char newChar = (char)simplifyChar(charVal);
				a[c] = newChar;
			} else {
				char newChar = (char)charVal;
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
		} else if (n>= 296 && n<= 300) {
			return 73; //I
		} else if (n == 301 || n == 303 || n == 305) {
			return 105; //i
		} else if (n == 308 || n == 309) {
			return 74; //J
		} else if (n == 310) {
			return 75; //K
		} else if (n == 311 || n == 312) {
			return 107; //k
		} else if (n == 313 || n == 315 || n == 317 || n == 319 || n == 321) {
			return 76; //L
		} else if (n == 314 || n == 316 || n == 318 || n == 320 || n == 322) {
			return 108; //l
		} else if (n == 323 || n == 325 || n == 327 || n == 330) {
			return 78; //N
		} else if (n == 324 || n == 326 || n == 328 || n == 329 || n == 331) {
			return 110; //n)
		} else if (n == 332 || n == 334 || n == 336) {
			return 79; //O
		} else if (n == 333 || n == 335 || n == 337) {
			return 111; //o
		} else if (n == 340 || n == 342 || n == 344) {
			return 82; //R
		} else if (n == 341 || n == 343 || n == 345) {
			return 114; //r
		} else if (n == 346 || n == 348 || n == 350 || n == 352) {
			return 83; //S
		} else if (n == 347 || n == 349 || n == 351 || n == 353) {
			return 115; //s
		} else if (n == 354 || n == 356 || n == 358) {
			return 84; //T
		} else if (n == 355 || n == 357 || n == 359) {
			return 116; //t
		} else if (n == 360 || n == 362 || n == 364 || n == 366 || n == 368 || n == 370) {
			return 85; //U
		} else if (n == 361 || n == 363 || n == 365 || n == 367 || n == 369 || n == 371) {
			return 117; //u
		} else if (n == 371) {
			return 87; //W
		} else if (n == 372) {
			return 119; //w
		} else if (n == 374 || n == 376) {
			return 89; //Y
		} else if (n == 375) {
			return 121; //y
		} else if (n == 377 || n == 379 || n == 381) {
			return 90; //Z
		} else if (n == 378 || n == 380 || n == 382) {
			return 122; //z
		}
		
		
		
		else {
			return 63; //?
		}

	}


}
