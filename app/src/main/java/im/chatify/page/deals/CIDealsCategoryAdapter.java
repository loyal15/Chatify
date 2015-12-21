package im.chatify.page.deals;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import im.chatify.R;
import im.chatify.model.CICategory;

/**
 * Created by administrator on 9/23/15.
 */
public class CIDealsCategoryAdapter extends RecyclerView.Adapter<CIDealsCategoryHolder> {

    private Context                         mContext;
    private ArrayList<CICategory>           mData = null;
    private int                             mListItemId;

    public CIDealsCategoryAdapter(Context context, ArrayList<CICategory> itemList) {
        mData = itemList;
        mContext = context;
    }
    /*
    public CIDealsCategoryAdapter(Context context, int resource, ArrayList<CICategory> datas) {
        super(context, resource, datas);

        mContext = context;
        mData = datas;
        mListItemId = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;

        CIBusinessHolder holder;

        CICategory business = (CICategory) mData.get(position);

        if ( row == null )
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mListItemId, parent, false);

            holder = new CIBusinessHolder(row);

            row.setTag(holder);

        } else {
            holder = (CIBusinessHolder)row.getTag();
        }

//        holder.setBusinessInfo(mContext, business);

        return row;
    }
    */

    @Override
    public CIDealsCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_deals_category, null);
        CIDealsCategoryHolder holder = new CIDealsCategoryHolder(layoutView);

        return holder;
    }

    @Override
    public void onBindViewHolder(CIDealsCategoryHolder holder, int position) {

        CICategory category = mData.get(position);

        holder.tvCategoryName.setText(category.categoryName);

        int resourceId = 0;

        switch (category.categoryId) {

            case 1:
                resourceId = R.mipmap.ic_category_activities;
                break;
            case 2:
                resourceId = R.mipmap.ic_category_fooddrink;
                break;
            case 3:
                resourceId = R.mipmap.ic_category_property;
                break;
            case 4:
                resourceId = R.mipmap.ic_category_travel;
                break;
            case 5:
                resourceId = R.mipmap.ic_category_healthbeauty;
                break;
            case 6:
                resourceId = R.mipmap.ic_category_fooddrink;
                break;
            case 7:
                resourceId = R.mipmap.ic_category_property;
                break;
            case 8:
                resourceId = R.mipmap.ic_category_healthbeauty;
                break;
            case 9:
                resourceId = R.mipmap.ic_category_shopping;
                break;
            case 10:
                resourceId = R.mipmap.ic_category_activities;
                break;
            case 11:
                resourceId = R.mipmap.ic_category_fooddrink;
                break;
            case 12:
                resourceId = R.mipmap.ic_category_shopping;
                break;
            case 13:
                resourceId = R.mipmap.ic_category_travel;
                break;
            case 14:
                resourceId = R.mipmap.ic_category_healthbeauty;
                break;
            case 15:
                resourceId = R.mipmap.ic_category_activities;
                break;
            default:
                break;
        }

        holder.ivCategory.setImageResource(resourceId);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
