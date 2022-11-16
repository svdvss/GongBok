package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ProblemData{
    String name;
    String link;
    long likeNum;
    long tier;

    ProblemData(String name, String link, long likeNum, long tier){
        this.name = name;
        this.link = link;
        this.likeNum = likeNum;
        this.tier = tier;
    }
}

public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ProblemViewHolder>{
    List<ProblemData> DataList;

    public ProblemAdapter(List<ProblemData> problemDataList){
        this.DataList = problemDataList;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_list, parent, false);
        return new ProblemViewHolder(view);
    }

    //onClickListener 부착(ProblemSelectScreen에서 함수 정의할 것)
    public interface OnItemClickListener{
        void onItemClick(View v, ProblemData data);
    }

    private ProblemAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(ProblemAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        ProblemData item = DataList.get(position);

        holder.problemName.setText(item.name);
        holder.likeNum.setText(String.valueOf(item.likeNum));
        //추후 tierImage가 추가되면 이에 대해서 이미지 바꿔주기

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition() ;
                if (pos != RecyclerView.NO_POSITION) {
                    if(mListener != null)
                        mListener.onItemClick(v, DataList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return DataList.size();
    }

    static class ProblemViewHolder extends RecyclerView.ViewHolder{
        TextView problemName;
        TextView likeNum;
        ImageView tier;

        public ProblemViewHolder(@NonNull View view) {
            super(view);

            problemName = view.findViewById(R.id.problemName);
            likeNum = view.findViewById(R.id.likeNum);
            tier = view.findViewById(R.id.tierImage);
        }
    }
}