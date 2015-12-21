
package com.gpit.android.animation;  

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewAnimator;

import com.gpit.android.library.R;

/**
 * This class contains methods for creating {@link Animation} objects for some of the most common animation, including a 3D flip animation, {@link FlipAnimation}.
 * Furthermore, utility methods are provided for initiating fade-in-then-out and flip animations.
 * 
 */
public class AnimationFactory {
	
	/**
	 * The {@code FlipDirection} enumeration defines the most typical flip view transitions: left-to-right and right-to-left. {@code FlipDirection} is used during the creation of {@link FlipAnimation} animations.
	 * 
	 *
	 */
	public static enum FlipDirection {
		LEFT_RIGHT, 
		RIGHT_LEFT;
		
		public float getStartDegreeForFirstView() {
			return 0;
		}
		
		public float getStartDegreeForSecondView() {
			switch(this) {
			case LEFT_RIGHT:
				return -90;
			case RIGHT_LEFT:
				return 90;
			default:
				return 0;
			}
		}
		
		public float getEndDegreeForFirstView() {
			switch(this) {
			case LEFT_RIGHT:
				return 90;
			case RIGHT_LEFT:
				return -90;
			default:
				return 0;
			}
		}
		
		public float getEndDegreeForSecondView() {
			return 0;
		}
		
		public FlipDirection theOtherDirection() {
			switch(this) {
			case LEFT_RIGHT:
				return RIGHT_LEFT;
			case RIGHT_LEFT:
				return LEFT_RIGHT;
			default:
				return null;
			}
		}
	};
	 
	
	/**
	 * Create a pair of {@link FlipAnimation} that can be used to flip 3D transition from {@code fromView} to {@code toView}. A typical use case is with {@link ViewAnimator} as an out and in transition.
	 * 
	 * NOTE: Avoid using this method. Instead, use {@link #flipTransition}.
	 *  
	 * @param fromView the view transition away from
	 * @param toView the view transition to
	 * @param dir the flip direction
	 * @param duration the transition duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 
	 * @return
	 */
	public static Animation[] flipAnimation(final View fromView, final View toView, FlipDirection dir, long duration, Interpolator interpolator) {
		Animation[] result = new Animation[2];
		float centerX;
		float centerY;
		
		centerX = fromView.getWidth() / 2.0f;
		centerY = fromView.getHeight() / 2.0f; 
		
		Animation outFlip= new FlipAnimation(dir.getStartDegreeForFirstView(), dir.getEndDegreeForFirstView(), centerX, centerY, FlipAnimation.SCALE_DEFAULT, FlipAnimation.ScaleUpDownEnum.SCALE_DOWN);
		outFlip.setDuration(duration);
		outFlip.setFillAfter(true);
		outFlip.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator); 

		AnimationSet outAnimation = new AnimationSet(true);
		outAnimation.addAnimation(outFlip); 
		result[0] = outAnimation; 
		
		// Uncomment the following if toView has its layout established (not the case if using ViewFlipper and on first show)
		//centerX = toView.getWidth() / 2.0f;
		//centerY = toView.getHeight() / 2.0f; 
		
		Animation inFlip = new FlipAnimation(dir.getStartDegreeForSecondView(), dir.getEndDegreeForSecondView(), centerX, centerY, FlipAnimation.SCALE_DEFAULT, FlipAnimation.ScaleUpDownEnum.SCALE_UP);
		inFlip.setDuration(duration);
		inFlip.setFillAfter(true);
		inFlip.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator);
		inFlip.setStartOffset(duration);   
		
		AnimationSet inAnimation = new AnimationSet(true); 
		inAnimation.addAnimation(inFlip); 
		result[1] = inAnimation; 
		
		return result;
		
	}
	
	/**
	 * Flip to the next view of the {@code ViewAnimator}'s subviews. A call to this method will initiate a {@link FlipAnimation} to show the next View.  
	 * If the currently visible view is the last view, flip direction will be reversed for this transition.
	 *  
	 * @param viewAnimator the {@code ViewAnimator}
	 * @param dir the direction of flip
	 */
	public static void flipTransition(final ViewAnimator viewAnimator, FlipDirection dir) {   
		
		final View fromView = viewAnimator.getCurrentView();
		final int currentIndex = viewAnimator.getDisplayedChild();
		final int nextIndex = (currentIndex + 1)%viewAnimator.getChildCount();
		
		final View toView = viewAnimator.getChildAt(nextIndex);

		Animation[] animc = AnimationFactory.flipAnimation(fromView, toView, (nextIndex < currentIndex?dir.theOtherDirection():dir), 200, null);
  
		viewAnimator.setOutAnimation(animc[0]);
		viewAnimator.setInAnimation(animc[1]);
		
		viewAnimator.showNext();   
	}
	
	//////////////

 
	/**
	 * Slide animations to enter a view from left.
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation inFromLeftAnimation(long duration, Interpolator interpolator) {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(duration);
		inFromLeft.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator); //AccelerateInterpolator
		return inFromLeft;
	}
 
	/**
	 * Slide animations to hide a view by sliding it to the right
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation outToRightAnimation(long duration, Interpolator interpolator) {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoRight.setDuration(duration);
		outtoRight.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator);
		return outtoRight;
	}
 
	/**
	 * Slide animations to enter a view from right.
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation inFromRightAnimation(long duration, Interpolator interpolator) {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromRight.setDuration(duration);
		inFromRight.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator);
		return inFromRight;
	}
 
	/**
	 * Slide animations to hide a view by sliding it to the left.
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation outToLeftAnimation(long duration, Interpolator interpolator) {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoLeft.setDuration(duration);
		outtoLeft.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator);
		return outtoLeft;
	} 
 
	/**
	 * Slide animations to enter a view from top.
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation inFromTopAnimation(long duration, Interpolator interpolator) {
		Animation infromtop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		infromtop.setDuration(duration);
		infromtop.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator);
		return infromtop;
	}
	
	public static Animation inFromBottomAnimation(long duration, Interpolator interpolator) {
		Animation infromtop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		infromtop.setDuration(duration);
		infromtop.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator);
		return infromtop;
	} 
 
	/**
	 * Slide animations to hide a view by sliding it to the top
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation outToTopAnimation(long duration, Interpolator interpolator) {
		Animation outtotop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT, -1.0f
		);
		outtotop.setDuration(duration); 
		outtotop.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator); 
		return outtotop;
	} 

	/**
	 * Slide animations to hide a view by sliding it to the bottom
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param interpolator the interpolator to use (pass {@code null} to use the {@link AccelerateInterpolator} interpolator) 	
	 * @return a slide transition animation
	 */
	public static Animation outToBottomAnimation(long duration, Interpolator interpolator) {
		Animation outtotop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT, 1.0f
		);
		outtotop.setDuration(duration); 
		outtotop.setInterpolator(interpolator==null?new AccelerateInterpolator():interpolator); 
		return outtotop;
	} 
	
	
	/**
	 * A fade animation that will fade the subject in by changing alpha from 0 to 1.
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param delay how long to wait before starting the animation, in milliseconds
	 * @return a fade animation
	 * @see #fadeInAnimation(View, long)
	 */
	public static Animation fadeInAnimation(long duration, long delay) {  
		
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeIn.setDuration(duration);
		fadeIn.setStartOffset(delay);
		
		return fadeIn;
	}
	
	public static Animation fadeInAnimation(float startAlpha, float endAlpha, long duration, long delay) {  
		Animation fadeIn = new AlphaAnimation(startAlpha, endAlpha);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeIn.setDuration(duration);
		fadeIn.setStartOffset(delay);
		
		return fadeIn;
	}

	/**
	 * A fade animation that will fade the subject out by changing alpha from 1 to 0.
	 * 
	 * @param duration the animation duration in milliseconds
	 * @param delay how long to wait before starting the animation, in milliseconds
	 * @return a fade animation
	 * @see #fadeOutAnimation(View, long)
	 */
	public static Animation fadeOutAnimation(long duration, long delay) {   

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new DecelerateInterpolator());
		fadeOut.setStartOffset(delay);
		fadeOut.setDuration(duration);

		return fadeOut;
	} 

	/**
	 * A fade animation that will ensure the View starts and ends with the correct visibility
	 * @param view the View to be faded in
	 * @param duration the animation duration in milliseconds
	 * @return a fade animation that will set the visibility of the view at the start and end of animation
	 */
	public static Animation fadeInAnimation(long duration, final View view) { 
		Animation animation = fadeInAnimation(500, 0); 

	    animation.setAnimationListener(new AnimationListener() { 
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.VISIBLE);
			} 
			
			public void onAnimationRepeat(Animation animation) { 
			}  
			
			public void onAnimationStart(Animation animation) {
				view.setVisibility(View.GONE); 
			} 
	    });
	    
	    return animation;
	}

	/**
	 * A fade animation that will ensure the View starts and ends with the correct visibility
	 * @param view the View to be faded out
	 * @param duration the animation duration in milliseconds
	 * @return a fade animation that will set the visibility of the view at the start and end of animation
	 */
	public static Animation fadeOutAnimation(long duration, final View view) {
		
		Animation animation = fadeOutAnimation(500, 0); 

	    animation.setAnimationListener(new AnimationListener() { 
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			} 
			
			public void onAnimationRepeat(Animation animation) { 
			}  
			
			public void onAnimationStart(Animation animation) {
				view.setVisibility(View.VISIBLE); 
			} 
	    });
	    
	    return animation;
		
	}

	/**
	 * Creates a pair of animation that will fade in, delay, then fade out
	 * @param duration the animation duration in milliseconds
	 * @param delay how long to wait after fading in the subject and before starting the fade out
	 * @return a fade in then out animations
	 */
	public static Animation[] fadeInThenOutAnimation(long duration, long delay) {  
		return new Animation[] {fadeInAnimation(duration,0), fadeOutAnimation(duration, duration+delay)};
	}  
	
	/**
	 * Fades the view in. Animation starts right away.
	 * @param v the view to be faded in
	 */
	public static void fadeOut(View v, long delay) { 
		if (v==null) return;  
	    v.startAnimation(fadeOutAnimation(delay, v)); 
	} 
	
	/**
	 * Fades the view out. Animation starts right away.
	 * @param v the view to be faded out
	 */
	public static void fadeIn(View v, long delay) { 
		if (v==null) return;
		
	    v.startAnimation(fadeInAnimation(delay, v)); 
	}
	
	/**
	 * Fades the view in, delays the specified amount of time, then fades the view out
	 * @param v the view to be faded in then out
	 * @param delay how long the view will be visible for
	 */
	public static void fadeInThenOut(final View v, long delay) {
		if (v==null) return;
		 
		v.setVisibility(View.VISIBLE);
		AnimationSet animation = new AnimationSet(true);
		Animation[] fadeInOut = fadeInThenOutAnimation(500,delay); 
	    animation.addAnimation(fadeInOut[0]);
	    animation.addAnimation(fadeInOut[1]);
	    animation.setAnimationListener(new AnimationListener() { 
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
			} 
			public void onAnimationRepeat(Animation animation) { 
			}  
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE); 
			} 
	    });
	    
	    v.startAnimation(animation); 
	}
	
	public static Animation shakeAnimation(Context context) {
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
	    return shake;
	}
	
	public static void shakeAnimation(Context context, View v) {
		Animation shakeAnimation = shakeAnimation(context);
		v.startAnimation(shakeAnimation);
	}

	public static Animation scaleOutAnimation(Context context, int duration) {
		ScaleAnimation scale = (ScaleAnimation) AnimationUtils.loadAnimation(context, R.anim.scale_out);
		scale.setDuration(duration);
		scale.setInterpolator(new AccelerateInterpolator());
		
	    return scale;
	}

	public static void scaleOutAnimation(Context context, View v, int duration) {
		Animation scaleAnimation = scaleOutAnimation(context, duration);
		v.startAnimation(scaleAnimation);
	}
	
	public static Animation scaleInAnimation(Context context, int duration) {
		ScaleAnimation scale = (ScaleAnimation) AnimationUtils.loadAnimation(context, R.anim.scale_in);
		scale.setDuration(duration);
		scale.setInterpolator(new DecelerateInterpolator());
		
	    return scale;
	}

	public static void scaleInAnimation(Context context, View v, int duration) {
		Animation scaleAnimation = scaleInAnimation(context, duration);
		v.startAnimation(scaleAnimation);
	}
}