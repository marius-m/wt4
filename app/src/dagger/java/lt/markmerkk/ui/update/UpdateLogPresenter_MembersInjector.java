package lt.markmerkk.ui.update;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import lt.markmerkk.storage2.BasicLogStorage;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class UpdateLogPresenter_MembersInjector implements MembersInjector<UpdateLogPresenter> {
  private final Provider<BasicLogStorage> storageProvider;

  public UpdateLogPresenter_MembersInjector(Provider<BasicLogStorage> storageProvider) {  
    assert storageProvider != null;
    this.storageProvider = storageProvider;
  }

  @Override
  public void injectMembers(UpdateLogPresenter instance) {  
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.storage = storageProvider.get();
  }

  public static MembersInjector<UpdateLogPresenter> create(Provider<BasicLogStorage> storageProvider) {  
      return new UpdateLogPresenter_MembersInjector(storageProvider);
  }
}

