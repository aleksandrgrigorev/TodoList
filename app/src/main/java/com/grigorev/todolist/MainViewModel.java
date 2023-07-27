package com.grigorev.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private NoteDatabase noteDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        noteDatabase = NoteDatabase.getInstance(application);
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void refreshList() {
        Disposable disposable = getNotesRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notesFromDd -> notes.setValue(notesFromDd),
                        throwable -> Log.d("MainViewModel", "Error refreshList")
                );
        compositeDisposable.add(disposable);
    }

    private Single<List<Note>> getNotesRx() {
        return Single.fromCallable(() -> noteDatabase.notesDao().getNotes());
    }

    public void remove(Note note) {
        Disposable disposable = removeRx(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Log.d("MainViewModel", "Removed: " + note.getId());
                            refreshList();
                        },
                        throwable -> Log.d("MainViewModel", "Error remove")
                );
        compositeDisposable.add(disposable);
    }

    private Completable removeRx(Note note) {
        return Completable.fromAction(() -> {
//            noteDatabase.notesDao().remove(note.getId());
            throw new Exception();
        });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
