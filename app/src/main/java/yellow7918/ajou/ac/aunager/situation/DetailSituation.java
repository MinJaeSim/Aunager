package yellow7918.ajou.ac.aunager.situation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import yellow7918.ajou.ac.aunager.R;

public class DetailSituation extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_situation, container, false);
        Situation situation = (Situation) getArguments().getSerializable("DATA");

        TextView title = view.findViewById(R.id.title);
        TextView before = view.findViewById(R.id.before);
        TextView after = view.findViewById(R.id.after);

        title.setText(situation.getSituation());
        before.setText(situation.getBefore());
        after.setText(situation.getAfter());

        return view;
    }

    public static DetailSituation newInstance(Situation situation) {
        DetailSituation f = new DetailSituation();

        Bundle args = new Bundle();
        args.putSerializable("DATA", situation);
        f.setArguments(args);

        return f;
    }
}
