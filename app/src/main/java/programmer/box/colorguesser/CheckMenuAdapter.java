package programmer.box.colorguesser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skydoves.powermenu.MenuBaseAdapter;

/**
 * Created by Jacob on 12/12/17.
 */

public class CheckMenuAdapter extends MenuBaseAdapter<CheckPowerMenuItem> {

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_check_menu, viewGroup, false);
        }

        CheckPowerMenuItem item = (CheckPowerMenuItem) getItem(index);
        //final ImageView icon = view.findViewById(R.id.item_icon);
        //icon.setImageDrawable(item.getIcon());
        final RelativeLayout background = view.findViewById(R.id.info_back);
        background.setBackgroundColor(item.getBackgroundColor());
        final CheckBox title = view.findViewById(R.id.checkbox);
        title.setOnCheckedChangeListener (null);
        title.setChecked(item.isChecked());
        title.setEnabled(item.isEnabled());
        title.setText(item.getTitle());
        title.setTextColor(item.getTextColor());
        title.setOnCheckedChangeListener(item.getListener());
        return view;
    }
}
