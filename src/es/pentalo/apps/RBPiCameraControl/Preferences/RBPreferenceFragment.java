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


package es.pentalo.apps.RBPiCameraControl.Preferences;

import java.lang.reflect.Type;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.pentalo.apps.RBPiCameraControl.R;
import es.pentalo.apps.RBPiCameraControl.API.Command;
import es.rocapal.utils.Download.DownloadTextFileAsyncTask;
import es.rocapal.utils.Download.IDownloadTextFileAsyncTask;

public class RBPreferenceFragment extends PreferenceFragment implements IDownloadTextFileAsyncTask{
	
	private final String TAG = getClass().getSimpleName();
		
	private String PREFIX_PHOTO = "photo_";
	private String PREFIX_VIDEO = "video_";
	private String PREFIX = PREFIX_PHOTO;
	
	private RBPFeatures mFeature = RBPFeatures.PHOTO;
	
	private List<Command> mCommands;
	
	public enum RBPFeatures { PHOTO, VIDEO };
	
		
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);              
        
    }
    
    public void setFeature (RBPFeatures feature)
    {
    	mFeature = feature;
    	if (mFeature == RBPFeatures.PHOTO)
    		PREFIX = PREFIX_PHOTO;
    	else
    		PREFIX = PREFIX_VIDEO;
    }
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	
    	   DownloadTextFileAsyncTask myTask = 
           		new DownloadTextFileAsyncTask(getActivity(), getString(R.string.pd_title_config), getString(R.string.pd_message_config));
           myTask.setListener(this);
           myTask.execute(Uri.parse(getString(R.string.rbpi_url) + "api/" + PREFIX.split("_")[0] + "/params/"));
    }
    
    @Override
	public void downloadedSuccessfully(Object data) {

    	try
    	{
			String strJson = (String) data;		
			Gson gson = new Gson();
	
			Type listType = new TypeToken<List<Command>>() {}.getType();
			mCommands = gson.fromJson(strJson, listType); 
			
			showPreferences(); 
    	}
    	catch (com.google.gson.JsonSyntaxException ex)
    	{
    		Log.e(TAG, ex.getMessage());
    		Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();    		
    	}
	}

	@Override
	public void downloadFailed (Object data) {
		Toast.makeText(getActivity(), getString(R.string.toast_error_preferences), Toast.LENGTH_SHORT).show();
		
	}

    
    private void showPreferences ()
    {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());
        
        root.addPreference( createCategoryPreference("Basic Config"));
        
        if (mCommands != null)
        {
        	for (Command command: mCommands)
        	{        		
        		// Add the categories
        		if (command.name.equals("exif"))
        			root.addPreference( createCategoryPreference("Advance Config"));
        		else if (command.name.equals("sharpness"))
        			root.addPreference( createCategoryPreference("Image Config"));
        		
        		// add checkbox
        		if ( command.options != null && command.options.size() == 2)
        			root.addPreference ( createCheckPrefFromCommand (command));
        			        	
        		// add list
        		else if ( command.options != null && command.options.size() > 2)
        			root.addPreference ( createListPrefFromCommand (command));
 
        		//add EditTex
        		else
        			root.addPreference ( createEditTextPrefFromCommand (command));
			}
        }
        
        setPreferenceScreen(root);
                               
    }
    

    
    private Preference createCategoryPreference (String title)
    {
    	PreferenceCategory prefCat = new PreferenceCategory(getActivity());
    	prefCat.setTitle(title);    	
    	return prefCat;
    	
    }
    
    private Preference createListPrefFromCommand (Command command)
    {
    	
    	 ListPreference listPref = new ListPreference(getActivity());
    	 listPref.setKey(PREFIX + command.name);
    	 listPref.setTitle(command.name); 
    	 listPref.setSummary(command.description);      	     					
    	 
    	 final CharSequence[] charsOptions = command.options.toArray(new CharSequence[command.options.size()]);    	   
    	 
    	 listPref.setEntryValues(charsOptions);
    	 listPref.setEntries(charsOptions);
         
         return listPref;
    }
    
    private Preference createCheckPrefFromCommand (Command command)
    {
    	
    	 CheckBoxPreference checkboxPref = new CheckBoxPreference(getActivity());
         checkboxPref.setKey(PREFIX + command.name);
         checkboxPref.setTitle(command.name); 
         checkboxPref.setSummary(command.description);            
         
         return checkboxPref;
    }
    
    
    private Preference createEditTextPrefFromCommand (Command command)
    {
    	
    	 EditTextPreference editTextPref = new EditTextPreference(getActivity());
         editTextPref.setKey(PREFIX + command.name);
         editTextPref.setTitle(command.name); 
         editTextPref.setSummary(command.description);            
         
         
         
         return editTextPref;
    }
   
    
    

	
    
    
}
