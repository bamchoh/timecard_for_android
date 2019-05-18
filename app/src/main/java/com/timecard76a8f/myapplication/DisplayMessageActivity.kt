package com.timecard76a8f.myapplication

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_display_message.*
import kotlinx.android.synthetic.main.timecard.*
import java.util.*
import kotlin.concurrent.schedule

class DisplayMessageActivity : AppCompatActivity() {

    private val timecardInfos = mutableListOf<TimeCardSet>()

    private var nextIndex: Int = 0

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        record_button.setOnClickListener { record() }

        val handler = Handler()

        Timer().schedule(0, 1000) {
            handler.post {
                updateRecordButtonUI()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when(it.itemId) {
                R.id.action_signout -> {
                    signOut()
                    return true
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signOut() {
        auth.signOut()

        googleSignInClient.signOut().addOnCompleteListener(this) {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
            this.finish()
        }
    }

    private fun calcNextIndex(currentIndex: Int) {
        nextIndex = (currentIndex + 1) % 2
    }

    private fun record() {
        Log.v("DMA/record", "record")
        auth.currentUser?.let {
            val data = TimeCard(nextIndex, System.currentTimeMillis())
            calcNextIndex(nextIndex)
            record_button.setBackgroundColor(Color.parseColor(workStateColor[nextIndex]))

            db.collection("users")
                .document(it.uid)
                .collection("works")
                .add(data)
                .addOnCompleteListener { Log.v("DMA/record", "送信完了")}
                .addOnSuccessListener { Log.v("DMA/record", "送信成功")}
                .addOnFailureListener { Log.v("DMA/record", "送信失敗")}
        }
    }

    private fun updatePhoto(user: FirebaseUser?) {
        val imgView = findViewById<ImageView>(R.id.imageView)
        if(user != null) {
            Glide.with(this).load(user.photoUrl).apply(RequestOptions.circleCropTransform()).into(imgView)
            imgView.visibility = View.VISIBLE
        } else {
            imgView.visibility = View.GONE
        }
    }

    private var pendingTimeCard: TimeCard? = null

    private fun timecardInfosAdd(tc: TimeCard) {
        if(tc.work_idx == 0) {
            pendingTimeCard?.also {
                var end = it
                var start = tc
                timecardInfos.add(TimeCardSet(start, end))
                pendingTimeCard = null
            } ?: run {
                var end = TimeCard()
                var start = tc
                timecardInfos.add(TimeCardSet(start, end))
            }
        } else {
            pendingTimeCard?.let {
                var end = it
                var start = TimeCard()
                timecardInfos.add(TimeCardSet(start, end))
            }
            pendingTimeCard = tc
        }
    }

    private fun updateRecordButtonUI() {
        val sdf = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN)
        record_button.text = "%s\n%s".format(workState[nextIndex], sdf.format(Date()))
        record_button.setBackgroundColor(Color.parseColor(workStateColor[nextIndex]))
    }

    private fun updateUI(user: FirebaseUser?) {
        updatePhoto(user)

        timecardInfos.clear()

        user?.let {
            nameTextView.text = it.displayName.toString()

            db.collection("users")
                .document(it.uid)
                .collection("works")
                .orderBy(WorkInfo::date.name, Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { r, _ ->
                    timecardInfos.clear()

                    if (r != null) {
                        val timeCards = r.toObjects(WorkInfo::class.java)
                        for (i in timeCards.indices) {
                            val timeCard = timeCards[i]
                            if(i == 0) {
                                calcNextIndex(timeCard.work_idx)
                                updateRecordButtonUI()
                            }

                            timecardInfosAdd(TimeCard(timeCard.work_idx, timeCard.date))
                        }

                        val arrayAdapter = TimeCardAdapter(this, timecardInfos)
                        val listView = findViewById<ListView>(R.id.list)
                        listView.adapter = arrayAdapter
                    }
                }
        }
    }
}
