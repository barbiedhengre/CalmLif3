package co.in.nextgencoder.calmlif3.Service;

import androidx.annotation.NonNull;

import java.util.List;

import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public interface UserService {
    public void addUser(@NonNull CallBack<Boolean> finishedCallback, User user);

    public void allUser(@NonNull CallBack<List<User>> finishedCallback);

    public void updateUser(@NonNull CallBack<Boolean> finishedCallback, User user);

    public void editUserPrivacy(@NonNull CallBack<Boolean> finishedCallback, String id, boolean privacy);

    public void editUserBioById(@NonNull CallBack<Boolean> finishedCallback, String id, String bio);

    public void deleteUser(@NonNull CallBack<Boolean> finishedCallback, User user);

    public void searchUserByName(@NonNull CallBack<List<User>> finishedCallback, String name);

    public void getUserById(@NonNull CallBack<User> finishedCallback, String id);

    public void sendUserVerification(@NonNull CallBack<Boolean> finishedCallback);
}
