package lt.markmerkk.storage2;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import lt.markmerkk.DBProdExecutor;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class BasicLogStorage_MembersInjector implements MembersInjector<BasicLogStorage> {
  private final Provider<DBProdExecutor> executorProvider;

  public BasicLogStorage_MembersInjector(Provider<DBProdExecutor> executorProvider) {  
    assert executorProvider != null;
    this.executorProvider = executorProvider;
  }

  @Override
  public void injectMembers(BasicLogStorage instance) {  
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.executor = executorProvider.get();
  }

  public static MembersInjector<BasicLogStorage> create(Provider<DBProdExecutor> executorProvider) {  
      return new BasicLogStorage_MembersInjector(executorProvider);
  }
}

