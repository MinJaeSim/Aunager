package yellow7918.ajou.ac.aunager.situation;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yellow7918.ajou.ac.aunager.R;
import yellow7918.ajou.ac.aunager.adpater.AbstractRecyclerAdapter;
import yellow7918.ajou.ac.aunager.adpater.viewholder.AbstractViewHolder;

public class SituationAdapter extends AbstractRecyclerAdapter<Situation> {
    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_situation, viewGroup, false);
        return new SituationViewHolder(view);
    }
}
