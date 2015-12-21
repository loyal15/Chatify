package com.gpit.android.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.gpit.android.library.R;

public class AnimatedTextSwitcher extends FrameLayout {
	private TextSwitcher mTextSwitcher;
	private TextView mTextView;
	
	private int mLayoutID;
	private int mTextViewID;
	private int mAnimInId = R.anim.anim_popup_slide_bottom;
	private int mAnimOutId = R.anim.anim_popup_slide_top;
	
	private CharSequence mDesignText;
	private int mDesignTextColor;
	private CharSequence mCurrText;
	
	public AnimatedTextSwitcher(Context context) {
		this(context, null);
	}

	public AnimatedTextSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);

		initAttrs(attrs);
		initUI();
	}
	
	public void setTextColor(int color) {
		if (!(getContext() instanceof Activity))
			mTextView.setTextColor(mDesignTextColor);
	}
	
	public void setText(CharSequence text) {
		if (mCurrText != null && text != null) {
			// Skip if there is no changes.
			if (mCurrText.equals(text)) {
				return;
			}
		}
		
		if (getContext() instanceof Activity)
			mTextSwitcher.setText(text);
		else
			mTextView.setText(text);
			
		mCurrText = text;
	}

	private void initAttrs(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AnimatedTextSwitcher);
		
		mLayoutID = a.getResourceId(R.styleable.AnimatedTextSwitcher_layoutID, 0);
		mTextViewID = a.getResourceId(R.styleable.AnimatedTextSwitcher_textViewID, 0);
		mAnimInId = a.getResourceId(R.styleable.AnimatedTextSwitcher_animateInID, R.anim.anim_popup_slide_top);
		mAnimOutId = a.getResourceId(R.styleable.AnimatedTextSwitcher_animateOutID, R.anim.anim_popup_out_to_top);
		mDesignTextColor = a.getColor(R.styleable.AnimatedTextSwitcher_txtColor, 0);
		mDesignText = a.getText(R.styleable.AnimatedTextSwitcher_txt);
		
		a.recycle();
	}
	
	public void setAnimation(int animIdResId, int animOutResId) {
		mAnimInId = animIdResId;
		mAnimOutId = animOutResId;
		
		// Declare the in and out animations and initialize them  
        Animation in = AnimationUtils.loadAnimation(getContext(), mAnimInId);
        Animation out = AnimationUtils.loadAnimation(getContext(), mAnimOutId);
        
        // set the animation type of textSwitcher
        mTextSwitcher.setInAnimation(in);
        mTextSwitcher.setOutAnimation(out);		
	}
	
	private TextView createTextView() {
		ViewGroup layout = (ViewGroup) View.inflate(getContext(), mLayoutID, null);
        TextView myText = (TextView) layout.findViewById(mTextViewID);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        myText.setLayoutParams(params);
        
        // Detach view from the parent
        layout.removeView(myText);
        
        return myText;
	}
	
	private void initUI() {
		if (getContext() instanceof Activity) {
			mTextSwitcher = new TextSwitcher(getContext());
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mTextSwitcher.setLayoutParams(params);
			addView(mTextSwitcher);
			
			// Set the ViewFactory of the TextSwitcher that will create TextView object when asked
			mTextSwitcher.setFactory(new ViewFactory() {
	            public View makeView() {
	                // create new textView and set the properties like clolr, size etc
	                TextView myText = createTextView();
	                if (mDesignTextColor != 0)
	                	myText.setTextColor(mDesignTextColor);
	                return myText;
	            }
	        });
	        
	        setAnimation();
		} else {
			// Design preview
			mTextView = createTextView();
			addView(mTextView);
		}
		
		if (mDesignText != null)
			setText(mDesignText);
		
		if (mDesignTextColor != 0)
			setTextColor(mDesignTextColor);
	}
	
	private void setAnimation() {
		setAnimation(mAnimInId, mAnimOutId);
	}
}
