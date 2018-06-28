package mji.tapia.com.okurahotel.dagger.fragment.module;

import dagger.Module;
import mji.tapia.com.okurahotel.dagger.fragment.DaggerFragment;


@Module
public class FragmentModule {

    private final DaggerFragment fragment;

    public FragmentModule(final DaggerFragment fragment) {
        this.fragment = fragment;
    }
}
