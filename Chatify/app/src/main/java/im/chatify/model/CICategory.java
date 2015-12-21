package im.chatify.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import im.chatify.CIApp;
import im.chatify.R;

/**
 * Created by administrator on 11/4/15.
 */
public class CICategory {

    private static ArrayList<CICategory> mCategoryListWithoutPlaceholder;
    private static ArrayList<CICategory> mCategoryListWithPlaceholder;

    public static ArrayList<CICategory> getCategoryList(Context context, boolean needPlaceholder) {

        if (mCategoryListWithoutPlaceholder == null) {
            mCategoryListWithoutPlaceholder = new ArrayList<CICategory>();
            mCategoryListWithoutPlaceholder.addAll(getLocalCategories(context));
        }

        if (mCategoryListWithPlaceholder == null) {

            mCategoryListWithPlaceholder = new ArrayList<CICategory>();

            if (needPlaceholder == true) {

                CICategory placeholderCategory = new CICategory(context);
                placeholderCategory.categoryName = context.getResources().getString(R.string.tag_business_category);
                mCategoryListWithPlaceholder.add(placeholderCategory);

                mCategoryListWithPlaceholder.addAll(getLocalCategories(context));
            }
        }

        if (needPlaceholder)
            return mCategoryListWithPlaceholder;

        return mCategoryListWithoutPlaceholder;
    }

    private static ArrayList<CICategory> getLocalCategories(Context context) {

        String categories = CIApp.getPreference(context).getString(CIConst.KEY_CATEGORY_BUSINESS, "");
        ArrayList<CICategory> categoryList = new ArrayList<CICategory>();

        if (categories.length() > 0) {

            Document doc = Jsoup.parse(categories);

            if (doc != null) {

                Elements catElements = doc.getElementsByTag("cat");

                for (int i = 0; i < catElements.size(); i ++) {

                    Element element = catElements.get(i);

                    if (element != null) {

                        CICategory category = new CICategory(context);
                        category.categoryName = element.attr("name");
                        category.categoryId = Integer.parseInt(element.attr("id"));
                        category.categoryLogo = element.text();

                        byte[] imageAsBytes = Base64.decode(category.categoryLogo.getBytes(), Base64.DEFAULT);

                        category.categoryBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        categoryList.add(category);
                    }
                }
            }
        }

        return categoryList;
    }

    public static void setCategoryList(ArrayList<CICategory> categories, boolean needPlaceholder) {

        if (needPlaceholder) {
            mCategoryListWithPlaceholder.clear();
            mCategoryListWithPlaceholder.addAll(categories);
        } else {
            mCategoryListWithoutPlaceholder.clear();
            mCategoryListWithoutPlaceholder.addAll(categories);
        }
    }

    public String       categoryLogo;
    public int          categoryId;
    public String       categoryName;
    public Bitmap       categoryBitmap;

    public CICategory(Context context) {

    }

    public CICategory(JSONObject object) {

        try {

            categoryLogo = object.getString("logo");
            categoryId = object.getInt("id");
            categoryName = object.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getImageByCategoryId() {

        switch (categoryId) {

            case 1:
                return R.mipmap.ic_business_business;
            case 2:
                return R.mipmap.ic_business_internet;
            case 3:
                return R.mipmap.ic_business_entertainment;
            case 4:
                return R.mipmap.ic_business_event;
            case 5:
                return R.mipmap.ic_business_home_services;
            case 6:
                return R.mipmap.ic_business_dinner;
            case 7:
                return R.mipmap.ic_business_government;
            case 8:
                return R.mipmap.ic_business_health;
            case 9:
                return R.mipmap.ic_business_legal;
            case 10:
                return R.mipmap.ic_business_other;
            case 11:
                return R.mipmap.ic_business_public_services;
            case 12:
                return R.mipmap.ic_business_shop;
            case 13:
                return R.mipmap.ic_business_tourism;
            case 14:
                return R.mipmap.ic_business_real_estate;
            case 15:
                return R.mipmap.ic_business_automotive;

            default:
                return R.mipmap.ic_business_other;
        }
    }
}
