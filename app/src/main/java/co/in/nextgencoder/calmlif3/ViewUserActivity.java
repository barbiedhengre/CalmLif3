package co.in.nextgencoder.calmlif3;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import co.in.nextgencoder.calmlif3.Service.MomentService;
import co.in.nextgencoder.calmlif3.Service.UserService;
import co.in.nextgencoder.calmlif3.ServiceIMPL.MomentServiceImpl;
import co.in.nextgencoder.calmlif3.ServiceIMPL.UserServiceImpl;
import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public class ViewUserActivity extends AppCompatActivity {

    UserService userService = new UserServiceImpl();
    MomentService momentService = new MomentServiceImpl();

    ImageView userImage;
    TextView userName, userBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        userName = findViewById( R.id.userViewName);
        userBio = findViewById( R.id.userViewBio);
        userImage = findViewById( R.id.userViewImage);

        String id = getIntent().getStringExtra("userId");

        userService.getUserById(new CallBack<User>() {
            @Override
            public void callback(User user) {
                userName.setText( user.getName());
                userBio.setText( user.getBio());

                if( user.getPic() != null)
                    Picasso.get()
                            .load( user.getPic())
                            .placeholder( R.drawable.icon_user_anonymous)
                            .into( userImage);

                momentService.publicMomentsByUserMail(new CallBack<List<Moment>>() {
                    @Override
                    public void callback(List<Moment> moments) {
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.userViewMomentRecyclerView);
                        Collections.reverse( moments);

                        MomentAdapter adapter = null;
                        try {
                            adapter = new MomentAdapter( moments);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(ViewUserActivity.this , 2));
                        } catch (Exception e) {}

                    }
                }, user.getMail());
            }
        }, id);
    }
}