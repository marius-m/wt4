package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.injection.PresenterFactory;
import lt.markmerkk.afterburner.InjectorNoDI;

import java.util.function.Function;

/**
 * @author mariusmerkevicius
 * @since 2016-07-17
 */
public class DebuggingInjector implements PresenterFactory {

    @Override
    public <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext) {
        System.out.println("--- clazz " + clazz + " context " + injectionContext);
        return InjectorNoDI.instantiatePresenter(clazz);
    }

}
