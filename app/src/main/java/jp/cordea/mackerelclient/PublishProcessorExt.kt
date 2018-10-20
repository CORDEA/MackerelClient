package jp.cordea.mackerelclient

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor

fun <T : Any> PublishProcessor<T>.toUiEvent(): Flowable<T> =
    onBackpressureLatest().observeOn(AndroidSchedulers.mainThread())
