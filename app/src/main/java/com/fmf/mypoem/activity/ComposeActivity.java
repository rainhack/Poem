package com.fmf.mypoem.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fmf.mypoem.R;
import com.fmf.mypoem.data.MyPoem;
import com.fmf.mypoem.data.PoemDao;
import com.fmf.mypoem.fragment.BaseDetailFragment;
import com.fmf.mypoem.fragment.DatePickerFragment;
import com.fmf.mypoem.model.Poem;
import com.fmf.mypoem.poem.PoemConstant;
import com.fmf.mypoem.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class ComposeActivity extends BaseActivity implements DatePickerFragment.OnDateSetListener {
    private EditText etTitle;
    private EditText etSubtitle;
    private EditText etContent;
    private EditText etAuthor;
    private EditText etCreated;

    private Poem poem;
//    private int year;
//    private int month;
//    private int day;

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        initViews();

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        final long id = intent.getLongExtra(PoemConstant.ARG_ID, 0);
        final boolean isUpdate = id > 0;
        if (isUpdate) {
            new AsyncTask<Void, Void, Poem>() {
                @Override
                protected Poem doInBackground(Void... params) {
                    return new PoemDao(ComposeActivity.this).get(id);
                }

                @Override
                protected void onPostExecute(Poem poem) {
                    bindViewData(poem);
                }
            }.execute();
        }
    }

    private void bindViewData(Poem poem) {
        this.poem = poem;

        String title = poem.getTitle();
        String subtitle = poem.getSubtitle();
        String content = poem.getContent();
        String author = poem.getAuthor();
        String created = poem.getCreated();

        etTitle.setText(title);
        etSubtitle.setText(subtitle);
        etContent.setText(content);
        etAuthor.setText(author);
        etCreated.setText(created);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_compose;
    }

    private void initViews() {
        etTitle = (EditText) findViewById(R.id.et_title);
        etSubtitle = (EditText) findViewById(R.id.et_subtitle);
        etContent = (EditText) findViewById(R.id.et_content);
        etAuthor = (EditText) findViewById(R.id.et_author);
        etCreated = (EditText) findViewById(R.id.et_created);

        // 只有获得焦点时，才能触发OnClickListner, 所以改用setOnTouchListener
        etCreated.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickerDialog();
                }
                return true;
            }
        });

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        setCreatedDate(year, month, day);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_compose, menu);

        //always return super method for the settings action
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
//            case R.id.action_:
//
//                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_compose:
                compose();
                break;

            case R.id.btn_craft:
                draft();

            default:
        }
    }

    private void draft() {
        savePoem(MyPoem.Poem.STATUS_DRAFT);
    }

    private void compose() {
        savePoem(MyPoem.Poem.STATUS_FINISHED);
    }


    private void savePoem(String status) {
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, getString(R.string.tip_no_content), Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.tip_default_title);
        }

        String subtitle = etSubtitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String created = etCreated.getText().toString().trim();
        String updated = DateUtil.formatDatetimeToSqlite(new Date());

        if (poem == null) {
            poem = new Poem();
        }
        poem.setTitle(title);
        poem.setSubtitle(subtitle);
        poem.setContent(content);
        poem.setAuthor(author);
        poem.setCreated(created);
        poem.setUpdated(updated);
        poem.setStatus(status);

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int tipId = R.string.tip_fail;

                PoemDao dao = new PoemDao(ComposeActivity.this);
                long result = dao.saveOrUpdate(poem);
                if (result > 0) {
                    if (MyPoem.Poem.STATUS_DRAFT.equals(poem.getStatus())) {
                        tipId = R.string.tip_draft_success;
                    } else if (MyPoem.Poem.STATUS_FINISHED.equals(poem.getStatus())) {
                        tipId = R.string.tip_compose_success;
                    }
                }

                return tipId;
            }

            @Override
            protected void onPostExecute(Integer tipId) {
                Toast.makeText(ComposeActivity.this, tipId, Toast.LENGTH_SHORT).show();
                ComposeActivity.this.finish();
                // todo: ComposeActivity关闭，如果是更新，返回detail，更新detail；否则返回list，导航到指定tab
            }
        }.execute();
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog() {
        DialogFragment datePickerFragment = new DatePickerFragment();
//        Bundle args = new Bundle();
//        if (year != 0 && month != 0 && year != 0) {
//            args.putInt(DatePickerFragment.YEAR, year);
//            args.putInt(DatePickerFragment.MONTH, month);
//            args.putInt(DatePickerFragment.DAY, day);
//        }
//        datePickerFragment.setArguments(args);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    public void setCreatedDate(int year, int monthOfYear, int dayOfMonth) {
//        this.year = year;
//        this.month = monthOfYear;
//        this.day = dayOfMonth;
        etCreated.setText(DateUtil.formatDateToMyPome(year, monthOfYear, dayOfMonth));
    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        setCreatedDate(year, monthOfYear, dayOfMonth);
    }

}
