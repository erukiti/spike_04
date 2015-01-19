package org.erukiti;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.erukiti.excel.WorkbookObservable;
import org.erukiti.httpClient.HttpClientObservable;
import org.erukiti.utils.BufferedReaderObservable;
import rx.Observable;
import rx.Observable.OnSubscribe;

import java.io.*;

class Spike04 {
    public static void main(String[] args) {
        WorkbookObservable.from(args).subscribe(workbook -> {
            Observable.create((OnSubscribe<Sheet>) obs -> {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    obs.onNext(workbook.getSheetAt(i));
                }
                obs.onCompleted();
            }).subscribe(sheet -> {
                Observable.create((OnSubscribe<Row>) obs -> {
                    int cnt = 0;
                    Row row;
                    while ((row = sheet.getRow(cnt)) != null) {
                        obs.onNext(row);
                        cnt++;
                    }
                    obs.onCompleted();
                }).subscribe(row -> {
                    Observable.create((OnSubscribe<String>) obs -> {
                        Cell cell = row.getCell(0);
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            obs.onNext(cell.getStringCellValue());
                        } else {
                            obs.onError(new Throwable("hoge"));
                        }
                        obs.onCompleted();
                    }).flatMap(str -> HttpClientObservable.from(str)
                    ).flatMap(br -> BufferedReaderObservable.readLines(br)
                    ).reduce((acc, x) -> acc + "¥n" + x).subscribe(content -> {
                        Cell cell = row.createCell(1);
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(content);
                        try {
                            FileOutputStream fos = new FileOutputStream("output.xlsx");
                            workbook.write(fos);
                        } catch (Throwable e) {
                            System.out.println(e);
                        }
                    });
                });
            });
        });
    }
}
