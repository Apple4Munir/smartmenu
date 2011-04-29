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
	
	// Which option is active
	private boolean	mChangedBackgroundColor = false;
	private boolean	mChangedPainterColor = false;
	private boolean	mChangedPainterStrokeWidth = false;
	private boolean	mChangedHaloCenterColor = false;
	private boolean mChangedHaloOutsideColor = false;
	private boolean mChangedHaloGradient = false;
	private boolean mChangedHaloRadius = false;
	
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
        final SmartMenuOverlayView smartMenu = (SmartMenuOverlayView) findViewById(R.id.gestures);
        smartMenu.setGestureLibrary(mLibrary);
        
        final Paint painter = smartMenu.getPainter();
        
        // Add items to the menu
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon1);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Background", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedBackgroundColor) {
    				smartMenu.setBackgroundColor(Color.argb(200, 0, 0, 0));
    				mChangedBackgroundColor = false;
    			} else {
    				smartMenu.setBackgroundColor(Color.argb(200, 255, 0, 0));
    				mChangedBackgroundColor = true;
    			}
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon2);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Painter color", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedPainterColor) {
    				painter.setColor(Color.WHITE);
    				mChangedPainterColor = false;
    			} else {
    				painter.setColor(Color.GREEN);
    				mChangedPainterColor = true;
    			}
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon3);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Stroke width", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedPainterStrokeWidth) {
    				painter.setStrokeWidth(3);
    				mChangedPainterStrokeWidth = false;
    			} else {
    				painter.setStrokeWidth(1);
    				mChangedPainterStrokeWidth = true;
    			}
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon4);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Halo center color", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedHaloCenterColor) {
    				smartMenu.setHaloCenterColor(Color.argb(100, 128, 128, 128));
    				mChangedHaloCenterColor = false;
    			} else {
    				smartMenu.setHaloCenterColor(Color.argb(100, 255, 0, 0));
    				mChangedHaloCenterColor = true;
    			}
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon5);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Halo out color", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedHaloOutsideColor) {
    				smartMenu.setHaloOutsideColor(Color.argb(10, 255, 255, 255));
    				mChangedHaloOutsideColor = false;
    			} else {
    				smartMenu.setHaloOutsideColor(Color.argb(20, 0, 0, 255));
    				mChangedHaloOutsideColor = true;
    			}
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon6);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Halo gradient", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedHaloGradient) {
    				smartMenu.setHaloGradient(15);
    				mChangedHaloGradient = false;
    			} else {
    				smartMenu.setHaloGradient(5);
    				mChangedHaloGradient = true;
    			}
				return true;
    		}
    	});
    	
    	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon7);
    	smartMenu.addMenuItem(new SmartMenuOverlayView.SmartMenuItem("Halo radius", bmp) {
    		public boolean onItemSelected() {
    			if (mChangedHaloRadius) {
    				smartMenu.setHaloRadius(30);
    				mChangedHaloRadius = false;
    			} else {
    				smartMenu.setHaloRadius(60);
    				mChangedHaloRadius = true;
    			}
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