package mobile.cross.itm.crossmobile;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

public class VariableManager {
	public final static String USER_ID = "user_id";
	public final static String USER_PW = "user_pw";
	
	public static String user_id = "";
	public static String user_pic = "";
	
	public final static String PRODUCT_NAME = "pn";
	public final static String PRODUCT_DETAIL = "pd";
	public final static String PRODUCT_PRICE = "pp";
	public final static String PRODUCT_HOST = "ph";
	public final static String PRODUCT_TIME = "pt";
	public final static String PRODUCT_IMAGE = "pi";
	public final static String PRODUCT_INDEX = "pid";
	public final static String PRODUCT_TYPE = "pty";
	public final static String PRODUCT_CATEGORY = "pcy";
	public final static String PRODUCT_STATUS = "pst";
	public final static String PRODUCT_METHOD = "pmd";
	public final static String PRODUCT_USER_PIC = "prp";
	
	public static int POSITION;
	public static boolean FLAG_PRODUCT =  true;
	public static boolean FLAG_USER = true;
	public static boolean FLAG_CATEGORY = true;
	
	public final static String RES_INDEX = "rid";
	public final static String RES_TYPE = "rty";
	public final static String RES_VISITOR = "rv";
	public final static String RES_MESSAGE = "rm";
	public final static String RES_TIME = "rt";
	public final static String RES_PICTURE = "rp";
	
	public static long ct = 0;
	
	public static String msg = "";
	public static String from = "";
	public static String when = "";
	
	public static String msg2 = "";
	public static String from2 = "";
	public static String when2 = "";
	public static String user_pic2 = "";
	
	public static String name = "";
	public static String message = "";
	public static String path = "";
	public static String email = "";
	public static String pdate = "";
	public static String price = "";
	public static String pindex = "";
	public static String ptype = "";
	
	// 관심사, 성별을 0,1과 같은 숫자로 표현합니다.
	
	public static final String INT_CLOTHES = "0";
	public static final String INT_COSMETIC = "1";
	public static final String INT_BOOK = "2";
	public static final String INT_PET = "4";
	public static final String INT_HOME_GADGET = "3";
	
	public static final String GENDER_M = "0";
	public static final String GENDER_F = "1";
	
	public static boolean rec = true;
}
