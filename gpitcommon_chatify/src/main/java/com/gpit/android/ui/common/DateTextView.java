package com.gpit.android.ui.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.gpit.android.library.R;
import com.gpit.android.util.Utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DateTextView extends TextView implements OnClickListener {
	public final static String DEFAULT_DATE_FORMAT = "MMM dd yyyy";
	private String mDateFormatStr = DEFAULT_DATE_FORMAT;
	
	private DatePicker mDatePicker;
	private static AlertDialog.Builder mBuilder;
	private AlertDialog mDlgDate;
	private ViewGroup mDateLayout;
	private OnDateChangeListener mDateChangeListener = null;
	
	public DateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Set events
		setOnClickListener(this);
		
		// Get components
		mDateLayout = (ViewGroup)View.inflate(getContext(), R.layout.date_selector, null);
		try {
			mDatePicker = (DatePicker)mDateLayout.findViewById(R.id.dpDate);
			if (mDatePicker == null)
				return;
			
			mDatePicker.setCalendarViewShown(false);
			setTime(Utils.getDateTimeMilis());
		} catch (Exception e) {
			// For sure to work at the mock view
		}
	}

	public void openDialog() {
		// Set default date
		try {
			// Create dialog builder
			if (mDlgDate == null) {
				createDialog();
			}
			mDlgDate.show();
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e("error", e.getMessage());
		}
	}
	
	public void setDateFormat(String format) {
		mDateFormatStr = format;
		
		// Refresh text
		setTime(getTime());
	}
	
	public String getDateFormat() {
		return mDateFormatStr;
	}
	
	public Date getDate() {
		Date date = new Date(mDatePicker.getYear() - 1900, mDatePicker.getMonth(), mDatePicker .getDayOfMonth());
		
		return date;
	}
	
	public long getTime() {
		Date date = getDate();
		return Utils.getDateTimeMilis(date.getTime());
	}
	
	public void setTime(long time) {
		Date date = new Date(time);

		setDate(date);
	}
	
	public void setDate(Date date) {
		long oldTime = getTime();
		mDatePicker.updateDate(1900 + date.getYear(), date.getMonth(), date.getDate());
		updateText();
		
		if (mDateChangeListener != null && oldTime != getTime()) {
			mDateChangeListener.onDateChangeListener(getTime());
		}
	}
	
	private void updateText() {
		Date date = getDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormatStr);
		String dateStr = dateFormat.format(date);
		setText(dateStr);
	}
	
	/**************** ACTION *********************/
	public void movePrevDay() {
		long time = getTime();
		time -= Constant.DATE_MILISECONDS;
		
		Date date = new Date(time);
		setDate(date);
	}
	
	public void moveNextDay() {
		long time = getTime();
		time += Constant.DATE_MILISECONDS;

		Date date = new Date(time);
		setDate(date);
	}

	public void movePrevWeek() {
		long time = getTime();
		time -= Constant.WEEK_MILISECONDS;
		
		Date date = new Date(time);
		setDate(date);
	}
	
	public void moveNextWeek() {
		long time = getTime();
		time += Constant.WEEK_MILISECONDS;

		Date date = new Date(time);
		setDate(date);
	}
	
	public void movePrevMonth() {
		long time = getTime();
		
		Calendar cal = new GregorianCalendar();
		Date date = new Date(time);
		cal.set(date.getYear() + 1900, date.getMonth(), cal.getMinimum(Calendar.DATE), 0, 0, 0);
		cal.getTimeInMillis();
		cal.add(Calendar.MONTH, -1);
		
		date = new Date(cal.getTimeInMillis());
		setDate(date);
	}
	
	public void moveNextMonth() {
		long time = getTime();

		Calendar cal = new GregorianCalendar();
		Date date = new Date(time);
		cal.set(date.getYear() + 1900, date.getMonth(), cal.getMinimum(Calendar.DATE), 0, 0, 0);
		cal.getTimeInMillis();
		cal.add(Calendar.MONTH, 1);

		date = new Date(cal.getTimeInMillis());
		setDate(date);
	}
	
	public void movePrevYear() {
		long time = getTime();
		
		Calendar cal = new GregorianCalendar();
		Date date = new Date(time);
		cal.set(date.getYear() + 1900, cal.getMinimum(Calendar.MONTH), cal.getMinimum(Calendar.DATE), 0, 0, 0);
		cal.getTimeInMillis();
		cal.add(Calendar.YEAR, -1);
		
		date = new Date(cal.getTimeInMillis());
		setDate(date);
	}
	
	public void moveNextYear() {
		long time = getTime();

		Calendar cal = new GregorianCalendar();
		Date date = new Date(time);
		cal.set(date.getYear() + 1900, cal.getMinimum(Calendar.MONTH), cal.getMinimum(Calendar.DATE), 0, 0, 0);
		cal.getTimeInMillis();
		cal.add(Calendar.YEAR, 1);

		date = new Date(cal.getTimeInMillis());
		setDate(date);
	}
	
	private void createDialog() {
		mBuilder = new AlertDialog.Builder(getContext());
		mBuilder.setTitle(R.string.select_date_dlg_title);
		if (mDateLayout.getParent() != null)
			((ViewGroup)(mDateLayout.getParent())).removeView(mDateLayout);
		mBuilder.setView(mDateLayout);
		mBuilder.setPositiveButton(getResources().getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						// Update text view
						updateText();

						if (mDateChangeListener != null) {
							Handler handle = new Handler();
							handle.post(new Runnable() {
								public void run() {
									mDateChangeListener.onDateChangeListener(getTime());
								}

							});
						}
					}
				});
		mBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					dialog.dismiss();
					return true;
				}

				return false;
			}
		});
		
		mDlgDate = mBuilder.create();
		mDlgDate.setCancelable(true);
	}
	
	public void onClick(View v) {
		openDialog();
	}

	public void setOnDateChangeListener(OnDateChangeListener dateChangeListener) {
		mDateChangeListener = dateChangeListener;
	}
}
