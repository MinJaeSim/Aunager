package yellow7918.ajou.ac.aunager.social;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yellow7918.ajou.ac.aunager.R;
import yellow7918.ajou.ac.aunager.adpater.AbstractRecyclerAdapter;
import yellow7918.ajou.ac.aunager.adpater.viewholder.AbstractViewHolder;

public class SocialAdapter extends AbstractRecyclerAdapter<SocialText> {

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_social_text, viewGroup, false);
        return new SocialViewHolder(view);
    }
}
