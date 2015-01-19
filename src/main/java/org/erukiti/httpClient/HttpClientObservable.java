package org.erukiti.httpClient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import rx.Observable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpClientObservable {
    public static Observable<BufferedReader> from(String url) {
        return Observable.create((Observable.OnSubscribe<BufferedReader>) obs -> {
            try {
                HttpGet httpGet = new HttpGet(url);
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            obs.onNext(new BufferedReader(new InputStreamReader(entity.getContent())));
                        }
                    }
                }
            } catch (Throwable e) {
                obs.onError(e);
            }
            obs.onCompleted();
        });
    }

    public static Observable<BufferedReader> from(Observable<String> strObs) {
        return strObs.flatMap(str -> from(str));
    }
}
