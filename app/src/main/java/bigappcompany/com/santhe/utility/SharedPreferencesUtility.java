package bigappcompany.com.santhe.utility;

import android.content.Context;

public class SharedPreferencesUtility {
	
	public static final String FIRSTTIMEBOOL = "FIRST_TIME_BOOL";
	public static final String ID = "ID";
	public static final String ISNORMALLOGIN = "ISNORMALLOGIN";
	public static final String ISSIGNUP = "issignup";
	public static final String USERPHONE = "userphone";
	private static final String APP_PREF = "Santhe";
	private static final String LONGITUDE = "longitude";
	private static final String PROFILE_NAME = "PROFILE_NAME";
	private static final String PROFILE_PICTURE = "PROFILE_PICTURE";
	private static final String LATITUDE = "LATITUDE";
	
	
	public static void clearSP(Context context) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit()
		    .clear().commit();
		
	}
	
	public static void setFirstTime(Context context, Boolean bool) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putBoolean(FIRSTTIMEBOOL, bool).commit();
	}
	
	
	public static Boolean getFirstTime(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getBoolean(FIRSTTIMEBOOL, true);
	}
	
	
	
	public static void setIsNormalLogin(Context context, Boolean Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putBoolean(ISNORMALLOGIN, Values).commit();
	}
	
	public static Boolean getIsNormalLogin(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getBoolean(ISNORMALLOGIN, true);
	}
	
	public static void setIssignup(Context context, Boolean Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putBoolean(ISSIGNUP, Values).commit();
	}
	
	public static Boolean getIssignup(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getBoolean(ISSIGNUP, true);
	}
	
	public static void setAuthKey(Context context, String Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString("Authkey", Values).commit();
	}
	
	public static String getAuthkey(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString("Authkey", "");
	}
	
	public static void setlatitude(Context context, String Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString(LATITUDE, Values).commit();
	}
	
	public static String getlatitude(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString(LATITUDE, "");
	}
	
	public static void setProfilePicture(Context context, String Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString(PROFILE_PICTURE, Values).commit();
	}
	
	public static String getProfilePicture(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString(PROFILE_PICTURE, "");
	}
	
	public static void setProfileName(Context context, String Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString(PROFILE_NAME, Values).commit();
	}
	
	public static String getProfileName(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString(PROFILE_NAME, "");
	}
	public static void setUserPhone(Context context, String Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString(USERPHONE, Values).commit();
	}
	
	public static String getUserPhone(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString(USERPHONE, "");
	}
	
	public static void setlongitude(Context context, String Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putString(LONGITUDE, Values).commit();
	}
	
	public static String getlongitude(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getString(LONGITUDE, "");
	}
	
	public static void setPositionOfSpinnerFamily(Context context, int Values) {
		context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit().putInt("positionFrag1", Values).commit();
	}
	
	public static int getPositionOfSpinnerFamily(Context context) {
		return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getInt("positionFrag1", 0);
	}
	
	
}
