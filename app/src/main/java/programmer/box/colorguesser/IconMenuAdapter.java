package programmer.box.colorguesser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skydoves.powermenu.MenuBaseAdapter;

/**
 * Created by Jacob on 12/6/17.
 */

public class IconMenuAdapter extends MenuBaseAdapter<IconPowerMenuItem> {

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_menu_item, viewGroup, false);
        }

        IconPowerMenuItem item = (IconPowerMenuItem) getItem(index);
        //final ImageView icon = view.findViewById(R.id.item_icon);
        //icon.setImageDrawable(item.getIcon());
        final RelativeLayout background = view.findViewById(R.id.info_back);
        background.setBackgroundColor(item.getBackgroundColor());
        final TextView title = view.findViewById(R.id.info_text);
        title.setText(Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_COMPACT));
        /*try {
            //String[] info = item.getTitle().split("]");
            //title.setText(Html.fromHtml(info[0], Html.FROM_HTML_MODE_COMPACT) + info[1]);

        } catch (ArrayIndexOutOfBoundsException e) {
            title.setText(item.getTitle());
        }*/
        title.setTextColor(item.getTextColor());
        return view;
    }
}

