package yellow7918.ajou.ac.aunager.situation;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.Query;

import yellow7918.ajou.ac.aunager.FireStoreAdapter;
import yellow7918.ajou.ac.aunager.R;

public class SituationAdapter extends FireStoreAdapter<SituationViewHolder> {

    private OnItemClickListener<Situation> onItemClickListener;

    public interface OnItemClickListener<Situation> {
        void onItemClick(Situation item, String key);
    }

    public void setOnItemClickListener(OnItemClickListener<Situation> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemLongClickListener<Situation> onItemLongClickListener;

    public interface OnItemLongClickListener<Situation> {
        void onItemClick(Situation item, String key);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<Situation> onItemClickListener) {
        this.onItemLongClickListener = onItemClickListener;
    }

    public SituationAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public SituationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_situation, viewGroup, false);
        return new SituationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SituationViewHolder holder, int i) {
        Situation item = getSnapshot(i).toObject(Situation.class);
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
