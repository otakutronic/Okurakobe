package mji.tapia.com.okurahotel.ui.staff_mode.log;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mji.tapia.com.data.error_log.ErrorLog;
import mji.tapia.com.okurahotel.R;

/**
 * Created by Sami on 2/1/2018.
 */

public class LogAdapter extends BaseAdapter {

    private List<ErrorLog> errorLogList;

    private LayoutInflater inflater;

    LogAdapter(LayoutInflater inflater, List<ErrorLog> errorLogList) {
        this.inflater = inflater;
        this.errorLogList = errorLogList;
    }

    @Override
    public int getCount() {
        return errorLogList.size();
    }

    @Override
    public Object getItem(int i) {
        return errorLogList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_log, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ErrorLog errorLog = errorLogList.get(i);
        holder.name_tv.setText(errorLog.getName());
        holder.stack_tv.setText(errorLog.getStack());

        holder.date_tv.setText(new SimpleDateFormat("MM/dd").format(errorLog.getTime()));

        holder.timestamp_tv.setText(String.format("Timestamp: %s", errorLog.getTime().toString()));
        view.setTag(R.id.error_log, errorLog);
        return view;
    }

    public void refreshList(List<ErrorLog> errorLogList) {
        this.errorLogList = errorLogList;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.message)
        TextView name_tv;
        @BindView(R.id.stack_trace)
        TextView stack_tv;
        @BindView(R.id.timestamp)
        TextView timestamp_tv;
        @BindView(R.id.date)
        TextView date_tv;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
