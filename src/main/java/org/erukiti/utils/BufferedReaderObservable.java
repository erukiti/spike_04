package org.erukiti.utils;

import rx.Observable;

import java.io.BufferedReader;

public class BufferedReaderObservable {
    public static Observable<String> readLines(BufferedReader br) {
        return Observable.create((Observable.OnSubscribe<String>) obs -> {
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    obs.onNext(line);
                }
                br.close();
                obs.onCompleted();
            } catch (Throwable e) {
                obs.onError(e);
            }
        });
    }

    public static Observable<String> readLines(Observable<BufferedReader> brObs) {
        return brObs.flatMap(br -> readLines(br));
    }
}
