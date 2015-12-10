package im.chatify.page.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import im.chatify.R;
import im.chatify.model.CICategory;

/**
 * Created by administrator on 11/6/15.
 */
public class CIBusinessCategoryAdapter extends ArrayAdapter<CICategory> {

    private LayoutInflater mLayoutInflater;

    public CIBusinessCategoryAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        final CICategoryHolder holder;

        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.spinneritem_category_drop, parent, false);
            holder = new CICategoryHolder();
            holder.mIVCategory = (ImageView) convertView.findViewById(R.id.ivCategory);
            holder.mTVCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
            convertView.setTag(holder);

        } else {
            holder = (CICategoryHolder) convertView.getTag();
        }

        CICategory category = getItem(position);

        holder.mIVCategory.setVisibility(View.GONE);

        if (category != null) {
            holder.mIVCategory.setImageBitmap(category.categoryBitmap);
            holder.mTVCategoryName.setText(category.categoryName);
        }

        return convertView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        CICategory category = getItem(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.spinneritem_category_drop, null);
        }

        ImageView ivCategory = (ImageView) convertView.findViewById(R.id.ivCategory);
        ivCategory.setVisibility(View.GONE);
        TextView tvCategory = (TextView) convertView.findViewById(R.id.tvCategoryName);

        ivCategory.setImageResource(category.getImageByCategoryId());
        tvCategory.setText(category.categoryName);

        return convertView;
    }

    private static class CICategoryHolder {
        public ImageView mIVCategory;
        public TextView mTVCategoryName;
    }
}