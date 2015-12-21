/*
 * TypeSmart Keyboard
 * Copyright (C) 2011 Barry Fruitman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.gpit.android.util;

import java.util.ArrayList;

import junit.framework.Assert;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PopupMenuView extends LinearLayout implements OnCreateContextMenuListener, OnKeyListener {
	// current popup menu
	private static PopupMenuView currPopupMenu = null;
	
	private ViewGroup parentView;
	
	public ContextMenu mMenu;
	public ContextMenuInfo mMenuInfo;
	
	@SuppressWarnings("unused")
	private boolean isShowed = false;
	@SuppressWarnings("unused")
	private static boolean isCloseAllMenu = false;
	
	// Menu item list
	private ArrayList<String> mMenuList = new ArrayList<String>(5);
	
	private int mIconResId;
	private int mTitleResId;
	private Object mTag;
	
	// Menu item handler
	private OnPopupMenuItemClickListener mListener;
	
	@Deprecated
	public PopupMenuView(ViewGroup view, 
			ArrayList<String> list, int titleResId, int iconResId) {
		this(view, list, titleResId, iconResId, null);
	}
	
	public PopupMenuView(ViewGroup view, 
			ArrayList<String> list, int titleResId, int iconResId, Object tag) {
		super(view.getContext());
		
		parentView = view;
		isCloseAllMenu = false;
		
		setMenuList(list);
		setTitle(titleResId);
		setIcon(iconResId);
		setTag(tag);
		
		setOnCreateContextMenuListener(this);
		setOnKeyListener(this);
		
		parentView.addView(this);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu);

		mMenu = menu;
		mMenuInfo = menuInfo;
		buildMenu();
		
		
		isShowed = true;
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			isShowed = false;
			return true;
		}
		
		return false;
	}
	
	public boolean showContextMenu() {
		currPopupMenu = this;
		
		return super.showContextMenu();
	}
	
	public static void closeAllMenu() {
		if (currPopupMenu != null && currPopupMenu.mMenu != null) {
			currPopupMenu.parentView.removeView(currPopupMenu);
			currPopupMenu.setVisibility(View.GONE);

			/*
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) currPopupMenu.mMenuInfo;

			if (info.targetView != null)
				info.targetView.setVisibility(View.GONE);
			
			MenuItem item = currPopupMenu.mMenu.findItem(0);
			item.setVisible(false);
			*/
			
			isCloseAllMenu = true;
		}
	}
	
	/**
	 * Build sub menu items
	 */
	public void buildMenu() {
		int index = 0;
		
		mMenu.clear();
		mMenu.setHeaderTitle(mTitleResId);
		mMenu.setHeaderIcon(mIconResId);
		for (String eachMenu : mMenuList) {
			mMenu.add(0, index, index, eachMenu).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					if (mListener != null) {
						mListener.onPopupMenuItemClicked(item.getItemId(), item.getTitle().toString());
					}
					
					return true;
				}
			});
			index++;
		}
	}
	
	/**
	 * Set menu items
	 */
	public void setMenuList(ArrayList<String> list) {
		Assert.assertTrue(list != null);
		
		mMenuList.clear();
		mMenuList.addAll(list);
	}
	
	public void setTitle(int resId) {
		mTitleResId = resId;
	}
	
	public void setIcon(int resId) {
		mIconResId = resId;
	}
	
	public void setTag(Object tag) {
		mTag = tag;
	}
	
	public void setOnPopupMenuItemClickListener(OnPopupMenuItemClickListener listener) {
		mListener = listener;
	}
}
