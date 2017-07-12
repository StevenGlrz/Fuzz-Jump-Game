package com.fuzzjump.api;

import com.fuzzjump.api.friends.FriendService;
import com.fuzzjump.api.friends.IFriendService;
import com.fuzzjump.api.invite.IInviteService;
import com.fuzzjump.api.invite.InviteService;
import com.fuzzjump.api.profile.IProfileService;
import com.fuzzjump.api.profile.ProfileService;
import com.fuzzjump.api.session.ISessionService;
import com.fuzzjump.api.session.SessionService;
import com.fuzzjump.api.unlockable.IUnlockableService;
import com.fuzzjump.api.unlockable.UnlockableService;
import com.fuzzjump.api.user.IUserService;
import com.fuzzjump.api.user.UserService;

import dagger.Binds;
import dagger.Module;

/**
 * This is where all the rest services will go
 */
@Module(
        includes = {
                RetrofitModule.class
        }
)
public abstract class ServiceModule {

    @Binds
    public abstract IUserService bindUserService(UserService impl);

    @Binds
    public abstract ISessionService bindSessionService(SessionService impl);

    @Binds
    public abstract IFriendService bindFriendService(FriendService impl);

    @Binds
    public abstract IProfileService bindProfileService(ProfileService impl);

    @Binds
    public abstract IUnlockableService bindUnlockableService(UnlockableService impl);

    @Binds
    public abstract IInviteService bindInviteService(InviteService impl);


}
