package com.adiaz.munisports.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


/**
 * Created by toni on 17/09/2017.
 */

public class PreferencesUtils {

	public static String queryUUID(Context context) {
		String clientId;
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		if (sharedPreferences.contains(MuniSportsConstants.KEY_UUID)) {
			clientId = sharedPreferences.getString(MuniSportsConstants.KEY_UUID, "");
		} else {
			clientId = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(MuniSportsConstants.KEY_UUID, clientId);
			editor.commit();
		}
		return clientId;
	}

	public static String queryPreferenceTown(Context context) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getString(MuniSportsConstants.KEY_TOWN_NAME, null);
	}

	public static Long queryPreferenceTownId(Context context) {
		Long idTown = null;
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		if (sharedPreferences.contains(MuniSportsConstants.KEY_TOWN_ID)) {
			idTown = sharedPreferences.getLong(MuniSportsConstants.KEY_TOWN_ID, 0L);
		}
		return idTown;
	}
}
