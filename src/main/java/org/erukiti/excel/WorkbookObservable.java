package org.erukiti.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import rx.Observable;

import java.io.File;

public class WorkbookObservable {
    public static Observable<Workbook> from(String filename) {
        return Observable.create((Observable.OnSubscribe<Workbook>)obs-> {
            try {
                File file = new File(filename);
                Workbook workbook = WorkbookFactory.create(file);
                obs.onNext(workbook);
            } catch (Throwable e) {
                obs.onError(e);
            }
            obs.onCompleted();
        });
    }

    public static Observable<Workbook> from(Observable<String> strObs) {
        return strObs.flatMap(filename -> from(filename));
    }

    public static Observable<Workbook> from(String[] filenames) {
        return from(Observable.from(filenames));
    }


}
