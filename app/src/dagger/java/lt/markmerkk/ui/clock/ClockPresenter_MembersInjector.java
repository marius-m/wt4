package lt.markmerkk.ui.clock;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.utils.hourglass.HourGlass;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ClockPresenter_MembersInjector implements MembersInjector<ClockPresenter> {
  private final Provider<DBProdExecutor> dbExecutorProvider;
  private final Provider<HourGlass> hourGlassProvider;

  public ClockPresenter_MembersInjector(Provider<DBProdExecutor> dbExecutorProvider, Provider<HourGlass> hourGlassProvider) {  
    assert dbExecutorProvider != null;
    this.dbExecutorProvider = dbExecutorProvider;
    assert hourGlassProvider != null;
    this.hourGlassProvider = hourGlassProvider;
  }

  @Override
  public void injectMembers(ClockPresenter instance) {  
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.dbExecutor = dbExecutorProvider.get();
    instance.hourGlass = hourGlassProvider.get();
  }

  public static MembersInjector<ClockPresenter> create(Provider<DBProdExecutor> dbExecutorProvider, Provider<HourGlass> hourGlassProvider) {  
      return new ClockPresenter_MembersInjector(dbExecutorProvider, hourGlassProvider);
  }
}

