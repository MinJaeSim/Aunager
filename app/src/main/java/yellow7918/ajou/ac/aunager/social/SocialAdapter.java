package yellow7918.ajou.ac.aunager.social;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.Query;

import yellow7918.ajou.ac.aunager.FireStoreAdapter;
import yellow7918.ajou.ac.aunager.R;

public class SocialAdapter extends FireStoreAdapter<SocialViewHolder> {

    private OnItemClickListener<SocialText> onItemClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(T item, String key);
    }

    public void setOnItemClickListener(OnItemClickListener<SocialText> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemLongClickListener<SocialText> onItemLongClickListener;

    public interface OnItemLongClickListener<T> {
        void onItemClick(T item, String key);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<SocialText> onItemClickListener) {
        this.onItemLongClickListener = onItemClickListener;
    }

    public SocialAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_social_text, viewGroup, false);
        return new SocialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int i) {
        SocialText item = getSnapshot(i).toObject(SocialText.class);
        String documentKey = getSnapshot(i).getId();
        holder.onBindView(item, i);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item, documentKey);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemClick(item, documentKey);
            }
            return false;
        });
    }
}
