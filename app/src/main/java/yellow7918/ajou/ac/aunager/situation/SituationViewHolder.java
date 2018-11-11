package yellow7918.ajou.ac.aunager.situation;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import yellow7918.ajou.ac.aunager.R;
import yellow7918.ajou.ac.aunager.adpater.viewholder.AbstractViewHolder;

public class SituationViewHolder extends AbstractViewHolder<Situation> {
    private TextView title;

    public SituationViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
    }

    @Override
    public void onBindView(@NonNull Situation item, int position) {
        title.setText(item.getSituation());
    }
}
