package net.azurewebsites.ashittyscheduler.ass.Adapters;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import net.azurewebsites.ashittyscheduler.ass.Overview.OverviewFragment;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.ToDo;
import net.azurewebsites.ashittyscheduler.ass.detailscreen;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.ViewHolder> {

    private ArrayList<ToDo> allTodos = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHeader;
        public TextView txtFooter;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.overviewTitle);
            txtFooter = (TextView) v.findViewById(R.id.overviewDate);
        }
    }

    public void remove(int position) {
        allTodos.remove(position);
        notifyItemRemoved(position);
    }

    public recyclerViewAdapter(ArrayList<ToDo> allTodos) {
        this.allTodos = allTodos;
    }

    @Override
    public recyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,
                parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ToDo todo = allTodos.get(position);


        //holder.txtHeader.setText((CharSequence) todo);
        holder.txtHeader.setText(todo.getTitle());

        String s = "?";
        Date d = todo.getDate().getTime();
        DateFormat df = new SimpleDateFormat("E, dd-MM-yyyy HH:mm aaa");

        try {
            s = df.format(d);
        }
        catch(Exception e) {

        }


        holder.txtFooter.setText(s);

        if (todo.isStatus()) {
            holder.itemView.setBackgroundColor(Color.rgb(109, 181, 106));
        }

        final String todoId = todo.getId();

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setSelected(true);
                view.setBackgroundColor(Color.DKGRAY);

                Intent intent = new Intent(view.getContext(), detailscreen.class);
                intent.putExtra("todoId", todoId);
                view.getContext().startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return allTodos.size();
    }
}