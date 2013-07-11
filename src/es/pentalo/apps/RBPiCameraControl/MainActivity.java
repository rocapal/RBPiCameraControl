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

package es.pentalo.apps.RBPiCameraControl;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;



public class MainActivity extends Activity {

	
	private String TAG = getClass().getSimpleName(); 
	
    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        
         
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
 
        viewPager.setAdapter(fragmentPagerAdapter);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.DKGRAY);
        
        /*
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float scaleFactor = metrics.density;
        
        int widthInPixels = metrics.widthPixels;
        int heightInPixels = metrics.heightPixels;
        
        float widthDp = widthInPixels / scaleFactor;
        float heightDp = heightInPixels / scaleFactor;
        
        float smallestWidth = Math.min(widthDp, heightDp);
        
        if (smallestWidth < 600) {
        	startActivity(new Intent(this, ImagePreference.class));
        }
        else
        	startActivity(new Intent(this, MainTablet.class));
        
        */
        
    }
	

    
    
    
    
}
