package groovinchip.com.callmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.MyViewHolder> {

    public CallListAdapter(List<Call> callList, Context context) {
        this.callList = callList;
        this.context = context;
    }

    private List<Call> callList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number, description;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.callTitle);
            number = (TextView) view.findViewById(R.id.callNumber);
            description = (TextView) view.findViewById(R.id.callDescription);
        }
    }

    public CallListAdapter(List<Call> callList) {
        this.callList = callList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Call call = callList.get(position);
        holder.name.setText(call.getName());
        holder.number.setText(call.getNumber());
        holder.description.setText(call.getDescription());
    }

    @Override
    public int getItemCount() {
        return callList.size();
    }
}