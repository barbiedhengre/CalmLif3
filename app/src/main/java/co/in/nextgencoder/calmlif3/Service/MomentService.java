package co.in.nextgencoder.calmlif3.Service;

import androidx.annotation.NonNull;

import java.util.List;

import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public interface MomentService {
    public void addMoment(@NonNull CallBack<Boolean> finishedCallback, Moment moment);

    public void allMoment(@NonNull CallBack<List<Moment>> finishedCallback);

    public void allVerifiedPublicMoments(@NonNull CallBack<List<Moment>> finishedCallback);

    public void momentByUser(@NonNull CallBack<List<Moment>> finishedCallback, String mail);

    public void momentById(@NonNull CallBack<Moment> finishedCallback, String id);

    public void updateMoment(@NonNull CallBack<Boolean> finishedCallback, Moment moment);

    public void deleteMoment(@NonNull CallBack<Boolean> finishedCallback, Moment moment);

    public void searchMomentByTitle(@NonNull CallBack<List<Moment>> finishedCallback, String title);
}
