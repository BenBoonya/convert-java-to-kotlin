/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.wordlistsql

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Implements a RecyclerView that displays a list of words from a SQL database.
 * - Clicking the fab button opens a second activity to add a word to the database.
 * - Clicking the Edit button opens an activity to edit the current word in the database.
 * - Clicking the Delete button deletes the current word from the database.
 */
class MainActivity : AppCompatActivity() {

    private val mDB by lazy { WordListOpenHelper(this) }
    private var mAdapter: WordListAdapter? = null
    private val mLastPosition: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = WordListAdapter(this, mDB)
        recyclerview?.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Add a floating action click handler for creating new entries.
        fab.setOnClickListener {
            // Starts empty edit activity.
            val intent = Intent(baseContext, EditWordActivity::class.java)
            startActivityForResult(intent, WORD_EDIT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_setting -> {
                    Toast.makeText(
                            applicationContext,
                            R.string.setting_menu,
                            Toast.LENGTH_LONG).show()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == WORD_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra(EditWordActivity.EXTRA_REPLY)?.let {
                    // Update the database.
                    if (!TextUtils.isEmpty(it)) {
                        val id = data.getIntExtra(WordListAdapter.EXTRA_ID, -99)

                        if (id == WORD_ADD) {
                            mDB?.insert(it)
                        } else if (id >= 0) {
                            mDB?.update(id, it)
                        }
                        // Update the UI.
                        mAdapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                                applicationContext,
                                R.string.empty_not_saved,
                                Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName

        const val WORD_EDIT = 1
        const val WORD_ADD = -1
    }
}