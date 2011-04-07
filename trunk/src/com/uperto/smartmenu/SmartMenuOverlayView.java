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

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class SmartMenuOverlayView extends GestureOverlayView implements OnGesturePerformedListener {
	// These two values will be modified according to the layout size...
	protected static int sCenterRadius = 32;
	protected static int sBranchesLength = 24;
	// ...but not this one
	protected static int sBranchesRadius = 24;
	
	// Edit these to change the drawing speed
	protected static int sCircleSpeed = 30;
	protected static int sLineSpeed = 10;
	
	// These values will be modified according to the layout size and orientation
	protected static int sLabelMargin = 10;
	protected static int sLabelHeight = 50;
	protected static int sLabelWidth = 200;
	protected static int sLabelFont = 30;
	
	// Make these static to compute them only once
	protected static final double RAD_45 = Math.PI/4;
	protected static final double COS_45 = Math.cos(RAD_45);
	protected static final double SIN_45 = Math.sin(RAD_45);
	
	private SmartMenuCircleView	mCircleView;
	private GestureLibrary		mLibrary;
	
	/**
	 * @param context
	 */
	public SmartMenuOverlayView(Context context) {
		super(context);
		init(context);
	}
	
	/**
	 * @param context
	 * @param attributes
	 */
	public SmartMenuOverlayView(Context context, AttributeSet attributes) {
		super(context, attributes);
		init(context);
	}
	
	/**
	 * Called by all the constructors
	 * @param context
	 */
	private void init(Context context) {
		mCircleView = new SmartMenuCircleView(context, this);
		mCircleView.setVisibility(INVISIBLE);
		addView(mCircleView);
		
		addOnGesturePerformedListener(this);
	}
	
	/**
	 * @return
	 */
	public GestureLibrary getGestureLibrary() {
		return mLibrary;
	}

	/**
	 * @param mLibrary
	 */
	public void setGestureLibrary(GestureLibrary mLibrary) {
		this.mLibrary = mLibrary;
	}
	
	/**
	 * Add an item to the menu
	 * @param item
	 */
	public void addMenuItem(SmartMenuItem item) {
		mCircleView.addMenuItem(item);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		
		// We want at least one prediction
		if (predictions.size() > 0) {
			Prediction prediction = predictions.get(0);
			// We want at least some confidence in the result
			if (prediction.score > 1.0) {
				bringChildToFront(mCircleView);
				mCircleView.reset();
				mCircleView.setVisibility(VISIBLE);
				setGestureVisible(false);
				setEventsInterceptionEnabled(false);
			}
		}
	}
	
	protected static class SmartMenuCircleView extends View {
		private int mCenterX, mCenterY;
		private int mCurrentAngle = 0;
		private int mCurrentBranch = 0;
		private int mCurrentBranchLength = 0;
		
		private boolean					mComputedValues = false;
		private Paint					mPainter;
		private SmartMenuOverlayView	mParent;
		private String					mText = null;
		private Vector<SmartMenuItem>	mItems;
		
		public SmartMenuCircleView(Context context, SmartMenuOverlayView parent) {
			super(context);
			this.mParent = parent;
			init();
		}
		
		protected void init() {
			setFocusable(true);
			setFocusableInTouchMode(true);
			
			setBackgroundColor(Color.argb(200, 0, 0, 0));
			// Init the painter, set parameters here
			mPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
			//painter.setAlpha(200);
			mPainter.setStyle(Paint.Style.STROKE);
			mPainter.setColor(Color.WHITE);
			mPainter.setStrokeWidth(3);
			mPainter.setTextAlign(Paint.Align.CENTER);
			mItems = new Vector<SmartMenuItem>();
		}
		
		protected void reset() {
			mCurrentAngle = 0;
			mCurrentBranch = 0;
			mCurrentBranchLength = 0;
			mComputedValues = false;
			setText(null);
			requestFocus();
		}
		
		protected void addMenuItem(SmartMenuItem item) {
			mItems.add(item);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				boolean isInMenu = false;
				for (int i=0; i<mItems.size(); i++) {
					int x = mItems.get(i).getX();
					int y = mItems.get(i).getY();
					
					if (Math.sqrt( (event.getX() - x)*(event.getX() - x) + (event.getY() - y) * (event.getY() - y) ) <= sBranchesRadius) {
						if (getText() == null || getText().equals(mItems.get(i).getText())) {
							setText(mItems.get(i).getText());
							invalidate();
						}
						isInMenu = true;
					}
				}
				if (!isInMenu) {
					if (getText() != null) {
						setText(null);
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				for (int i=0; i<mItems.size(); i++) {
					int x = mItems.get(i).getX();
					int y = mItems.get(i).getY();
					
					if (Math.sqrt( (event.getX() - x)*(event.getX() - x) + (event.getY() - y) * (event.getY() - y) ) <= sBranchesRadius) {
						if (mItems.get(i).onItemSelected()) {
							// Close the menu
							setVisibility(INVISIBLE);
							mParent.clear(false);
							mParent.setGestureVisible(true);
							mParent.setEventsInterceptionEnabled(true);
						}
					}
				}
				break;
			default:
			}
			return true;
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				setVisibility(INVISIBLE);
				mParent.clear(false);
				mParent.setGestureVisible(true);
				mParent.setEventsInterceptionEnabled(true);
			}
			return true;
		}

		@Override
		public void onDraw(Canvas canvas) {
			// Init global values
			if (!mComputedValues) { // Do it only once until reset
				if (getMeasuredHeight() > 400) {
					sLabelHeight = 40;
					sLabelMargin = 10;
					sLabelFont = 30;
				} else {
					sLabelHeight = 20;
					sLabelMargin = 5;
					sLabelFont = 15;
				}
				
				if (getMeasuredWidth() > getMeasuredHeight()) {
					// Landscape
					sBranchesLength = sCenterRadius = getMeasuredHeight() / 4 - sBranchesRadius - 5;
					sLabelWidth = getMeasuredWidth()/2 - sCenterRadius * 2 - sBranchesRadius * 2 - sLabelMargin;
				} else {
					// Portrait
					sBranchesLength = sCenterRadius = getMeasuredWidth() / 4 - sBranchesRadius - 5;
					sLabelWidth = getMeasuredWidth() - 2 * sLabelMargin;
				}
				
				mCenterX = getMeasuredWidth() / 2 ;
				mCenterY = getMeasuredHeight() / 2 ;
				
				mComputedValues = true;
			}
			
			// Draw the label only if necessary
			if (mText != null && mText.length() > 0) {
				drawLabel(canvas, mText);
			}
			
			// Draw the centered circle
			canvas.drawArc(new RectF(mCenterX-sCenterRadius, mCenterY-sCenterRadius, mCenterX+sCenterRadius, mCenterY+sCenterRadius),
					-90, mCurrentAngle, false, mPainter);
			
			if (mCurrentAngle < 360) {
				mCurrentAngle += sCircleSpeed;
				invalidate();
				return;
			} else {
				// End of circle, draw branches
				if (mItems.size() <= 4) {
					// Use positions 0, 2, 4, 6
					for (int i=0; i<mCurrentBranch; i++) {
						drawBranch(canvas, i*2, sBranchesLength, mItems.get(i));
					}
					
					if (mCurrentBranch < mItems.size()) {
						drawBranch(canvas, mCurrentBranch*2, mCurrentBranchLength, mItems.get(mCurrentBranch));
						
						if (mCurrentBranchLength >= sBranchesLength) {
							// Go to the next branch
							mCurrentBranchLength = 0;
							mCurrentBranch++;
						} else {
							mCurrentBranchLength += sLineSpeed;
						}
						invalidate();
						return;
					} else {
						// Ended
					}
				} else {
					// Use all positions
					for (int i=0; i<mCurrentBranch; i++) {
						drawBranch(canvas, i, sBranchesLength, mItems.get(i));
					}
					
					if (mCurrentBranch < mItems.size()) {
						drawBranch(canvas, mCurrentBranch, mCurrentBranchLength, mItems.get(mCurrentBranch));
						
						if (mCurrentBranchLength >= sBranchesLength) {
							mCurrentBranchLength = 0;
							mCurrentBranch++;
						} else {
							mCurrentBranchLength += sLineSpeed;
						}
						invalidate();
						return;
					} else {
						// Ended
					}
				}
			}
		}
		
		private void drawBranch(Canvas canvas, int position, int length, SmartMenuItem icon) {
			float x1=0, x2=0, y1=0, y2=0, x3=0, y3=0;
			switch (position) {
			case 0:
				x1 = x2 = x3 = mCenterX;
				y1 = mCenterY - sCenterRadius;
				y2 = y1 - length;
				y3 = y2 - sBranchesRadius;
				break;
			case 1:
				x1 = (float)(mCenterX + COS_45 * sCenterRadius);
				y1 = (float)(mCenterY - SIN_45 * sCenterRadius);
				x2 = (float)(x1 + COS_45 * length);
				y2 = (float)(y1 - SIN_45 * length);
				x3 = (float)(x2 + COS_45 * sBranchesRadius);
				y3 = (float)(y2 - SIN_45 * sBranchesRadius);
				break;
			case 2:
				y1 = y2 = y3 = mCenterY;
				x1 = mCenterX + sCenterRadius;
				x2 = x1 + length;
				x3 = x2 + sBranchesRadius;
				break;
			case 3:
				x1 = (float)(mCenterX + COS_45 * sCenterRadius);
				y1 = (float)(mCenterY + SIN_45 * sCenterRadius);
				x2 = (float)(x1 + COS_45 * length);
				y2 = (float)(y1 + SIN_45 * length);
				x3 = (float)(x2 + COS_45 * sBranchesRadius);
				y3 = (float)(y2 + SIN_45 * sBranchesRadius);
				break;
			case 4:
				x1 = x2 = x3 = mCenterX;
				y1 = mCenterY + sCenterRadius;
				y2 = y1 + length;
				y3 = y2 + sBranchesRadius;
				break;
			case 5:
				x1 = (float)(mCenterX - COS_45 * sCenterRadius);
				y1 = (float)(mCenterY + SIN_45 * sCenterRadius);
				x2 = (float)(x1 - COS_45 * length);
				y2 = (float)(y1 + SIN_45 * length);
				x3 = (float)(x2 - COS_45 * sBranchesRadius);
				y3 = (float)(y2 + SIN_45 * sBranchesRadius);
				break;
			case 6:
				y1 = y2 = y3 = mCenterY;
				x1 = mCenterX - sCenterRadius;
				x2 = x1 - length;
				x3 = x2 - sBranchesRadius;
				break;
			case 7:
				x1 = (float)(mCenterX - COS_45 * sCenterRadius);
				y1 = (float)(mCenterY - SIN_45 * sCenterRadius);
				x2 = (float)(x1 - COS_45 * length);
				y2 = (float)(y1 - SIN_45 * length);
				x3 = (float)(x2 - COS_45 * sBranchesRadius);
				y3 = (float)(y2 - SIN_45 * sBranchesRadius);
				break;
			default:
				return;
			}
			
			canvas.drawLine(x1, y1, x2, y2, mPainter);
			// Draw the final circle
			if (length >= sBranchesLength) {
				icon.setPosition((int)x3, (int)y3);
				canvas.drawBitmap(icon.getIcon(), x3 - sBranchesRadius, y3 - sBranchesRadius, mPainter);
				canvas.drawCircle(x3, y3, sBranchesRadius, mPainter);
			}
		}
	
		public void drawLabel(Canvas canvas, String text) {
			RectF rect = new RectF(sLabelMargin, sLabelMargin, sLabelMargin + sLabelWidth, sLabelMargin + sLabelHeight);
			canvas.drawRoundRect(rect, 10, 10, mPainter);
			Paint textPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
			textPainter.setTextAlign(Paint.Align.CENTER);
			textPainter.setTextSize(sLabelFont);
			textPainter.setColor(Color.WHITE);
			canvas.drawText(mText, sLabelMargin + sLabelWidth/2, sLabelMargin*2 + sLabelHeight/2, textPainter);
		}

		public void setText(String mText) {
			this.mText = mText;
		}

		public String getText() {
			return mText;
		}
	}
	
	public static class SmartMenuItem {
		private String mText;
		private Bitmap mIcon;
		// Position is used to test if the user is touching one of the elements
		private int mX = 0, mY = 0;
		
		/**
		 * @param text Text displayed
		 * @param icon Bitmap displayed into the circle
		 */
		public SmartMenuItem(String text, Bitmap icon) {
			this.mText = text;
			this.setIcon(icon);
		}

		/**
		 * @param mText
		 */
		public void setText(String mText) {
			this.mText = mText;
		}

		/**
		 * @return
		 */
		public String getText() {
			return mText;
		}

		/**
		 * @param mIcon
		 */
		public void setIcon(Bitmap mIcon) {
			this.mIcon = SmartMenuIconFactory.createRoundedBitmap(mIcon);
		}

		/**
		 * @return
		 */
		public Bitmap getIcon() {
			return mIcon;
		}
		
		/**
		 * @param x
		 * @param y
		 */
		public void setPosition (int x, int y) {
			this.setX(x);
			this.setY(y);
		}

		/**
		 * @param mX
		 */
		public void setX(int mX) {
			this.mX = mX;
		}

		/**
		 * @return
		 */
		public int getX() {
			return mX;
		}

		/**
		 * @param mY
		 */
		public void setY(int mY) {
			this.mY = mY;
		}

		/**
		 * @return
		 */
		public int getY() {
			return mY;
		}
		
		public boolean onItemSelected() {
			return true;
		}
	}
	
	public static class SmartMenuIconFactory {
		/**
		 * @param input Any input bitmap
		 * @return A bitmap resized and croped to be only a circle
		 */
		public static Bitmap createRoundedBitmap(Bitmap input) {
			// Create the mask
			Bitmap mask = Bitmap.createBitmap(sBranchesRadius * 2, sBranchesRadius * 2, Bitmap.Config.ARGB_8888);
			Canvas maskCanvas = new Canvas(mask);
			maskCanvas.drawARGB(0, 255, 255, 255);
			
			// Draw the circle into the mask
			Paint painter = new Paint(Paint.ANTI_ALIAS_FLAG);
			painter.setStyle(Paint.Style.FILL);
			painter.setColor(Color.argb(255, 0, 0, 0));
			maskCanvas.drawCircle(sBranchesRadius, sBranchesRadius, sBranchesRadius, painter);
			
			// Copy only the central part from the bitmap
			int beginX = input.getWidth() / 2 - sBranchesRadius;
			int beginY = input.getHeight() / 2 - sBranchesRadius;
			Bitmap temp = Bitmap.createBitmap(input, beginX, beginY, sBranchesRadius*2, sBranchesRadius*2);
			Bitmap output = temp.copy(Bitmap.Config.ARGB_8888, true);
			// Apply the mask
			for (int i=0; i<output.getWidth(); i++) {
				for (int j=0; j<output.getHeight(); j++) {
					int a = Color.alpha(mask.getPixel(i, j));
					if (a == 0)
						output.setPixel(i, j, Color.argb(0, 255, 0, 0));
				}
			}
			
			// Really needed ? Transparency doesn't work without this...
			Bitmap newoutput = Bitmap.createBitmap(sBranchesRadius * 2, sBranchesRadius * 2, Bitmap.Config.ARGB_8888);
			Canvas outputCanvas = new Canvas(newoutput);
			outputCanvas.drawARGB(0, 255, 255, 255);
			outputCanvas.drawBitmap(output, 0, 0, painter);
			
			return newoutput;
		}
	}
}
