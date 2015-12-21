package com.gpit.android.ui.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gpit.android.util.Utils;

public class TimeTextView extends TextView implements OnClickListener {
	public final static String DEFAULT_DATE_FORMAT = "hh:mm:ss a";
	private String mTimeFormatStr = DEFAULT_DATE_FORMAT;
	
	private static AlertDialog.Builder mBuilder;
	private TimePickerDialog mDlgDate;

	private OnDateChangeListener mDateChangeListener = null;
	
	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Set events
		setOnClickListener(this);
		
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}

	public void setTimeFormat(String format) {
		mTimeFormatStr = format;
	}
	
	public String getTimeFormat() {
		return mTimeFormatStr;
	}
	
	public Date getDate(String dateStr) {
		Date date = Utils.getDate(dateStr, mTimeFormatStr);
		
		return date;
	}
	
	private TimePickerDialog createDialog() {
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		String dateStr = getText().toString();
		Date date = getDate(dateStr);
		
		calendar.setTimeInMillis(date.getTime());
		TimePickerDialog dialog = new TimePickerDialog(getContext(), mTimeSetListener, 
				calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
		
		return dialog;
	}
	
	public long getTime() {
		String dateStr = getText().toString();
		Date date = getDate(dateStr);
		long miliseconds = (date.getHours() * 60 + date.getMinutes()) * 60 * 1000;
		
		return miliseconds;
	}
	
	public void setTime(long miliseconds) {
		miliseconds /= (1000 * 60);
		int hours = (int)(miliseconds / 60);
		int minutes = (int)(miliseconds % 60);
		
		setTime(hours, minutes);
	}
	
	public void setTime(int hour, int min) {
		/*
		long miliseconds = (hour * 60 + min) * 60 * 1000;
		SimpleDateFormat dateFormat = new SimpleDateFormat(mTimeFormatStr, Locale.getDefault());
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT")); 
		cal.set(0 + 1900, 1, 0, 0, 0, 0);
		miliseconds = cal.getTimeInMillis() + miliseconds;
		
		Date date = new Date(miliseconds);
		String dateStr = dateFormat.format(date);
		setText(dateStr);
		*/
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, min);
		SimpleDateFormat dateFormat = new SimpleDateFormat(mTimeFormatStr);
		String dateStr =  dateFormat.format(cal.getTime());
		setText(dateStr);
		
	}
	
	public void onClick(View v) {
		// Set default date
		try {
			mDlgDate = createDialog();
			mDlgDate.show();
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e("error", e.getMessage());
		}
	}

	public OnTimeSetListener mTimeSetListener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setTime(hourOfDay, minute);
			
			if (mDateChangeListener != null)
				mDateChangeListener.onTimeChangeListener(hourOfDay, minute);
		}
	};
	
	public void setOnDateChangeListener(OnDateChangeListener dateChangeListener) {
		mDateChangeListener = dateChangeListener;
	}
}
