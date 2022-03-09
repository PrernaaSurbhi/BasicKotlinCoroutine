package com.example.basickotlincoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.*

/* this project is based on the couroutine
    here co - means cooperating
    -Routine means - function , so here we used cooperative functions
    -one thread can have many coroutines, thread creation having limitation,
    -Cheap
    - Coroutine Scope- it define lifetime
    - Coroutine  Scope type
         -CoroutineScope --
         -ViewModelScope -- it work on viewModel, when ever view model dies , all coroutine of ViewModelScope will automatically dies
         -lifeCycleScope -- it work on view,activity , fragment
         -GobalScope- it work on application level ,Co-routineContext
         -Main Scope - it is attached with the main activity
    - Coroutine Context - it define Thread on which co routine will run
    - Dispatchers --  it define the thread pool type , on which we have to execute the co-routine.
    - Dispatcher , dispatches coroutine on threads
     -Dispatcher Type
         - Dispatcher.IO -- for dispatching IO type co routine , it work on onther thread
         - Dispatcher.Main-- for dispatching coroutine to work on main thread.
         -
       Coroutine Builder - it is used to build the coroutine with the help of Coroutine Scope,
       Coroutine Builder Type
            - launch() - return Job, also known as fire and forget .
            - asyc() -- coroutine where we are expecting output and result , then we use asyc coroutine, when ever working on dataBase use async() or in api call use async()


       Coroutine can be suspend and it can be resume after some time
       Suspending fun -- fun with suspending modifier
         - it help coroutine to sspending the coroutine at a parrticular point
         - it must be called either from couroutine or from other suspending fun
       Suspending fun type
          - yield() - if we add it inside any fun , it indiacte the suspending function

          job.cancel() - this function is used to cancel the coroutine
          job.join() - this fun is used to suspend the coroutine until corotine staarted above to complete.

          isActive() -- check if a coroutine is active or cancel.

*/


class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.execute_task_button)
        button.setOnClickListener {
            doAction(it)
        }

        /*
        OUTPUT for below both coroutine
           D/MainActivityTagSuspending fun job1:: Suspending fun job1:started
           D/MainActivityTagSuspending fun job2:: Suspending fun job2:started
           D/MainActivityTagSuspending fun job1End:: Suspending fun job1:ended
           D/MainActivityTagSuspending fun job2End:: Suspending fun job2:ended
         */
        //first couroutine
        CoroutineScope(Dispatchers.Main).launch {
            task1()
        }
        //second coroutine
        CoroutineScope(Dispatchers.Main).launch {
            task2()
        }

        CoroutineScope(Dispatchers.IO).launch {
            printCustomerCount()
        }

        CoroutineScope(Dispatchers.IO).launch {
            printNumber()
        }
        CoroutineScope(Dispatchers.IO).launch {
            printAsyncCount()
        }

        CoroutineScope(Dispatchers.IO).launch {
            getParentChildRelationShip()
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContextFun()
        }

        CoroutineScope(Dispatchers.IO).launch {
            runBlockingCall()
        }
    }

    //output
    /*
        D/MainActivityTagCoroutineScope:: DefaultDispatcher-worker-1
        D/MainActivityTagMainScope:: DefaultDispatcher-worker-1
        D/MainActivityTagGlobalScope:: main
     */

    fun doAction(view: View) {
        // add coroutine different type of scope
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG + "CoroutineScope: ", "${Thread.currentThread().name}")
        }

        GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG + "GlobalScope: ", "${Thread.currentThread().name}")
        }

        MainScope().launch(Dispatchers.Default) {
            Log.d(TAG + "MainScope: ", "${Thread.currentThread().name}")
        }
    }

    suspend fun task1() {
        Log.d(TAG + "Suspending fun job1: ", "Suspending fun job1:started")
        // - yield() - if we add it inside any fun , it indiacte the suspending function
        yield()
        Log.d(TAG + "Suspending fun job1End: ", "Suspending fun job1:ended")
    }

    suspend fun task2() {
        Log.d(TAG + "Suspending fun job2: ", "Suspending fun job2:started")
        yield()
        Log.d(TAG + "Suspending fun job2End: ", "Suspending fun job2:ended")
    }

    //fun example for using async builder
    suspend fun printNumber() {
        var custNumber = 0
        val job = CoroutineScope(Dispatchers.IO).async {
            custNumber = getCustomerDetail()
        }
        //this job .join(), this make the above coroutine in the suspended function, until above job got completed
        job.join()
        Log.d(TAG + "printNumber: ", custNumber.toString())
    }

    //output of this function--  D/MainActivityTagprintAsyncCount:: CUSTOMER 54 LINE 113
    // this fun is similar implementation as of fun printCustomerCount(), but since async() added, it is cleaner fun
    //when async() - fun is called , it is faster, than launch()
    //async function is being used for asynchronous function
    suspend fun printAsyncCount() {
        CoroutineScope(Dispatchers.IO).launch {
            val customer = async { getCustomerDetail() }
            val line = async { getLineCountDetail() }
            Log.d(TAG + "printAsyncCount: ", "CUSTOMER ${customer.await()} LINE ${line.await()}")
        }

        //Another way of writing the code
//        val job = CoroutineScope(Dispatchers.IO).async {
//            getCustomerDetail()
//        }
//        val job1 = CoroutineScope(Dispatchers.IO).async {
//            getLineCountDetail()
//        }
//        //this job .join(), this make the above coroutine in the suspended function, until above job got completed
//
//        Log.d(TAG + "printAsyncCount: ", "CUSTOMER ${job.await()} LINE ${job1.await()}")
    }


    //output is 54 after adding job.join(), otherwise it will give 0
    //D/MainActivityTagprintCustomerCount:: CUSTOMER 54 LINE 113

    suspend fun printCustomerCount() {
        var custNumber = 0
        var lineNUMBER = 0
        val job = CoroutineScope(Dispatchers.IO).launch {
            custNumber = getCustomerDetail()
        }
        val job1 = CoroutineScope(Dispatchers.IO).launch {
            lineNUMBER = getLineCountDetail()
        }
        //this job .join(), this make the above coroutine in the suspended function, until above job got completed
        job.join()
        job1.join()
        Log.d(TAG + "printCustomerCount: ", "CUSTOMER $custNumber LINE $lineNUMBER")
    }

    suspend fun getCustomerDetail(): Int {
        //delay is coroutine suspended function
        delay(1000)
        return 54
    }

    suspend fun getLineCountDetail(): Int {
        //delay is coroutine suspended function
        delay(1000)
        return 113
    }

    //if the parent coroutine dead , child coroutine will automatically got dead
    /*output D/MainActivityTagparentJob:: parentJob started
             D/MainActivityTagchildJob:: childJob started
             D/MainActivityTagchildJob:: childJob ended
             D/MainActivityTag: parent ended
             D/MainActivityTag: getParentChildRelationShip ended
   */

    suspend fun getParentChildRelationShip() {
        val parentJob = GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG + "parentJob: ", "parentJob started")

            //if we not explicitly mention the child job , it will run on the parent thread, in the above case it is main thread, but in this case it will run on main thread
            val childJob = launch(Dispatchers.IO) {
                Log.d(TAG + "childJob: ", "childJob started")
                //delay is coroutine suspention function
                delay(1000)
                Log.d(TAG + "childJob: ", "childJob ended")
            }
            delay(3000)
            Log.d(TAG, "parent ended")
        }

        delay(1000)
        parentJob.join()
        Log.d(TAG, "getParentChildRelationShip ended")

    }

    /* output -- com.example.basickotlincoroutine D/withContextFun: before
                 com.example.basickotlincoroutine D/withContextFun: Inside
                 com.example.basickotlincoroutine D/withContextFun: After


                 withContextFun - is a blocking suspension function , where as lauch is non blocking function
     */
    suspend fun withContextFun() {
        Log.d("withContextFun", "before")
        //withContext() is a suspending coroutine function , it block the coroutine , so coroutine block will execute first
        withContext(Dispatchers.IO) {
            delay(1000)
            Log.d("withContextFun", "Inside")
        }
        Log.d("withContextFun", "After")
    }

    /* output
        runBlocking - it will block the thread until the coroutine complete
       basickotlincoroutine I/System.out: runBlocking Hello
       basickotlincoroutine I/System.out: runBlocking world
    */

    suspend fun runBlockingCall() {
        runBlocking {
            launch {
                delay(1000)
                println("runBlocking world")
            }
            println("runBlocking Hello")
        }
    }

    companion object {
        const val TAG = "MainActivityTag"
    }
}