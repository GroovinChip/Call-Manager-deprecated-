package groovinchip.com.callmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.MyViewHolder> {

    public NotificationHistoryAdapter(List<Call> remindersList, Context context) {
        this.remindersList = remindersList;
        this.context = context;
    }

    private List<Call> remindersList;
    private Context context;
    String pattern = "E MM/dd/yyyy h:mm a";
    SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number, reminderDate;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.callTitle);
            number = (TextView) view.findViewById(R.id.callNumber);
            reminderDate = (TextView) view.findViewById(R.id.callDescription);
        }
    }

    public NotificationHistoryAdapter(List<Call> remindersList) {
        this.remindersList = remindersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Call call = remindersList.get(position);
        int hour;
        int minute;
        holder.name.setText(call.getName());
        holder.number.setText(call.getNumber());
        String[] dateHolder = String.valueOf(call.getReminderTime()).split(":|\\s+");
        hour = new Integer(dateHolder[3]);
        minute = new Integer(dateHolder[4]);
        /*if(minute < 10){

        }*/

        String notification = format.format(call.getReminderTime());
        holder.reminderDate.setText(notification);
    }

    @Override
    public int getItemCount() {
        return remindersList.size();
    }
}