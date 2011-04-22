/*
   Copyright 2011 Devoteam/Uperto

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.uperto.smartmenu;

import android.app.Activity;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SmartMenuActivity extends Activity {
	private GestureLibrary	mLibrary;
	private TextView		mText;
	private Button 			mButton1;
	private Button			mButton2;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        // Init the gesture library
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
        	finish();
        }
        
        mText = (TextView) findViewById(R.id.text);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        
        // Init the buttons
        mButton1.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mText.setText("I did things");
				return false;
			}
        });
        
        mButton2.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mText.setText("I did other things");
				return false;
			}
        });
        
        // Init the SmartMenu with our gesture library
        SmartMenuOverlayView smartMenu = (SmartMenuOverlayView) findViewById(R.id.gestures);
        smartMenu.setGestureLibrary(mLibrary);
        
        // Uncomment to change background color and line properties
        //Paint painter = smartMenu.getPainter();
        //painter.setColor(Color.GREEN);
        //painter.setStrokeWidth(1);
        //smartMenu.setBackgroundColor(Color.argb(200, 255, 0, 0));
        
        // Add items to the menu
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon1);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Item 1", bmp) {
    		public boolean onItemSelected() {
    			mText.setText("Item 1 selected");
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon2);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Item 2", bmp) {
    		public boolean onItemSelected() {
    			mText.setText("Item 2 selected");
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon3);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Item 3", bmp) {
    		public boolean onItemSelected() {
    			mText.setText("Item 3 selected");
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon4);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Item 4", bmp) {
    		public boolean onItemSelected() {
    			mText.setText("Item 4 selected");
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon5);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Item 5", bmp) {
    		public boolean onItemSelected() {
    			mText.setText("Item 5 selected");
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon6);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Item 6", bmp) {
    		public boolean onItemSelected() {
    			mText.setText("Item 6 selected");
				return true;
    		}
    	});
    	
    	if (savedInstanceState != null && savedInstanceState.getBoolean("menuOpened")) {
    		smartMenu.open(false);
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putBoolean("menuOpened", true);
    }
}
