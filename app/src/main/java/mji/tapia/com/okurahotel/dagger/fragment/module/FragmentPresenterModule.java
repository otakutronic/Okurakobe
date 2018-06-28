package mji.tapia.com.okurahotel.dagger.fragment.module;

import dagger.Module;
import dagger.Provides;
import mji.tapia.com.okurahotel.dagger.fragment.DaggerFragment;
import mji.tapia.com.okurahotel.dagger.fragment.FragmentComponent;
import mji.tapia.com.okurahotel.dagger.fragment.FragmentScope;

@Module
public final class FragmentPresenterModule {

    private final DaggerFragment daggerFragment;

    public FragmentPresenterModule(final DaggerFragment daggerFragment) {
        this.daggerFragment = daggerFragment;
    }

    private FragmentComponent getFragmentComponent() {
        return daggerFragment.getFragmentComponent();
    }

//    @Provides
//    @FragmentScope
//    public PolicyContract.Presenter provideRegisterPolicyPresenter() {
//        final PolicyPresenter policyPresenter= new PolicyPresenter((PolicyContract.View) daggerFragment);
//        getFragmentComponent().inject(policyPresenter);
//        return policyPresenter;
//    }


}
