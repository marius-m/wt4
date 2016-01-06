package lt.markmerkk.ui.clock;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.utils.hourglass.HourGlass;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ClockPresenter_MembersInjector implements MembersInjector<ClockPresenter> {
  private final Provider<HourGlass> hourGlassProvider;
  private final Provider<BasicLogStorage> storageProvider;

  public ClockPresenter_MembersInjector(Provider<HourGlass> hourGlassProvider, Provider<BasicLogStorage> storageProvider) {  
    assert hourGlassProvider != null;
    this.hourGlassProvider = hourGlassProvider;
    assert storageProvider != null;
    this.storageProvider = storageProvider;
  }

  @Override
  public void injectMembers(ClockPresenter instance) {  
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.hourGlass = hourGlassProvider.get();
    instance.storage = storageProvider.get();
  }

  public static MembersInjector<ClockPresenter> create(Provider<HourGlass> hourGlassProvider, Provider<BasicLogStorage> storageProvider) {  
      return new ClockPresenter_MembersInjector(hourGlassProvider, storageProvider);
  }
}

