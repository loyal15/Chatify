package im.chatify.page.business;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import im.chatify.R;
import im.chatify.model.CIBusiness;
import im.chatify.model.CICategory;

/**
 * Created by administrator on 9/23/15.
 */
public class CIBusinessHolder {

    private ImageView       mIVBusiness;
    private TextView        mTVName;
    private TextView        mTVLocation;
    private TextView        mTVDistance;

    public CIBusinessHolder(View view) {

        mIVBusiness = (ImageView)view.findViewById(R.id.ivBusiness);
        mTVName = (TextView)view.findViewById(R.id.tvName);
        mTVLocation = (TextView)view.findViewById(R.id.tvLocation);
        mTVDistance = (TextView)view.findViewById(R.id.tvDistance);
    }

    public void setBusinessInfo(Context context, CIBusiness business) {

//        mIVBusiness.setImageResource(business.getBusinessDrawableIdFromType());
        mIVBusiness.setVisibility(View.INVISIBLE);

        ArrayList<CICategory> categories = CICategory.getCategoryList(context, true);

        for (int i = 0; i < categories.size(); i ++) {
            CICategory category = categories.get(i);

            if (business.type == category.categoryId) {
                mIVBusiness.setImageBitmap(category.categoryBitmap);
                mIVBusiness.setVisibility(View.VISIBLE);
                break;
            }
        }

        mTVName.setText(business.name);
        mTVLocation.setText(business.location);
        mTVDistance.setText(String.format("%.2f km", business.getDistanceFromLocation(40.1, -82.3)));
    }
}
