package vsppsgv.chatify.im.page.business;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import vsppsgv.chatify.im.model.CIBusiness;

/**
 * Created by administrator on 9/23/15.
 */
public class CIBusinessListAdapter extends ArrayAdapter<CIBusiness> {

    private Context                         mContext;
    private ArrayList<CIBusiness>           mData = null;

    public CIBusinessListAdapter(Context context, int resource, ArrayList<CIBusiness> datas) {
        super(context, resource, datas);

        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;

        CIBusinessHolder holder;

        CIBusiness business = (CIBusiness) mData.get(position);

        if ( row == null )
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
//            row = inflater.inflate(mListItemId, parent, false);

            holder = new CIBusinessHolder();

            row.setTag(holder);

        } else {
            holder = (CIBusinessHolder)row.getTag();
        }

//        holder.setEventInfo(mContext, event, mEventPicDisplayOptions);

        return row;
    }

}
