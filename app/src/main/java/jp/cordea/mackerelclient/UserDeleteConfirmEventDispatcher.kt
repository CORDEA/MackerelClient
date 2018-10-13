package jp.cordea.mackerelclient

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class UserDeleteConfirmEventDispatcher @Inject constructor(
) : UserDeleteConfirmSink, UserDeleteConfirmSource {
    private val subject = PublishSubject.create<Unit>()

    override fun notifyUserDeleteCompleted() = subject.onNext(Unit)

    override fun onUserDeleteCompleted(): Observable<Unit> = subject
}

interface UserDeleteConfirmSink {
    fun notifyUserDeleteCompleted()
}

interface UserDeleteConfirmSource {
    fun onUserDeleteCompleted(): Observable<Unit>
}
