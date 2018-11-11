package yellow7918.ajou.ac.aunager.situation;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import yellow7918.ajou.ac.aunager.R;
import yellow7918.ajou.ac.aunager.adpater.viewholder.AbstractViewHolder;

public class SituationViewHolder extends AbstractViewHolder<Situation> {
    private TextView before;
    private TextView after;

    public SituationViewHolder(@NonNull View itemView) {
        super(itemView);
        before = itemView.findViewById(R.id.before);
        after = itemView.findViewById(R.id.after);
    }

    @Override
    public void onBindView(@NonNull Situation item, int position) {
        before.setText(item.getBefore());
        after.setText(item.getAfter());
    }
}
