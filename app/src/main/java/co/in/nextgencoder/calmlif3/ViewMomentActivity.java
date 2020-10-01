package co.in.nextgencoder.calmlif3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.in.nextgencoder.calmlif3.Service.MomentService;
import co.in.nextgencoder.calmlif3.Service.UserService;
import co.in.nextgencoder.calmlif3.ServiceIMPL.MomentServiceImpl;
import co.in.nextgencoder.calmlif3.ServiceIMPL.UserServiceImpl;
import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public class ViewMomentActivity extends AppCompatActivity {

    MomentService momentService = new MomentServiceImpl();
    UserService userService = new UserServiceImpl();

    ImageView mood;
    TextView moodTitle, userName, moodDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_moment);

        mood = findViewById( R.id.momentViewMood);
        moodTitle = findViewById( R.id.momentViewTitle);
        userName = findViewById( R.id.momentViewUserName);
        moodDescription = findViewById( R.id.momentViewDescription);

        String id = getIntent().getStringExtra("momentId");

        momentService.momentById(new CallBack<Moment>() {
            @Override
            public void callback(final Moment moment) {
                if( moment.getMood().equals("happy")) {
                    mood.setBackgroundResource( R.drawable.icon_happy);
                }

                if( moment.getMood().equals("crazy")) {
                    mood.setBackgroundResource( R.drawable.icon_crazy1);
                }

                if( moment.getMood().equals("romantic")) {
                    mood.setBackgroundResource( R.drawable.icon_kiss);
                }

                if( moment.getMood().equals("sad")) {
                    mood.setBackgroundResource( R.drawable.icon_sad);
                }

                if( moment.getMood().equals("angry")) {
                    mood.setBackgroundResource( R.drawable.icon_angry);
                }


                moodTitle.setText( moment.getTitle());
                moodDescription.setText( moment.getMomentDescription());
                userName.setText( moment.getUser().getName());

                userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent( view.getContext(), ViewUserActivity.class);
                        intent.putExtra("userId", moment.getUser().getId());
                        view.getContext().startActivity( intent);
                    }
                });
            }
        }, id);
    }
}