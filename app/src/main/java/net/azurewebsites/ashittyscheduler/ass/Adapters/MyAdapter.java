package net.azurewebsites.ashittyscheduler.ass.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.ToDo;

import java.util.ArrayList;

//import butterknife.BindView;
//import butterknife.BindViews;
//import butterknife.ButterKnife;

public class MyAdapter  {
    /*private Context c;
    //String[] allTodos;
    private ArrayList<ToDo> Todos = new ArrayList<>();

    public MyAdapter(Context context, ArrayList<ToDo> allTodos) {
        c = context;
        //this.allTodos = allTodos;
        Todos = allTodos;
    }

    @NonNull
    @Override
    public TodosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        TodosViewHolder viewHolder  = new TodosViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodosViewHolder holder, int position) {
        ToDo todoObject = Todos.get(position);
        //holder.bindTodos(Todos.get(position));
        String firstTxt = todoObject.getTitle();
        holder.getOverviewTitle.setText(firstTxt);
    }

    @Override
    public int getItemCount() {
        return Todos.size();
    }


    public class TodosViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.overviewTitle) TextView getOverviewTitle;
        @BindView(R.id.overviewDate) TextView getOverviewDate;
        private Context context;

        public TodosViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this.itemView);
            context = itemView.getContext();
        }

        public void bindTodos(ToDo toDo) {
            getOverviewTitle.setText(toDo.getTitle());
            getOverviewDate.setText(toDo.getDate().toString());
        }
    }*/
}
