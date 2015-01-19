package org.erukiti;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.erukiti.excel.ExcelObservable;
import rx.Observable;
import rx.Observable.OnSubscribe;

import java.io.*;

class Spike04 {
    public static void main(String[] args) {
        ExcelObservable.from(args).subscribe(workbook -> {
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
                    }).flatMap(str -> {
                        return Observable.create((OnSubscribe<HttpGet>) obs -> {
                            try {
                                obs.onNext(new HttpGet(str));
                            } catch (Throwable e) {
                                obs.onError(e);
                            }
                            obs.onCompleted();
                        });
                    }).flatMap(httpGet -> {
                        return Observable.create((OnSubscribe<BufferedReader>) obs -> {
                            try {
                                CloseableHttpClient httpClient = HttpClients.createDefault();
                                CloseableHttpResponse response = httpClient.execute(httpGet);
                                HttpEntity entity = response.getEntity();
                                if (entity != null) {
                                    obs.onNext(new BufferedReader(new InputStreamReader(entity.getContent())));
                                }
                            } catch (Throwable e) {
                                obs.onError(e);
                            }
                            obs.onCompleted();
                        });
                    }).flatMap(br -> {
                        return Observable.create((OnSubscribe<String>) obs -> {
                            try {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    obs.onNext(line);
                                }
                                obs.onCompleted();
                            } catch (Throwable e) {
                                obs.onError(e);
                            }
                        });
                    }).reduce((acc, x) -> acc + "¥n" + x).subscribe(content -> {
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
