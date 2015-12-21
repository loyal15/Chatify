package im.chatify.page.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import im.chatify.R;
import im.chatify.common.ui.phonecode.Country;

/**
 * Created by administrator on 11/6/15.
 */
public class CIBusinessCountryAdapter extends ArrayAdapter<Country> {

    private LayoutInflater mLayoutInflater;

    public CIBusinessCountryAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.spinneritem_country_drop, parent, false);
            holder = new ViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.image);
            holder.mNameView = (TextView) convertView.findViewById(R.id.country_name);
            holder.mCodeView = (TextView) convertView.findViewById(R.id.country_code);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Country country = getItem(position);
        if (country != null) {
            holder.mNameView.setText(country.getName());
//            holder.mCodeView.setText(country.getCountryCodeStr());
            holder.mImageView.setImageResource(country.getResId());
        }
        return convertView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Country country = getItem(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.spinneritem_country_drop, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        TextView textView = (TextView) convertView.findViewById(R.id.country_name);

        imageView.setImageResource(country.getResId());
        textView.setText(country.getName());

        return convertView;
    }

    private static class ViewHolder {
        public ImageView mImageView;
        public TextView mNameView;
        public TextView mCodeView;
    }
}
