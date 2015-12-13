package lt.markmerkk.storage2;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import lt.markmerkk.DBProdExecutor;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class BasicStorage_Factory implements Factory<BasicLogStorage> {
  private final Provider<DBProdExecutor> executorProvider;

  public BasicStorage_Factory(Provider<DBProdExecutor> executorProvider) {  
    assert executorProvider != null;
    this.executorProvider = executorProvider;
  }

  @Override
  public BasicLogStorage get() {
    return new BasicLogStorage(executorProvider.get());
  }

  public static Factory<BasicLogStorage> create(Provider<DBProdExecutor> executorProvider) {
    return new BasicStorage_Factory(executorProvider);
  }
}

