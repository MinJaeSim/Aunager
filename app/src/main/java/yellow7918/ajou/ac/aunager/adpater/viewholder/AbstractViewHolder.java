package yellow7918.ajou.ac.aunager.adpater.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbstractViewHolder<T> extends RecyclerView.ViewHolder {

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBindView(@NonNull T item, int position);

}
