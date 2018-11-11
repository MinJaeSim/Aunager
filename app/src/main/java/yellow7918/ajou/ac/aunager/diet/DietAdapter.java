package yellow7918.ajou.ac.aunager.diet;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.Query;

import yellow7918.ajou.ac.aunager.FireStoreAdapter;
import yellow7918.ajou.ac.aunager.R;


public class DietAdapter extends FireStoreAdapter<DietAdapter.DietViewHolder> {

    private OnDietClickListener listener;

    public void setOnDietClickListener(OnDietClickListener listener) {
        this.listener = listener;
    }

    public DietAdapter(Query query) {
        super(query);
    }

    @Override
    public DietViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_diet_holder, parent, false);
        return new DietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DietViewHolder holder, int position) {

        final Diet diet = getSnapshot(position).toObject(Diet.class);
        final String documentKey = getSnapshot(position).getId();

        holder.timeText.setText(diet.getDate());

        Glide.with(holder.dietImageView)
                .load(diet.getProfileImageUrl())
                .apply(new RequestOptions()
                        .override(150, 150)
                        .placeholder(R.drawable.ic_plus)
                )
                .into(holder.dietImageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(diet, documentKey);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(diet, documentKey);
            }
        });


    }

    static class DietViewHolder extends RecyclerView.ViewHolder {
        private ImageView dietImageView = itemView.findViewById(R.id.diet_image_view);
        private TextView timeText = itemView.findViewById(R.id.text_view_date);
        private ImageView deleteButton = itemView.findViewById(R.id.image_view_delete);

        public DietViewHolder(View itemView) {
            super(itemView);
        }
    }
}
