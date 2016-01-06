package lt.markmerkk.ui.display;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import lt.markmerkk.storage2.BasicLogStorage;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class SimpleLogPresenter_MembersInjector implements MembersInjector<DisplayLogPresenter> {
  private final Provider<BasicLogStorage> storageProvider;

  public SimpleLogPresenter_MembersInjector(Provider<BasicLogStorage> storageProvider) {  
    assert storageProvider != null;
    this.storageProvider = storageProvider;
  }

  @Override
  public void injectMembers(DisplayLogPresenter instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.storage = storageProvider.get();
  }

  public static MembersInjector<DisplayLogPresenter> create(Provider<BasicLogStorage> storageProvider) {
      return new SimpleLogPresenter_MembersInjector(storageProvider);
  }
}

