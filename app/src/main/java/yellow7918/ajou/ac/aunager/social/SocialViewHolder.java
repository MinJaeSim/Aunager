package yellow7918.ajou.ac.aunager.social;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import yellow7918.ajou.ac.aunager.R;
import yellow7918.ajou.ac.aunager.AbstractViewHolder;

public class SocialViewHolder extends AbstractViewHolder<SocialText> {
    private TextView category;
    private TextView title;
    private TextView time;
    private TextView email;

    public SocialViewHolder(@NonNull View itemView) {
        super(itemView);
        category = itemView.findViewById(R.id.category);
        title = itemView.findViewById(R.id.title);
        email = itemView.findViewById(R.id.email);
        time = itemView.findViewById(R.id.time);
    }

    @Override
    public void onBindView(@NonNull SocialText item, int position) {
        category.setText(String.format("[%s]", item.getCategory()));
        title.setText(item.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd, HH:mm");
        Date resultdate = new Date(item.getDate());
        time.setText(sdf.format(resultdate));

        email.setText(item.getEmail());

    }
}
