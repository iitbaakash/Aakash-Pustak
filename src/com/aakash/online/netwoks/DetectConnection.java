package com.aakash.online.netwoks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class DetectConnection {
	private Context context;

	public DetectConnection(Context context){
		this.context = context;
	}

	public boolean isInternetConnected(){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  if (connectivity != null)
		  {
			  NetworkInfo[] info = connectivity.getAllNetworkInfo();
			  if (info != null)
				  for (int i = 0; i < info.length; i++)
					  if (info[i].getState() == NetworkInfo.State.CONNECTED)
					  {
						  Log.d("networkstate", "connected");
						  return true;
					  }

		  }
		  return false;
	}
}
