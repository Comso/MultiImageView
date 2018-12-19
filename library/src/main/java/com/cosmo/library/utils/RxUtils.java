package com.cosmo.library.utils;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Author:ruchao.jiang
 * Created: 2018/12/19 09:29
 * Email:ruchao.jiang@uama.com.cn
 */
public class RxUtils {
    /*intervalRange操作符
    intervalRange(long start, long count, long initialDelay, long period, TimeUnit unit)
    start：起始数值
    count：发射数量
    initialDelay：延迟执行时间
    period：发射周期时间
    unit：时间单位*/
    public static Disposable countDown(final long targetTime,final CountListener listener){
        Disposable mdDisposable = Observable
                .intervalRange(0,targetTime,0,1,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long time) throws Exception {
                        if (listener !=null)listener.accept(targetTime-time);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (listener !=null)listener.complete();
                    }
                })
                .subscribe();
        return mdDisposable;
    }

    public interface CountListener{
        void accept(long count);
        void complete();
    }

}
