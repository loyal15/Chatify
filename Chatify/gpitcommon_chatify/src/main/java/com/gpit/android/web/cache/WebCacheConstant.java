/*
 * WEB Cache Module
 * Copyright (C) 2011 ZheXue Ding
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


package com.gpit.android.web.cache;

public class WebCacheConstant {
	public static WebCacheConstant CONSTANT;
	
	public static String WEB_CACHE_MODULE_NAME = "WEB_CACHE";
	
	// Storage
	public static final String APPLICATION_RES_CACHE_FOLDER = "res/cache/";
	public static final String APPLICATION_RES_HTML_FOLDER = APPLICATION_RES_CACHE_FOLDER + "html/";
	public static final String APPLICATION_RES_SOAP_FOLDER = APPLICATION_RES_CACHE_FOLDER + "soap/";
	public static final String APPLICATION_RES_IMAGE_FOLDER = APPLICATION_RES_CACHE_FOLDER + "image/";
	
	/*******************************************************************************
	 * APP CONSTANT
	 ******************************************************************************/
	// Others
	public static final int MAX_URL_PATH = 1024;
}
