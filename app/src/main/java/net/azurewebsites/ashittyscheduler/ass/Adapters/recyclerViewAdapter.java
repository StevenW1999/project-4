package net.azurewebsites.ashittyscheduler.ass.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.ToDo;
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
        //holder.txtFooter.setText(todo.getDate().toString());
    }

    public int getItemCount() {
        return allTodos.size();
    }
}