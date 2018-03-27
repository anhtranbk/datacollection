package com.vcc.bigdata.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class RxJavas {

    public static <T> rx.Observable<T> observableFrom(Future<T>... futures) {
        List<rx.Observable<T>> observables = new ArrayList<>();
        for (Future<T> fut : futures) {
            observables.add(rx.Observable.from(fut));
        }
        return rx.Observable.merge(observables);
    }
}
