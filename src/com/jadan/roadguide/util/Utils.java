package com.jadan.roadguide.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Utils {

	public static void showToast(Context context, CharSequence text) {
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
