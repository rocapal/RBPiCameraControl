/*
 *
 *  Copyright (C) Roberto Calvo Palomino
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal at gmail dot com>
 *
 */

package es.rocapal.utils.Download;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class DownloadTextFileAsyncTask extends AsyncTask<Uri, Void, String>
{

	private Context mContext;
	private String mTitle, mMessage;
	private ProgressDialog mPd;
	private IDownloadTextFileAsyncTask mListener;

	public DownloadTextFileAsyncTask (Context ctx, String title, String message)
	{
		mContext = ctx;
		mTitle = title;
		mMessage = message;
	}

	public void setListener (IDownloadTextFileAsyncTask listener)
	{
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {    		
		super.onPreExecute();    		
		mPd = ProgressDialog.show(mContext, mTitle, mMessage);
	}

	@Override
	protected String doInBackground(Uri... params) {

		return new HTTPActions().doGetPetition(params[0].toString());		

	}

	@Override
	protected void onPostExecute(String result) {
		
		super.onPostExecute(result);

		if (mPd.isShowing())
			mPd.dismiss();
		
		if (result != null)
		{
			if (mListener != null)
				mListener.downloadedSuccessfully(result);
				
		}
		else
		{		
			if (mListener != null)
				mListener.downloadFailed(result);
		}
	}



}