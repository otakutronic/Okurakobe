package mji.tapia.com.okurahotel;

import android.util.Log;

import org.junit.Test;

import io.reactivex.subjects.BehaviorSubject;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        BehaviorSubject<String> test = BehaviorSubject.create();
        test.onNext("test2");

        test.subscribe(str -> {
            System.out.println(str);
        });
        test.onNext("test3");


        Thread.sleep(2000);
    }
 }