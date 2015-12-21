package im.chatify.page.business;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import im.chatify.model.CIBusiness;

/**
 * Created by administrator on 9/23/15.
 */
public class CIBusinessListAdapter extends ArrayAdapter<CIBusiness> {

    private Context                         mContext;
    private ArrayList<CIBusiness>           mData = null;
    private int                             mListItemId;

    public CIBusinessListAdapter(Context context, int resource, ArrayList<CIBusiness> datas) {
        super(context, resource, datas);

        mContext = context;
        mData = datas;
        mListItemId = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;

        CIBusinessHolder holder;

        CIBusiness business = (CIBusiness) mData.get(position);

        if ( row == null )
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mListItemId, parent, false);

            holder = new CIBusinessHolder(row);

            row.setTag(holder);

        } else {
            holder = (CIBusinessHolder)row.getTag();
        }

        holder.setBusinessInfo(mContext, business);

        return row;
    }

}
