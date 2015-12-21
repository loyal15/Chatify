package im.chatify.page.deals;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.chatify.R;

/**
 * Created by administrator on 11/7/15.
 */
public class CIDealsCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView         tvCategoryName;
    public ImageView        ivCategory;

    public CIDealsCategoryHolder(View itemView) {
        super(itemView);

        ivCategory = (ImageView)itemView.findViewById(R.id.ivCategory);
        tvCategoryName = (TextView)itemView.findViewById(R.id.tvCategoryName);
    }

    @Override
    public void onClick(View view) {

    }
}
